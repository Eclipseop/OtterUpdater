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

	default Expression find(Class find) {
		if (getLeft().getClass().equals(find)) {
			return getLeft();
		} else {
			return getRight();
		}
	}
}
