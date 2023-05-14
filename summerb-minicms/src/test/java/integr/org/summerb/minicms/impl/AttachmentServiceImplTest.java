/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package integr.org.summerb.minicms.impl;

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
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.Query;
import org.summerb.minicms.api.ArticleService;
import org.summerb.minicms.api.AttachmentService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-articles-app-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class AttachmentServiceImplTest {
  @Autowired private AttachmentService attachmentService;
  @Autowired private ArticleService articleService;

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

  private long createTestArticle() throws ValidationException, NotAuthorizedException {
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

    PaginatedList<Attachment> result =
        attachmentService.find(
            new PagerParams(0, 100), Query.n().eq(Attachment.FN_ARTICLE_ID, a.getArticleId()));
    assertEquals(2, result.getItems().size());
  }

  public static InputStream createValidContentsReader() {
    return AttachmentServiceImplTest.class.getClassLoader().getResourceAsStream("file.contents");
  }

  public static void assertStreamsEquals(
      InputStream expectedInputStream, InputStream subjectInputStream) throws IOException {
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
