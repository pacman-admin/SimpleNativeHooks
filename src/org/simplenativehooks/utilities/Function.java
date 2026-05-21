package org.simplenativehooks.utilities;

public abstract class Function<D, R> {
	public abstract R apply(D d);

	public static <E> Function<E, Boolean> trueFunction() {
		return new Function<E, Boolean>() {
			@Override
			public Boolean apply(E e) {
				return true;
			}
		};
	}

}
