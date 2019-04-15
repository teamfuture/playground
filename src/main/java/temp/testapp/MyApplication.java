package temp.testapp;

import com.webobjects.appserver.NGApplication;

public class MyApplication extends NGApplication {

	public static void main( String[] args ) {
		NGApplication.main( args, MyApplication.class );
	}
}