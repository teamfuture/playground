package tng.kvc;

import tng.adaptor.NGNotImplementedException;

public interface NGKeyValueCoding {

	public Object valueForKey( String key );

	public void takeValueForKey( Object value, String key );

	public static class Utility {

		public static Object valueForKey( Object object, String key ) {

			if( object instanceof NGKeyValueCoding ) {
				return ((NGKeyValueCoding)object).valueForKey( key );
			}

			throw new NGNotImplementedException();

			// method()
			// _method()
			// getMethod()
			// _getMethod()
			// variable
			// _variable
		}

		public static void takeValueForKey( Object object, Object value, String key ) {

		}
	}
}