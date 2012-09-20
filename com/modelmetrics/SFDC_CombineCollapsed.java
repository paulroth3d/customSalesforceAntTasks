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
 *  Task that combines the second package into the first package
**/
public class SFDC_CombineCollapsed extends Task {
	
	public static final String ERROR_WHILE_COMPARING = "Error while comparing";
	
	/** first file to compare with **/
	private File firstFile;
	
	/** second file to compare against **/
	private File secondFile;
	
	/** path to the file to write out to **/
	private File targetFile;
	
	private Boolean isChatty;
	
	public SFDC_CombineCollapsed(){
		this.isChatty = false;
	}
	
	public void execute() throws BuildException {
		FileUtil.checkCanRead( firstFile );
		FileUtil.checkCanRead( secondFile );
		FileUtil.checkCanWrite( targetFile );
		
		String NEWLINE = System.getProperty( "line.separator" );
		
		//-- allow comparison between case insensitive strings
		HashMap<String,String> lineMap = new HashMap<String,String>();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = null;
		String lowerLine = null;
		Iterator<String> itrStr = null;
		
		try {
			reader = new BufferedReader( new FileReader( this.firstFile ));
			writer = new BufferedWriter( new FileWriter( this.targetFile ));
			
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
				line = line.trim();
				
				if( !("").equals( line )){
					lowerLine = line.toLowerCase();
					if( isChatty ) System.out.println( "checking[" + lowerLine + "," + line + "]" );
					if( lineMap.containsKey( lowerLine )){
						if( isChatty ) System.out.println( "line FOUND     [" + lowerLine + "]" );
					} else {
						System.out.println( "adding [" + lowerLine + "]" );
						lineMap.put( lowerLine, line );
					}
				}
			}
			
			if( isChatty ) System.out.println( "left with [" + lineMap.size() + "] lines" );
			
			itrStr = lineMap.values().iterator();
			while( itrStr.hasNext() ){
				line = itrStr.next();
				writer.write( line + NEWLINE );
			}
			
			if( reader != null ){
				reader.close();
				reader = null;
			}
			if( writer != null ){
				writer.close();
				writer = null;
			}
			
		} catch( Exception err ){
			throw( new BuildException( ERROR_WHILE_COMPARING ));
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
	public void setFirstFile( String sourcePath ){
		this.firstFile = new File( sourcePath );
	}
	
	public void setSecondFile( String sourcePath ){
		this.secondFile = new File( sourcePath );
	}
	
	public void setTargetFile( String targetPath ){
		this.targetFile = new File( targetPath );
	}
	
	public void setChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
