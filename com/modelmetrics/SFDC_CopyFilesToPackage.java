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

public class SFDC_CopyFilesToPackage extends Task {
	
	public static final String ERR_PACKAGE_DIR_IS_FILE = "Package directory is file:";
	
	public static final String ERR_PACKAGE_DIR_CANNOT_BE_CREATED = "Package directory could not be created:";
	
	public static final String ERR_WHILE_COPYING = "Error occurred while copying files";
	public static final String ERR_WHILE_CREATING_PACKAGE = "Error occurred while creating a package";

	/** file that contains the list of files to copy **/
	private File listFile;
	
	/** Directory of the source files to copy from **/
	private File sourceDir;
	
	/** Directory to create the package in **/
	private File packageDir;
	
	/** salesforce package version **/
	private String version;
	
	/** whether the task is chatty **/
	private Boolean isChatty;
	
	private String NEWLINE;
	
	public SFDC_CopyFilesToPackage(){
		this.listFile = null;
		this.sourceDir = null;
		this.packageDir = null;
		this.version = "25.0";
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
		
		String folderName = null;
		String metaFolderName = null;
		String fileName = null;
		String strippedFileName = null;
		String targetFile = null;
		
		File fileToCheck = null;
		File fileDestination = null;
		File parentFile = null;
		
		String[] lineFolderFile = null;
		
		String sourceDirOffset = this.sourceDir.getPath() + "/";
		String packageDirOffset = this.packageDir.getPath() + "/";
		String packageDirPackage = packageDirOffset + "package.xml";
		
		SFDC_NewPackage newPackage = new SFDC_NewPackage();
		newPackage.setVersion( this.version );
		newPackage.setTarget( packageDirPackage );
		newPackage.execute();
		
		SFDC_AddPackageMember addMemberTask = new SFDC_AddPackageMember();
		addMemberTask.setSourceFile( packageDirPackage );
		addMemberTask.setTargetFile( packageDirPackage );
		addMemberTask.setIsChatty( this.isChatty );
		
		if( this.isChatty ) System.out.println( "sourceDirOffset:" + sourceDirOffset );
		
		try {
			reader = new BufferedReader( new FileReader( this.listFile ));
			
			while( (line= reader.readLine()) != null ){
				if( line != null ){
					line = line.trim();
				} else {
					line = "";
				}
				
				if( this.isChatty ) System.out.println( "checking for file:" + line );
				fileToCheck = new File( sourceDirOffset + line );
				if( !fileToCheck.exists() ){
					System.out.println( "cannot find file:" + fileToCheck.getPath() );
					continue;
				}
				
				lineFolderFile = line.split( "/" );
				if( lineFolderFile != null && lineFolderFile.length >= 2 ){
					
					folderName = lineFolderFile[ lineFolderFile.length - 2];
					fileName = lineFolderFile[ lineFolderFile.length - 1];
					
					if( isChatty ) System.out.println( "old: " + folderName + "/" + fileName );
					
					metaFolderName = PackageUtil.convertFolderToMeta( folderName );
					strippedFileName = FileUtil.removeExtension( fileName );
					
					fileDestination = new File( packageDirOffset + folderName + "/" + fileName );
					if( isChatty ) System.out.println( "new: " + fileDestination.getPath() );
					
					parentFile = fileDestination.getParentFile();
					if( isChatty ) System.out.println( "target parent directory:" + parentFile.getPath() );
					parentFile.mkdir();
					
					if( !FileUtil.copyFile( fileToCheck, fileDestination )){
						throw( new BuildException( ERR_WHILE_COPYING + NEWLINE + fileToCheck.getPath() + "	-	" + fileDestination.getPath() ));
					}
					
					System.out.println( "copied:" + fileToCheck.getPath() + NEWLINE + "to:" + fileDestination.getPath() );
					
					addMemberTask.setMetadataType( metaFolderName );
					addMemberTask.setMember( strippedFileName );
					addMemberTask.execute();
					
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
		FileUtil.checkCanRead( this.sourceDir );
		
		try {
			if( packageDir.exists() ){
				if( packageDir.isDirectory() ){
					if( !packageDir.delete() ){
						if( isChatty ) System.out.println( "directory could not be deleted:" + packageDir.getPath() );
						throw( new BuildException( ERR_PACKAGE_DIR_CANNOT_BE_CREATED + packageDir.getPath() ));
					}
				} else {
					if( isChatty ) System.out.println( "directory is file" );
					throw( new BuildException( ERR_PACKAGE_DIR_IS_FILE + packageDir.getPath() ));
				}
			}
			if( !packageDir.mkdir() ){
				if( isChatty ) System.out.println( "directory could not be created" );
				throw( new BuildException( ERR_PACKAGE_DIR_CANNOT_BE_CREATED + packageDir.getPath() ));
			}
		} catch( SecurityException err ){
			throw( new BuildException( ERR_PACKAGE_DIR_CANNOT_BE_CREATED + packageDir.getPath() + NEWLINE + err.getMessage() ));
		}
	}
	

	
	//-- getter/setters
	public void setListFile( String listFileAddr ){
		this.listFile = new File( listFileAddr );
	}
	
	public void setSourceDir( String sourceDirAddr ){
		this.sourceDir = new File( sourceDirAddr );
	}
	
	public void setPackageDir( String packageDirAddr) {
		this.packageDir = new File( packageDirAddr );
	}
	
	public void setVersion( String version ){
		this.version = version;
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
