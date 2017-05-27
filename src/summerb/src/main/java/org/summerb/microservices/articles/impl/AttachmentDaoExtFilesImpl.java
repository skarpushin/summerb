package org.summerb.microservices.articles.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.FileCopyUtils;
import org.summerb.approaches.jdbccrud.api.ParameterSourceBuilder;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.impl.EasyCrudDaoMySqlImpl;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.AttachmentDao;
import org.summerb.microservices.articles.api.dto.Attachment;

/**
 * Alternative impl which stores files externally. Not in database! That is
 * fragile because files on disk are not managed by container and could be
 * removed. So that needs to be paid attention to a lot! In some cases it's
 * worth it
 * 
 * @author sergeyk
 *
 */
public class AttachmentDaoExtFilesImpl extends EasyCrudDaoMySqlImpl<Long, Attachment>implements AttachmentDao {
	private Logger log = Logger.getLogger(getClass());

	private String targetFolder = "article-attachments";

	public AttachmentDaoExtFilesImpl() {
		setRowMapper(rowMapper);
		setParameterSourceBuilder(parameterSourceBuilder);
		setDtoClass(Attachment.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		assertTargetFolder();
	}

	private void assertTargetFolder() throws IOException {
		File target = new File(targetFolder);
		if (!target.exists() && !target.mkdirs()) {
			throw new IllegalArgumentException("Target folder doesn't exist and could not be created: " + targetFolder);
		}

		File testFile = new File(targetFolder + "/test-file-create-" + new Date().getTime());
		if (!testFile.createNewFile()) {
			throw new IllegalArgumentException(
					"Failed to create test file, looks like we don't ahve write permission to storage folder: "
							+ targetFolder);
		}
		testFile.delete();

		log.info("Using that folder for attachments storage and retrieval: " + target.getAbsolutePath());
	}

	@Override
	protected SimpleJdbcInsert buildJdbcInsert() {
		SimpleJdbcInsert ret = super.buildJdbcInsert();
		return ret.usingColumns("name", "article_id", "size");
	}

	private ParameterSourceBuilder<Attachment> parameterSourceBuilder = new ParameterSourceBuilder<Attachment>() {
		@Override
		public SqlParameterSource buildParameterSource(Attachment dto) {
			MapSqlParameterSource params = new MapSqlParameterSource();
			if (dto.getId() != null) {
				params.addValue("id", dto.getId());
			}
			params.addValue("name", dto.getName());
			params.addValue("article_id", dto.getArticleId());
			params.addValue("size", dto.getSize()/* , java.sql.Types.BIGINT */);
			return params;
		}
	};

	@Override
	protected String buildFieldsForSelect() {
		return "id,name,article_id,size";
	};

	private RowMapper<Attachment> rowMapper = new RowMapper<Attachment>() {
		@Override
		public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Attachment ret = new Attachment();
			ret.setId(rs.getLong("id"));
			ret.setName(rs.getString("name"));
			ret.setArticleId(rs.getLong("article_id"));
			ret.setSize(rs.getLong("size"));
			return ret;
		}
	};

	@Override
	public void create(Attachment dto) throws FieldValidationException {
		super.create(dto);
		putContentInputStream(dto.getId(), dto.getContents());
	}

	@Override
	public void putContentInputStream(long id, InputStream contents) {
		try (FileOutputStream fos = new FileOutputStream(buildAttachmentFsName(id))) {
			FileCopyUtils.copy(contents, fos);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to store attachment contents on disk", e);
		}
	}

	private String buildAttachmentFsName(long attachmentId) {
		return targetFolder + "/" + attachmentId;
	};

	@Override
	public InputStream getContentInputStream(long id) {
		try {
			return new FileInputStream(buildAttachmentFsName(id));
		} catch (Throwable e) {
			throw new RuntimeException("Failed to open attachment " + id + " input stream", e);
		}
	}

	@Override
	public int delete(Long id) {
		int ret = super.delete(id);
		if (ret > 0) {
			File fl = new File(buildAttachmentFsName(id));
			ret = !fl.exists() || fl.delete() ? 1 : 0;
		}
		return ret;
	}

	@Override
	public int delete(Long id, long modifiedAt) {
		int ret = super.delete(id, modifiedAt);
		if (ret > 0) {
			File fl = new File(buildAttachmentFsName(id));
			ret = !fl.exists() || fl.delete() ? 1 : 0;
		}
		return ret;
	}

	@Override
	public int deleteByQuery(Query query) {
		PaginatedList<Attachment> aa = query(PagerParams.ALL, query);
		int ret = 0;
		for (Attachment a : aa.getItems()) {
			ret += delete(a.getId());
		}
		return ret;
	}

	public String getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(String filesStorageFolderPathName) {
		this.targetFolder = filesStorageFolderPathName;
	}

}
