package org.summerb.easycrud.join_query.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.join_query.JoinQuery;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.model.JoinedRow;
import org.summerb.easycrud.query.Query;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.wireTaps.EasyCrudWireTap;
import org.summerb.utils.easycrud.api.dto.PagerParams;

@SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
@ExtendWith(MockitoExtension.class)
class JoinedSelectImplTest {
  @Test
  void find_shouldInvokeWireTaps_forRowsRetrievedByJoinedQuery() {
    // Arrange: Mocks and test doubles
    NamedParameterJdbcTemplateEx jdbc = mock(NamedParameterJdbcTemplateEx.class);
    QuerySpecificsResolver querySpecificsResolver = mock(QuerySpecificsResolver.class);
    SqlBuilder sqlBuilder = mock(SqlBuilder.class);
    JoinQuery<?, ?> joinQuery = mock(JoinQuery.class);

    // Two selected queries in the joined select
    Query<Long, TestRow> q1 = mock(Query.class);
    Query<Long, TestRow> q2 = mock(Query.class);
    List<Query<?, ?>> entitiesToSelect = List.of(q1, q2);

    // Wire taps for each query
    EasyCrudWireTap<TestRow> tap1 = mock(EasyCrudWireTap.class);
    EasyCrudWireTap<TestRow> tap2 = mock(EasyCrudWireTap.class);

    when(querySpecificsResolver.getWireTap(q1)).thenReturn(tap1);
    when(querySpecificsResolver.getWireTap(q2)).thenReturn(tap2);

    // Both require onRead hooks
    when(tap1.requiresOnRead()).thenReturn(true);
    when(tap2.requiresOnRead()).thenReturn(true);
    // "Multiple" hook only for tap1
    when(tap1.requiresOnReadMultiple()).thenReturn(true);
    when(tap2.requiresOnReadMultiple()).thenReturn(true); // but we'll give it no rows below

    // Prepare a JoinedSelectImpl spy that bypasses JDBC and returns a tailored extractor
    JoinedSelectImpl target =
        spy(
            new JoinedSelectImpl(
                joinQuery, entitiesToSelect, jdbc, querySpecificsResolver, sqlBuilder));

    // Build a fake extractor that reports mapped rows per query
    Map<Object, HasId<?>> mappedQ1 = new HashMap<>();
    mappedQ1.put(1L, new TestRow(1L));
    mappedQ1.put(2L, new TestRow(2L));
    Map<Object, HasId<?>> mappedQ2 =
        new HashMap<>(); // empty -> afterRead should NOT be called for tap2

    FakeExtractor extractor = new FakeExtractor(Map.of(q1, mappedQ1, q2, mappedQ2));

    // doQuery(...) should return our extractor
    doReturn(extractor).when(target).doQuery(any(PagerParams.class), any());

    // Act: execute find
    List<JoinedRow> items = target.find(PagerParams.ALL).getItems();

    // Assert: beforeRead invoked for both wire taps that requireOnRead
    verify(tap1, times(1)).beforeRead();
    verify(tap2, times(1)).beforeRead();

    // Assert: afterRead(list) invoked for tap1 exactly once with the mapped rows for q1
    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    verify(tap1, times(1)).afterRead(captor.capture());
    List received = captor.getValue();
    // Should contain exactly the values we placed for q1 (order not important)
    assertEquals(2, received.size());

    // Assert: tap2.afterRead was NOT called due to no mapped rows for q2
    verify(tap2, never()).afterRead(any(List.class));

    // And ensure that find returned the same list we pre-filled in extractor
    assertEquals(extractor.rows, items);
  }

  // --- Test helpers
  static class TestRow implements HasId<Long> {
    Long id;

    TestRow(Long id) {
      this.id = id;
    }

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public void setId(Long id) {
      this.id = id;
    }
  }

  /**
   * A minimal extractor that mimics what JDBC would prepare inside JoinedSelectImpl#doQuery. It
   * only needs to provide getMappedRows(query) and a non-null rows list for the outer result.
   */
  static class FakeExtractor extends ResultSetExtractorJoinedQueryImpl {
    private final Map<Query<?, ?>, Map<?, ? extends HasId<?>>> data;

    FakeExtractor(Map<Query<?, ?>, Map<?, ? extends HasId<?>>> data) {
      super(new ArrayList<>(), new ArrayList<>(), mock(QuerySpecificsResolver.class));
      this.data = data;
      // The select method will wrap this field into PaginatedList; it's fine to be any list
      this.rows = new ArrayList<>();
      this.rows.add(new JoinedRowImpl());
      this.totalResultsCount = this.rows.size();
    }

    @Override
    public Map<?, ? extends HasId<?>> getMappedRows(Query<?, ?> query) {
      return data.getOrDefault(query, Map.of());
    }
  }
}
