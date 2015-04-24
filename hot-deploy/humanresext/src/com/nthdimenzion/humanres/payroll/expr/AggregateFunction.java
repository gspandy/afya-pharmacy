package com.nthdimenzion.humanres.payroll.expr;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;

public abstract class AggregateFunction {

	public static final NumberFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#.0#################");

	private final static MinFunction MIN_FUNC = new MinFunction();
	private final static MaxFunction MAX_FUNC = new MaxFunction();
	private final static SumFunction SUM_FUNC = new SumFunction();

	private final static Map<String, AggregateFunction> registry = FastMap.newInstance();

	private final static String module = "AggregateFunction";
	static {
		register("MAX", MAX_FUNC);
		register("MIN", MIN_FUNC);
		register("SUM", SUM_FUNC);
	}

	public static void register(String key, AggregateFunction func) {
		registry.put(key, func);
	}

	public static Number evaluate(String aggregateFunc, Number... numbers) {
		Debug.logInfo(" EVAL " + aggregateFunc + " FROM " + numbers, module);
		return registry.get(aggregateFunc).evaluate(numbers);
	}

	public abstract Number evaluate(Number... numbers);

	private static void sortNumbers(List<Number> numberToSort) {

		Collections.sort(numberToSort, new Comparator<Number>() {

			public int compare(Number arg0, Number arg1) {
				String s = DEFAULT_DECIMAL_FORMAT.format(arg0.doubleValue());
				BigDecimal num1 = new BigDecimal(s);
				s = DEFAULT_DECIMAL_FORMAT.format(arg1.doubleValue());
				BigDecimal num2 = new BigDecimal(s);
				return num1.compareTo(num2);
			}
		});
	}

	static class MaxFunction extends AggregateFunction {

		@Override
		public Number evaluate(Number... numbers) {
			List<Number> numberToSort = Arrays.asList(numbers);
			sortNumbers(numberToSort);
			Number res = numberToSort.get(numbers.length - 1);
			return res;
		}
	}

	static class MinFunction extends AggregateFunction {
		@Override
		public Number evaluate(Number... numbers) {
			List<Number> numberToSort = Arrays.asList(numbers);
			sortNumbers(numberToSort);
			Number res = numberToSort.get(0);
			Debug.logInfo(" EVAL RETURNING " + res, module);
			return res;
		}
	}

	static class SumFunction extends AggregateFunction {
		@Override
		public Number evaluate(Number... numbers) {
			BigDecimal total = BigDecimal.ZERO;
			for (Number num : numbers) {
				String s = DEFAULT_DECIMAL_FORMAT.format(num.doubleValue());
				BigDecimal num1 = new BigDecimal(s);
				total = total.add(num1);
			}
			return total;
		}
	}
}