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
public class Success extends Task {
	
	public static final String SUCCESS = "BUILD SUCCESSFUL";
	public static final String ABORT = "BUILD HALTED";
	
	private String msg = null;
	
	private Boolean isAbort = false;
	
	/**
	 *  Converts a metadata folder name to a metadata name.
	 *  @param folderName (String)
	 *  @return String
	**/
	public void execute() throws BuildException {
		if( this.isAbort == true ){
			System.out.println( ABORT );
		} else {
			System.out.println( SUCCESS );
		}
		
		if( this.msg != null ){
			System.out.println( this.msg );
		}
		System.exit(0);
	}
	/*
	public void addText( String msg ){
		if( msg == null ){
			msg = "";
		}
		this.msg = msg;
	}
	*/
	
	public void setIsAbort( Boolean isAbort ){
		this.isAbort = isAbort == true;
	}
	
	public void setMsg( String msg ){
		this.msg = msg;
	}
	
	public void addText( String msg ){
		this.msg = getProject().replaceProperties(msg);
	}
}
