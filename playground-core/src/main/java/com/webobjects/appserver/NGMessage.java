package com.webobjects.appserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NGMessage {

	private Map<String, List<String>> _headers = new HashMap<String, List<String>>();
	private String _httpVersion; // FIXME: An Enum ight be better in this case, but the current frameworks use a string.
	private byte[] _content = new byte[0]; // FIXME: A byte array might not be the correct data structure for content. Should we create an NSData wrapper? // Hugi 2019-04-15
	private String _contentEncoding = "utf-8"; // FIXME: Get from properties // Hugi 2019-04-15
	private Map<String, Object> _userInfo; // FIXME: Is userInfo really neccessary used for anything in message passing? // Hugi 2019-04-15

	public void appendContentString( String stringToAppend ) {
		Objects.requireNonNull( stringToAppend );

		try {
			appendContentData( stringToAppend.getBytes( contentEncoding() ) );
		}
		catch( UnsupportedEncodingException e ) {
			throw new RuntimeException( e ); // FIXME: Don't just rethrow
		}
	}

	public void appendContentData( byte[] bytesToAppend ) {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		try {
			output.write( content() );
			output.write( bytesToAppend );
		}
		catch( IOException e ) {
			throw new RuntimeException( e ); // FIXME: Don't just rethrow
		}

		setContent( output.toByteArray() );
	}

	public byte[] content() {
		return _content;
	}

	public String contentEncoding() {
		return _contentEncoding;
	}

	public String contentString() {
		try {
			return new String( content(), contentEncoding() );
		}
		catch( UnsupportedEncodingException e ) {
			throw new RuntimeException( "We all know this never happens, right?", e );
		}
	}

	public Map<String, List<String>> headers() {
		return _headers;
	}

	public String httpVersion() {
		return _httpVersion;
	}

	/**
	 * Completely replace the content of this message with the given byte content.
	 */
	public void setContent( byte[] newContent ) {
		Objects.requireNonNull( newContent );

		_content = newContent;
	}

	/**
	 * Completely replace the content of this message with the given string content, encoded in the encoding specified by contentEncoding().
	 */
	public void setContent( String newContent ) {
		Objects.requireNonNull( newContent );

		try {
			setContent( newContent.getBytes( contentEncoding() ) );
		}
		catch( UnsupportedEncodingException e ) {
			throw new RuntimeException( "An error occurred while attempting to convert string content to data", e );
		}
	}

	public void setContentEncoding( String encoding ) {
		_contentEncoding = encoding;
	}

	public void setHeaders( Map<String, List<String>> headers ) {
		Objects.requireNonNull( headers );

		_headers = headers;
	}

	public void setHTTPVersion( String value ) {
		_httpVersion = value;
	}

	public void setUserInfo( Map<String, Object> map ) {
		_userInfo = map;
	}

	public Map<String, Object> userInfo() {
		return _userInfo;
	}

	public Object userInfoForKey( String key ) {
		Objects.requireNonNull( key );

		return userInfo().get( key );
	}

	@Override
	public boolean equals( Object obj ) {
		return Objects.equals( obj, this );
	}

	@Override
	public String toString() {
		return Objects.toString( this );
	}
}