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
 * Simple halt message to make it clearer
**/
public class Halt extends Task {
	
	public static final String ABORT = "BUILD HALTED";
	
	private String msg = null;
	
	/**
	 *  Converts a metadata folder name to a metadata name.
	 *  @param folderName (String)
	 *  @return String
	**/
	public void execute() throws BuildException {
		System.out.println( ABORT );
		
		if( this.msg != null ){
			System.out.println( this.msg );
		}
		System.exit(0);
	}
	
	public void setMsg( String msg ){
		this.msg = msg;
	}
	
	public void addText( String msg ){
		this.msg = getProject().replaceProperties(msg);
	}
}
