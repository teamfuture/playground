package com.webobjects.appserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.webobjects.temp.WOExperimentalAdaptor;

public class WOApplication {

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
	 * Only WOApplication can construct instances of itself.
	 */
	private WOApplication() {}

	/**
	 * Registers a new request handler that will respond to requests under the given key. @see WORequestHandler
	 */
	public void registerRequestHandler( WORequestHandler requestHandler, String requestHandlerKey ) {
		Objects.requireNonNull( requestHandler );
		Objects.requireNonNull( requestHandlerKey );

		_requestHandlers.put( requestHandlerKey, requestHandler );
	}

	/**
	 * @return The default encoding of the application.
	 */
	public String defaultEncoding() {
		return "utf-8";
	}

	/**
	 * @return The instance of the running application.
	 */
	public static WOApplication application() {
		return _application;
	}

	/**
	 * Primary entry point of requests into the server stack (above WOAdaptor)
	 */
	public WOResponse dispatchRequest( WORequest request ) {
		Objects.requireNonNull( request );

		WORequestHandler r = requestHandlerForKey( directActionRequestHandlerKey() );
		return r.handleRequest( request );
	}

	private WORequestHandler requestHandlerForKey( String key ) {
		Objects.requireNonNull( key );

		return _requestHandlers.get( key );
	}

	private String directActionRequestHandlerKey() {
		return "wa";
	}
}