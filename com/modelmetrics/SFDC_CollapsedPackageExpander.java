package com.modelmetrics;

import com.modelmetrics.meta.pack.PackageMetadataGroup;

import com.modelmetrics.util.XML_Util;
import com.modelmetrics.util.PackageUtil;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 *  Task that expands a collapsed salesforce metadata package file
 *  assuming that what is before the delimiter is the type.
**/
public class SFDC_CollapsedPackageExpander extends Task {
	
	public static final String ERROR_WHILE_EXPANDING = "Error occurred while expanding:";
	
	/** path to the current package file **/
	private File sourceFile;
	
	/** path to the file to write out to **/
	private File targetFile;
	
	/** delimiter used to collapse the package **/
	private String delimiter;
	
	/** whether the expander is chatty (describes ignored lines) **/
	private Boolean isChatty;
	
	public SFDC_CollapsedPackageExpander(){
		this.isChatty = false;
		this.delimiter = PackageUtil.DELIMITER;
	}
	
	public void execute() throws BuildException {
		XML_Util.checkCanRead( sourceFile );
		XML_Util.checkCanWrite( targetFile );
		
		PackageUtil.checkDelimiter( this.delimiter );
		
		String NEWLINE = System.getProperty( "line.separator" );
		
		PackageMetadataGroup currentMember = new PackageMetadataGroup( null );
		
		String line = null, type = null, member = null;
		
		int delimiterIndex;
		int delimiterLength = this.delimiter.length();
		
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			writer = new BufferedWriter( new FileWriter( this.targetFile ));
			reader = new BufferedReader( new FileReader( this.sourceFile ));
			
			writer.write( "<?xml version='1.0'?>" + NEWLINE );
			writer.write( "<Package xmlns='http://soap.sforce.com/2006/04/metadata'>" + NEWLINE );
			
			while( (line = reader.readLine()) != null ){
				System.out.println( "found line[" + line + "]" );
				
				delimiterIndex = line.indexOf( this.delimiter );
				if( delimiterIndex < 0 ){
					if( this.isChatty ) System.out.println( "ignoring line[" + line + "]" );
				} else {
					System.out.println( "found delimiter" );
					type = line.substring( 0, delimiterIndex );
					member = line.substring( delimiterIndex + delimiterLength);
					
					if( currentMember.isSameType( type )){
						System.out.println( "isSameType" );
						currentMember.addMember( member );
					} else {
						System.out.println( "not same type" );
						//-- we're starting a new group
						writer.write( currentMember.toString() );
						
						currentMember = new PackageMetadataGroup( type );
						currentMember.addMember( member );
					}
				}
			}
			
			if( !currentMember.isEmpty() ){
				writer.write( currentMember.toString() );
			}
			
			writer.write( "\t<version>22.0</version>" + NEWLINE );
			writer.write( "</Package>" );
		} catch( Exception err ){
			throw( new BuildException( ERROR_WHILE_EXPANDING + err ));
		} finally {
			try {
				if( writer != null ) writer.close();
			} catch( Exception err ){}
			try {
				if( reader != null ) reader.close();
			} catch( Exception err ){}
		}
	}
	
	//-- getter/setters
	public void setSourceFile( String sourcePath ){
		this.sourceFile = new File( sourcePath );
	}
	
	public void setTargetFile( String targetPath ){
		this.targetFile = new File( targetPath );
	}
	
	public void setDelimiter( String delimiter ){
		this.delimiter = delimiter;
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
