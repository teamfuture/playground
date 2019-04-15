package com.webobjects.appserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import temp.adaptor.WOExperimentalAdaptor;

public class WOApplication {

	private static final Logger logger = LoggerFactory.getLogger( WOApplication.class );

	/**
	 * Each running application has a singleton instance of WOApplication.
	 */
	private static WOApplication _application;

	/**
	 * A map, mapping request handler keys to instances of "request handlers" (@see WORequestHandler).
	 */
	private Map<String, WORequestHandler> _requestHandlers = new HashMap<>();

	public static void main( String[] args ) {
		main( args, WOApplication.class );
	}

	/**
	 * This method should be invoked by subclasses
	 */
	public static void main( String[] args, Class<? extends WOApplication> applicationClass ) {
		WOExperimentalAdaptor.listen();

		try {
			_application = applicationClass.newInstance();
		}
		catch( InstantiationException | IllegalAccessException e ) {
			e.printStackTrace();
		}

		application().registerRequestHandler( new WODirectActionRequestHandler(), "wa" ); // FIXME: Consider location of initialization
	}

	/**
	 * @return The instance of the running application.
	 */
	public static WOApplication application() {
		return _application;
	}

	/**
	 * Registers a new request handler that will respond to requests under the given key. @see WORequestHandler
	 */
	public void registerRequestHandler( WORequestHandler requestHandler, String requestHandlerKey ) {
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
	public WOResponse dispatchRequest( WORequest request ) {
		Objects.requireNonNull( request );
		logger.debug( "Dispatching request {}", request );

		final String requestHandlerKey = directActionRequestHandlerKey(); // FIXME: Implement request handler key lookup from URL // Hugi 2019-04-15
		final WORequestHandler requestHandler = requestHandlerForKey( requestHandlerKey );

		if( requestHandler == null ) {
			throw new IllegalArgumentException( "No request handler found for key: " + requestHandlerKey );
		}

		return requestHandler.handleRequest( request );
	}

	private WORequestHandler requestHandlerForKey( String key ) {
		Objects.requireNonNull( key );

		return _requestHandlers.get( key );
	}

	private String directActionRequestHandlerKey() {
		return "wa";
	}

	/**
	 * FIXME: This is included merely to avoid the HotSwapAdaptor to DCEVM from throwing an exception on startup.
	 */
	public void run() {}
}