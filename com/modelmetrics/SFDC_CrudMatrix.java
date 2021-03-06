package com.modelmetrics;

import java.net.URLDecoder;

import com.modelmetrics.util.XML_Util;
import com.modelmetrics.util.PackageUtil;
import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.FileUtil;

import com.modelmetrics.meta.profile.ProfilePermissions;
import com.modelmetrics.meta.profile.ProfilePermissionCollection;
import com.modelmetrics.meta.profile.ProfilePermissionFactory;

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
	
	public static final String SUFFIX_PROFILE = ".profile";
	public static final String SUFFIX_PERMISSION_SET = ".permissionset";
	
	public static final String TYPE_PROFILE = "Profile";
	public static final String TYPE_PERMISSION_SET = "PermissionSet";
	
	/** path to the directory that contains the profiles **/
	private File profileDirectory;
	
	/** path to the directory that contains the permission sets **/
	private File permissionDirectory;
	
	/** path to the result file **/
	private File resultFilePath;
	
	/** list of profile permissions **/
	private LinkedList< ProfilePermissionCollection > permissionListings;
	
	/** list of all objects encountered **/
	private HashSet<String> columnSet;
	
	private String type;
	
	public SFDC_CrudMatrix(){
		this.type = ProfilePermissionFactory.PROFILE_OBJECT;
	}
	
	public void execute() throws BuildException {
				
		FileUtil.checkCanWrite( resultFilePath );
		
		try {
			if( permissionDirectory == null ){
				//-- ignore for now
			} else if( !permissionDirectory.exists() ){
				permissionDirectory = null;
			} else {
				FileUtil.checkCanRead( permissionDirectory );
			}
		} catch( Exception err ){
			permissionDirectory = null;
		}
		
		if( profileDirectory == null ){
			throw( new BuildException( "profileDirectory must be set" ));
		} else if( !profileDirectory.exists() ){
			throw( new BuildException( "profileDirectory does not exist[" + profileDirectory + "]" ));
		}
		
		this.permissionListings = new LinkedList< ProfilePermissionCollection >();
		this.columnSet = new HashSet<String>();
		
		//-- iterate through all the files in profileDirectory
		FileFilter fileFilter = new FileFilter(){
			public boolean accept( File f, String fileName ){
				if( fileName != null ) fileName = fileName.toLowerCase();
				
				return( fileName.endsWith( SUFFIX_PROFILE ) );
			}
			public boolean accept( File f ){
				String fileName = f.getName();
				return( fileName.endsWith( SUFFIX_PROFILE ));
			}
		};
		File[] profileFiles = profileDirectory.listFiles( fileFilter );
		for( File profileFile : profileFiles ){
			System.out.println( "found file[" + profileFile + "]" );
			parseProfile( profileFile );
		}
		
		//-- iterate through all the files in the PERMISSION directory
		if( permissionDirectory != null ){
			//System.out.println( "permission directory not null" );
			fileFilter = new FileFilter(){
				public boolean accept( File f, String fileName ){
					if( fileName != null ) fileName = fileName.toLowerCase();
					
					return( fileName.endsWith( SUFFIX_PERMISSION_SET ));
				}
				
				public boolean accept( File f ){
					String fileName = f.getName();
					return( fileName.endsWith( SUFFIX_PERMISSION_SET ) );
				}
			};
			
			//System.out.println( this.permissionListings );
			File[] permissionSetFiles = permissionDirectory.listFiles( fileFilter );
			for( File permissionSetFile : permissionSetFiles ){
				System.out.println( "found file[" + permissionSetFile + "]" );
				parseProfile( permissionSetFile );
			}
		}
		
		//-- construct the csv
		Iterator<String> itr;
		String currentColumn;
		Iterator<ProfilePermissionCollection> permissionItr;
		ProfilePermissionCollection currentPermissions;
		ProfilePermissions objectPermissions;
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter( new FileWriter( this.resultFilePath ));
			
			writer.write( "Profile Name" );
			writer.write( ",Type" );
			
			List<String> columnList = new ArrayList<String>( columnSet );
			Collections.sort( columnList );
			itr = columnList.iterator();
			
			while( itr.hasNext() ){
				writer.write( "," + URLDecoder.decode( itr.next() ) );
			}
			writer.newLine();
			
			
			permissionItr = this.permissionListings.iterator();
			while( permissionItr.hasNext() ){
				currentPermissions = permissionItr.next();
				writer.write( currentPermissions.profileName );
				writer.write( "," );
				writer.write( currentPermissions.type );
				
				itr = columnList.iterator();
				while( itr.hasNext() ){
					currentColumn = itr.next();
					writer.write( "," );
					if( currentPermissions.containsKey( currentColumn )){
						objectPermissions = currentPermissions.get( currentColumn );
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
	}
	
	private void parseProfile( File profileFile ){
		Document doc = XML_Util.parseXML_Document( profileFile );
		Node container = doc.getFirstChild();
		
		String type = null;
		Boolean foundNode = false;
		HashMap<String,String> nodeMap = null;
		ProfilePermissions objPerm = null;
		String profileType = TYPE_PROFILE;
		
		String profileName = profileFile.getName();
		if( profileName.endsWith( SUFFIX_PERMISSION_SET ) ){
			profileType = TYPE_PERMISSION_SET;
		} else {
			profileType = TYPE_PROFILE;
		}
		
		profileName = URLDecoder.decode( profileName.replaceAll( "\\.[^.]+", "" ));
		profileName = profileName.replaceAll( "_", " " );
		
		ProfilePermissionCollection permissions = new ProfilePermissionCollection( profileName, profileType );
		
		try {
			objPerm = ProfilePermissionFactory.build( this.type );
			
			NodeList nodes = doc.getElementsByTagName( objPerm.getNodeType() );
			Node node = null;
			
			for( int i = 0; i < nodes.getLength(); i++ ){
				node = nodes.item(i);
				
				objPerm = ProfilePermissionFactory.build( this.type );
				if( objPerm.load( XML_Util.createTextNodeMap( node ))){
					//-- imported successfully
					permissions.put( objPerm.getName(), objPerm );
					columnSet.add( objPerm.getName() );
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
	
	public void setPermissionDirectory( String path ){
		this.permissionDirectory = new File( path );
	}
	
	public void setResultFilePath( String path ){
		this.resultFilePath = new File( path );
	}
	
	public void setType( String type ){
		this.type = type;
	}
}
