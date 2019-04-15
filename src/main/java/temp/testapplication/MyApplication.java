package temp.testapplication;

import com.webobjects.appserver.WOApplication;

public class MyApplication extends WOApplication {

	public static void main( String[] args ) {
		WOApplication.main( args, MyApplication.class );
	}
}