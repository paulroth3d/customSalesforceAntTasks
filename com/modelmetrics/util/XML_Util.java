package com.modelmetrics.util;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import org.apache.tools.ant.BuildException;

/**
 *  Utility class for dealing with XML
**/
public class XML_Util {
	
	/** Error message provided if there are no child nodes **/
	public static final String ERROR_NO_CHILDREN = "No children were found";
	public static final String ERROR_NULL_NODE_NAME = "Child node cannot have a null name";
	public static final String ERROR_CANNOT_READ = "Cannot read from:";
	public static final String ERROR_CANNOT_WRITE = "Cannot write to:";
	
	/**
	 *  Checks if the sourceFile can be read from
	 *  @param sourceFile (File)
	 *  @throws BuildException - if the file could not be read from
	**/
	public static void checkCanRead( File sourceFile ) throws BuildException {
		if( !sourceFile.exists() || !sourceFile.canRead() ){
			throw( new BuildException( ERROR_CANNOT_READ + sourceFile ));
		}
	}
	
	/**
	 *  Checks if the target file can be written to
	 *  @param targetFile (File)
	 *  @throws BuildException - if the file cannot be written to
	**/
	public static void checkCanWrite( File targetFile ) throws BuildException {
		if( !targetFile.exists() ){
			try {
				targetFile.createNewFile();
			} catch( Exception err ){
				throw( new BuildException( ERROR_CANNOT_WRITE + targetFile ));
			}
		} else if( !targetFile.canWrite() ){
			throw( new BuildException( ERROR_CANNOT_WRITE + targetFile ));
		}
	}
	
	/**
	 *  Opens a new Document based on a filePath
	 *  @param filepath
	 *  @return Document
	**/
	public static Document parseXML_Document( File targetFile ) throws BuildException {
		Document result = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			result = db.parse( targetFile );
		} catch( Exception err ){
			throw( new BuildException( "could not open file:" + targetFile ));
		}
		return( result );
	}
	
	/**
	 *  Writes an xml document out to String
	 *  @param Document doc
	 *  @return String
	**/
	public static String documentToString( Document doc ) throws BuildException {
		String str = null;
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult( new StringWriter() );
			DOMSource source = new DOMSource( doc );
			transformer.transform( source, result );
			
			str = result.getWriter().toString();
		} catch( Exception err ){
			throw( new BuildException( "could not write xml:" + err ));
		}
		return( str );
	}
	
	/**
	 *  Determines if the document node is empty
	**/
	public static Boolean isEmptyNode( Node node ){
		if( node == null ){
			return( true );
		} else if( node.getNodeType() == Node.TEXT_NODE ){
			String nodeValue = node.getNodeValue();
			if( nodeValue == null ){
				return( true );
			} else {
				nodeValue = nodeValue.trim();
				return( ("").equals( nodeValue ));
			}
		} else {
			return( false );
		}
	}
	
	/**
	 *  Trims a string value safely (does not throw errors if given a null value)
	 *  @param str (String)
	 *  @return String - null if null is given or the trimmed string
	**/
	public static String safeTrim( String str ){
		if( str == null ){
			return( "" );
		} else {
			return( str.trim() );
		}
	}
	
	/**
	 *  Creates a hashMap of the textNodes underneath the current node,
	 *  using the nodeNames as the keys.
	 *  @param parentNode (Node)
	 *  @return HashMap<String,String> - map of the textNodes held underneath the parent node using the nodeNames as strings
	**/
	public static HashMap<String,String> createTextNodeMap( Node parentNode ){
		//-- qualify
		if( parentNode == null ){
			return( null );
		} else if( !parentNode.hasChildNodes() ){
			//-- for now, don't throw an exception since this should fail gracefully if no children are found
			//throw( new Exception( ERROR_NO_CHILDREN ));
			return( null );
		}
		
		HashMap<String,String> result = new HashMap<String,String>();
		Node attributeNode = parentNode.getFirstChild(), valueNode = null;
		String nodeValue = null;
		while( attributeNode != null ){
			if( !XML_Util.isEmptyNode( attributeNode )){
				nodeValue = null;
				if( !attributeNode.hasChildNodes() ){
					System.out.println( "unable to fetch value from node '" + parentNode.getNodeName() + "'" );
				} else {
					nodeValue = XML_Util.safeTrim( attributeNode.getTextContent() );
					result.put( attributeNode.getNodeName(), nodeValue );
				}
			}
			attributeNode = attributeNode.getNextSibling();
		}
		
		return( result );
	}
	
	/**
	 *  Creates a multi-map of the textNodes underneath the parentNode.
	 *  <p>A Multi-map is a map of linked lists, so conflicts do not overwrite
	 *  one another, they instead add them to the list</p>
	 *  @param parentNode (Node)
	 *  @return HashMap<String,LinkedList<String>> - map of multiple items
	**/
	public static HashMap<String,LinkedList<String>> createTextNodeMultiMap( Node parentNode ){
		if( parentNode == null ){
			return( null );
		} else if( !parentNode.hasChildNodes() ){
			return( null );
		}
		
		HashMap<String,LinkedList<String>> result = new HashMap<String,LinkedList<String>>();
		Node attributeNode = parentNode.getFirstChild();
		String nodeValue = null, nodeKey = null;
		while( attributeNode != null ){
			if( !XML_Util.isEmptyNode( attributeNode )){
				nodeValue = null;
				nodeKey = attributeNode.getNodeName();
				
				if( !attributeNode.hasChildNodes() ){
					System.out.println( "unable to fetch value from node '" + parentNode.getNodeName() + "'" );
				} else {
					nodeValue = XML_Util.safeTrim( attributeNode.getTextContent() );

					if( !result.containsKey( nodeKey )){
						result.put( nodeKey, new LinkedList<String>() );
					}
					result.get( nodeKey ).add( nodeValue );
				}
			}
			attributeNode = attributeNode.getNextSibling();
		}
		
		return( result );
	}
	
	/**
	 *  Retrieves the first value of a MultiMap index
	 *  @param multiMap (Map<String,LinkedList<String>>)
	 *  @param index (String)
	 *  @return String
	**/
	public static String popMultiMap( Map<String,LinkedList<String>> multiMap, String key ){
		if( multiMap == null ) return( null );
		
		if( multiMap.containsKey( key ) ){
			LinkedList<String> list = multiMap.get( key );
			if( list != null ){
				return( list.pop() );
			} else {
				return( null );
			}
		} else {
			return( null );
		}
	}
	
	/**
	 *  Finds a textNode in a parent node given a nodeName and nodeValue
	 *  @param parentNode (Node)
	 *  @param childNodeName (String)
	 *  @param childNodeValue (String)
	 *  @return Node - the matching node found
	**/
	public static Node findChildTextNode( Node parentNode, String childNodeName, String childNodeValue ) throws Exception {
		if( parentNode == null || !parentNode.hasChildNodes() ){
			return( null );
		} else if( childNodeName == null ){
			throw( new Exception( ERROR_NULL_NODE_NAME ));
		} else if( childNodeValue == null ){
			childNodeValue = "";
		}
		
		Node attributeNode = parentNode.getFirstChild();
		String nodeName = null, nodeValue = null;
		while( attributeNode != null ){
			if( !XML_Util.isEmptyNode( attributeNode )){
				nodeName = attributeNode.getNodeName();
				nodeValue = XML_Util.safeTrim( attributeNode.getTextContent() );
				//System.out.println( "checking attributeNode[" + nodeName + ":" + nodeValue + "][" + childNodeName + ":" + childNodeValue + "]");
				
				if( childNodeName.equals( nodeName ) && childNodeValue.equals( nodeValue )){
					return( attributeNode );
				}
			}
			attributeNode = attributeNode.getNextSibling();
		}
		
		return( null );
	}
	
	/**
	 *  Determines if a parent node contains a child textNode with a type and value.
	 *  @param parentNode (Node)
	 *  @param childNodeName (String)
	 *  @param childNodeValue (String)
	 *  @return Boolean - whether a childNode exists of the parentNode with the value of childNodeValue
	**/
	public static Boolean doesNodeContainTextNode( Node parentNode, String childNodeName, String childNodeValue ) throws Exception {
		Node resultNode = findChildTextNode( parentNode, childNodeName, childNodeValue );
		return( resultNode != null );
	}
}
