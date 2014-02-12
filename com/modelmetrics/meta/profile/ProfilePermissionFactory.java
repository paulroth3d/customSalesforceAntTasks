package com.modelmetrics.meta.profile;

import org.apache.tools.ant.BuildException;

public class ProfilePermissionFactory {
	
	public static final String PROFILE_PAGE = "page";
	public static final String PROFILE_OBJECT = "object";
	public static final String PROFILE_CLASS = "class";
	public static final String PROFILE_FIELD = "field";
	public static final String PROFILE_LAYOUT = "layout";
	public static final String PROFILE_RECORD_TYPE = "recordType";
	public static final String PROFILE_RECORD_TYPE2 = "rt";
	
	public static ProfilePermissions build( String type ){
		if( PROFILE_PAGE.equals( type )){
			return( new ProfilePagePermissions() );
		} else if( PROFILE_OBJECT.equals( type )){
			return( new ProfileObjectPermissions() );
		} else if( PROFILE_CLASS.equals( type )){
			return( new ProfileClassPermissions() );
		} else if( PROFILE_FIELD.equals( type )){
			return( new ProfileFieldPermissions() );
		} else if( PROFILE_LAYOUT.equals( type )){
			return( new ProfileLayoutPermissions() );
		} else if( PROFILE_RECORD_TYPE.equals( type ) || PROFILE_RECORD_TYPE2.equals( type )){
			return( new ProfileRecordTypePermissions() );
		} else {
			throw( new BuildException( "Attempted Crud matrix on unrecoginized [" + type + "], expected types:" +
				PROFILE_OBJECT + "," +
				PROFILE_CLASS + "," +
				PROFILE_FIELD + "," +
				PROFILE_LAYOUT + "," +
				"[" + PROFILE_RECORD_TYPE2 + " or " + PROFILE_RECORD_TYPE + "]" +
				PROFILE_PAGE ));
		}
	}
}
