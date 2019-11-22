package temp.testapp;

import com.webobjects.appserver.NGActionResults;
import com.webobjects.appserver.NGDirectAction;
import com.webobjects.appserver.NGRequest;
import com.webobjects.appserver.NGResponse;

public class MyDirectAction extends NGDirectAction {

	public MyDirectAction( NGRequest request ) {
		super( request );
	}

	@Override
	public NGActionResults defaultAction() {
		NGResponse r = new NGResponse();
		r.setContent( "What an awesome application" );
		return r;
	}

	public NGActionResults testingAction() {
		NGResponse r = new NGResponse();
		r.setContent( "This is another direct action" );
		return r;
	}
}