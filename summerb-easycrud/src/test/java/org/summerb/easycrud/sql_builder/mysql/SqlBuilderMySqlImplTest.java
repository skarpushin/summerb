package org.summerb.easycrud.sql_builder.mysql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import integr.org.summerb.easycrud.dtos.CommentRow;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.dao.EasyCrudDao;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinQueryFactory;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.SelectFactory;
import org.summerb.easycrud.join_query.impl.JoinQueryFactoryImpl;
import org.summerb.easycrud.join_query.impl.SelectFactoryImpl;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.impl.FieldsEnlisterCachingImpl;
import org.summerb.easycrud.sql_builder.impl.FieldsEnlisterImpl;
import org.summerb.easycrud.sql_builder.impl.ParamIdxIncrementer;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;

@ExtendWith(MockitoExtension.class)
public class SqlBuilderMySqlImplTest {

  private final PropertyNameResolverFactoryImpl propertyNameResolverFactory =
      new PropertyNameResolverFactoryImpl(new MethodCapturerProxyClassFactoryImpl());

  private QuerySpecificsResolver querySpecificsResolver;

  private SelectFactory selectFactory;

  private SqlBuilder sqlBuilder;

  private UserServiceTestImpl userService = new UserServiceTestImpl();
  private PostServiceTestImpl postService = new PostServiceTestImpl();
  private CommentServiceTestImpl commentService = new CommentServiceTestImpl();

  @BeforeEach
  void setUp() {
    querySpecificsResolver = mock(QuerySpecificsResolver.class);

    FieldsEnlisterCachingImpl fieldsEnlister =
        new FieldsEnlisterCachingImpl(new FieldsEnlisterImpl());
    sqlBuilder =
        new SqlBuilderMySqlImpl(
            querySpecificsResolver,
            fieldsEnlister,
            new QueryToSqlMySqlImpl(),
            new OrderByToSqlMySqlImpl());

    selectFactory =
        new SelectFactoryImpl(
            querySpecificsResolver,
            sqlBuilder,
            mock(NamedParameterJdbcTemplateEx.class),
            fieldsEnlister);

    JoinQueryFactory joinQueryFactory =
        new JoinQueryFactoryImpl(selectFactory, querySpecificsResolver, fieldsEnlister);

    userService.setJoinQueryFactory(joinQueryFactory);
    postService.setJoinQueryFactory(joinQueryFactory);
    commentService.setJoinQueryFactory(joinQueryFactory);
  }

  private <TId, TRow extends HasId<TId>> Query<TId, TRow> createQuery(
      String tableName, Class<TRow> rowClass, EasyCrudServiceTestImpl<TId, TRow> service) {

    Query<TId, TRow> ret = new Query<>(service);
    lenient().when(querySpecificsResolver.getTableName(ret)).thenReturn(tableName);
    lenient().when(querySpecificsResolver.getRowClass(ret)).thenReturn(rowClass);
    return ret;
  }

  @Test
  void appendFromClause_shouldGenerateCorrectFromClauseForSingleTable() {
    // Given
    JoinQuery<String, UserRow> joinQuery = userService.query().toJoin();

    // When
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    assertEquals("\n\tusers", sql.toString());
  }

  @Test
  void appendFromClause_shouldUseCustomAliasesWhenSpecified() {
    // Given
    JoinQuery<String, UserRow> joinQuery = userService.query("uuu").toJoin();
    joinQuery.joinBack(postService.query("ppp"), PostRow::getAuthorId);

    // When
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    assertEquals("\n\tusers AS uuu\n\tJOIN posts AS ppp ON ppp.author_id = uuu.id", sql.toString());
  }

  @Test
  void appendFromClause_shouldUseProperAliasesWhenSameTableReferencedTwice() {
    // GIVEN
    Query<String, UserRow> u1 = userService.query();
    Query<String, UserRow> u2 = userService.query();
    JoinQuery<String, UserRow> joinQuery = u1.toJoin().join(u2, UserRow::getCreatedBy);

    // WHEN
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    assertEquals(
        "\n\tusers AS users0\n\tJOIN users AS users1 ON users0.created_by = users1.id",
        sql.toString());
  }

  @Test
  void appendFromClause_shouldGenerateCorrectFromClauseForJoinWithRefererFirst() {
    // Given
    JoinQuery<String, UserRow> joinQuery = userService.query().toJoin();
    joinQuery.joinBack(postService.query(), PostRow::getAuthorId);

    // When
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    assertEquals("\n\tusers\n\tJOIN posts ON posts.author_id = users.id", sql.toString());
  }

  @Test
  void appendFromClause_shouldGenerateCorrectFromClauseForJoinWithRefererFirstAndAnnotation() {
    // Given
    JoinQuery<String, UserRow> joinQuery = userService.query().toJoin();
    joinQuery.joinBack(postService.query(), PostRow::getAuthorId);

    // When
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    assertEquals("\n\tusers\n\tJOIN posts ON posts.author_id = users.id", sql.toString());
  }

  @Test
  void appendFromClause_shouldGenerateCorrectFromClauseForJoinWithReferredFirst() {
    // Given
    JoinQuery<String, UserRow> joinQuery = userService.query().toJoin();
    // Create a scenario where the referred table needs to be joined
    joinQuery.joinBack(postService.query(), PostRow::getAuthorId); // post refers to user

    // When
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    assertEquals("\n\tusers" + "\n\tJOIN posts ON posts.author_id = users.id", sql.toString());
  }

  @Test
  void appendFromClause_shouldGenerateCorrectFromClauseForMultipleJoins() {
    // Given
    JoinQuery<String, UserRow> joinQuery = userService.query().toJoin();
    Query<Long, PostRow> postQuery = postService.query();
    joinQuery.joinBack(postQuery, PostRow::getAuthorId);
    joinQuery.joinBack(postQuery, commentService.query(), CommentRow::getPostId);

    // When
    StringBuilder sql = new StringBuilder();
    sqlBuilder.appendFromClause(
        joinQuery, sql, new MapSqlParameterSource(), new ParamIdxIncrementer());

    // Then
    String expected =
        "\n\tusers"
            + "\n\tJOIN posts ON posts.author_id = users.id"
            + "\n\tJOIN comments ON comments.post_id = posts.id";
    assertEquals(expected, sql.toString());
  }

  @Test
  void appendFromClause_shouldThrowExceptionWhenBothRefererAndReferredAreAlreadySeen() {
    // Given
    Query<String, UserRow> userQuery = userService.query();
    JoinQuery<String, UserRow> joinQuery = userQuery.toJoin();
    // Create a circular join scenario that would cause both to be seen
    joinQuery.joinBack(postService.query(), PostRow::getAuthorId);

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> joinQuery.join(userQuery, UserRow::getId));
  }

  @Test
  void appendFieldConditionsToWhereClause_expectConditionsAddedCorrectly() {
    // Given
    Query<String, UserRow> userQuery =
        userService
            .query()
            .or(
                userService.query().greater(UserRow::getId, "AAA"),
                userService.query().eq(UserRow::getName, "author name"));

    Query<Long, PostRow> postQuery = postService.query().contains(PostRow::getTitle, "post title");
    Query<Long, CommentRow> commentQuery =
        commentService.query().eq(CommentRow::getAuthorId, "1239123");

    JoinQuery<String, UserRow> joinQuery =
        userQuery
            .toJoin()
            .joinBack(postQuery, PostRow::getAuthorId)
            .joinBack(postQuery, commentQuery, CommentRow::getPostId);

    // When
    StringBuilder sql = new StringBuilder();
    MapSqlParameterSource params = new MapSqlParameterSource();
    sqlBuilder.appendFieldConditionsToWhereClause(
        joinQuery.getJoinedQueries(), sql, params, new ParamIdxIncrementer());

    // Then
    String expected =
        "\n\t((users.id > :arg0) OR (users.name = :arg1))"
            + "\n\tAND posts.title LIKE :arg2"
            + "\n\tAND comments.author_id = :arg3";
    assertEquals(expected, sql.toString());
  }

  class UserServiceTestImpl extends EasyCrudServiceTestImpl<String, UserRow> {
    public UserServiceTestImpl() {
      super("users", UserRow.class);
    }
  }

  class PostServiceTestImpl extends EasyCrudServiceTestImpl<Long, PostRow> {
    public PostServiceTestImpl() {
      super("posts", PostRow.class);
    }
  }

  class CommentServiceTestImpl extends EasyCrudServiceTestImpl<Long, CommentRow> {
    public CommentServiceTestImpl() {
      super("comments", CommentRow.class);
    }
  }

  abstract class EasyCrudServiceTestImpl<TId, TRow extends HasId<TId>>
      extends EasyCrudServiceImpl<TId, TRow, EasyCrudDao<TId, TRow>> {

    private final String tableName;

    public EasyCrudServiceTestImpl(String tableName, Class<TRow> rowClass) {
      super(rowClass);
      this.tableName = tableName;
      setPropertyNameResolverFactory(SqlBuilderMySqlImplTest.this.propertyNameResolverFactory);
    }

    @Override
    public Query<TId, TRow> query() {
      return createQuery(tableName, getRowClass(), this);
    }

    @Override
    public Query<TId, TRow> query(String alias) {
      Query<TId, TRow> ret = createQuery(tableName, getRowClass(), this);
      ret.setAlias(alias);
      return ret;
    }

    public String getRowMessageCode() {
      return getRowClass().getSimpleName();
    }
  }
}
