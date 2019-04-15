package com.webobjects.appserver;

import java.util.Objects;

public abstract class WOAction {

	private WORequest _request;

	public WOAction( WORequest request ) {
		_request = request;
	}

	public abstract String getSessionIDForRequest( WORequest request );

	public abstract WOActionResults performActionNamed( String name );

	public WORequest request() {
		return _request;
	}

	@Override
	public String toString() {
		return Objects.toString( this );
	}
}