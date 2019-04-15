package com.webobjects.appserver;

public class NGResponse extends NGMessage implements NGActionResults {

	private int _status = 200;

	public int status() {
		return _status;
	}

	public void setStatus( int value ) {
		_status = value;
	}

	public NGResponse generateResponse() {
		return this;
	}
}