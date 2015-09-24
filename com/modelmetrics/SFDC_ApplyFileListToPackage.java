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

/**
 *  Merges a file list into a package
**/
public class SFDC_ApplyFileListToPackage extends Task {
	
	public static final String ERR_PACKAGE_DIR_IS_FILE = "Package directory is file:";
	public static final String ERR_PACKAGE_DIR_CANNOT_BE_CREATED = "Package directory could not be created:";
	public static final String ERR_READING_IGNORE_FILE = "Error occurred while reading ignore file:";
	public static final String ERR_WHILE_COPYING = "Error occurred while copying files";
	public static final String ERR_WHILE_CREATING_PACKAGE = "Error occurred while creating a package";
	public static final String ERR_CANNOT_FIND_FILE = "cannot find file:";
	public static final String ERR_MISSING_EXTENSION = " --  is it missing an extension?";
	
	/** file that contains the list of files to copy **/
	private File listFile;
	
	/** file that contains the list of folder/files to ignore **/
	private File ignoreFile;
	
	/** file of the package to be merged in **/
	private File packageFile;
	
	/** set of folder/files to ignore **/
	private Set<String> ignoreSet;
	
	/** whether the task is chatty **/
	private Boolean isChatty;
	
	/** whether to ignore missing files **/
	private Boolean shouldIgnoreMissingFiles;
	
	private String NEWLINE;
	
	public SFDC_ApplyFileListToPackage(){
		this.listFile = null;
		this.ignoreFile = null;
		this.packageFile = null;
		this.isChatty = false;
	}
	
	/**
	 *  Reads a list of file paths from the listFile, and copies those into a package
	**/
	public void execute() throws BuildException {
		NEWLINE = System.getProperty( "line.separator" );
		
		validateArguments();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = null;
		
		int folderIndex = 0;
		int fileIndex = 0;
		String folderName = null;
		String metaFolderName = null;
		String intermediary = null; //-- this can contain additional folders, such as documents, emails, reports
		String fileName = null;
		String strippedFileName = null;
		String targetFile = null;
		String optionalOffset = null;
		
		File fileToCheck = null;
		File fileDestination = null;
		File parentFile = null;
		
		
		String[] lineFolderFile = null;
		String[] folderSplit = null;
		
		String packageFilePath = this.packageFile.getPath();
		
		SFDC_AddPackageMember addMemberTask = new SFDC_AddPackageMember();
		addMemberTask.setSourceFile( packageFilePath );
		addMemberTask.setTargetFile( packageFilePath );
		addMemberTask.setIsChatty( this.isChatty );
		
		//-- try to get the list of files to ignore
		this.ignoreSet = new HashSet<String>();
		try {
			//System.out.println( "attempting to read the ignore file" );
			
			if( ignoreFile != null && ignoreFile.exists() && ignoreFile.canRead() ){
				System.out.println( "ignore file found: " + ignoreFile.getPath() );
				reader = new BufferedReader( new FileReader( this.ignoreFile ));
				
				while( (line=reader.readLine()) != null ){
					if( line != null ){
						line = line.trim().toLowerCase();
					} else {
						line = "";
					}
					
					if( !("".equals(line))){
						ignoreSet.add( line );
					}
				}
			} else {
				System.out.println( "could not find ignore file" );
			}
		} catch( Exception err ){
			System.out.println( ERR_READING_IGNORE_FILE + ":" + err.getMessage() );
		} finally {
			try {
				if( reader != null ) reader.close();
			} catch( Exception err2 ){}
		}
		
		System.out.println( "ignored files[" + this.ignoreSet.size() + "]" );
		if( isChatty ) System.out.println( this.ignoreSet.toString() );
		
		//-- loop through each file
		try {
			reader = new BufferedReader( new FileReader( this.listFile ));
			
			while( (line= reader.readLine()) != null ){
				if( line != null ){
					line = line.trim();
				} else {
					line = "";
				}
				
				if( this.isChatty ) System.out.println( "checking for file:" + line );
				
				folderSplit = line.split( "/" );
				
				if( folderSplit == null || folderSplit.length < 2 || line.trim().startsWith("#") ){
					if( isChatty ) System.out.println( "ignoring line[" + line + "]" );
					continue;
				} else {
					//-- assume file is good
				}
				
				//-- take everything after src/ if it exists
				lineFolderFile = line.split( "\\bsrc/" );
				if( lineFolderFile != null && lineFolderFile.length == 2 ){
					line = lineFolderFile[1];
				}
				//System.out.println( "line["+ line + "]" );
				
				//-- take ONLY lines that have folders
				lineFolderFile = line.split( "/" );
				if( lineFolderFile != null && lineFolderFile.length >= 2 ){
					
					folderIndex = line.indexOf( '/' );
					fileIndex = line.lastIndexOf( "/" );
					
					folderName = line.substring( 0, folderIndex );
					if( folderIndex < fileIndex ){
						intermediary = line.substring( folderIndex+1, fileIndex+1 );
					} else {
						intermediary = "";
					}
					fileName = line.substring( fileIndex + 1 );
					
					if( isChatty ) System.out.println( "old: " + folderName + intermediary + "/" + fileName );
					
					if( PackageUtil.AURA_FOLDER.equals( folderName ) &&
						lineFolderFile.length > 1
					){
						if( isChatty ) System.out.println( "appears [" + line + "] is an aura bundle" );
						fileName = lineFolderFile[1];
						if( !fileName.endsWith( ".aura" ) ){
							fileName += ".aura";
						}
						intermediary = "";
					}
					
					if( this.ignoreSet.contains( line.toLowerCase() )){
						if( isChatty ) System.out.println( "ignoring: file[" + line + "]" );
					} else if( fileName.indexOf( '.' ) < 0 ){
						if( isChatty ) System.out.println( "seems the following is a folder:" + folderName + "/" + fileName );
					} else {
						metaFolderName = PackageUtil.convertFolderToMeta( folderName, fileName );
						strippedFileName = FileUtil.removeExtension( folderName, fileName );
						
						if( metaFolderName == null || metaFolderName.isEmpty() ){
							throw( new BuildException( PackageUtil.ERROR_UNKNOWN_CONVERSION + folderName ));
						}
						//System.out.println( "converting metadatatype for:" + folderName + ":" + metaFolderName );
						
						System.out.println( "Adding " + metaFolderName + ", " + intermediary + strippedFileName );
						
						addMemberTask.setMetadataType( metaFolderName );
						addMemberTask.setMember( intermediary + strippedFileName );
						addMemberTask.execute();
					}
					
				} else {
					if( isChatty ) System.out.println( "File provided does not give the containing folder:" + line );
				}
			}
			
		} catch( BuildException err ){
			throw( err );
		} catch( Exception err ){
			throw( new BuildException( ERR_WHILE_COPYING + NEWLINE + err.getMessage() ));
		}
	}
	
	/**
	 *  Validate the arguments sent through getters/setters
	 *  @throws BuildException if there is a problem
	**/
	private void validateArguments(){
		FileUtil.checkCanRead( this.listFile );
		FileUtil.checkCanRead( this.packageFile );
	}
	

	
	//-- getter/setters
	
	/**
	 *  File that contains the addresses of the files to be used in the package.
	 *  (Such as a subversion or git status)
	**/
	public void setListFile( String listFileAddr ){
		this.listFile = new File( listFileAddr );
	}
	
	/**
	 *  File that contains the list of folder/filename pairs that should be ignored
	 *  (such as classes/MyTestClass.cls)
	 *  Notice that these files should include their standard folder name and extensions
	**/
	public void setIgnoreFile( String ignoreFileAddr ){
		this.ignoreFile = new File( ignoreFileAddr );
	}
	
	/**
	 *  Path to the package that should be merged into.
	 *  <p>Please note that this applies in place</p>
	**/
	public void setPackage( String packageAddr ){
		this.packageFile = new File( packageAddr );
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
