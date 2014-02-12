package com.modelmetrics.meta.profile;

import com.modelmetrics.util.ProfileUtil;
import com.modelmetrics.util.XML_Util;

import java.util.HashMap;

public class ProfilePagePermissions implements ProfilePermissions {
	
	public static final String NODE_ENABLED = "enabled";
	public static final String NODE_PAGE = "apexPage";
	
	public String page;
	public boolean enabled;
	
	public ProfilePagePermissions(){
	}
	
	public String getNodeType(){
		return( ProfileUtil.NODE_PAGE_PERMISSIONS );
	}
	
	public String getName(){
		return( this.page );
	}
	
	public Boolean load( HashMap<String,String> nodeMap ){
		if( nodeMap != null ){
			if( nodeMap.containsKey( NODE_PAGE )) this.page = nodeMap.get( NODE_PAGE );
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
