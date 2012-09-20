package com.modelmetrics.util;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;

public class FileUtil {

	public static final String ERROR_CANNOT_READ = "Cannot read from:";
	public static final String ERROR_CANNOT_WRITE = "Cannot write to:";
	
	/**
	 *  Checks if the sourceFile can be read from
	 *  @param sourceFile (File)
	 *  @throws BuildException - if the file could not be read from
	**/
	public static void checkCanRead( File sourceFile ) throws BuildException {
		if( !sourceFile.exists() || !sourceFile.canRead() ){
			throw( new BuildException( ERROR_CANNOT_READ + sourceFile ));
		}
	}
	
	/**
	 *  Checks if the target file can be written to
	 *  @param targetFile (File)
	 *  @throws BuildException - if the file cannot be written to
	**/
	public static void checkCanWrite( File targetFile ) throws BuildException {
		if( !targetFile.exists() ){
			try {
				targetFile.createNewFile();
			} catch( Exception err ){
				throw( new BuildException( ERROR_CANNOT_WRITE + targetFile ));
			}
		} else if( !targetFile.canWrite() ){
			throw( new BuildException( ERROR_CANNOT_WRITE + targetFile ));
		}
	}
	
	/**
	 *  Removes the extension from a fileName
	 *  @param fileName (String)
	 *  @return String
	**/
	public static String removeExtension( String fileName ){
		if( fileName == null ) return( fileName );
		
		return( fileName.replaceAll( "\\.[^.]+", "" ));
	}
	
	/**
	 *  Copies a file from one file to another
	 *  @param sourceFile (File)
	 *  @param targetFile (File)
	 *  @return Boolean - whether the copy was successful or not
	**/
	public static Boolean copyFile( File sourceFile, File targetFile ) {
		InputStream  in = null;
		OutputStream out = null;  
			
		try {
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(targetFile);
			
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			return( true );
		} catch( Exception err ){
			System.out.println( "Exception occurred while copying:" + err.getMessage() );
		} finally {
			try {
				if( in != null ) in.close();
				if( out != null ) out.close();
			} catch( Exception err ){}
		}
		
		return( false );
	}	
}
