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
	public static final String ERR_READING_IGNORE_FILE = "Error occurred while reading ignore file:";
	public static final String ERR_WHILE_COPYING = "Error occurred while copying files";
	public static final String ERR_WHILE_CREATING_PACKAGE = "Error occurred while creating a package";
	public static final String ERR_CANNOT_FIND_FILE = "cannot find file:";
	public static final String ERR_MISSING_EXTENSION = " --  is it missing an extension?";
	
	/** file that contains the list of files to copy **/
	private File listFile;
	
	/** file that contains the list of folder/files to ignore **/
	private File ignoreFile;
	
	/** set of folder/files to ignore **/
	private Set<String> ignoreSet;
	
	/** Directory of the source files to copy from **/
	private File sourceDir;
	
	/** Optional relative path from the source dir that can also be tried
		this allows full path location, such as "force/src/classes/MyClass.cls"
		or relative locations, such as "classes/MyClass.cls"
		to both be accepted as package lists.
		
		Allows the task to support finding files from
		'sourceDir + packageList line' or from
		'sourceDir + optionalSourceOffset + packageList Line' as both valid paths.
		
		<p>Occasionally, package lists can be generated based on different base
		directories.  Such as version control may provide file lists several
		levels above the 'src' directory.</p>
		
		<p>As implied, this is an optional property</p>
	**/
	private String optionalSourceOffset[];
	
	/** Directory to create the package in **/
	private File packageDir;
	
	/** salesforce package version **/
	private String version;
	
	/** whether the task is chatty **/
	private Boolean isChatty;
	
	/** whether to ignore missing files **/
	private Boolean shouldIgnoreMissingFiles;
	
	private String NEWLINE;
	
	public SFDC_CopyFilesToPackage(){
		this.listFile = null;
		this.shouldIgnoreMissingFiles = false;
		this.ignoreFile = null;
		this.sourceDir = new File( "." );
		this.packageDir = null;
		this.version = "25.0";
		this.isChatty = false;
		this.optionalSourceOffset = new String[0];
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
		File fileToCheck2 = null;
		File fileDestination = null;
		File parentFile = null;
		
		
		String[] lineFolderFile = null;
		String[] folderSplit = null;
		
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
				fileToCheck = new File( sourceDirOffset + line );
				
				folderSplit = line.split( "/" );
				
				if( folderSplit == null || folderSplit.length < 2 || line.trim().startsWith("#") ){
					if( isChatty ) System.out.println( "ignoring line[" + line + "]" );
					continue;
				} else if( fileToCheck.exists() ){
					
				} else {
					//-- file is not found by the source directory, check optional offsets
					fileToCheck = null;
					if( this.optionalSourceOffset != null ){
						for( int i = 0; i < this.optionalSourceOffset.length && fileToCheck == null; i++ ){
							optionalOffset = this.optionalSourceOffset[i];
							
							fileToCheck2 = new File( sourceDirOffset + optionalOffset + line );
							//System.out.println( "optionalOffset:" + optionalOffset );
							if( fileToCheck2.exists() ){
								if( isChatty ) System.out.println( "missing FORCE_OFFSET on line:" + line + ", adding:" + optionalOffset );
								line = optionalOffset + line;
								fileToCheck = fileToCheck2;
								break;
							} else {
								System.out.println( "did not find file:" + fileToCheck2.getPath() );
							}
						}
					}
				}
				
				if( fileToCheck == null ){
					if( this.shouldIgnoreMissingFiles ){
						System.out.println( ERR_CANNOT_FIND_FILE + fileToCheck.getPath() + ERR_MISSING_EXTENSION );
						continue;
					} else {
						throw( new BuildException( ERR_CANNOT_FIND_FILE + line + ERR_MISSING_EXTENSION ));
					}
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
					
					if( this.ignoreSet.contains( line.toLowerCase() )){
						if( isChatty ) System.out.println( "ignoring: file[" + line + "]" );
					} else if( fileName.indexOf( '.' ) < 0 ){
						if( isChatty ) System.out.println( "seems the following is a folder:" + packageDirOffset + folderName + "/" + fileName );
					} else {
						metaFolderName = PackageUtil.convertFolderToMeta( folderName, fileName );
						strippedFileName = FileUtil.removeExtension( fileName );
						if( metaFolderName == null || metaFolderName.isEmpty() ){
							throw( new BuildException( PackageUtil.ERROR_UNKNOWN_CONVERSION + folderName ));
						}
						//System.out.println( "converting metadatatype for:" + folderName + ":" + metaFolderName );
						
						fileDestination = new File( packageDirOffset + folderName + "/" + intermediary + fileName );
						if( isChatty ) System.out.println( "new: " + fileDestination.getPath() );
						//if( isChatty ) System.out.println( "packageDirOffset[" + packageDirOffset + "] folderName[" + folderName + "] intermediary[" + intermediary + "] fileName[" + fileName + "]" );
						
						parentFile = fileDestination.getParentFile();
						if( isChatty ) System.out.println( "target parent directory:" + parentFile.getPath() );
						parentFile.mkdirs();
						
						//if( isChatty ) System.out.println( "fileToCheck:" + fileToCheck.getPath() );
						//if( isChatty ) System.out.println( "fileDestination:" + fileDestination.getPath() );
						
						if( !FileUtil.copyFile( fileToCheck, fileDestination )){
							throw( new BuildException( ERR_WHILE_COPYING + NEWLINE + fileToCheck.getPath() + "	-	" + fileDestination.getPath() ));
						}
						
						if( !FileUtil.copyFile( new File( fileToCheck.getPath() + "-meta.xml" ), new File( fileDestination.getPath() + "-meta.xml" ), true )){
							//System.out.println( "could not copy meta file:" + fileToCheck.getPath() + "-meta.xml" );
						}
						
						if( isChatty ) System.out.println( "copied:" + fileToCheck.getPath() + NEWLINE + "to:" + fileDestination.getPath() );
						if( !isChatty ) System.out.println( "copied:" + fileDestination.getPath() );
						
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
	 *  Folder offset to be considered the 'Base' for the fileList
	 *  (Defaults to the current directory)
	**/
	public void setSourceDir( String sourceDirAddr ){
		this.sourceDir = new File( sourceDirAddr );
	}
	
	/**
	 *  Directory of where to create the resulting package
	**/
	public void setPackageDir( String packageDirAddr) {
		this.packageDir = new File( packageDirAddr );
	}
	
	/**
	 *  SFDC Version to use in the package
	**/
	public void setVersion( String version ){
		this.version = version;
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
	
	public void setIgnoreMissingFiles( Boolean shouldIgnoreMissingFiles ){
		this.shouldIgnoreMissingFiles = shouldIgnoreMissingFiles;
	}
	
	public void setOptionalSourceOffset( String offsetList ){
		this.optionalSourceOffset = new String[0];
		if( offsetList != null ){
			this.optionalSourceOffset = offsetList.split( "," );
			String offset = null;
			for( int i = 0; i < this.optionalSourceOffset.length; i++ ){
				offset = this.optionalSourceOffset[i];
				if( offset != null ){
					offset = offset.trim();
					if( !offset.endsWith( "/" )){
						offset = offset + "/";
					}
					this.optionalSourceOffset[i] = offset;
				}
			}
		}
	}
}
