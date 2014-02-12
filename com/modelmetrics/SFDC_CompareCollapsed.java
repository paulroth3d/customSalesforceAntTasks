package com.modelmetrics;

import com.modelmetrics.meta.pack.PackageMetadataGroup;

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
 *  Task that compares two files on a line by line comparison
**/
public class SFDC_CompareCollapsed extends Task {
	
	public static final String ERROR_WHILE_COMPARING = "Error while comparing";
	
	/** first file to compare with **/
	private File firstFile;
	
	/** second file to compare against **/
	private File secondFile;
	
	private Boolean isChatty;
	
	public SFDC_CompareCollapsed(){
		this.isChatty = false;
	}
	
	public void execute() throws BuildException {
		FileUtil.checkCanRead( firstFile );
		FileUtil.checkCanRead( secondFile );
		
		String NEWLINE = System.getProperty( "line.separator" );
		//System.out.println( "isChatty[" + isChatty + "]" );
		
		//-- allow comparison between case insensitive strings
		HashMap<String,String> lineMap = new HashMap<String,String>();
		
		BufferedReader reader = null;
		String line = null;
		String lowerLine = null;
		Iterator<String> itrStr = null;
		
		try {
			reader = new BufferedReader( new FileReader( this.firstFile ));
			
			while( (line = reader.readLine()) != null ){
				line = line.trim();
				if( !("").equals( line )){
					lowerLine = line.toLowerCase();
					lineMap.put( lowerLine, line);
					if( isChatty ) System.out.println( "found[" + lowerLine + "," + line + "]");
				}
			}
			
			if( isChatty ) System.out.println( "found [" + lineMap.size() + "] lines" );
			
			if( reader != null ){
				reader.close();
				reader = null;
			}
			
			//-- second file
			reader = new BufferedReader( new FileReader( this.secondFile ));
			
			while( (line = reader.readLine()) != null){
				line = line.trim().toLowerCase();
				
				if( !("").equals( line )){
					lowerLine = line.toLowerCase();
					if( isChatty ) System.out.println( "checking[" + lowerLine + "," + line + "]" );
					if( lineMap.containsKey( lowerLine )){
						System.out.println( "line FOUND     [" + lowerLine + "]" );
					} else {
						System.out.println( "line NOT FOUND [" + lowerLine + "]" );
					}
				}
			}
			
			if( reader != null ){
				reader.close();
				reader = null;
			}
			
		} catch( Exception err ){
			throw( new BuildException( ERROR_WHILE_COMPARING ));
		} finally {
			try {
				if( reader != null ) reader.close();
			} catch( Exception err ){}
		}
	}
	
	//-- getter/setters
	public void setFirstFile( String sourcePath ){
		this.firstFile = new File( sourcePath );
	}
	
	public void setSecondFile( String sourcePath ){
		this.secondFile = new File( sourcePath );
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
