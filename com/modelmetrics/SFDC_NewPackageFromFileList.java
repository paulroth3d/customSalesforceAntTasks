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
 *  Uses a list of files and creates a package
**/
public class SFDC_NewPackageFromFileList extends Task {
	
	public static final String ERR_PACKAGE_COULD_NOT_BE_CREATED = "Package could not be created";
	
	public static final String ERR_INVALID_FILE_LIST = "Invalid file list. Expecting at least FOLDERNAME/FILENAME, but found:";
	
	/** version of the package **/
	private String version;
	
	/** whether the code is chatty or not **/
	private Boolean isChatty;
	
	/** location of the fileList **/
	private File fileList;
	
	/** location of the package to write to **/
	private File targetFile;
	
	private String NEWLINE;
	
	
	public SFDC_NewPackageFromFileList(){
		this.version = "25.0";
		this.isChatty = false;
		
		this.targetFile = null;
		this.fileList = null;
		
		this.NEWLINE = System.getProperty( "line.separator" );
	}
	
	public void execute() throws BuildException {

		validateArguments();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = null;
		String[] lineFolderList = null;
		
		String folderName = null;
		String fileName = null;
		String metaFolderName = null;
		String strippedFileName = null;
		
		String packageDirPackage = this.targetFile.getPath();
		
		SFDC_NewPackage newPackage = new SFDC_NewPackage();
		newPackage.setVersion( this.version );
		newPackage.setTarget( packageDirPackage );
		newPackage.execute();
		
		SFDC_AddPackageMember addMemberTask = new SFDC_AddPackageMember();
		addMemberTask.setSourceFile( packageDirPackage );
		addMemberTask.setTargetFile( packageDirPackage );
		addMemberTask.setIsChatty( this.isChatty );
		
		try {
			reader = new BufferedReader( new FileReader( this.fileList ));
			
			while( (line = reader.readLine()) != null ){
				if( line != null ){
					line = line.trim();
				} else {
					line = "";
				}
				
				if( isChatty ) System.out.println( "looking at:" + line );
				
				if( line != null && !("".equals( line )) ){
					
					lineFolderList = line.split( "/" );
					if( lineFolderList != null && lineFolderList.length >= 2 ){
						if( isChatty ) System.out.println( "found folder and fileName" );
						
						folderName = lineFolderList[ lineFolderList.length - 2];
						fileName = lineFolderList[ lineFolderList.length - 1];
						
						if( isChatty ) System.out.println( "old: " + folderName + "/" + fileName );
						
						metaFolderName = PackageUtil.convertFolderToMeta( folderName );
						strippedFileName = FileUtil.removeExtension( fileName );
						
						if( isChatty ) System.out.println( "new: " + metaFolderName + "/" + strippedFileName );
						
						addMemberTask.setMetadataType( metaFolderName );
						addMemberTask.setMember( strippedFileName );
						addMemberTask.execute();
						
						System.out.println( "added " + metaFolderName + "/" + strippedFileName );
					} else {
						throw( new BuildException( ERR_INVALID_FILE_LIST + "[" + line + "]" ));
					}
				}
			}
			
		} catch( BuildException err ){
			throw( err );
		} catch( Exception err ){
			throw( new BuildException( ERR_PACKAGE_COULD_NOT_BE_CREATED + NEWLINE + err.getMessage() ));
		} finally {
			try {
				if( reader != null ) reader.close();
				if( writer != null ) writer.close();
			} catch( Exception err2 ){}
		}
	}
	
	private void validateArguments(){
		FileUtil.checkCanRead( fileList );
		FileUtil.checkCanRead( targetFile );
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
	public void setFileList( String fileListAddress ){
		this.fileList = new File( fileListAddress );
	}
	
	public void setTarget( String targetAddr ){
		this.targetFile = new File( targetAddr );
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}