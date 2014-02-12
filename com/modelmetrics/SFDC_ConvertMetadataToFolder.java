package com.modelmetrics;

import com.modelmetrics.util.XML_Util;
import com.modelmetrics.util.PackageUtil;
import com.modelmetrics.util.FileUtil;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 *  Converts a metdata folder name to the appropriate metadata name.
 *  <p>If no translation is found, then an empty string is used</p>
**/
public class SFDC_ConvertMetadataToFolder extends Task {
	
	/** metadata type **/
	private String meta;
	
	/** target property to set the value into **/
	private String targetProperty;
	
	/**
	 *  Converts a metadata folder name to a metadata name.
	 *  @param folderName (String)
	 *  @return String
	**/
	public void execute() throws BuildException {
		
		if( meta == null ){
			throw( new BuildException( "meta is required" ));
		}
		if( targetProperty == null ){
			throw( new BuildException( "targetProperty is required" ));
		}
		
		Project p = getProject();
		
		String folder = PackageUtil.convertMetaToFolder( this.meta );
		p.setProperty( this.targetProperty, folder );
	}
	
	//-- getter setter
	public void setMetadata( String meta ){
		this.meta = meta;
	}
	
	public void setTargetProperty( String targetProperty ){
		this.targetProperty = targetProperty;
	}
}
