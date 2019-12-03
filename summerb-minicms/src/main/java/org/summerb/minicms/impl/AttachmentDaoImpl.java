/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.minicms.impl;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.ParameterSourceBuilder;
import org.summerb.easycrud.impl.mysql.EasyCrudDaoMySqlImpl;
import org.summerb.minicms.api.AttachmentDao;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.validation.FieldValidationException;

public class AttachmentDaoImpl extends EasyCrudDaoMySqlImpl<Long, Attachment> implements AttachmentDao {
	private String sqlGetFileContentsByUuid;
	private String sqlUpdateFileContents;

	public AttachmentDaoImpl() {
		setRowMapper(rowMapper);
		setParameterSourceBuilder(parameterSourceBuilder);
		setDtoClass(Attachment.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		sqlGetFileContentsByUuid = String.format("SELECT * FROM %s WHERE id = :id", getTableName());
		sqlUpdateFileContents = String.format("UPDATE %s SET contents = :contents WHERE id = :id", getTableName());
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
			if (dto.getContents() != null) {
				params.addValue("contents", dto.getContents(), java.sql.Types.LONGVARBINARY);
			}
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
			// NOTE: Not mapping content (InputStream) intentionally! We dson't
			// want to open it each time we just want to list attachments
			return ret;
		}
	};

	private RowMapper<InputStream> rowMapperFileContents = new RowMapper<InputStream>() {
		@Override
		public InputStream mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getBinaryStream("contents");
		}
	};

	@Override
	public void create(Attachment dto) throws FieldValidationException {
		super.create(dto);
		putContentInputStream(dto.getId(), dto.getContents());
	}

	@Override
	public void putContentInputStream(long id, InputStream contents) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		params.addValue("contents", contents);
		int affected = jdbc.update(sqlUpdateFileContents, params);
		if (affected != 1) {
			throw new RuntimeException(
					"Failed to update attachment record with contents, " + affected + " records affected instead of 1");
		}
	}

	@Override
	public InputStream getContentInputStream(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		List<InputStream> results = jdbc.query(sqlGetFileContentsByUuid, paramMap, rowMapperFileContents);
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		return (InputStream) results.get(0);
	}

}
