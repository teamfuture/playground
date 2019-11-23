package com.webobjects.appserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import temp.adaptor.NBHttpComponentsAdaptor;

public class NGApplication {

	private static final Logger logger = LoggerFactory.getLogger( NGApplication.class );

	/**
	 * Each running application has a singleton instance of WOApplication.
	 */
	private static NGApplication _application;

	/**
	 * A map, mapping request handler keys to instances of "request handlers" (@see WORequestHandler).
	 */
	private Map<String, NGRequestHandler> _requestHandlers = new HashMap<>();

	public static void main( String[] args ) {
		main( args, NGApplication.class );
	}

	/**
	 * This method should be invoked by subclasses
	 */
	public static void main( String[] args, Class<? extends NGApplication> applicationClass ) {
		Objects.requireNonNull( applicationClass );

		NBHttpComponentsAdaptor.listen();

		try {
			_application = applicationClass.newInstance();
		}
		catch( InstantiationException | IllegalAccessException e ) {
			throw new RuntimeException( e );
		}

		application().registerRequestHandler( new NGDirectActionRequestHandler(), "wa" ); // FIXME: Consider location of initialization
	}

	/**
	 * @return The instance of the running application.
	 */
	public static NGApplication application() {
		return _application;
	}

	/**
	 * Registers a new request handler that will respond to requests under the given key. @see WORequestHandler
	 */
	public void registerRequestHandler( NGRequestHandler requestHandler, String requestHandlerKey ) {
		Objects.requireNonNull( requestHandler );
		Objects.requireNonNull( requestHandlerKey );

		_requestHandlers.put( requestHandlerKey, requestHandler );
	}

	/**
	 * @return The default text encoding of the application.
	 *
	 *         FIXME: This should be settable.
	 */
	public String defaultEncoding() {
		return "utf-8";
	}

	/**
	 * Primary entry point of requests into the server stack (above WOAdaptor)
	 */
	public NGResponse dispatchRequest( NGRequest request ) {
		Objects.requireNonNull( request );
		logger.debug( "Dispatching request {}", request );

		final String requestHandlerKey = directActionRequestHandlerKey(); // FIXME: Implement request handler key lookup from URL // Hugi 2019-04-15
		final NGRequestHandler requestHandler = requestHandlerForKey( requestHandlerKey );

		if( requestHandler == null ) {
			throw new IllegalArgumentException( "No request handler found for key: " + requestHandlerKey );
		}

		return requestHandler.handleRequest( request );
	}

	/**
	 * @return The request handler instance registered with the given key
	 */
	private NGRequestHandler requestHandlerForKey( String key ) {
		Objects.requireNonNull( key );

		return _requestHandlers.get( key );
	}

	/**
	 * @return The default Direct Action request handler key
	 */
	private String directActionRequestHandlerKey() {
		return "wa";
	}

	/**
	 * FIXME: This is included merely to avoid the HotSwapAdaptor to DCEVM from throwing an exception on startup.
	 */
	public void run() {}
}