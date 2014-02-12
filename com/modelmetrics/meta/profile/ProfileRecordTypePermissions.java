package com.modelmetrics.meta.profile;

import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.XML_Util;

import java.util.HashMap;

public class ProfileRecordTypePermissions implements ProfilePermissions {
	
	public static final String NODE_VISIBLE = "visible";
	public static final String NODE_RECORD_TYPE = "recordType";
	
	public String recordType;
	public boolean visible;
	
	public ProfileRecordTypePermissions(){
	}
	
	public String getNodeType(){
		return( ProfileUtil.NODE_RECORD_TYPE_PERMISSIONS );
	}
	
	public String getName(){
		return( this.recordType );
	}
	
	public Boolean load( HashMap<String,String> nodeMap ){
		if( nodeMap != null ){
			if( nodeMap.containsKey( NODE_RECORD_TYPE )) this.recordType = nodeMap.get( NODE_RECORD_TYPE );
			if( nodeMap.containsKey( NODE_VISIBLE )) this.visible = XML_Util.parseStringToBoolean( nodeMap.get( NODE_VISIBLE ));
			return( true );
		} else {
			return( false );
		}
	}
	
	public String toString(){
		String result = "";
		result += this.visible ? "TRUE" : "FALSE";
		return( result );
	}
}
