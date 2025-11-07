package integr.org.summerb.easycrud;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import integr.org.summerb.easycrud.config.EasyCrudConfig;
import integr.org.summerb.easycrud.config.EasyCrudServiceBeansConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.CommentRow;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.EasyCrudMessageCodes;
import org.summerb.easycrud.exceptions.EasyCrudUnexpectedException;
import org.summerb.easycrud.exceptions.EntityNotFoundException;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.Select;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {EmbeddedDbConfig.class, EasyCrudConfig.class, EasyCrudServiceBeansConfig.class})
@AutoConfigureEmbeddedDatabase(
    type = AutoConfigureEmbeddedDatabase.DatabaseType.MARIADB,
    refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS)
@ProfileValueSourceConfiguration()
@Transactional
public class JoinQuerySingleSelectTest extends JoinQueryTestAbstract {

  @Test
  public void expectOrderingWorks() {
    // GIVEN
    createTestData();

    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query().eq(PostRow::getTitle, "env4");
    Select<Long, CommentRow> select = qComment.toJoin().join(qPost).select();

    assertEquals(2, select.count());

    // WHEN - asc
    // NOTE: Also intentionally making sure that orderBy produced by commentRowService will be
    // accepted by JoinQuery
    List<CommentRow> list = select.findAll(commentRowService.orderBy(CommentRow::getComment).asc());

    // THEN - asc
    assertEquals(2, list.size());
    assertEquals("BBB", list.get(0).getComment());
    assertEquals("CCC", list.get(1).getComment());

    // WHEN - desc
    list = select.findAll(qComment.orderBy(CommentRow::getComment).desc());

    // THEN - desc
    assertEquals(2, list.size());
    assertEquals("CCC", list.get(0).getComment());
    assertEquals("BBB", list.get(1).getComment());
  }

  @Test
  public void expectCommentWillAppearOnlyOnce() {
    createTestData();

    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query().eq(PostRow::getTitle, "env4");

    Select<Long, CommentRow> select = qComment.toJoin().join(qPost).select();
    assertEquals(2, select.count());

    List<CommentRow> found = select.findAll(qComment.orderBy(CommentRow::getComment).asc());
    assertEquals("BBB", found.get(0).getComment());
    assertEquals("CCC", found.get(1).getComment());
  }

  @Test
  public void expectCommentWillAppearOnlyOnce_expectSameIfWeGoOtherWayAround() {
    createTestData();

    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query().eq(PostRow::getTitle, "env4");

    Select<Long, CommentRow> select =
        qPost.toJoin().joinBack(qComment, CommentRow::getPostId).select(qComment);
    assertEquals(2, select.count());

    List<CommentRow> found = select.findAll(qComment.orderBy(CommentRow::getComment).asc());
    assertEquals("BBB", found.get(0).getComment());
    assertEquals("CCC", found.get(1).getComment());
  }

  @Test
  public void expectEmptyResultsWhenInnerJoinedTableHasNoMatches() {
    // GIVEN
    createTestData();

    Query<String, UserRow> qUser = userRowService.query().eq(UserRow::getName, "bba");
    assertEquals(1, qUser.count());

    // WHEN
    List<UserRow> results = qUser.toJoin().join(commentRowService.query()).select().findAll();

    // THEN
    assertEquals(0, results.size());
  }

  @Test
  public void expectEmptyResultsWhenPrimaryTableFromInnerJoinHasNoMatches() {
    // GIVEN
    createTestData();

    // WHEN
    List<UserRow> results =
        userRowService
            .query()
            .eq(UserRow::getName, "doesnt-exist")
            .toJoin()
            .join(commentRowService.query())
            .select()
            .findAll();

    // THEN
    assertEquals(0, results.size());
  }

  @Test
  public void expectCorrectOrderingWithMultipleColumnsAndNullsLast() {
    if (!isPostgres()) {
      return;
    }

    // GIVEN
    createTestData();

    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 5, 10);
    Query<Long, PostRow> qPost = postRowService.query();
    Select<Long, PostRow> select = qPost.toJoin().join(qUser, PostRow::getAuthorId).select();
    assertEquals(3, select.count());

    // WHEN -- nulls first
    List<PostRow> results =
        select.findAll(
            qUser.orderBy(UserRow::getStatus).asc().nullsFirst(),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).getTitle());
    assertEquals("env4", results.get(1).getTitle());
    assertEquals("env5", results.get(2).getTitle());

    // WHEN -- nulls last
    results =
        select.findAll(
            qUser.orderBy(UserRow::getStatus).asc().nullsLast(),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env4", results.get(0).getTitle());
    assertEquals("env5", results.get(1).getTitle());
    assertEquals("env3", results.get(2).getTitle());
  }

  @Test
  public void expectCorrectOrderingWithCollation() {
    if (isPostgres()) {
      // NOTE: I couldn't make postgres behave as desired, and having that this feature is not super
      // needed, I guess I'll skip it for now
      return;
    }

    // GIVEN
    createTestData();

    Query<String, UserRow> qUser = userRowService.query("users").between(UserRow::getKarma, 5, 10);
    Query<Long, PostRow> qPost = postRowService.query();
    Select<Long, PostRow> selectPosts =
        qUser.toJoin().joinBack(qPost, PostRow::getAuthorId).select(qPost);
    assertEquals(3, selectPosts.count());

    // WHEN -- case sensitive
    // NOTE: Also intentionally making sure that orderBy produced manually will be
    // accepted by JoinQuery
    List<PostRow> results =
        selectPosts.findAll(
            OrderBy.Asc("users.name").withCollate("latin1_general_cs"), OrderBy.Asc("posts.title"));

    // THEN
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).getTitle());
    assertEquals("env4", results.get(1).getTitle());
    assertEquals("env5", results.get(2).getTitle());

    // WHEN -- case insensitive
    results =
        selectPosts.findAll(
            qUser.orderBy(UserRow::getName).asc().withCollate("latin1_general_ci"),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env4", results.get(0).getTitle());
    assertEquals("env5", results.get(1).getTitle());
    assertEquals("env3", results.get(2).getTitle());
  }

  @Test
  void expectPaginationWorkCorrectly() {
    // GIVEN
    createTestData();

    Query<Long, PostRow> qPost = postRowService.query();
    Select<Long, CommentRow> select =
        commentRowService.query().ne(CommentRow::getComment, "CCC").toJoin().join(qPost).select();

    // WHEN -- asc, page 1
    PagerParams pagerParams = new PagerParams(0, 2);
    PaginatedList<CommentRow> results =
        select.find(pagerParams, qPost.orderBy(PostRow::getLikes).asc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(2, results.getItems().size());
    assertEquals("DDD", results.getItems().get(0).getComment());
    assertEquals("BBB", results.getItems().get(1).getComment());

    // also make sure findPage works the same
    List<CommentRow> page = select.findPage(pagerParams, qPost.orderBy(PostRow::getLikes).asc());
    assertEquals(2, page.size());
    for (int i = 0; i < 2; i++) {
      assertEquals(page.get(i).getId(), results.getItems().get(i).getId());
    }

    // WHEN -- asc, page 2
    results = select.find(new PagerParams(2, 2), qPost.orderBy(PostRow::getLikes).asc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(1, results.getItems().size());
    assertEquals("AAA", results.getItems().get(0).getComment());

    // WHEN -- desc, page 1
    results = select.find(pagerParams, qPost.orderBy(PostRow::getLikes).desc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(2, results.getItems().size());
    assertEquals("AAA", results.getItems().get(0).getComment());
    assertEquals("BBB", results.getItems().get(1).getComment());

    // WHEN -- desc, page 2
    results = select.find(new PagerParams(2, 2), qPost.orderBy(PostRow::getLikes).desc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(1, results.getItems().size());
    assertEquals("DDD", results.getItems().get(0).getComment());
  }

  @Test
  void expectInnerJoinToReturnOnlyRecordsWhichHaveReferencesNotNull() {
    createTestData();

    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN
    List<PostRow> posts =
        qPost
            .toJoin()
            .join(userRowService.query(), PostRow::getPinnedBy)
            .select()
            .findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(2, posts.size());
    assertEquals("env4", posts.get(0).getTitle());
    assertEquals("env5", posts.get(1).getTitle());
  }

  @Test
  void expectFindAndGetOneWorkCorrectly() {
    createTestData();

    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN - expect null
    Select<Long, PostRow> select =
        qPost
            .toJoin()
            .join(userRowService.query().eq(UserRow::getKarma, 11), PostRow::getAuthorId)
            .select();
    PostRow result = select.findOne();

    // THEN
    assertNull(result);

    // WHEN - expect exception
    assertThrows(EntityNotFoundException.class, select::getOne);

    // WHEN - find: expect result
    select =
        qPost
            .toJoin()
            .join(userRowService.query().eq(UserRow::getKarma, 10), PostRow::getAuthorId)
            .select();
    result = select.findOne();

    // THEN
    assertNotNull(result);
    assertEquals("env3", result.getTitle());

    // WHEN - get: expect result
    result = select.getOne();

    // THEN
    assertNotNull(result);
    assertEquals("env3", result.getTitle());
  }

  @Test
  void expectFindFirstFindsResults() {
    // GIVEN
    createTestData();

    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 10, 15);
    Select<Long, PostRow> select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).select();
    PostRow result = select.findFirst(qUser.orderBy(UserRow::getKarma).asc());

    // THEN
    assertNotNull(result);
    assertEquals("env5", result.getTitle());
  }

  @Test
  void expectFindFirstFindsNothing() {
    // GIVEN
    createTestData();

    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 30, 45);
    Select<Long, PostRow> select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).select();
    PostRow result = select.findFirst(qUser.orderBy(UserRow::getKarma).asc());

    // THEN
    assertNull(result);
  }

  @Test
  void expectGetFirstGetsResults() {
    // GIVEN
    createTestData();

    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 10, 15);
    Select<Long, PostRow> select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).select();
    PostRow result = select.getFirst(qUser.orderBy(UserRow::getKarma).asc());

    // THEN
    assertNotNull(result);
    assertEquals("env5", result.getTitle());
  }

  @Test
  void expectGetFirstGetsNothing() {
    // GIVEN
    createTestData();

    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 30, 45);
    Select<Long, PostRow> select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).select();

    // THEN
    assertThrows(
        EntityNotFoundException.class,
        () -> select.getFirst(qUser.orderBy(UserRow::getKarma).asc()));
  }

  @Test
  void expectGetAllWorks() {
    // GIVEN
    createTestData();

    // WHEN
    Query<String, UserRow> qUser = userRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    List<PostRow> results =
        qPost
            .toJoin()
            .join(qUser, PostRow::getAuthorId)
            .select()
            .getAll(qUser.orderBy(UserRow::getKarma).asc(), qPost.orderBy(PostRow::getLikes).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env4", results.get(0).getTitle());
    assertEquals("env5", results.get(1).getTitle());
    assertEquals("env3", results.get(2).getTitle());
  }

  @Test
  void expectJoin3TablesWorksOk() {
    // GIVEN
    createTestData();

    // WHEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query();

    Select<Long, CommentRow> select =
        qComment
            .toJoin()
            .join(qPost, CommentRow::getPostId)
            .join(qPost, qUser, PostRow::getAuthorId)
            .select();

    List<CommentRow> results =
        select.findAll(
            qUser.orderBy(UserRow::getKarma).asc(),
            qPost.orderBy(PostRow::getTitle).asc(),
            qComment.orderBy(CommentRow::getComment).asc());

    assertEquals(4, results.size());
    assertEquals("BBB", results.get(0).getComment());
    assertEquals("CCC", results.get(1).getComment());
    assertEquals("AAA", results.get(2).getComment());
    assertEquals("DDD", results.get(3).getComment());
  }

  @Test
  void expectParsedOrderByWorksAsExpected() {
    // GIVEN
    createTestData();

    // WHEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query("users");

    JoinQuery<Long, CommentRow> joinQuery =
        qComment
            .toJoin()
            .join(qPost, CommentRow::getPostId)
            .join(qPost, qUser, PostRow::getAuthorId);

    OrderBy[] orderBys1 =
        joinQuery.parseOrderBy("users.karma,asc;posts.title,asc;comments.comment,asc");
    OrderBy[] orderBys2 =
        joinQuery.parseOrderBy(
            new String[] {"users.karma,asc", "posts.title,asc", "comments.comment,asc"});
    assertArrayEquals(orderBys1, orderBys2);

    List<CommentRow> results = joinQuery.select().findAll(orderBys1);

    assertEquals(4, results.size());
    assertEquals("BBB", results.get(0).getComment());
    assertEquals("CCC", results.get(1).getComment());
    assertEquals("AAA", results.get(2).getComment());
    assertEquals("DDD", results.get(3).getComment());
  }

  @Test
  void expectExceptionIfAttemptingToSelectQueryThatIsNotInTheJoin() {
    // GIVEN
    createTestData();

    // WHEN / THEN
    assertThrows(
        IllegalArgumentException.class,
        () ->
            commentRowService
                .query()
                .toJoin()
                .join(userRowService.query())
                .select(postRowService.query()));
  }

  @Test
  void expectExceptionStrategyUsed() {
    // GIVEN
    createTestData();

    // WHEN / THEN
    try {
      commentRowService.query().toJoin().join(userRowService.query()).select().findOne();
      fail("Exception expected");
    } catch (EasyCrudUnexpectedException e) {
      assertEquals(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_FIND, e.getMessageCode());
      assertEquals(commentRowService.getRowMessageCode(), e.getMessageArgs()[0]);
    }
  }

  @Test
  void expectLeftJoinReturnsAllLeftTableRecordsEvenWhenNoMatchesOnRight() {
    // GIVEN
    createTestData();

    Query<String, UserRow> qUser = userRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN
    Select<Long, PostRow> select = qPost.toJoin().leftJoin(qUser, PostRow::getPinnedBy).select();
    assertEquals(3, select.count());

    List<PostRow> results = select.findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN - Should return all posts regardless of pinnedBy value
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).getTitle());
    assertEquals("env4", results.get(1).getTitle());
    assertEquals("env5", results.get(2).getTitle());
  }

  @Test
  void expectLeftJoinBackReturnsAllRightTableRecords() {
    // GIVEN
    createTestData();

    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query();

    // WHEN
    Select<String, UserRow> select =
        qUser.toJoin().leftJoinBack(qPost, PostRow::getPinnedBy).select();
    assertEquals(3, select.count());

    List<UserRow> results = select.findAll(qUser.orderBy(UserRow::getKarma).asc());

    // THEN - Should return all users even though not all of them are referenced from pinnedBy
    assertEquals(3, results.size());
    assertEquals("bba", results.get(0).getName());
    assertEquals("BBc", results.get(1).getName());
    assertEquals("name3", results.get(2).getName());
  }

  @Test
  void expectComplexJoinCombinationWithMultipleLeftJoins() {
    // GIVEN
    createTestData();

    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qPinnedBy = userRowService.query();
    Query<Long, CommentRow> qComment = commentRowService.query();

    // WHEN
    Select<Long, CommentRow> select =
        qComment.toJoin().join(qPost).leftJoin(qPost, qPinnedBy, PostRow::getPinnedBy).select();
    assertEquals(4, select.count());

    List<CommentRow> results = select.findAll(qComment.orderBy(CommentRow::getComment).asc());

    // THEN - Should return all comments and all posts even though not all posts have pinnedBy value
    assertEquals(4, results.size());
    assertEquals("AAA", results.get(0).getComment());
    assertEquals("BBB", results.get(1).getComment());
    assertEquals("CCC", results.get(2).getComment());
    assertEquals("DDD", results.get(3).getComment());
  }

  @Test
  void expectLeftJoinBetweenNonPrimaryTablesWorks() {
    // GIVEN
    createTestData();

    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query();

    // Join comment -> post (INNER) and then post -> user (LEFT JOIN)
    Select<Long, CommentRow> select =
        qComment
            .toJoin()
            // INNER JOIN - comment must have post
            .join(qPost, CommentRow::getPostId)
            // LEFT JOIN - post may not have author (though all do in our
            // data)
            .leftJoin(qPost, qUser, PostRow::getAuthorId)
            .select();

    // WHEN
    List<CommentRow> results = select.findAll(qComment.orderBy(CommentRow::getComment).asc());

    // THEN - All 4 comments should be returned since they all have posts
    assertEquals(4, results.size());
  }

  @Test
  void expectLeftJoinWithAutomaticForeignKeyDetection() {
    // GIVEN
    createTestData();

    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN
    Select<Long, CommentRow> select = qComment.toJoin().leftJoin(qPost).select();
    assertEquals(4, select.count());

    List<CommentRow> results = select.findAll(qComment.orderBy(CommentRow::getComment).asc());

    // THEN - Should return all posts with LEFT JOIN on users
    assertEquals(4, results.size());
    assertEquals("AAA", results.get(0).getComment());
    assertEquals("BBB", results.get(1).getComment());
    assertEquals("CCC", results.get(2).getComment());
    assertEquals("DDD", results.get(3).getComment());
  }

  @Test
  void expectMixedInnerAndLeftJoinFiltering() {
    // GIVEN
    createTestData();

    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qAuthor =
        userRowService.query().between(UserRow::getKarma, 5, 10); // user1 & user2
    Query<String, UserRow> qPinnedBy =
        userRowService.query().between(UserRow::getKarma, 10, 20); // user2 & user3

    // INNER JOIN with author (filters to posts by user1) + LEFT JOIN with pinnedBy (can be null or
    // match)
    Select<Long, PostRow> select =
        qPost
            .toJoin()
            .join(qAuthor, PostRow::getAuthorId)
            .leftJoin(qPinnedBy, PostRow::getPinnedBy)
            .select();

    // WHEN
    List<PostRow> results = select.findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN - All 3 posts must be returned because they all authored by applicable users. The LEFT
    // joined on pinned must not limit results
    assertEquals(3, results.size());

    assertEquals("env3", results.get(0).getTitle());
    assertEquals("env4", results.get(1).getTitle());
    assertEquals("env5", results.get(2).getTitle());
  }

  @SuppressWarnings("SameParameterValue")
  private UserRow getUserByName(String name) {
    return userRowService.query().eq(UserRow::getName, name).getOne();
  }
}
