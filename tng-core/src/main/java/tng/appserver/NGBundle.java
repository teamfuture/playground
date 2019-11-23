package tng.appserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NGBundle {

	/**
	 * FIXME: This is put here as a simple replacement until we have full resource lookup
	 */
	private static final Map<String, Class<?>> _directActionClassesForSimpleNames = new HashMap<>();

	public static void registerSimpleName( String simpleName, Class<?> clazz ) {
		Objects.requireNonNull( simpleName );
		Objects.requireNonNull( clazz );

		_directActionClassesForSimpleNames.put( simpleName, clazz );
	}

	public static Class<?> classForSimpleName( String simpleName ) {
		Objects.requireNonNull( simpleName );

		return _directActionClassesForSimpleNames.get( simpleName );
	}
}