package com.nurkiewicz.java8;

import com.nurkiewicz.java8.atomic.RangeCollector;
import org.junit.Ignore;
import org.junit.Test;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * *Adder
 * *Accumulator
 * Atomic* improvements
 * - Decide which atomic.* class best suits given requirements
 */
@Ignore
public class J11b_AtomicTest {

	@Test
	public void shouldHaveExtremeRangesInTheBeginning() throws Exception {
		//given
		final RangeCollector range = new RangeCollector();

		//when
		final double min = range.getMin();
		final double max = range.getMax();

		//then
		assertThat(min).isEqualTo(Double.MAX_VALUE);
		assertThat(max).isEqualTo(Double.MIN_VALUE);
	}

	@Test
	public void shouldCountSingleValueAsBothMinAndMax() throws Exception {
		//given
		final RangeCollector range = new RangeCollector();
		final int someValue = 42;

		//when
		range.save(someValue);

		//then
		assertThat(range.getMin()).isCloseTo(someValue, offset(0.01));
		assertThat(range.getMax()).isCloseTo(someValue, offset(0.01));
	}

	@Test
	public void shouldRememberMinAndMaxOfMultipleValues() throws Exception {
		//given
		final RangeCollector range = new RangeCollector();

		//when
		range.save(3);
		range.save(1);
		range.save(5);
		range.save(-1);
		range.save(2);

		//then
		assertThat(range.getMin()).isEqualTo(-1, offset(0.1));
		assertThat(range.getMax()).isEqualTo(5, offset(0.1));
	}

	@Test
	public void shouldRememberMinAndMaxInMultipleThreads() throws Exception {
		//given
		final RangeCollector range = new RangeCollector();

		//when
		MultiRunner.runMultiThreaded(1000, () -> range.save(randomDigit()));

		//then
		await().until(() -> assertThat(range.getMin()).isCloseTo(0, offset(0.01)));
		await().until(() -> assertThat(range.getMax()).isCloseTo(9, offset(0.01)));
	}

	private int randomDigit() {
		return (int) (Math.random() * 10);
	}

}
