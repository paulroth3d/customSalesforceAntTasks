package com.modelmetrics;

import com.modelmetrics.util.PackageUtil;
import com.modelmetrics.util.XML_Util;
import com.modelmetrics.util.FileUtil;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

//-- TODO: convert files by iterating through all files in a folder
//-- instead of converting a package to a file list as * is not used

/**
 *  Creates a file list from a package
**/
public class SFDC_PackageToFileList extends Task {
	
	public static final String ERR_SRC_NOT_EXIST = "Source directory does not exist:";
	
	public static final String ERR_FILE_LIST_COULD_NOT_BE_CREATED = "target could not be created";
	
	public static final String ERR_INVALID_FILE_LIST = "Invalid Package.";
	
	/** version of the package **/
	private String version;
	
	/** whether the code is chatty or not **/
	private Boolean isChatty;
	
	/** location of the target **/
	private File target;
	
	/** location of the packageFile to write to **/
	private File packageFile;
	
	/** location of the source (src) directory that contains all the metadata **/
	private String sourceDir;
	
	private String NEWLINE;
	
	
	public SFDC_PackageToFileList(){
		this.version = "25.0";
		this.isChatty = false;
		
		this.packageFile = null;
		this.target = null;
		
		this.NEWLINE = System.getProperty( "line.separator" );
	}
	
	public void execute() throws BuildException {

		validateArguments();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = null;
		String[] lineFolderList = null;
		
		//int folderIndex = 0;
		//int fileIndex = 0;
		
		String folderName = null;
		//String intermediary = null;
		String fileName = null;
		String metaFolderName = null;
		//String strippedFileName = null;
		String extension = null;
		String newLine = null;
		StringBuilder builder = new StringBuilder();
		
		String packageDirPackage = this.packageFile.getPath();
		
		SFDC_PackageCollapser collapser = new SFDC_PackageCollapser();
		collapser.setSourceFile( this.packageFile.getAbsolutePath() );
		collapser.setTargetFile( this.target.getAbsolutePath() );
		
		builder = new StringBuilder();
		
		try {
			collapser.execute();
			
			reader = new BufferedReader( new FileReader( this.target ));
			
			
			while( (line = reader.readLine()) != null ){
				if( line != null ){
					line = line.trim();
				} else {
					line = "";
				}
				
				lineFolderList = line.split( "/" );
				
				if( lineFolderList.length < 2 ){
					System.out.println( "invalid line found:" + line );
				} else if( line.indexOf( '*' ) > -1 ){
					if( isChatty ) System.out.println( "ignoring * line: " + line );
				} else {
					metaFolderName = lineFolderList[0].trim();
					folderName = PackageUtil.convertMetaToFolder( metaFolderName );
					extension = PackageUtil.convertMetaToExtension( metaFolderName );
					
					if( isChatty ) System.out.println( "old: " + metaFolderName + ", new:" + folderName + ", extension:" + extension );
					
					newLine = sourceDir + line.replace( metaFolderName + "/", folderName + "/" ) + extension;
					
					if( isChatty ) System.out.println( "newLine: " + newLine );
					
					builder.append( newLine ).append( this.NEWLINE );
				}
			}
		} catch( BuildException err ){
			throw( err );
		} catch( Exception err ){
			throw( new BuildException( ERR_FILE_LIST_COULD_NOT_BE_CREATED + NEWLINE + err.getMessage() ));
		} finally {
			try {
				if( reader != null ) reader.close();
			} catch( Exception err2 ){}
			
			try {
				if( writer != null ) writer.close();
			} catch( Exception err2 ){}
		}
		
		try {
			writer = new BufferedWriter( new FileWriter( this.target ));
			writer.write( builder.toString() );
			
			System.out.println( "Wrote file to: " + this.target );
			
		} catch( BuildException err ){
			throw( err );
		} catch( Exception err ){
			throw( new BuildException( ERR_FILE_LIST_COULD_NOT_BE_CREATED + NEWLINE + err.getMessage() ));
		} finally {
			try {
				if( reader != null ) reader.close();
			} catch( Exception err2 ){}
			
			try {
				if( writer != null ) writer.close();
			} catch( Exception err2 ){}
		}
	}
	
	private void validateArguments(){
		FileUtil.checkCanRead( packageFile );
		FileUtil.checkCanWrite( target );
	}
	
	//-- getters/setters
	public void setVersion( String version ){
		this.version = version;
	}
	
	/**
	 *  The address of the list of files that we want to create the packages from.
	 *  This can either be a list of urls (such as from a SVN Diff or Git status)
	 *  or a list of local files
	 *  @param fileListAddress (String) the address of the file with the list of files.
	**/
	public void setTarget( String fileListAddress ){
		this.target = new File( fileListAddress );
	}
	
	public void setPackage( String targetAddr ){
		this.packageFile = new File( targetAddr );
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
	
	public void setSourceDir( String sourceDir ){
		try {
			File sourceDirFile = new File( sourceDir );
			if( !sourceDirFile.exists() ){
				throw( new BuildException( ERR_SRC_NOT_EXIST + sourceDir ));
			}
			
		} catch( BuildException err ){
			throw( err );
		} catch( Exception err ){
			throw( new BuildException( ERR_SRC_NOT_EXIST + sourceDir ));
		}
		
		if( !sourceDir.endsWith( "/" )){
			sourceDir = sourceDir + "/";
		}
		
		this.sourceDir = sourceDir;
	}
}
