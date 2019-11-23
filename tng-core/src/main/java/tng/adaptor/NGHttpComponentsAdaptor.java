package tng.adaptor;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tng.appserver.NGApplication;
import tng.appserver.NGRequest;
import tng.appserver.NGResponse;

/**
 * An experimental adaptor to pass experimental requests into the experimental stack. It's experimental 's what I'm saying.
 *
 * FIXME: This is put here purely as a placeholder for an *actual* adaptor, to be implemented at a later time.
 */

public class NGHttpComponentsAdaptor extends NGAdaptor  {

	private static final Logger logger = LoggerFactory.getLogger( NGHttpComponentsAdaptor.class );

	// FIXME: Make the port settable via properties
	private static final int DEFAULT_HTTP_PORT = 1200;

	/**
	 * Start listening for requests on the default port.
	 */
	public static void listen() {
		try {
			Thread t = new RequestListenerThread( DEFAULT_HTTP_PORT );
			t.setDaemon( false );
			t.start();
		}
		catch( IOException e ) {
			logger.error( "Error while registering WOAdaptor", e );
		}
	}

	static class Handler implements HttpRequestHandler {

		@Override
		public void handle( HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext ) throws HttpException, IOException {
			NGResponse ngresponse = NGApplication.application().dispatchRequest( httpRequestToNGRequest( httpRequest ) );
			assignNGResponseToHttpResponse( ngresponse, httpResponse );
		}

		private static NGRequest httpRequestToNGRequest( HttpRequest httpRequest ) {
			final String method = httpRequest.getRequestLine().getMethod().toUpperCase( Locale.ENGLISH );
			final String uri = httpRequest.getRequestLine().getUri();
			final String httpVersion = httpRequest.getProtocolVersion().toString();
			final Map<String, List<String>> headers = headers( httpRequest );
			final byte[] content = new byte[0]; // FIXME: Actually read the request content, if required
			final Map<String, Object> userInfo = new HashMap<>();
			final NGRequest ngRequest = new NGRequest( method, uri, httpVersion, headers, content, userInfo );
			return ngRequest;
		}

		/**
		 * FIXME: We need to account for headers with multiple values.
		 */
		private static Map<String, List<String>> headers( HttpRequest httpRequest ) {
			Map<String, List<String>> headers = new HashMap<String, List<String>>();

			for( HeaderIterator i = httpRequest.headerIterator() ; i.hasNext() ; ) {
				Header header = i.nextHeader();
				String name = header.getName();
				List<String> values = Arrays.asList( header.getValue() );
				headers.put( name, values );
			}

			return headers;
		}

		private static void assignNGResponseToHttpResponse( NGResponse ngResponse, HttpResponse httpResponse ) {
			// FIXME: This currently only handles strings
			final String contentString = ngResponse.contentString();

			// FIXME: Determine the actual content type form the NGResponse // Hugi 2019-04-15
			final ContentType contentType = ContentType.create( "text/html", ngResponse.contentEncoding() );
			final StringEntity entity = new StringEntity( contentString, contentType );
			httpResponse.setEntity( entity );
			httpResponse.setStatusCode( ngResponse.status() );
		}
	}

	static class RequestListenerThread extends Thread {

		private final ServerSocket serversocket;
		private final HttpParams params;
		private final HttpService httpService;

		public RequestListenerThread( int port ) throws IOException {
			this.serversocket = new ServerSocket( port );
			this.params = new SyncBasicHttpParams();
			this.params.setIntParameter( CoreConnectionPNames.SO_TIMEOUT, 5000 ).setIntParameter( CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024 ).setBooleanParameter( CoreConnectionPNames.STALE_CONNECTION_CHECK, false ).setBooleanParameter( CoreConnectionPNames.TCP_NODELAY, true ).setParameter( CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1" );

			// Set up the HTTP protocol processor
			HttpProcessor httpproc = new ImmutableHttpProcessor( new HttpResponseInterceptor[] {
					new ResponseDate(), new ResponseServer(), new ResponseContent(), new ResponseConnControl()
			} );

			// Set up request handlers
			HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
			reqistry.register( "*", new Handler() );

			// Set up the HTTP service
			this.httpService = new HttpService( httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), reqistry, this.params );
		}

		@Override
		public void run() {
			logger.info( "Listening on port " + serversocket.getLocalPort() );

			while( !Thread.interrupted() ) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					logger.debug( "Incoming connection from " + socket.getInetAddress() );
					conn.bind( socket, this.params );

					// Start worker thread
					Thread t = new WorkerThread( this.httpService, conn );
					t.setDaemon( true );
					t.start();
				}
				catch( InterruptedIOException ex ) {
					break;
				}
				catch( IOException e ) {
					logger.error( "I/O error initialising connection thread: " + e.getMessage() );
					break;
				}
			}
		}
	}

	static class WorkerThread extends Thread {

		private final HttpService httpservice;
		private final HttpServerConnection conn;

		public WorkerThread( final HttpService httpservice, final HttpServerConnection conn ) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			HttpContext context = new BasicHttpContext( null );
			try {
				while( !Thread.interrupted() && this.conn.isOpen() ) {
					this.httpservice.handleRequest( this.conn, context );
					this.conn.close(); // FIXME: Added to avoid connection read timeout. Synchronization problems? // Hugi 2019-04-15
				}
			}
			catch( ConnectionClosedException ex ) {
				logger.error( "Client closed connection" );
			}
			catch( IOException ex ) {
				logger.error( "I/O error: " + ex.getMessage() );
			}
			catch( HttpException ex ) {
				logger.error( "Unrecoverable HTTP protocol violation: " + ex.getMessage() );
			}
			finally {
				try {
					this.conn.shutdown();
				}
				catch( IOException ignore ) {}
			}
		}
	}
}