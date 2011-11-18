package com.coolblue;

import com.coolblue.util.PackageUtil;

import com.coolblue.util.XML_Util;
import com.coolblue.util.PackageUtil;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 *  Converts a metdata folder name to the appropriate metadata name.
 *  <p>If no translation is found, then an empty string is used</p>
**/
public class ListTasks extends Task {
	
	/**
	 *  Converts a metadata folder name to a metadata name.
	 *  @param folderName (String)
	 *  @return String
	**/
	public void execute() throws BuildException {
		System.out.println( "SFDC_PackageCollapser : Collapses a salesforce metadata package file to prefix each member by the type" );
		System.out.println( "	@param sourceFile (String) - path to the file to collapse" );
		System.out.println( "	@param targetFile (String) - path to the file to output to" );
		System.out.println( "	@param delimiter (String:default '/') - delimiter to use between the metadata type and the member" );
		System.out.println( "	" );
		System.out.println( "SFDC_CollapsedPackageExpander : Task that expands a collapsed salesforce metadata package file assuming that what is before the delimiter is the type." );
		System.out.println( "	@param sourceFile (String) - path to the file to expand" );
		System.out.println( "	@param targetFile (String) - path to the file to output to" );
		System.out.println( "	@param delimiter (String:default'/') - delimiter to use between the metadata type and the member" );
		System.out.println( "" );
		System.out.println( "SFDC_ConvertMetadataToFolder : Converts a metdata name to the appropriate 'folder name' (like used in eclipse)" );
		System.out.println( "	(If no translation is found, then an empty string is used)" );
		System.out.println( "	@param metadata (String) - metadata type from salesforce" );
		System.out.println( "	@param targetProperty (String) - property to export the value out to" );
		System.out.println( "" );
		System.out.println( "SFDC_ConvertFolderToMetadata : Converts a metdata 'folder name' (like used in eclipse) to the appropriate folder name." );
		System.out.println( "	(If no translation is found, then an empty string is used)" );
		System.out.println( "	@param folderName (String) - name of the eclipse style folder" );
		System.out.println( "	@param targetProperty (String) - property to export the value out to" );
		System.out.println( "	" );
		System.out.println( "SFDC_AddPackageMember : Task that adds a member to a Salesforce Metadata package file." );
		System.out.println( "	If the metadata type does not exist then a new one is added" );
		System.out.println( "	Likewise, if the metadata/type exists then a new one is not added." );
		System.out.println( "	@param sourceFile (String) path of the current package file (file to read from)" );
		System.out.println( "	@param targetFile (String:default sourceFile) path of the package file to write out to (can be same or different)" );
		System.out.println( "	@param metadataType (String) - metadata type of the member" );
		System.out.println( "	@param memberToAdd (String) - name of the member to add" );
		System.out.println( "" );
		System.out.println( "SFDC_RemovePackageMember : Task that removes a member from a Salesforce Metadata package file." );
		System.out.println( "	If one does not exist then it prints a warning, but does not stop" );
		System.out.println( "	@param sourceFile (String) path of the current package file (file to read from)" );
		System.out.println( "	@param targetFile (String:default sourceFile) path of the package file to write out to (can be same or different)" );
		System.out.println( "	@param metadataType (String) - metadata type of the member" );
		System.out.println( "	@param memberToAdd (String) - name of the member to add" );
		System.out.println( "	" );
		System.out.println( "SFDC_RemovePackageType : Task that removes an entire Metadata package type" );
		System.out.println( "	@param sourceFile (String) path of the current package file (file to read from)" );
		System.out.println( "	@param targetFile (String:default sourceFile) path of the package file to write out to (can be same or different)" );
		System.out.println( "	@param metadataType (String) - metadata type of the member" );
	}
}
