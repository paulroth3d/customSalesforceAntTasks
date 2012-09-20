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
 *  Task that creates a new Package file
**/
public class SFDC_NewPackage extends Task {
	
	public static final String ERR_COULD_NOT_WRITE = "Could not write to file:";
	
	/** version of the package to write **/
	private String version;
	
	private File targetFile;
	
	public SFDC_NewPackage(){
		this.version = "25.0";
	}
	
	public void execute() throws BuildException {
		FileUtil.checkCanWrite( targetFile );
		
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter( new FileWriter( targetFile ));
			out.write( "<?xml version='1.0' encoding='UTF-8'?>" ); out.newLine();
			out.write( "<package xmlns='http://soap.sforce.com/2006/04/metadata'>" ); out.newLine();
			out.write( "	<version>" + this.version + "</version>" ); out.newLine();
			out.write( "</package>" ); out.newLine();
			
			out.close();
			
			System.out.println( "New package created at:" + targetFile.getPath() );
		} catch( Exception err ){
			try {
				if( out != null ) out.close();
			} catch( Exception err2 ){}
		}
	}
	
	public void setTarget( String targetAddr ){
		this.targetFile = new File( targetAddr );
	}
	
	//- getter setter
	public void setVersion( String version ){
		this.version = version;
	}
}
