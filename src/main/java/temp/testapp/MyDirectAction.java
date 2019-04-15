package temp.testapp;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

public class MyDirectAction extends WODirectAction {

	public MyDirectAction( WORequest request ) {
		super( request );
	}

	@Override
	public WOActionResults defaultAction() {
		WOResponse r = new WOResponse();
		r.setContent( "What an awesome application" );
		return r;
	}
}