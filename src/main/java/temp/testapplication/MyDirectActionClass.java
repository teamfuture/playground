package temp.testapplication;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

public class MyDirectActionClass extends WODirectAction {

	public MyDirectActionClass( WORequest request ) {
		super( request );
	}

	@Override
	public WOActionResults defaultAction() {
		WOResponse r = new WOResponse();
		r.setContent( "Testing" );
		return r;
	}
}