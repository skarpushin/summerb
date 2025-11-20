package org.summerb.easycrud.sql_builder.postgres;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import integr.org.summerb.easycrud.dtos.CommentRow;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.summerb.easycrud.dao.EasyCrudDao;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.JoinQueryFactory;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.ReferringToFieldsFinder;
import org.summerb.easycrud.join_query.SelectFactory;
import org.summerb.easycrud.join_query.impl.JoinQueryFactoryImpl;
import org.summerb.easycrud.join_query.impl.ReferringToFieldsFinderCachingImpl;
import org.summerb.easycrud.join_query.impl.ReferringToFieldsFinderImpl;
import org.summerb.easycrud.join_query.impl.SelectFactoryImpl;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.impl.FieldsEnlisterCachingImpl;
import org.summerb.easycrud.sql_builder.impl.FieldsEnlisterImpl;
import org.summerb.easycrud.sql_builder.model.FromAndWhere;
import org.summerb.easycrud.sql_builder.model.QueryData;
import org.summerb.easycrud.sql_builder.mysql.OrderByToSqlMySqlImpl;
import org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;
import org.summerb.utils.easycrud.api.dto.PagerParams;

@ExtendWith(MockitoExtension.class)
public class SqlBuilderPostgresImplTest {

  private final PropertyNameResolverFactoryImpl propertyNameResolverFactory =
      new PropertyNameResolverFactoryImpl(new MethodCapturerProxyClassFactoryImpl());

  private QuerySpecificsResolver querySpecificsResolver;

  private ReferringToFieldsFinder referringToFieldsFinder;
  private SelectFactory selectFactory;

  private SqlBuilderPostgresImpl sqlBuilder;

  private UserServiceTestImpl userService = new UserServiceTestImpl();
  private PostServiceTestImpl postService = new PostServiceTestImpl();
  private CommentServiceTestImpl commentService = new CommentServiceTestImpl();

  @BeforeEach
  void setUp() {
    referringToFieldsFinder =
        new ReferringToFieldsFinderCachingImpl(new ReferringToFieldsFinderImpl());

    querySpecificsResolver = mock(QuerySpecificsResolver.class);

    FieldsEnlisterCachingImpl fieldsEnlister =
        new FieldsEnlisterCachingImpl(new FieldsEnlisterImpl());
    sqlBuilder =
        new SqlBuilderPostgresImpl(
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
        new JoinQueryFactoryImpl(
            referringToFieldsFinder, selectFactory, querySpecificsResolver, fieldsEnlister);

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
  void appendFieldConditionsToWhereClause_expectConditionsAddedCorrectly() {
    // Given
    Query<String, UserRow> qUser = userService.query();
    Query<Long, PostRow> qPost = postService.query();
    JoinQuery<Long, PostRow> join = qPost.toJoin().join(qUser, PostRow::getAuthorId);

    // When
    FromAndWhere fromAndWhere = sqlBuilder.fromAndWhere(join);
    OrderBy[] orderBy = {qPost.orderBy(PostRow::getId).asc()};
    QueryData queryData =
        sqlBuilder.joinedSingleTableMultipleRows(
            join, qPost, PagerParams.ALL, orderBy, fromAndWhere);

    // Then
    String expected =
        "SELECT posts.*\n"
            + "FROM\n"
            + "\tposts\n"
            + "\tJOIN users ON posts.author_id = users.id\n"
            + "ORDER BY posts.id";
    assertEquals(expected, queryData.getSql());
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
      setPropertyNameResolverFactory(SqlBuilderPostgresImplTest.this.propertyNameResolverFactory);
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
