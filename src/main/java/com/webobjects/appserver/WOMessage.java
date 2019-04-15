package com.webobjects.appserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WOMessage {

	private static final Logger logger = LoggerFactory.getLogger( WOMessage.class );

	private Map<String, List<String>> _headers = new HashMap<String, List<String>>();
	private String _httpVersion;
	private byte[] _content = new byte[0]; // FIXME: A byte array might not be the correct data structure for content. Should we create an NSData wrapper? // Hugi 2019-04-15
	private String _contentEncoding = "utf-8"; // FIXME: Get from properties // Hugi 2019-04-15
	private Map<String, Object> _userInfo; // FIXME: Is userInfo really neccessary used for anything in message passing? // Hugi 2019-04-15

	public void appendContentString( String stringToAppend ) {
		logger.debug( "appendContentString: {}", stringToAppend );

		try {
			byte[] bytesToAppend = stringToAppend.getBytes( contentEncoding() );
			appendContentData( bytesToAppend );
		}
		catch( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
	}

	public void appendContentData( byte[] bytesToAppend ) {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		try {
			output.write( content() );
			output.write( bytesToAppend );
		}
		catch( IOException e ) {
			throw new RuntimeException( e );
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
			throw new RuntimeException( "An error occurred while attempting to encode the content as a string", e );
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
		_content = newContent;
	}

	/**
	 * Completely replace the content of this message with the given string content, encoded in the encoding specified by contentEncoding().
	 */
	public void setContent( String value ) {

		try {
			setContent( value.getBytes( contentEncoding() ) );
		}
		catch( UnsupportedEncodingException e ) {
			throw new RuntimeException( "An error occurred while attempting to convert string content to data", e );
		}
	}

	public void setContentEncoding( String encoding ) {
		_contentEncoding = encoding;
	}

	public void setHeaders( Map<String, List<String>> headers ) {
		_headers = headers;
	}

	public void setHTTPVersion( String value ) {
		_httpVersion = value;
	}

	public void setUserInfo( Map<String, Object> map ) {
		_userInfo = map;
	}

	@Override
	public boolean equals( Object obj ) {
		return Objects.equals( obj, this );
	}

	@Override
	public String toString() {
		return Objects.toString( this );
	}

	public Map<String, Object> userInfo() {
		return _userInfo;
	}

	public Object userInfoForKey( String key ) {
		return userInfo().get( key );
	}
}