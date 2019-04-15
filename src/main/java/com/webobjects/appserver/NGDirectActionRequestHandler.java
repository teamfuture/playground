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

public class NGDirectActionRequestHandler extends NGRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger( NGDirectActionRequestHandler.class );

	/**
	 * Name of the default direct action class.
	 */
	private static final String DEFAULT_DIRECT_ACTION_CLASS_NAME = NGDirectAction.class.getName();

	/**
	 * Name of the default direct action method.
	 */
	private static final String DEFAULT_DIRECT_ACTION_NAME = "default";

	@Override
	public NGResponse handleRequest( NGRequest request ) {
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

		NGActionResults results = invokeDirectAction( directActionClassName, directActionMethodName, request );
		return results.generateResponse();
	}

	/**
	 * @return a class corresponding to the class identifier in the URL. Can be a simple classname or fully qualified.
	 */
	private Class<? extends NGDirectAction> directActionClassFromName( String directActionClassName ) {
		try {
			Class<? extends NGDirectAction> directActionClass = (Class<? extends NGDirectAction>)NGBundle.classForSimpleName( directActionClassName );

			// If the class is not found from the simple name lookup, we attempt to find it by full name.
			if( directActionClass == null ) {
				directActionClass = (Class<? extends NGDirectAction>)Class.forName( directActionClassName );
			}

			// FIXME: Better handle the case of the Direct Action class not being found at all // Hugi 2019-04-15
			if( directActionClass == null ) {
				throw new RuntimeException( "Direct action class not found: " + directActionClassName );
			}

			return directActionClass;
		}
		catch( ClassNotFoundException e ) {
			throw new RuntimeException( e ); // FIXME: Can't just keep rethrowing
		}
	}

	private NGActionResults invokeDirectAction( String directActionClassName, String directActionName, NGRequest request ) {

		try {
			// FIXME: That class declaration needs to have a more efficient way of being (a) discovered and (b) cached
			Class<? extends NGDirectAction> directActionClass = directActionClassFromName( directActionClassName );
			Constructor<? extends NGDirectAction> constructor = directActionClass.getConstructor( NGRequest.class );
			NGDirectAction newInstance = constructor.newInstance( request );
			return newInstance.performActionNamed( directActionName );
		}
		catch( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
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