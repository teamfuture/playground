package com.webobjects.appserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import temp.adaptor.NGNotImplementedException;

public class NGDirectAction extends NGAction {

	private static final Logger logger = LoggerFactory.getLogger( NGDirectAction.class );

	public NGDirectAction( NGRequest request ) {
		super( request );
	}

	@Override
	public NGActionResults performActionNamed( String actionName ) {
		try {
			String methodName = actionName + "Action";
			Method m = getClass().getMethod( methodName, null );
			Object invoke = m.invoke( this, null );
			return (NGActionResults)invoke;
		}
		catch( NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new RuntimeException( e ); // FIXME: I can't just keep rethrowing // Hugi 2019-04-15
		}
	}

	@Override
	public String getSessionIDForRequest( NGRequest request ) {
		throw new NGNotImplementedException();
	}

	public NGActionResults defaultAction() {
		NGResponse r = new NGResponse();
		r.setStatus( 200 );
		r.setContent( "I am the default action" );
		return r;
	}
}