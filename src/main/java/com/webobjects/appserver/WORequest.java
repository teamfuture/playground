package com.webobjects.appserver;

import java.util.Map;
import java.util.Objects;

public class WORequest extends WOMessage {

	private String _method;
	private String _uri;

	protected WORequest() {}

	/**
	 * Check out NSData
	 */
	public WORequest( String method, String url, String httpVersion, Map headers, byte[] content, Map userInfo ) {
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
		return Objects.toString( this );
	}

	public String uri() {
		return _uri;
	}
}