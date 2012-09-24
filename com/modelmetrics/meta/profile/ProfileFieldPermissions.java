package com.modelmetrics.meta.profile;

import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.XML_Util;

import java.util.HashMap;

public class ProfileFieldPermissions implements ProfilePermissions {
	
	public static final String NODE_READIBLE = "readable";
	public static final String NODE_EDITABLE = "editable";
	public static final String NODE_FIELD = "field";
	
	public String field;
	public boolean editable;
	public boolean readable;
	
	public ProfileFieldPermissions(){
	}
	
	public String getNodeType(){
		return( ProfileUtil.NODE_FIELD_PERMISSIONS );
	}
	
	public String getName(){
		return( this.field );
	}
	
	public Boolean load( HashMap<String,String> nodeMap ){
		if( nodeMap != null ){
			if( nodeMap.containsKey( NODE_FIELD )) this.field = nodeMap.get( NODE_FIELD );
			if( nodeMap.containsKey( NODE_EDITABLE )) this.editable = XML_Util.parseStringToBoolean( nodeMap.get( NODE_EDITABLE ));
			if( nodeMap.containsKey( NODE_READIBLE )) this.readable = XML_Util.parseStringToBoolean( nodeMap.get( NODE_READIBLE ));
			return( true );
		} else {
			return( false );
		}
	}
	
	public String toString(){
		String result = "";
		result += this.readable ? "R" : "_";
		result += this.editable ? "U" : "_";
		return( result );
	}
}
