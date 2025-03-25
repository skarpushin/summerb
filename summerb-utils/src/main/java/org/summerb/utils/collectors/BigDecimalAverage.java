package org.summerb.utils.collectors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class BigDecimalAverage {

  static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

  public static <I> Collector<I, ?, BigDecimal> collect(Function<I, BigDecimal> mapper, int scale) {

    return new DoublesAverageImpl<>(mapper, scale, CH_NOID);
  }

  static class DoublesAverageState {
    int count;
    BigDecimal sum = new BigDecimal(0);
  }

  static class DoublesAverageImpl<I> implements Collector<I, DoublesAverageState, BigDecimal> {
    private Function<I, BigDecimal> mapper;
    private int scale;
    private Set<Characteristics> chNoid;

    public DoublesAverageImpl(
        Function<I, BigDecimal> mapper, int scale, Set<Characteristics> chNoid) {
      this.mapper = mapper;
      this.scale = scale;
      this.chNoid = chNoid;
    }

    @Override
    public Supplier<DoublesAverageState> supplier() {
      return DoublesAverageState::new;
    }

    @Override
    public BiConsumer<DoublesAverageState, I> accumulator() {
      return (state, newSource) -> {
        BigDecimal newValue = mapper.apply(newSource);
        if (newValue == null) {
          return;
        }

        state.sum = state.sum.add(newValue);
        state.count++;
      };
    }

    @Override
    public BinaryOperator<DoublesAverageState> combiner() {
      return (a, b) -> {
        a.sum = a.sum.add(b.sum);
        a.count += b.count;
        return a;
      };
    }

    @Override
    public Function<DoublesAverageState, BigDecimal> finisher() {
      return (state) ->
          state.count == 0
              ? null
              : state.sum.divide(BigDecimal.valueOf(state.count), scale, RoundingMode.HALF_UP);
    }

    @Override
    public Set<Characteristics> characteristics() {
      return chNoid;
    }
  }
}
