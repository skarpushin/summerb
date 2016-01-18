package integr.ru.skarpushin.services.articles.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.errors.DataTooLongValidationError;
import org.summerb.approaches.validation.errors.DuplicateNameValidationError;
import org.summerb.microservices.articles.api.ArticleService;
import org.summerb.microservices.articles.api.dto.Article;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-articles-app-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class ArticleServiceImplTest {
	private static final String AKEY = "akey";

	@Autowired
	private ArticleService articleService;

	@Test
	public void testCreateArticle_expectOk() throws Exception {
		Article a = buildTestDto();

		Article result = articleService.create(a);
		assertNotNull(result);

		Article b = articleService.findById(result.getId());
		assertNotNull(b);

		assertEquals(a.getArticleKey(), b.getArticleKey());
		assertEquals(a.getTitle(), b.getTitle());
		assertEquals(a.getAnnotation(), b.getAnnotation());
		assertEquals(a.getContent(), b.getContent());
		assertEquals(a.getArticleGroup(), b.getArticleGroup());
	}

	@Test
	public void testCreateArticle_expectFveOnDuplicate() throws Exception {
		articleService.create(buildTestDto());
		try {
			articleService.create(buildTestDto());
			fail("FVE expected");
		} catch (FieldValidationException fve) {
			assertNotNull(fve.findErrorOfTypeForField(DuplicateNameValidationError.class, Article.FN_KEY));
		}
	}

	@Test
	public void testCreateArticleContent_testDataTruncationError() throws Exception {
		Article a = buildTestDto();
		a.setArticleKey(generateLongString(Article.FN_KEY_SIZE, "Z"));
		a.setLang("ZZ");
		a.setTitle(generateLongString(Article.FN_TITLE_SIZE, "Ф"));
		a.setAnnotation(generateLongString(Article.FN_ANNOTATION_SIZE, "Ф"));
		a.setContent(generateLongString(Article.FN_ANNOTATION_SIZE, "Ф"));
		a.setArticleGroup(generateLongString(Article.FN_GROUP_SIZE, "Z"));
		articleService.create(a);

		// Update data with wrong length text
		try {
			a = buildTestDto();
			a.setArticleKey(generateLongString(Article.FN_KEY_SIZE + 1, "ф"));
			a.setLang("ZZZ");
			a.setTitle(generateLongString(Article.FN_TITLE_SIZE + 1, "Ф"));
			a.setAnnotation(generateLongString(Article.FN_ANNOTATION_SIZE + 1, "ф"));
			a.setContent(generateLongString(Article.FN_ANNOTATION_SIZE + 1, "ф"));
			a.setArticleGroup(generateLongString(Article.FN_GROUP_SIZE + 1, "Z"));
			articleService.create(a);
			fail("FVE expected");
		} catch (FieldValidationException fve) {
			assertEquals(5, fve.getErrors().size());
			assertNotNull(fve.findErrorOfTypeForField(DataTooLongValidationError.class, Article.FN_KEY));
			assertNotNull(fve.findErrorOfTypeForField(DataTooLongValidationError.class, Article.FN_LANG));
			assertNotNull(fve.findErrorOfTypeForField(DataTooLongValidationError.class, Article.FN_TITLE));
			assertNotNull(fve.findErrorOfTypeForField(DataTooLongValidationError.class, Article.FN_ANNOTATION));
			assertNotNull(fve.findErrorOfTypeForField(DataTooLongValidationError.class, Article.FN_GROUP));
		}
	}

	public static String generateLongString(int desiredStringLength, String exampleChars) {
		StringBuilder ret = new StringBuilder(desiredStringLength + exampleChars.length());
		while (ret.length() < desiredStringLength) {
			ret.append(exampleChars);
		}
		ret.setLength(desiredStringLength);
		return ret.toString();
	}

	private Article buildTestDto() {
		Article a = new Article();
		a.setArticleKey(AKEY);
		a.setTitle("title1");
		a.setAnnotation("ann");
		a.setContent("asdasd");
		a.setArticleGroup("somegroup");
		a.setLang("ru");
		return a;
	}
}
