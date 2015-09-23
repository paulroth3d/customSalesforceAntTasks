package com.modelmetrics.meta.profile;

import java.util.HashMap;

public class ProfilePermissionCollection {
	
	public String DEFAULT_TYPE = "Profile";
	
	public String profileName;
	public String type;
	
	public HashMap<String,ProfilePermissions> profilePermissions;
	
	public ProfilePermissionCollection( String profileName, String type ){
		this.profileName = profileName;
		this.type = type;
		if( this.type == null ){
			this.type = DEFAULT_TYPE;
		}
		this.profilePermissions = new HashMap<String,ProfilePermissions>();
	}
	
	public ProfilePermissions put( String key, ProfilePermissions permissions ){
		return( this.profilePermissions.put( key, permissions ));
	}
	
	public Boolean containsKey( String key ){
		return( this.profilePermissions.containsKey(key ));
	}
	
	public ProfilePermissions get( String key ){
		return( this.profilePermissions.get( key ));
	}
}

/*
public interface TypePermissions {
	
	public void init( String name );
	
	ProfilePermissions put( String key, ProfilePermissions permissions );
	
	Boolean containsKey( String key );
	
	ProfilePermissions get( String key );
}
*/
