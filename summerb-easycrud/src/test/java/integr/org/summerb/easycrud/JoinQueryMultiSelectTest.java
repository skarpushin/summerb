package integr.org.summerb.easycrud;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import org.junit.jupiter.api.BeforeEach;
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
import org.summerb.easycrud.join_query.JoinedSelect;
import org.summerb.easycrud.join_query.model.JoinedRow;
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
public class JoinQueryMultiSelectTest extends JoinQueryTestAbstract {

  private boolean testDataCreated;

  @BeforeEach
  public void setUp() {
    if (!testDataCreated) {
      testDataCreated = true;
      createTestData();
    }
  }

  @Test
  public void expectGracefulBehaviorInCaseOneOfTheQueriesCannotYieldResults() {
    // GIVEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost =
        postRowService.query().eq(PostRow::getTitle, "env4").in(PostRow::getLikes, null);
    JoinedSelect select = qComment.toJoin().join(qPost).selectAll();

    // WHEN / THEN - count
    assertEquals(0, select.count());

    // WHEN / THEN - find
    assertEquals(0, select.findAll().size());
  }

  @Test
  public void expectGracefulBehaviorInCaseAllOfTheQueriesInOrWithNoResultsQuery() {
    // GIVEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost =
        postRowService
            .query()
            .eq(PostRow::getTitle, "env4")
            .or(
                postRowService.query().in(PostRow::getBody, null),
                postRowService.query().in(PostRow::getTitle, null));
    JoinedSelect select = qComment.toJoin().join(qPost).selectAll();

    // WHEN / THEN - count
    assertEquals(0, select.count());

    // WHEN / THEN - find
    assertEquals(0, select.findAll().size());
  }

  @Test
  public void expectGracefulBehaviorInCaseOneOfTheQueriesHasOrWithNoResultsQuery() {
    // GIVEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost =
        postRowService
            .query()
            .eq(PostRow::getTitle, "env4")
            .or(
                postRowService.query().in(PostRow::getBody, null),
                postRowService.query().eq(PostRow::getBody, "link11"));
    JoinedSelect select = qComment.toJoin().join(qPost).selectAll();

    // WHEN / THEN - count
    assertEquals(2, select.count());

    // WHEN / THEN - find
    List<JoinedRow> results = select.findAll(qComment.orderBy(CommentRow::getComment).asc());
    assertEquals(2, results.size());
    assertEquals("BBB", results.get(0).get(qComment).getComment());
    assertEquals("CCC", results.get(1).get(qComment).getComment());
  }

  @Test
  public void expectGracefulBehaviorInCaseNotExistsUsesQueryWithNoResults() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query().eq(PostRow::getTitle, "env5");
    JoinedSelect select =
        qPost
            .toJoin()
            .notExists(
                commentRowService.query().in(CommentRow::getComment, null), CommentRow::getPostId)
            .selectAll();

    // WHEN / THEN - count
    assertEquals(1, select.count());

    // WHEN / THEN - find
    List<JoinedRow> results = select.findAll();
    assertEquals(1, results.size());
    assertEquals("link1", results.get(0).get(qPost).getBody());
  }

  @Test
  public void expectGracefulBehaviorInCaseNotExistsUsesOrQueryWithNoResults() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query().eq(PostRow::getTitle, "env5");
    JoinedSelect select =
        qPost
            .toJoin()
            .notExists(
                commentRowService
                    .query()
                    .or(
                        commentRowService.query().in(CommentRow::getComment, null),
                        commentRowService.query().in(CommentRow::getAuthorId, null)),
                CommentRow::getPostId)
            .selectAll();

    // WHEN / THEN - count
    assertEquals(1, select.count());

    // WHEN / THEN - find
    List<JoinedRow> results = select.findAll();
    assertEquals(1, results.size());
    assertEquals("link1", results.get(0).get(qPost).getBody());
  }

  @Test
  public void expectOrderingWorks() {
    // GIVEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query().eq(PostRow::getTitle, "env4");

    JoinedSelect select = qComment.toJoin().join(qPost).select(qComment, qPost);

    assertEquals(2, select.count());

    // WHEN - asc
    List<JoinedRow> list = select.findAll(qComment.orderBy(CommentRow::getComment).asc());

    // THEN - asc
    assertEquals(2, list.size());
    assertEquals("BBB", list.get(0).get(qComment).getComment());
    assertEquals("env4", list.get(0).get(qPost).getTitle());

    assertEquals("CCC", list.get(1).get(qComment).getComment());
    assertEquals("env4", list.get(1).get(qPost).getTitle());

    // WHEN - desc
    list = select.findAll(qComment.orderBy(CommentRow::getComment).desc());

    // THEN - desc
    assertEquals(2, list.size());
    assertEquals("CCC", list.get(0).get(qComment).getComment());
    assertEquals("env4", list.get(0).get(qPost).getTitle());

    assertEquals("BBB", list.get(1).get(qComment).getComment());
    assertEquals("env4", list.get(1).get(qPost).getTitle());
  }

  @Test
  public void expectEmptyResultsWhenInnerJoinedTableHasNoMatches() {
    // GIVEN
    Query<String, UserRow> qUser = userRowService.query().eq(UserRow::getName, "bba");
    assertEquals(1, qUser.count());

    // WHEN
    List<JoinedRow> results = qUser.toJoin().join(commentRowService.query()).selectAll().findAll();

    // THEN
    assertEquals(0, results.size());
  }

  @Test
  public void expectEmptyResultsWhenPrimaryTableFromInnerJoinHasNoMatches() {
    // GIVEN - test data (created before tests)
    // WHEN
    List<JoinedRow> results =
        userRowService
            .query()
            .eq(UserRow::getName, "doesnt-exist")
            .toJoin()
            .join(commentRowService.query())
            .selectAll()
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
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 5, 10);
    Query<Long, PostRow> qPost = postRowService.query();
    JoinedSelect select = qPost.toJoin().join(qUser, PostRow::getAuthorId).selectAll();
    assertEquals(3, select.count());

    // WHEN -- nulls first
    List<JoinedRow> results =
        select.findAll(
            qUser.orderBy(UserRow::getStatus).asc().nullsFirst(),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).get(qPost).getTitle());
    assertEquals("BBc", results.get(0).get(qUser).getName());

    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("bba", results.get(1).get(qUser).getName());

    assertEquals("env5", results.get(2).get(qPost).getTitle());
    assertEquals("bba", results.get(2).get(qUser).getName());

    // WHEN -- nulls last
    results =
        select.findAll(
            qUser.orderBy(UserRow::getStatus).asc().nullsLast(),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env4", results.get(0).get(qPost).getTitle());
    assertEquals("bba", results.get(0).get(qUser).getName());

    assertEquals("env5", results.get(1).get(qPost).getTitle());
    assertEquals("bba", results.get(1).get(qUser).getName());

    assertEquals("env3", results.get(2).get(qPost).getTitle());
    assertEquals("BBc", results.get(2).get(qUser).getName());
  }

  @Test
  public void expectCorrectOrderingWithCollation() {
    if (isPostgres()) {
      // NOTE: I couldn't make postgres behave as desired, and having that this feature is not
      // super
      // needed, I guess I'll skip it for now
      return;
    }

    // GIVEN
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 5, 10);
    Query<Long, PostRow> qPost = postRowService.query();
    JoinedSelect selectPosts = qUser.toJoin().joinBack(qPost, PostRow::getAuthorId).selectAll();
    assertEquals(3, selectPosts.count());

    // WHEN -- case sensitive
    List<JoinedRow> results =
        selectPosts.findAll(
            qUser.orderBy(UserRow::getName).asc().withCollate("latin1_general_cs"),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).get(qPost).getTitle());
    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("env5", results.get(2).get(qPost).getTitle());

    // WHEN -- case insensitive
    results =
        selectPosts.findAll(
            qUser.orderBy(UserRow::getName).asc().withCollate("latin1_general_ci"),
            qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env4", results.get(0).get(qPost).getTitle());
    assertEquals("env5", results.get(1).get(qPost).getTitle());
    assertEquals("env3", results.get(2).get(qPost).getTitle());
  }

  @Test
  void expectAliasedQueryWillBeAssimilatedOk() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query("ppp");

    // WHEN
    List<JoinedRow> results =
        qPost
            .toJoin()
            .join(userRowService.query(), PostRow::getAuthorId)
            .selectAll()
            .findAll(OrderBy.Asc("ppp.title"));

    // THEN
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).get(qPost).getTitle());
    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("env5", results.get(2).get(qPost).getTitle());
  }

  @Test
  void expectPaginationWorkCorrectly() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<Long, CommentRow> qComment = commentRowService.query().ne(CommentRow::getComment, "CCC");
    JoinedSelect select = qComment.toJoin().join(qPost).selectAll();

    // WHEN -- asc, page 1
    PagerParams pagerParams = new PagerParams(0, 2);
    PaginatedList<JoinedRow> results =
        select.find(pagerParams, qPost.orderBy(PostRow::getLikes).asc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(2, results.getItems().size());
    assertEquals("DDD", results.getItems().get(0).get(qComment).getComment());
    assertEquals("BBB", results.getItems().get(1).get(qComment).getComment());

    // also make sure findPage works the same
    List<JoinedRow> page = select.findPage(pagerParams, qPost.orderBy(PostRow::getLikes).asc());
    assertEquals(2, page.size());
    for (int i = 0; i < 2; i++) {
      assertEquals(
          page.get(i).get(qComment).getId(), results.getItems().get(i).get(qComment).getId());
    }

    // WHEN -- asc, page 2
    results = select.find(new PagerParams(2, 2), qPost.orderBy(PostRow::getLikes).asc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(1, results.getItems().size());
    assertEquals("AAA", results.getItems().get(0).get(qComment).getComment());

    // WHEN -- desc, page 1
    results = select.find(pagerParams, qPost.orderBy(PostRow::getLikes).desc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(2, results.getItems().size());
    assertEquals("AAA", results.getItems().get(0).get(qComment).getComment());
    assertEquals("BBB", results.getItems().get(1).get(qComment).getComment());

    // WHEN -- desc, page 2
    results = select.find(new PagerParams(2, 2), qPost.orderBy(PostRow::getLikes).desc());

    // THEN
    assertEquals(3, results.getTotalResults());
    assertEquals(1, results.getItems().size());
    assertEquals("DDD", results.getItems().get(0).get(qComment).getComment());
  }

  @Test
  void expectInnerJoinToReturnOnlyRecordsWhichHaveReferencesNotNull() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN
    List<JoinedRow> posts =
        qPost
            .toJoin()
            .join(userRowService.query(), PostRow::getPinnedBy)
            .selectAll()
            .findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals(2, posts.size());
    assertEquals("env4", posts.get(0).get(qPost).getTitle());
    assertEquals("env5", posts.get(1).get(qPost).getTitle());
  }

  @Test
  void expectFindAndGetOneWorkCorrectly() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN - expect null
    JoinedSelect select =
        qPost
            .toJoin()
            .join(userRowService.query().eq(UserRow::getKarma, 11), PostRow::getAuthorId)
            .selectAll();
    JoinedRow result = select.findOne();

    // THEN
    assertNull(result);

    // WHEN - expect exception
    assertThrows(EntityNotFoundException.class, select::getOne);

    // WHEN - find: expect result
    select =
        qPost
            .toJoin()
            .join(userRowService.query().eq(UserRow::getKarma, 10), PostRow::getAuthorId)
            .selectAll();
    result = select.findOne();

    // THEN
    assertNotNull(result);
    assertEquals("env3", result.get(qPost).getTitle());

    // WHEN - get: expect result
    result = select.getOne();

    // THEN
    assertNotNull(result);
    assertEquals("env3", result.get(qPost).getTitle());
  }

  @Test
  void expectFindFirstFindsResults() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 10, 15);
    JoinedSelect select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).selectAll();
    JoinedRow result = select.findFirst(qUser.orderBy(UserRow::getKarma).asc());

    // THEN
    assertNotNull(result);
    assertEquals("env5", result.get(qPost).getTitle());
    assertEquals("BBc", result.get(qUser).getName());
  }

  @Test
  void expectFindFirstFindsNothing() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 30, 45);
    JoinedSelect select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).selectAll();
    JoinedRow result = select.findFirst(qUser.orderBy(UserRow::getKarma).asc());

    // THEN
    assertNull(result);
  }

  @Test
  void expectGetFirstGetsResults() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 10, 15);
    JoinedSelect select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).selectAll();
    JoinedRow result = select.getFirst(qUser.orderBy(UserRow::getKarma).asc());

    // THEN
    assertNotNull(result);
    assertEquals("env5", result.get(qPost).getTitle());
  }

  @Test
  void expectGetFirstGetsNothing() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().between(UserRow::getKarma, 30, 45);
    JoinedSelect select = qPost.toJoin().join(qUser, PostRow::getPinnedBy).selectAll();

    // THEN
    assertThrows(
        EntityNotFoundException.class,
        () -> select.getFirst(qUser.orderBy(UserRow::getKarma).asc()));
  }

  @Test
  void expectGetAllWorks() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<String, UserRow> qUser = userRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    List<JoinedRow> results =
        qPost
            .toJoin()
            .join(qUser, PostRow::getAuthorId)
            .selectAll()
            .getAll(qUser.orderBy(UserRow::getKarma).asc(), qPost.orderBy(PostRow::getLikes).asc());

    // THEN
    assertEquals(3, results.size());
    assertEquals("env4", results.get(0).get(qPost).getTitle());
    assertEquals("bba", results.get(0).get(qUser).getName());

    assertEquals("env5", results.get(1).get(qPost).getTitle());
    assertEquals("bba", results.get(1).get(qUser).getName());

    assertEquals("env3", results.get(2).get(qPost).getTitle());
    assertEquals("BBc", results.get(2).get(qUser).getName());
  }

  @Test
  void expectJoin3TablesWorksOk() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query();

    JoinedSelect select =
        qComment
            .toJoin()
            .join(qPost, CommentRow::getPostId)
            .join(qPost, qUser, PostRow::getAuthorId)
            .selectAll();

    List<JoinedRow> results =
        select.findAll(
            qUser.orderBy(UserRow::getKarma).asc(),
            qPost.orderBy(PostRow::getTitle).asc(),
            qComment.orderBy(CommentRow::getComment).asc());

    assertEquals(4, results.size());
    assertEquals("BBB", results.get(0).get(qComment).getComment());
    assertEquals("env4", results.get(0).get(qPost).getTitle());
    assertEquals("bba", results.get(0).get(qUser).getName());

    assertEquals("CCC", results.get(1).get(qComment).getComment());
    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("bba", results.get(1).get(qUser).getName());

    assertEquals("AAA", results.get(2).get(qComment).getComment());
    assertEquals("env5", results.get(2).get(qPost).getTitle());
    assertEquals("bba", results.get(2).get(qUser).getName());

    assertEquals("DDD", results.get(3).get(qComment).getComment());
    assertEquals("env3", results.get(3).get(qPost).getTitle());
    assertEquals("BBc", results.get(3).get(qUser).getName());
  }

  @Test
  void expectParsedOrderByWorksAsExpected() {
    // GIVEN - test data (created before tests)
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

    List<JoinedRow> results = joinQuery.selectAll().findAll(orderBys1);

    assertEquals(4, results.size());
    assertEquals("BBB", results.get(0).get(qComment).getComment());
    assertEquals("CCC", results.get(1).get(qComment).getComment());
    assertEquals("AAA", results.get(2).get(qComment).getComment());
    assertEquals("DDD", results.get(3).get(qComment).getComment());
  }

  @Test
  void expectExceptionIfAttemptingToSelectQueryThatIsNotInTheJoin() {
    // GIVEN
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
    // WHEN / THEN
    try {
      commentRowService.query().toJoin().join(userRowService.query()).selectAll().findOne();
      fail("Exception expected");
    } catch (EasyCrudUnexpectedException e) {
      assertEquals(EasyCrudMessageCodes.UNEXPECTED_FAILED_TO_FIND, e.getMessageCode());
      assertEquals(commentRowService.getRowMessageCode(), e.getMessageArgs()[0]);
    }
  }

  @Test
  void expectLeftJoinReturnsAllLeftTableRecordsEvenWhenNoMatchesOnRight() {
    // GIVEN
    Query<String, UserRow> qUser = userRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();

    // WHEN
    JoinedSelect select = qPost.toJoin().leftJoin(qUser, PostRow::getPinnedBy).selectAll();
    assertEquals(3, select.count());

    List<JoinedRow> results = select.findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN - Should return all posts regardless of pinnedBy value
    assertEquals(3, results.size());
    assertEquals("env3", results.get(0).get(qPost).getTitle());
    assertNull(results.get(0).get(qUser));

    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("name3", results.get(1).get(qUser).getName());

    assertEquals("env5", results.get(2).get(qPost).getTitle());
    assertEquals("BBc", results.get(2).get(qUser).getName());
  }

  @Test
  void expectLeftJoinBackReturnsAllRightTableRecords() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query().ge(PostRow::getLikes, 4);
    Query<String, UserRow> qUser = userRowService.query();

    // WHEN
    JoinedSelect select = qUser.toJoin().leftJoinBack(qPost, PostRow::getPinnedBy).selectAll();
    assertEquals(3, select.count());

    List<JoinedRow> results = select.findAll(qUser.orderBy(UserRow::getKarma).asc());

    // THEN - Should return all users even though not all of them are referenced from pinnedBy
    assertEquals(3, results.size());
    assertEquals("bba", results.get(0).get(qUser).getName());
    assertEquals("BBc", results.get(1).get(qUser).getName());
    assertEquals("name3", results.get(2).get(qUser).getName());
  }

  @Test
  void expectComplexJoinCombinationWithMultipleLeftJoins() {
    // GIVEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qPinnedBy = userRowService.query();
    Query<Long, CommentRow> qComment = commentRowService.query();

    // WHEN
    JoinedSelect select =
        qComment.toJoin().join(qPost).leftJoin(qPost, qPinnedBy, PostRow::getPinnedBy).selectAll();
    assertEquals(4, select.count());

    List<JoinedRow> results = select.findAll(qComment.orderBy(CommentRow::getComment).asc());

    // THEN - Should return all comments and all posts even though not all posts have pinnedBy
    // value
    assertEquals(4, results.size());
    assertEquals("AAA", results.get(0).get(qComment).getComment());
    assertEquals("env5", results.get(0).get(qPost).getTitle());
    assertEquals("BBc", results.get(0).get(qPinnedBy).getName());

    assertEquals("BBB", results.get(1).get(qComment).getComment());
    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("name3", results.get(1).get(qPinnedBy).getName());

    assertEquals("CCC", results.get(2).get(qComment).getComment());
    assertEquals("env4", results.get(2).get(qPost).getTitle());
    assertEquals("name3", results.get(2).get(qPinnedBy).getName());

    assertEquals("DDD", results.get(3).get(qComment).getComment());
    assertEquals("env3", results.get(3).get(qPost).getTitle());
    assertNull(results.get(3).get(qPinnedBy));
  }

  @Test
  void expectLeftJoinBetweenNonPrimaryTablesWorks() {
    // GIVEN
    Query<Long, CommentRow> qComment = commentRowService.query();
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query();

    // Join comment -> post (INNER) and then post -> user (LEFT JOIN)
    JoinedSelect select =
        qComment
            .toJoin()
            // INNER JOIN - comment must have post
            .join(qPost, CommentRow::getPostId)
            // LEFT JOIN - post may not have author (though all do in our
            // data)
            .leftJoin(qPost, qUser, PostRow::getAuthorId)
            .selectAll();

    // WHEN
    List<JoinedRow> results = select.findAll(qComment.orderBy(CommentRow::getComment).asc());

    // THEN - All 4 comments should be returned since they all have posts
    assertEquals(4, results.size());
  }

  @Test
  void expectJoinedSelectSmokeTestWorks() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query();
    List<JoinedRow> results =
        qPost
            .toJoin()
            .join(qUser, PostRow::getAuthorId)
            .selectAll()
            .findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals("env3", results.get(0).get(qPost).getTitle());
    assertEquals("BBc", results.get(0).get(qUser).getName());

    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("bba", results.get(1).get(qUser).getName());

    assertEquals("env5", results.get(2).get(qPost).getTitle());
    assertEquals("bba", results.get(2).get(qUser).getName());

    // THEN - same row is not mapped twice, but rather reused
    assertSame(results.get(1).get(qUser), results.get(2).get(qUser));
  }

  @Test
  void expectNullsReturnedGracefullyOnLeftJoins() {
    // GIVEN - test data (created before tests)
    // WHEN
    Query<Long, PostRow> qPost = postRowService.query();
    Query<String, UserRow> qUser = userRowService.query().ge(UserRow::getKarma, 10);
    List<JoinedRow> results =
        qPost
            .toJoin()
            .leftJoin(qUser, PostRow::getPinnedBy)
            .selectAll()
            .findAll(qPost.orderBy(PostRow::getTitle).asc());

    // THEN
    assertEquals("env3", results.get(0).get(qPost).getTitle());
    assertNull(results.get(0).get(qUser));

    assertEquals("env4", results.get(1).get(qPost).getTitle());
    assertEquals("name3", results.get(1).get(qUser).getName());

    assertEquals("env5", results.get(2).get(qPost).getTitle());
    assertEquals("BBc", results.get(2).get(qUser).getName());
  }
}
