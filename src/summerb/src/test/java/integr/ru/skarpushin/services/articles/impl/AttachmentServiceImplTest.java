package integr.ru.skarpushin.services.articles.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.ArticleService;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-articles-app-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class AttachmentServiceImplTest {
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private ArticleService articleService;

	@Test
	public void testAttachment_expectCreatedAndRetrieved() throws Exception {
		Attachment a = new Attachment();
		a.setArticleId(createTestArticle());
		a.setName("test attachment");
		a.setSize(123);
		a.setContents(createValidContentsReader());
		Attachment result = attachmentService.create(a);
		assertNotNull(result);
		assertNotNull(result.getId());
		assertTrue(result.getId() > 0);

		Attachment b = attachmentService.findById(result.getId());
		assertNotNull(b);
		// NOTE: By default we don't want to open stream
		assertNull(b.getContents());
	}

	@Test
	public void testAttachment_expectCanRetrieveInputStream() throws Exception {
		Attachment a = new Attachment();
		a.setArticleId(createTestArticle());
		a.setName("test attachment");
		a.setSize(123);
		a.setContents(createValidContentsReader());
		Attachment result = attachmentService.create(a);

		InputStream b = attachmentService.getContentInputStream(result.getId());
		assertStreamsEquals(createValidContentsReader(), b);
	}

	private long createTestArticle() throws FieldValidationException, NotAuthorizedException {
		Article dto = new Article();
		dto.setArticleKey("key");
		dto.setLang("en");
		dto.setTitle("tttl");
		dto.setContent("cccc");
		Article result = articleService.create(dto);
		return result.getId();
	}

	@Test
	public void test_expectStreamComparisonWorksCorrectly() throws Exception {
		assertStreamsEquals(createValidContentsReader(), createValidContentsReader());
	}

	@Test
	public void testAttachment_expectMultipleResults() throws Exception {
		Attachment a = new Attachment();
		a.setArticleId(createTestArticle());
		a.setName("test attachment");
		a.setSize(123);
		a.setContents(createValidContentsReader());
		attachmentService.create(a);

		a.setContents(createValidContentsReader());
		a.setName("test attachment 2");
		attachmentService.create(a);

		PaginatedList<Attachment> result = attachmentService.query(new PagerParams(0, 100),
				Query.n().eq(Attachment.FN_ARTICLE_ID, a.getArticleId()));
		assertEquals(2, result.getItems().size());
	}

	public static InputStream createValidContentsReader() {
		return AttachmentServiceImplTest.class.getClassLoader().getResourceAsStream("file.contents");
	}

	public static void assertStreamsEquals(InputStream expectedInputStream, InputStream subjectInputStream)
			throws IOException {
		int byteExpected = expectedInputStream.read();
		int byteSubject = subjectInputStream.read();

		int i = 0;
		while (byteSubject != -1 || byteExpected != -1) {
			assertEquals("Failed at pos: " + i, byteExpected, byteSubject);
			byteExpected = expectedInputStream.read();
			byteSubject = subjectInputStream.read();
			i++;
		}
	}

}
