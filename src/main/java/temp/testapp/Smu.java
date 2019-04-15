package temp.testapp;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class Smu {

	public static void main( String[] args ) {
		try {
			Enumeration<URL> r = Smu.class.getClassLoader().getResources( "" );

			while( r.hasMoreElements() ) {
				System.out.println( r.nextElement() );
			}

		}
		catch( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}