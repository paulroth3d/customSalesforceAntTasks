package com.modelmetrics.meta.profile;

import java.util.HashMap;

public class TypePermissions {
	
	public String profileName;
	public HashMap<String,ProfilePermissions> profilePermissions;
	
	public void init( String profileName ){
		this.profileName = profileName;
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
