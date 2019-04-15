package com.webobjects.appserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles direct action requests
 */

public class WODirectActionRequestHandler extends WORequestHandler {

	private static final Logger logger = LoggerFactory.getLogger( WODirectActionRequestHandler.class );

	/**
	 * Name of the default direct action class.
	 */
	private static final String DEFAULT_DIRECT_ACTION_CLASS_NAME = WODirectAction.class.getName();

	/**
	 * Name of the default direct action method.
	 */
	private static final String DEFAULT_DIRECT_ACTION_NAME = "default";

	@Override
	public WOResponse handleRequest( WORequest request ) {
		Objects.requireNonNull( request );

		logger.debug( "Handling direct action request {}", request );

		final WODirectActionURLDecoder decoder = new WODirectActionURLDecoder( request.uri() );

		String directActionClassName = decoder.directActionClassName();

		if( directActionClassName == null ) {
			directActionClassName = DEFAULT_DIRECT_ACTION_CLASS_NAME;
		}

		String directActionMethodName = decoder.directActionName();

		if( directActionMethodName == null ) {
			directActionMethodName = DEFAULT_DIRECT_ACTION_NAME;
		}

		WOActionResults results = invokeDirectAction( directActionClassName, directActionMethodName, request );
		return results.generateResponse();
	}

	private WOActionResults invokeDirectAction( String directActionClassName, String directActionName, WORequest request ) {

		try {
			// FIXME: That class declaration needs to have a more efficient way of being (a) discovered and (b) cached
			Class<? extends WODirectAction> directActionClass = (Class<? extends WODirectAction>)Class.forName( directActionClassName );
			Constructor<? extends WODirectAction> constructor = directActionClass.getConstructor( WORequest.class );
			WODirectAction newInstance = constructor.newInstance( request );
			return newInstance.performActionNamed( directActionName );
		}
		catch( ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
			logger.error( "The direct action class {} was not found", directActionClassName, e );
			throw new RuntimeException( e ); // FIXME: Can't just keep rethrowing
		}
	}

	/**
	 * FIXME: This class is put in place here as a temporary measure to decode only Direct Action URLs. Some of the logic here can be stabilized and centralized when more parts of the system start to be implemented.
	 */
	private static class WODirectActionURLDecoder {

		private final String _adaptorPrefix;
		private final String _adaptorName;
		private final String _applicationName;
		private final String _requestHandlerKey;
		private final String _directActionClassName;
		private final String _directActionName;

		public WODirectActionURLDecoder( String url ) {
			Objects.requireNonNull( url );

			// Remove the first slash since it messes with out string slicing
			url = url.substring( 1 );

			final String[] parts = url.split( "/" );

			logger.debug( "Decoded URL {} to parts {} ", Arrays.asList( parts ) );

			_adaptorPrefix = pathElementIfPresent( parts, 0 );
			_adaptorName = pathElementIfPresent( parts, 1 );
			_applicationName = pathElementIfPresent( parts, 2 );
			_requestHandlerKey = pathElementIfPresent( parts, 3 );
			_directActionClassName = pathElementIfPresent( parts, 4 );
			_directActionName = pathElementIfPresent( parts, 5 );
		}

		/**
		 * @return the element of the path if it is present, otherwise null
		 */
		private static final String pathElementIfPresent( String[] pathParts, int positionInPath ) {
			if( pathParts.length <= positionInPath ) {
				return null;
			}

			return pathParts[positionInPath];
		}

		/**
		 * @return The name of the direct action class that should respond to this request.
		 */
		public String directActionClassName() {
			return _directActionClassName;
		}

		/**
		 * @return The name of the direct action method that should respond to this request.
		 */
		public String directActionName() {
			return _directActionName;
		}
	}
}