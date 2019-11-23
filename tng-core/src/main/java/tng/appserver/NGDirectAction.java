package tng.appserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tng.util.NGNotImplementedException;

public class NGDirectAction extends NGAction {

	private static final Logger logger = LoggerFactory.getLogger( NGDirectAction.class );

	/**
	 * To be considered a direct action, method names must end with this suffix.
	 */
	private static final String DEFAULT_DIRECT_ACTION_METHOD_SUFFIX = "Action";

	/**
	 * A direct action must always originate from a request, so that's passed in as an argument to the constructor.
	 */
	public NGDirectAction( NGRequest request ) {
		super( request );
	}

	/**
	 * Performs the heavy lifting of direct action invocation, from nothing but the passed in name
	 */
	@Override
	public NGActionResults performActionNamed( String directActionName ) {
		logger.debug( "Performing direct action with name {}", directActionName );

		try {
			String methodName = directActionName + DEFAULT_DIRECT_ACTION_METHOD_SUFFIX;
			Method m = getClass().getMethod( methodName, null );
			Object invoke = m.invoke( this, null );
			return (NGActionResults)invoke;
		}
		catch( NoSuchMethodException e ) {
			logger.error( "Direct action method not found" );
			throw new RuntimeException( e ); // FIXME: I can't just keep rethrowing // Hugi 2019-04-15
		}
		catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			throw new RuntimeException( e ); // FIXME: I can't just keep rethrowing // Hugi 2019-04-15
		}
	}

	@Override
	public String getSessionIDForRequest( NGRequest request ) {
		throw new NGNotImplementedException();
	}

	public NGActionResults defaultAction() {
		NGResponse r = new NGResponse();
		r.setContent( "This is the default action" );
		return r;
	}
}