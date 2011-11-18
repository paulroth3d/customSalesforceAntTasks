package com.modelmetrics;

import com.modelmetrics.util.XML_Util;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
http://download.oracle.com/javase/6/docs/api/org/w3c/dom/Document.html
http://download.oracle.com/javase/6/docs/api/org/w3c/dom/NodeList.html
http://download.oracle.com/javase/6/docs/api/org/w3c/dom/Node.html
http://download.oracle.com/javase/6/docs/api/org/w3c/dom/Element.html
**/

public class SFDC_XML_Writer extends Task {
	private String filePath;
	
	public static final String APEX_CLASS = "apexClass";
	public static final String META_ENABLED = "enabled";
	
	public void execute() throws BuildException {
		//System.out.println( msg );
		//log( "here is project '" + getProject().getProperty( "some.property" ) + "'" );
		//Project p = getProject();
		
		Project p = getProject();
		String val = p.getProperty( "some.property" );
		String nodeName = null;
		log( "reading from:" + this.filePath );
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( new File( this.filePath ));
			log( "found file:" + doc.getDocumentURI() );
			
			NodeList nodes = doc.getElementsByTagName( "classAccesses" );
			Node node = null;
			for( int i =0; i < nodes.getLength(); i++ ){
				node = nodes.item( i );
				HashMap<String,String> nodeMap = XML_Util.createTextNodeMap( node );
				//log( "found node!" + node.getNodeName() + ":" + node.getNodeType() + ":" + node.getNodeValue() + ":" + node.hasChildNodes()+ ":" + node.getTextContent() );
				log( "map:" + nodeMap.toString() );
				
			}
		} catch( Exception e ){
			throw( new BuildException( "error occurred while reading file:" + e ));
		}
	}
	
	public void setFilePath( String path ){
		this.filePath = path;
	}
}
