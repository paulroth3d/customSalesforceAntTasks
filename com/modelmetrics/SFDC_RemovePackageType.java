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
 *  Task that removes a member from Salesforce Metadata package file.
**/
public class SFDC_RemovePackageType extends Task {
	
	public static final String ERROR_REMOVING = "Error occurred while removing package type";
	
	/** path to the current package file **/
	private File sourceFile;
	
	/** path to the file to write out to **/
	private File targetFile;
	
	/** metadata type to add it to **/
	private String metadataType;
	
	private Boolean isChatty = false;
	
	public SFDC_RemovePackageType(){
		this.isChatty = false;
	}
	
	public void execute() throws BuildException {
		FileUtil.checkCanRead( sourceFile );
		FileUtil.checkCanWrite( targetFile );
		
		PackageUtil.checkMetadataType( this.metadataType );
		
		Document doc = XML_Util.parseXML_Document( sourceFile );
		Node container = doc.getFirstChild();
		
		String type = null;
		Boolean foundNode = false;
		try {
			NodeList nodes = doc.getElementsByTagName( PackageUtil.TAG_TYPES );
			Node node = null, targetNode = null, potentialWhitespace = null;
			for( int i = 0; i < nodes.getLength(); i++ ){
				node = nodes.item(i);
				if( XML_Util.doesNodeContainTextNode( node, PackageUtil.TAG_NAME, this.metadataType ) ){
					//-- this is the node
					potentialWhitespace = node.getNextSibling();
					if( XML_Util.isEmptyNode( potentialWhitespace )){
						container.removeChild(potentialWhitespace);
					}
					container.removeChild( node );
					foundNode = true;
				}
			}
			
			if( !foundNode ){
				System.out.println( "[" + this.metadataType + "] does not exist" );
				return;
			}
		} catch( Exception err ){
			throw( new BuildException( ERROR_REMOVING ));
		}
		
		String xmlString = XML_Util.documentToString( doc );
		if( xmlString == null ) return;
		
		//System.out.println( xmlString );
		
		BufferedWriter out;
		try {
			out = new BufferedWriter( new FileWriter( this.targetFile ));
			out.write( xmlString );
		} catch( Exception err ){
			throw( new BuildException( FileUtil.ERROR_CANNOT_WRITE + this.targetFile ));
		}
		try {
			if( out != null ) out.close();
		} catch( Exception err ){}
	}
	
	//-- getter/setters
	public void setSourceFile( String sourcePath ){
		this.sourceFile = new File( sourcePath );
	}
	
	public void setTargetFile( String targetPath ){
		this.targetFile = new File( targetPath );
	}
	
	public void setMetadataType( String metadataType ){
		this.metadataType = metadataType;
		if( this.metadataType == null ) this.metadataType = "";
	}
	
	public void setIsChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
