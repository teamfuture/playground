package com.webobjects.appserver;

import java.util.Map;

public class NGRequest extends NGMessage {

	private String _method;
	private String _uri;

	protected NGRequest() {}

	/**
	 * Check out NSData
	 */
	public NGRequest( String method, String url, String httpVersion, Map headers, byte[] content, Map userInfo ) {
		setMethod( method );
		_uri = url;
		setHTTPVersion( httpVersion );
		setHeaders( headers );
		setContent( content );
		setUserInfo( userInfo );
	}

	public void setMethod( String value ) {
		_method = value;
	}

	public String method() {
		return _method;
	}

	@Override
	public String toString() {
		return "[WORequest: uri: " + uri() + "]";
	}

	public String uri() {
		return _uri;
	}
}