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
public class SFDC_RemovePackageMember extends Task {
	
	public static final String ERROR_REMOVING = "Error occurred while removing package members";
	
	/** path to the current package file **/
	private File sourceFile;
	
	/** path to the file to write out to **/
	private File targetFile;
	
	/** metadata type to add it to **/
	private String metadataType;
	
	/** member to add **/
	private String memberToRemove;
	
	private Boolean isChatty;
	
	public SFDC_RemovePackageMember(){
		this.isChatty = false;
	}
	
	public void execute() throws BuildException {
		if( targetFile == null ) targetFile = sourceFile;
		
		FileUtil.checkCanRead( sourceFile );
		FileUtil.checkCanWrite( targetFile );
		
		PackageUtil.checkMetadataType( this.metadataType );
		PackageUtil.checkMetaMember( this.memberToRemove );
		
		Document doc = XML_Util.parseXML_Document( sourceFile );
		Node container = doc.getFirstChild();
		
		Boolean foundNode = false;
		try {
			NodeList nodes = doc.getElementsByTagName( PackageUtil.TAG_TYPES );
			Node node = null, targetNode = null, potentialWhitespace = null;
			for( int i = 0; i < nodes.getLength(); i++ ){
				node = nodes.item(i);
				targetNode = PackageUtil.findMemberNode( node, this.metadataType, this.memberToRemove );
				if( targetNode != null ){
					potentialWhitespace = targetNode.getNextSibling();
					if( XML_Util.isEmptyNode( potentialWhitespace )){
						node.removeChild(potentialWhitespace);
					}
					node.removeChild( targetNode );
					foundNode = true;
					//System.out.println( "found node!:" + targetNode );
				}
			}
			
			if( !foundNode ){
				System.out.println( "[" + this.metadataType + "/" + this.memberToRemove + "] does not exist" );
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
	
	public void setMember( String memberToRemove ){
		this.memberToRemove = memberToRemove;
		if( this.memberToRemove == null ) this.memberToRemove = "";
	}
	
	public void setIsChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
