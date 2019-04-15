package com.webobjects.appserver;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Searches the classpath for a class, by searching by it's simple name.
 *
 * @author Hugi Thordarson
 */

public class ResourceFinder {

	private static final Logger logger = LoggerFactory.getLogger( ResourceFinder.class );

	/**
	 * Caches already found classes.
	 */
	private Map<String, Class> _cache = new HashMap<String, Class>();

	/**
	 * Path to directory to search for Jar files on. Subdirectories are not included.
	 */
	private String _searchPath;

	/**
	 * All available class names, cached
	 */
	private List<String> _allAvailableResourcePaths;

	/**
	 * Cached list of JAR-files to search.
	 */
	private List<String> _jarsToSearch;

	/**
	 * A singleton
	 */
	private static ResourceFinder _defaultInstance;

	/**
	 * Instances constructed using this constructor will search the java.class.path.
	 */
	public ResourceFinder() {}

	/**
	 * Instances constructed using this constructor will search for classes in jar files there.
	 *
	 * @param searchPath Path to directory to search for Jar files on. Subdirectories are not included.
	 */
	public ResourceFinder( String searchPath ) {
		_searchPath = searchPath;
	}

	/**
	 * Sets the singleton instance
	 */
	public static void setDefaultInstance( ResourceFinder value ) {
		_defaultInstance = value;
	}

	/**
	 * @return Eturns the default instance (should have been set by Eplica at startup).
	 */
	public static ResourceFinder defaultInstance() {
		if( _defaultInstance == null ) {
			_defaultInstance = new ResourceFinder();
		}

		return _defaultInstance;
	}

	/**
	 * @return The list of JAR-files this Class searcher will load from.
	 */
	public List<String> jarsToSearch() {
		if( _jarsToSearch == null ) {
			if( _searchPath == null ) {
				_jarsToSearch = jarsOnClasspath();
			}
			else {
				_jarsToSearch = jarsInDirectory( _searchPath );
			}
		}

		return _jarsToSearch;
	}

	/**
	 * A list of all jar files in the given directory
	 */
	private static List<String> jarsInDirectory( String directoryPath ) {
		File directory = new File( directoryPath );
		if( logger.isDebugEnabled() ) {
			logger.debug( "Reading jar in directory: " + directory.getAbsolutePath() );
		}
		List<String> jars = new ArrayList<String>();

		File[] files = directory.listFiles();
		if( files != null ) {
			for( File file : files ) {
				if( file.getName().endsWith( ".jar" ) ) {
					jars.add( file.getAbsolutePath() );
				}
			}
		}

		return jars;
	}

	/**
	 * @return An instance of the named class.
	 */
	public Class<?> classFromSimpleName( String simpleName ) {
		Class<?> clazz = _cache.get( simpleName );

		if( clazz != null ) {
			if( clazz.equals( _NoClassInTheEntireUniverse.class ) ) {
				return null;
			}
			else {
				return clazz;
			}
		}

		String fullyQualifiedClassName = classNameFromSimpleName( simpleName );

		if( fullyQualifiedClassName == null ) {
			_cache.put( simpleName, _NoClassInTheEntireUniverse.class );
			return null;
		}

		try {
			clazz = Class.forName( fullyQualifiedClassName );
			_cache.put( simpleName, clazz );
			return clazz;
		}
		catch( ClassNotFoundException e ) {
			throw new RuntimeException( "Couldn't find a class on the classpath with the name " + fullyQualifiedClassName, e );
		}
	}

	/**
	 * @return A class in the classpath by simple name. If many classes match the name, this will return a random result.
	 */
	public String classNameFromSimpleName( String simpleName ) {
		List<String> classNames = classNamesFromSimpleName( simpleName );

		if( classNames != null && classNames.size() > 0 ) {
			return classNames.get( 0 );
		}

		return null;
	}

	/**
	 * @return All classes on the classpath with the given simple name.
	 */
	public List<String> classNamesFromSimpleName( String simpleName ) {
		List<String> matchingNames = new ArrayList<String>();

		for( String className : allAvailableClassNames() ) {
			if( className.endsWith( "." + simpleName ) ) {
				matchingNames.add( className );
			}
		}

		return matchingNames;
	}

	/**
	 * @return Entries on the java.class.path.
	 */
	private static List<String> classpathEntries() {
		return Arrays.asList( System.getProperty( "java.class.path" ).split( System.getProperty( "path.separator" ) ) );
	}

	/**
	 * @return A list of jars on the classpath
	 */
	private List<String> jarsOnClasspath() {
		List<String> list = new ArrayList<String>();

		for( String classpathEntry : classpathEntries() ) {

			if( classpathEntry.endsWith( ".jar" ) ) {
				list.add( classpathEntry );
			}
		}

		return list;
	}

	/**
	 * @return A list of every available (fully qualified) class name this finder searches in.
	 */
	private List<String> allAvailableClassNames() {
		List<String> result = new ArrayList<String>();

		for( String entryName : allAvailableResourcePaths() ) {
			if( entryName != null && entryName.endsWith( ".class" ) ) {
				entryName = entryName.substring( 0, entryName.length() - 6 );
				entryName = entryName.replace( '/', '.' );
				result.add( entryName );
			}
		}

		return result;
	}

	/**
	 * @return A list of every available resource path.
	 */
	public synchronized List<String> allAvailableResourcePaths() {
		if( _allAvailableResourcePaths == null ) {
			try {
				_allAvailableResourcePaths = new ArrayList<>();

				JarEntry entry;
				JarFile jarFile;
				File file;
				for( String jarPath : jarsToSearch() ) {
					file = new File( jarPath );
					jarFile = new JarFile( file );

					JarInputStream is = new JarInputStream( new FileInputStream( file ) );

					while( (entry = is.getNextJarEntry()) != null ) {
						if( entry.getName() != null ) {
							_allAvailableResourcePaths.add( entry.getName() );
						}
					}
				}
			}
			catch( Exception e ) {
				String msg = "An exception occurred while attempting to find class by simple name";
				logger.error( msg, e );
				throw new RuntimeException( msg, e );
			}
		}

		System.out.println( _allAvailableResourcePaths );
		return _allAvailableResourcePaths;
	}

	/**
	 * This class is just used in the cache, as an indicator that no class was found.
	 */
	private static class _NoClassInTheEntireUniverse {}
}