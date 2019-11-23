package tng.testapp;

import tng.appserver.NGApplication;
import tng.appserver.NGBundle;

public class MyApplication extends NGApplication {

	public static void main( String[] args ) {
		NGApplication.main( args, MyApplication.class );
	}

	public MyApplication() {
		NGBundle.registerSimpleName( "MyDirectAction", MyDirectAction.class );
	}
}