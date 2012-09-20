package com.modelmetrics;

import com.modelmetrics.util.XML_Util;
import com.modelmetrics.util.PackageUtil;
import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.FileUtil;

import com.modelmetrics.meta.profile.ProfileObjectPermissions;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 *  Task that creates a CRUD matrix of the profiles specified in a specific
 *  directory
 **/
public class SFDC_CrudMatrix extends Task {
	
	public static final String ERROR_PROCESSING = "Error occurred while processing:";
	
	/** path to the directory that contains the profiles **/
	private File profileDirectory;
	
	/** path to the result file **/
	private File resultFilePath;
	
	/** list of profile permissions **/
	private LinkedList< ProfilePermissions > permissionListings;
	
	/** list of all objects encountered **/
	private HashSet<String> objectSet;
	
	public void execute() throws BuildException {
		FileUtil.checkCanWrite( resultFilePath );
		
		if( profileDirectory == null ){
			throw( new BuildException( "profileDirectory must be set" ));
		} else if( !profileDirectory.exists() ){
			throw( new BuildException( "profileDirectory does not exist[" + profileDirectory + "]" ));
		}
		
		this.permissionListings = new LinkedList< ProfilePermissions >();
		this.objectSet = new HashSet<String>();
		
		//-- iterate through all the files in profileDirectory
		FileFilter fileFilter = new FileFilter(){
			public boolean accept( File f, String fileName ){
				if( fileName != null ) fileName = fileName.toLowerCase();
				
				return( fileName.endsWith( ".profile" ) );
			}
			public boolean accept( File f ){
				String fileName = f.getName();
				return( fileName.endsWith( ".profile" ));
			}
		};
		File[] profileFiles = profileDirectory.listFiles( fileFilter );
		for( File profileFile : profileFiles ){
			System.out.println( "found file[" + profileFile + "]" );
			parseProfile( profileFile );
		}
		
		//System.out.println( this.permissionListings );
		
		//-- construct the csv
		Iterator<String> itr;
		String currentObject;
		Iterator<ProfilePermissions> permissionItr;
		ProfilePermissions currentPermissions;
		ProfileObjectPermissions objectPermissions;
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter( new FileWriter( this.resultFilePath ));
			
			writer.write( "Profile Name" );
			
			itr = objectSet.iterator();
			while( itr.hasNext() ){
				writer.write( "," + itr.next() );
			}
			writer.newLine();
			
			permissionItr = this.permissionListings.iterator();
			while( permissionItr.hasNext() ){
				currentPermissions = permissionItr.next();
				writer.write( currentPermissions.profileName );
				
				itr = objectSet.iterator();
				while( itr.hasNext() ){
					currentObject = itr.next();
					writer.write( "," );
					if( currentPermissions.containsKey( currentObject )){
						objectPermissions = currentPermissions.get( currentObject );
						writer.write( objectPermissions.toString() );
					} else {
						writer.write( "-" );
					}
				}
				
				writer.newLine();
			}
			
		} catch( Exception err ){
			throw( new BuildException( "Error occurred while writing out result:" + err ));
		} finally {
			try {
				if( writer != null ) writer.close();
			} catch( Exception err ){}
		}
		/*
		ProfileObjectPermissions objectPermissions = null;
		String objectName = null;
		Iterator<ProfileObjectPermissions> objectItr = this.permissionListings.iterator();
		while( objectItr.hasNext() ){
			objectPermissions = objectItr.next();
			line = objectPermissions.object;
			
			itr = objectSet.iterator();
			while( itr.hasNext() ){
				objectName = itr.next();
				
			}
		}
		*/
	}
	
	private void parseProfile( File profileFile ){
		Document doc = XML_Util.parseXML_Document( profileFile );
		Node container = doc.getFirstChild();
		
		String type = null;
		Boolean foundNode = false;
		HashMap<String,String> nodeMap = null;
		ProfileObjectPermissions objPerm = null;
		
		ProfilePermissions permissions = new ProfilePermissions( profileFile.getName() );
		
		try {
			NodeList nodes = doc.getElementsByTagName( ProfileUtil.NODE_OBJECT_PERMISSIONS );
			Node node = null;
			
			for( int i = 0; i < nodes.getLength(); i++ ){
				node = nodes.item(i);
				
				objPerm = new ProfileObjectPermissions();
				if( objPerm.load( XML_Util.createTextNodeMap( node ))){
					//-- imported successfully
					permissions.put( objPerm.object, objPerm );
					objectSet.add( objPerm.object );
				}
			}
			permissionListings.add( permissions );
		} catch( Exception err ){
			System.out.println( "error:" + err );
			throw( new BuildException( ERROR_PROCESSING + profileFile.getPath() ));
		}
	}
	
	//-- getters/setters
	public void setProfileDirectory( String path ){
		this.profileDirectory = new File( path );
	}
	
	public void setResultFilePath( String path ){
		this.resultFilePath = new File( path );
	}
	
	private class ProfilePermissions {
		String profileName;
		HashMap<String,ProfileObjectPermissions> objectPermissions;
		
		ProfilePermissions( String profileName ){
			this.profileName = profileName;
			this.objectPermissions = new HashMap<String,ProfileObjectPermissions>();
		}
		
		ProfileObjectPermissions put( String key, ProfileObjectPermissions permissions ){
			return( this.objectPermissions.put( key, permissions ) );
		}
		
		Boolean containsKey( String key ){
			return( this.objectPermissions.containsKey( key ));
		}
		
		ProfileObjectPermissions get( String key ){
			return( this.objectPermissions.get( key ));
		}
	}
}