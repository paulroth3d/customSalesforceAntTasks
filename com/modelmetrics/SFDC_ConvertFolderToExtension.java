package com.modelmetrics;

import com.modelmetrics.util.PackageUtil;

import com.modelmetrics.util.XML_Util;
import com.modelmetrics.util.PackageUtil;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 *  Converts a folder name to the appropriate metadata name.
 *  <p>If no translation is found, then an empty string is used</p>
**/
public class SFDC_ConvertFolderToExtension extends Task {
	
	/** metadata type **/
	private String folderName;
	
	/** target property to set the value into **/
	private String targetProperty;
	
	/**
	 *  Converts a metadata folder name to a metadata name.
	 *  @param folderName (String)
	 *  @return String
	**/
	public void execute() throws BuildException {
		
		if( folderName == null ){
			throw( new BuildException( "folderName is required" ));
		}
		if( targetProperty == null ){
			throw( new BuildException( "targetProperty is required" ));
		}
		
		Project p = getProject();
		
		String folder = PackageUtil.convertFolderToExtension( this.folderName );
		//System.out.println( "folder[" + folder + "]" );
		p.setProperty( this.targetProperty, folder );
	}
	
	//-- getter setter
	public void setFolderName( String folderName ){
		this.folderName = folderName;
	}
	
	public void setTargetProperty( String targetProperty ){
		this.targetProperty = targetProperty;
	}
}
