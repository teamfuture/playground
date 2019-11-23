package tng.testapp;

import tng.appserver.NGActionResults;
import tng.appserver.NGDirectAction;
import tng.appserver.NGRequest;
import tng.appserver.NGResponse;

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