package com.webobjects.appserver;

import java.util.Objects;

public abstract class NGAction {

	private NGRequest _request;

	public NGAction( NGRequest request ) {
		Objects.requireNonNull( request );

		_request = request;
	}

	public abstract String getSessionIDForRequest( NGRequest request );

	public abstract NGActionResults performActionNamed( String name );

	public NGRequest request() {
		return _request;
	}

	@Override
	public String toString() {
		return Objects.toString( this );
	}
}