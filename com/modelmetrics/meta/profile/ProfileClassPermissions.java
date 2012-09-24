package com.modelmetrics.meta.profile;

import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.XML_Util;

import java.util.HashMap;

public class ProfileClassPermissions implements ProfilePermissions {
	
	public static final String NODE_ENABLED = "enabled";
	public static final String NODE_CLASS = "apexClass";
	
	public String className;
	public boolean enabled;
	
	public ProfileClassPermissions(){
	}
	
	public String getNodeType(){
		return( ProfileUtil.NODE_CLASS_PERMISSIONS );
	}
	
	public String getName(){
		return( this.className );
	}
	
	public Boolean load( HashMap<String,String> nodeMap ){
		if( nodeMap != null ){
			if( nodeMap.containsKey( NODE_CLASS )) this.className = nodeMap.get( NODE_CLASS );
			if( nodeMap.containsKey( NODE_ENABLED )) this.enabled = XML_Util.parseStringToBoolean( nodeMap.get( NODE_ENABLED ));
			return( true );
		} else {
			return( false );
		}
	}
	
	public String toString(){
		String result = "";
		result += this.enabled;
		return( result );
	}
}
