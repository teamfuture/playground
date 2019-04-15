package com.webobjects.appserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import temp.adaptor.WONotImplementedException;

public class WODirectAction extends WOAction {

	private static final Logger logger = LoggerFactory.getLogger( WODirectAction.class );

	public WODirectAction( WORequest request ) {
		super( request );
	}

	@Override
	public WOActionResults performActionNamed( String actionName ) {
		try {
			String methodName = actionName + "Action";
			Method m = getClass().getMethod( methodName, null );
			Object invoke = m.invoke( this, null );
			return (WOActionResults)invoke;
		}
		catch( NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new RuntimeException( e ); // FIXME: I can't just keep rethrowing // Hugi 2019-04-15
		}
	}

	@Override
	public String getSessionIDForRequest( WORequest request ) {
		throw new WONotImplementedException();
	}

	public WOActionResults defaultAction() {
		WOResponse r = new WOResponse();
		r.setStatus( 200 );
		r.setContent( "I am the default action" );
		return r;
	}
}