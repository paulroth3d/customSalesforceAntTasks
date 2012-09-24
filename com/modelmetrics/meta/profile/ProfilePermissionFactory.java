package com.modelmetrics.meta.profile;

import org.apache.tools.ant.BuildException;

public class ProfilePermissionFactory {
	
	public static final String PROFILE_PAGE = "page";
	public static final String PROFILE_OBJECT = "object";
	
	public static ProfilePermissions build( String type ){
		if( PROFILE_PAGE.equals( type )){
			return( new ProfilePagePermissions() );
		} else if( PROFILE_OBJECT.equals( type )){
			return( new ProfileObjectPermissions() );
		} else {
			throw( new BuildException( "Attempted Crud matrix on unrecoginized [" + type + "], expected types:" + PROFILE_OBJECT + "," + PROFILE_PAGE ));
		}
	}
}
