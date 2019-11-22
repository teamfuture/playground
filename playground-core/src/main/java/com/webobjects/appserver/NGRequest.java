package com.webobjects.appserver;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NGRequest extends NGMessage {

	private String _method;
	private String _uri;
	private Map<String, List<String>> _formValues; // FIXME: Init here?

	/**
	 * Check out NSData
	 */
	public NGRequest( String method, String url, String httpVersion, Map<String, List<String>> headers, byte[] content, Map<String, Object> userInfo ) {
		Objects.requireNonNull( url );
		Objects.requireNonNull( httpVersion );
		Objects.requireNonNull( headers );
		Objects.requireNonNull( content );
		Objects.requireNonNull( userInfo );

		setMethod( method );
		_uri = url;
		setHTTPVersion( httpVersion );
		setHeaders( headers );
		setContent( content );
		setUserInfo( userInfo );
	}

	public Map<String, List<String>> formValues() {
		return _formValues;
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