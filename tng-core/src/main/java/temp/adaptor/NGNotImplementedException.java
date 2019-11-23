package temp.adaptor;

/**
 * For helping to mark locations where stuff has not yet been implemented
 */

public class NGNotImplementedException extends RuntimeException {

	public NGNotImplementedException() {
		super();
	}

	public NGNotImplementedException( String message ) {
		super( message );
	}
}