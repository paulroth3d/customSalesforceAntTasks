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
	
	public static final String ERR_INVALID_FILE_LIST = "Entry does not appear to be a valid fileList metadata entry. Expecting at least FOLDERNAME/FILENAME, but found:";
	
	/** version of the package **/
	private String version;
	
	/** whether the code is chatty or not **/
	private Boolean isChatty;
	
	/** whether folder types that are unknown should throw an error or not **/
	private Boolean ignoreUnknown;
	
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
		this.ignoreUnknown = false;
		
		this.NEWLINE = System.getProperty( "line.separator" );
	}
	
	public void execute() throws BuildException {

		validateArguments();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = null;
		String[] lineFolderList = null;
		
		int folderIndex = 0;
		int fileIndex = 0;
		
		String folderName = null;
		String intermediary = null;
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
				
				if( line.endsWith( ".aura" ) ){
					line = line.substring( 0, line.lastIndexOf( ".aura" ));
				}
				
				lineFolderList = line.split( "\\bsrc/" );
				if( lineFolderList != null && lineFolderList.length == 2 ){
					line = lineFolderList[1];
				}
				
				if( isChatty ) System.out.println( "looking at:" + line );
				
				if( line == null || "".equals( line ) || line.trim().startsWith("#") ){
					if( isChatty ) System.out.println( "ignoring line[" + line + "]" );
				} else if( line.endsWith( ".xml" ) ){
				} else {
					
					lineFolderList = line.split( "/" );
					if( lineFolderList != null && lineFolderList.length >= 2 ){
						if( isChatty ) System.out.println( "found folder and fileName" );
						
						folderIndex = line.indexOf( '/' );
						fileIndex = line.lastIndexOf( "/" );
						if( folderIndex < fileIndex ){
							intermediary = line.substring( folderIndex + 1, fileIndex + 1 );
						} else {
							intermediary = "";
						}
						folderName = line.substring( 0, folderIndex ).trim();
						fileName = line.substring( fileIndex + 1 ).trim();
						
						if( isChatty ) System.out.println( "old: " + folderName + "/" + intermediary + fileName );
						
						if( PackageUtil.AURA_FOLDER.equals( folderName ) &&
							lineFolderList.length > 1
						){
							metaFolderName = PackageUtil.convertFolderToMeta( folderName, null );
							strippedFileName = lineFolderList[1];
							intermediary = "";
						} else {
						
							metaFolderName = PackageUtil.convertFolderToMeta( folderName, fileName );
							strippedFileName = FileUtil.removeExtension( folderName, fileName );
						}
							
						if( isChatty ) System.out.println( "new: " + metaFolderName + "/" + intermediary + strippedFileName );
						
						if( ("").equals( metaFolderName )){
							if( this.ignoreUnknown ){
								//System.out.println( "ignoring - unable to convert:" + metaFolderName );
								if( isChatty ) System.out.println( "ignoring - unable to convert:" + folderName );
							} else {
								throw( new BuildException( PackageUtil.ERROR_UNKNOWN_CONVERSION ));
							}
						} else {
							addMemberTask.setMetadataType( metaFolderName );
							addMemberTask.setMember( intermediary + strippedFileName );
							addMemberTask.execute();						
							if( isChatty ) System.out.println( "added " + metaFolderName + "/" + strippedFileName );
						}
					} else {
						System.out.println( ERR_INVALID_FILE_LIST + " - Ignoring:" + line );
					}
				}
			}
			
		} catch( BuildException err ){
			throw( err );
		} catch( Exception err ){
			//System.out.println( err );
			err.printStackTrace();
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
	
	/**
	 *  Whether unknown file types should be ignored.
	 *  @param ignoreUnknown (Boolean) true will skip any files not understood. False will throw an error.s
	**/
	public void setIgnoreUnknownTypes( Boolean ignoreUnknown ){
		this.ignoreUnknown = ignoreUnknown;
	}
}
