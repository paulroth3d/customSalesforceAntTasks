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
 *  Task that adds a member to a Salesforce Metadata package file.
**/
public class SFDC_AddPackageMember extends Task {
	
	public static final String ERROR_ADDING = "Error occurred while adding package member";
	
	/** path to the current package file **/
	private File sourceFile;
	
	/** path to the file to write out to **/
	private File targetFile;
	
	/** metadata type to add it to **/
	private String metadataType;
	
	/** member to add **/
	private String memberToAdd;
	
	private Boolean isChatty;
	
	public SFDC_AddPackageMember(){
		this.isChatty = false;
	}
	
	public void execute() throws BuildException {
		
		if( targetFile == null ) targetFile = sourceFile;
		
		FileUtil.checkCanRead( sourceFile );
		FileUtil.checkCanWrite( targetFile );
		
		PackageUtil.checkMetadataType( this.metadataType );
		PackageUtil.checkMetaMember( this.memberToAdd );
		
		Document doc = XML_Util.parseXML_Document( sourceFile );
		if( doc == null ) return;
		
		String NEWLINE = System.getProperty("line.separator");
		
		Node container = doc.getFirstChild();
		
		try {
			NodeList nodes = doc.getElementsByTagName( PackageUtil.TAG_TYPES );
			Node node = null, targetNode = null, typeNode = null;
			String nodeName = null;
			for( int i = 0; i < nodes.getLength(); i++ ){
				node = nodes.item(i);
				
				targetNode = PackageUtil.findMemberNode( node, this.metadataType, this.memberToAdd );
				if( targetNode != null ){
					//-- already exists so don't add it again.
					System.out.println( "[" + this.metadataType + "/" + this.memberToAdd + "] already exists" );
					return;
				} else if( XML_Util.doesNodeContainTextNode( node, PackageUtil.TAG_NAME, this.metadataType )){
					typeNode = node;
				}
			}
			
			Node memberNode = doc.createElement( PackageUtil.TAG_MEMBERS );
			memberNode.setTextContent( memberToAdd );

			if( typeNode == null ){
				//-- create the type to contain the metadata
				container.appendChild( doc.createTextNode( "\t" ) );
				typeNode = doc.createElement( PackageUtil.TAG_TYPES );
				container.appendChild( typeNode );
				
				typeNode.appendChild( doc.createTextNode( NEWLINE + "\t\t" ));
				Node nameNode = doc.createElement( PackageUtil.TAG_NAME );
				nameNode.setTextContent( metadataType );
				typeNode.appendChild( nameNode );
				
				container.appendChild( doc.createTextNode( NEWLINE ));
				
				typeNode.appendChild( doc.createTextNode( NEWLINE + "\t\t" ) );
			} else {
				typeNode.appendChild( doc.createTextNode( "\t" ) );
			}
			
			typeNode.appendChild( memberNode );
			typeNode.appendChild( doc.createTextNode( NEWLINE + "\t" ) );

		} catch( Exception err ){
			throw( new BuildException( ERROR_ADDING ));
		}
		
		String xmlString = XML_Util.documentToString( doc );
		if( xmlString == null ) return;
		
		//System.out.println( xmlString );
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter( new FileWriter( this.targetFile ));
			out.write( xmlString );
		} catch( Exception err ){
			throw( new BuildException( FileUtil.ERROR_CANNOT_WRITE + targetFile ));
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
	
	public void setMember( String memberToAdd ){
		this.memberToAdd = memberToAdd;
		if( this.memberToAdd == null ) this.memberToAdd = "";
	}
	
	public void setIsChatty( Boolean isChatty ){
		this.isChatty = isChatty;
	}
}
