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
 *  Task that reads a Salesforce Metadata package file.
**/
public class SFDC_PackageCollapser extends Task {
	
	/** path to the current package file **/
	private File sourceFile;
	
	/** path to the file to write out to **/
	private File targetFile;
	
	/** delimiter used to collapse the package **/
	private String delimiter;
	
	public SFDC_PackageCollapser(){
		this.delimiter = PackageUtil.DELIMITER;
	}
	
	public void execute() throws BuildException {
		FileUtil.checkCanRead( sourceFile );
		FileUtil.checkCanWrite( targetFile );
		
		Document doc = XML_Util.parseXML_Document( sourceFile );
		if( doc == null ) return;
		
		String NEWLINE = System.getProperty("line.separator");
		
		BufferedWriter out = null;
		try {
			log( "found " + doc.getDocumentURI() );
			
			out = new BufferedWriter( new FileWriter( this.targetFile ));
			
			NodeList nodes = doc.getElementsByTagName( PackageUtil.TAG_TYPES );
			Node node = null;
			Map<String,LinkedList<String>> map = null;
			String type = null, folderType = null, fileExtension = "";
			Iterator<String> itr = null;
			LinkedList<String> list = null;
			
			for( int i = 0; i < nodes.getLength(); i++ ){
				node = nodes.item(i);
				map = XML_Util.createTextNodeMultiMap( node );
				//System.out.println( "map:" + map );
				
				if( map != null && map.containsKey( PackageUtil.TAG_NAME ) && map.containsKey( PackageUtil.TAG_MEMBERS ) ){
					type = XML_Util.popMultiMap( map, PackageUtil.TAG_NAME );
					folderType = PackageUtil.convertMetaToFolder( type );
					if( folderType == null || ("").equals( folderType ) ){
						folderType = type;
					}
					fileExtension = PackageUtil.convertMetaToExtension( type );
					list = map.get( PackageUtil.TAG_MEMBERS );
					if( list != null ){
						itr = list.iterator();
						
						while( itr.hasNext() ){
							out.write( folderType + this.delimiter + itr.next() + fileExtension + NEWLINE );
						}
					}
				}
			}
		} catch( Exception err ){
			throw( new BuildException( "Error occurred while collapsing the package['" + this.sourceFile + "]:" + err ));
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
	
	public void setDelimiter( String delimiter ){
		this.delimiter = delimiter;
	}
}
