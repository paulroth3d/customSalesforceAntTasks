package com.modelmetrics.meta.profile;

import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.XML_Util;

import java.util.HashMap;

/**
 *  Object that facilitates working with object permissions from a profile
 **/
public class ProfileObjectPermissions implements ProfilePermissions {
	
	public static final String NODE_ALLOW_CREATE = "allowCreate";
	public static final String NODE_ALLOW_DELETE = "allowDelete";
	public static final String NODE_ALLOW_EDIT = "allowEdit";
	public static final String NODE_ALLOW_READ = "allowRead";
	public static final String NODE_MODIFY_ALL = "modifyAllRecords";
	public static final String NODE_OBJECT = "object";
	public static final String NODE_VIEW_ALL = "viewAllRecords";
	
	public boolean allowCreate;
	public boolean allowDelete;
	public boolean allowEdit;
	public boolean allowRead;
	public boolean modifyAllRecords;
	public boolean viewAllRecords;
	public String object;
	
	public ProfileObjectPermissions(){
	}
	
	public String getNodeType(){
		return( ProfileUtil.NODE_OBJECT_PERMISSIONS );
	}
	
	public String getName(){
		return( this.object );
	}
	
	public Boolean load( HashMap<String,String> nodeMap ){
		if( nodeMap != null ){
			if( nodeMap.containsKey( NODE_ALLOW_CREATE )) this.allowCreate = XML_Util.parseStringToBoolean( nodeMap.get( NODE_ALLOW_CREATE ));
			if( nodeMap.containsKey( NODE_ALLOW_DELETE )) this.allowDelete = XML_Util.parseStringToBoolean( nodeMap.get( NODE_ALLOW_DELETE ));
			if( nodeMap.containsKey( NODE_ALLOW_EDIT )) this.allowEdit = XML_Util.parseStringToBoolean( nodeMap.get( NODE_ALLOW_EDIT ));
			if( nodeMap.containsKey( NODE_ALLOW_READ )) this.allowRead = XML_Util.parseStringToBoolean( nodeMap.get( NODE_ALLOW_READ ));
			if( nodeMap.containsKey( NODE_MODIFY_ALL )) this.modifyAllRecords = XML_Util.parseStringToBoolean( nodeMap.get( NODE_MODIFY_ALL ));
			if( nodeMap.containsKey( NODE_VIEW_ALL )) this.viewAllRecords = XML_Util.parseStringToBoolean( nodeMap.get( NODE_VIEW_ALL ));
			if( nodeMap.containsKey( NODE_OBJECT )) this.object = nodeMap.get( NODE_OBJECT );
			return( true );
		} else {
			return( false );
		}
	}
	
	public String toString(){
		String result = "";
		result += allowCreate ? "C" : "_";
		result += allowRead ? "R" : "_";
		result += allowEdit ? "U" : "_";
		result += allowDelete ? "D" : "_";
		result += modifyAllRecords ? "M" : "_";
		result += viewAllRecords ? "V" : "_";
		return( result );
	}
}
