package com.modelmetrics.meta.profile;

import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.XML_Util;

import java.util.HashMap;

public class ProfileLayoutPermissions implements ProfilePermissions {
	
	public static final String NODE_LAYOUT = "layout";
	public static final String NODE_RECORD_TYPE = "recordType";
	
	public String recordType;
	public String layout;
	
	public ProfileLayoutPermissions(){
		this.recordType = null;
		this.layout = null;
	}
	
	public String getNodeType(){
		return( ProfileUtil.NODE_LAYOUT_PERMISSIONS );
	}
	
	public String getName(){
		return( this.layout );
	}
	
	public Boolean load( HashMap<String,String> nodeMap ){
		if( nodeMap != null ){
			if( nodeMap.containsKey( NODE_RECORD_TYPE )) this.recordType = nodeMap.get( NODE_RECORD_TYPE );
			if( nodeMap.containsKey( NODE_LAYOUT )) this.layout = nodeMap.get( NODE_LAYOUT );
			return( true );
		} else {
			return( false );
		}
	}
	
	public String toString(){
		String result = "";
		result += this.recordType != null ? "TRUE" : "FALSE";
		return( result );
	}
}
