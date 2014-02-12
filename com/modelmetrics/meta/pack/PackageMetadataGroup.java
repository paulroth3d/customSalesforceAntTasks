package com.modelmetrics.meta.pack;

import com.modelmetrics.util.PackageUtil;

import java.util.*;

/**
 *  metadata grouping in a package that contains the members for a specific salesforce
 *  metadata type.
**/
public class PackageMetadataGroup {
	/** metadata type that this group represents (ex: apexClass, profile, etc) **/
	public String type;
	
	/** Collection of members that belong to this group **/
	private Set<String> members;
	
	public PackageMetadataGroup( String type ){
		//-- @TODO
		this.type = type;
		this.members = new HashSet<String>();
	}
	
	/**
	 *  Returns the type of the metadata
	**/
	public String getType(){
		return( this.type );
	}
	
	/**
	 *  Determines whether the group is empty
	 *  @return Boolean
	**/
	public Boolean isEmpty(){
		return( this.type == null || this.members.size() < 1 );
	}
	
	/**
	 *  Determines if this group is the same as oldGroup
	 *  @param oldGroup
	 *  @return Boolean
	**/
	public Boolean isSameType( PackageMetadataGroup oldGroup ){
		return( oldGroup != null && oldGroup.type != null && oldGroup.type.equals( this.type ));
	}
	
	/**
	 *  Determines if this group is the same as oldType
	 *  @param type (String)
	 *  @return Boolean
	**/
	public Boolean isSameType( String type ){
		return( type != null && type.equals( this.type ));
	}
	
	/**
	 *  Clears all the members of this group
	**/
	public void clear(){
		this.members.clear();
	}
	
	/**
	 *  Cautiously adds a member to the metadata group, disallowing duplicates.
	 *  @param newMember (String)
	**/
	public void addMember( String newMember ){
		this.members.add( newMember );
	}
	
	/**
	 *  removes a member from the metadata group in a safe manner.
	 *  (i.e. safely removing a value even if it doesn't exist as a member)
	 *  @param member (String)
	**/
	public void removeMember( String member ){
		this.members.remove( member );
	}
	
	/**
	 *  Returns a list of the members of the group
	 *  @return Array<String> - members
	**/
	public String[] getMembers(){
		String[] members = new String[ this.members.size() ];
		this.members.toArray( members);
		return( members );
	}
	
	/**
	 *  Returns the group as a string
	 *  @return String
	**/
	public String toString(){
		//-- short circuit if nothing was set
		if( this.isEmpty() ) return( "" );
		
		String NEWLINE = System.getProperty( "line.separator" );
		
		String result = "\t<" + PackageUtil.TAG_TYPES + ">" + NEWLINE;
		
		Iterator<String> iterator = this.members.iterator();
		while( iterator.hasNext() ){
			result += "\t\t<" + PackageUtil.TAG_MEMBERS + ">" + iterator.next() + "</" + PackageUtil.TAG_MEMBERS + ">" + NEWLINE;
		}
		
		result += "\t\t<" + PackageUtil.TAG_NAME + ">" + this.type + "</" + PackageUtil.TAG_NAME + ">" + NEWLINE;
		result += "\t</" + PackageUtil.TAG_TYPES + ">" + NEWLINE;
		
		return( result );
	}
}
