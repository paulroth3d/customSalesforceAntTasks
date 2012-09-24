package com.modelmetrics.meta.profile;

import java.util.HashMap;

public interface ProfilePermissions {
	
	public String getNodeType();
	
	public String getName();

	public Boolean load( HashMap<String,String> nodeMap );

	public String toString();	
}
