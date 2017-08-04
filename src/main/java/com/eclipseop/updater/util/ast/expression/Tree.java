package com.eclipseop.updater.util.ast.expression;

/**
 * Created by Eclipseop.
 * Date: 8/2/2017.
 */
public interface Tree {

	Expression getLeft();

	Expression getRight();

	default boolean isExpectedExpressions(Class first, Class second) {
		if (getLeft().getClass().equals(first)) {
			if (getRight().getClass().equals(second)) {
				return true;
			}
		}

		if (getLeft().getClass().equals(second)) {
			if (getRight().getClass().equals(first)) {
				return true;
			}
		}

		return false;
	}

	default boolean containsExpression(Class expression) {
		return getLeft().getClass().equals(expression) || getRight().getClass().equals(expression);
	}

	default <T extends Expression> T find(Class<T> find) {
		if (getLeft().getClass().equals(find)) {
			return (T) getLeft();
		} else {
			return (T) getRight();
		}
	}
}
