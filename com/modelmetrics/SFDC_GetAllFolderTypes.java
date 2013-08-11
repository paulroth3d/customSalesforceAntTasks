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
 *  Creates a CSV of all the folder types known
**/
public class SFDC_GetAllFolderTypes extends Task {
	
	/** target property to set the value into **/
	private String targetProperty;
	
	/**
	 *  Converts a metadata folder name to a metadata name.
	**/
	public void execute() throws BuildException {
		String[] results = PackageUtil.getAllFolderTypes();
		String result = PackageUtil.convertArrayToString( results );
		//System.out.println( result );
		Project p = getProject();
		p.setProperty( this.targetProperty, result );
	}
	
	//-- getter setter
	public void setTargetProperty( String targetProperty ){
		this.targetProperty = targetProperty;
	}
}
