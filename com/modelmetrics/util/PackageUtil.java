package com.modelmetrics.util;

import com.modelmetrics.util.XML_Util;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;

/**
 *  Object that represents a metadata package.
**/
public class PackageUtil {
	
	/** default delimiter to use when collapsing a package **/
	public static final String DELIMITER = "/";
	
	public static final String ERROR_EMPTY_DELIMITER = "Delimiter cannot be blank";
	
	public static final String ERROR_META_TYPE_BLANK = "MetadataType cannot be blank";
	public static final String ERROR_META_MEMBER_BLANK = "Member cannot be blank";
	
	public static final String ERROR_UNKNOWN_CONVERSION = "Unable to convert:";
	
	public static final String TAG_TYPES = "types";
	public static final String TAG_MEMBERS = "members";
	public static final String TAG_NAME = "name";
	
	public static final String SETTINGS = "settings";
	
	public static final String[][] SETTINGS_MAP = {
		{ "Account.settings", "AccountSettings" },
		{ "Activities.settings", "ActivitiesSettings" },
		{ "Address.settings", "AddressSettings" },
		{ "BusinessHours.settings", "BusinessHoursSettings" },
		{ "Case.settings", "CaseSettings" },
		{ "ChatterAnswers.settings", "ChatterAnswersSettings" },
		{ "Company.settings", "CompanySettings" },
		{ "Contract.settings", "ContractSettings" },
		{ "Entitlement.settings", "EntitlementSettings" },
		{ "Forecasting.settings", "ForecastingSettings" },
		{ "Ideas.settings", "IdeasSettings" },
		{ "Knowledge.settings", "KnowledgeSettings" },
		{ "LiveAgent.settings", "LiveAgentSettings" },
		{ "Mobile.settings", "MobileSettings" },
		{ "Opportunity.settings", "OpportunitySettings" },
		{ "Product.settings", "ProductSettings" },
		{ "Quote.settings", "QuoteSettings" },
		{ "Security.settings", "SecuritySettings" }
	};
	
	public static String KNOWN_SETTINGS_TYPES = null;
	
	static { 
		String[] knownTypes = new String[ SETTINGS_MAP.length];
		for( int i = 0; i < SETTINGS_MAP.length; i++ ){
			knownTypes[i] = SETTINGS_MAP[i][1];
		}
		KNOWN_SETTINGS_TYPES = Arrays.toString( knownTypes );
	}
	
	public static final String[][] META_MAP = {
		{ "ActionOverride", "objects", ".object" },
		{ "AnalyticSnapshot", "analyticsnapshots", ".analyticsnapshot" },
		{ "ApexClass", "classes", ".cls" },
		{ "ApexComponent", "components", ".component" },
		{ "ApexPage", "pages", ".page" },
		{ "ApexTrigger", "triggers", ".trigger" },
		{ "ArticleType", "objects", ".object" },
		{ "ConnectedApp", "connectedapps", "*.connectedapp" },
		{ "BusinessProcess", "objects", ".object" },
		{ "CustomApplication", "applications", ".app" },
		{ "CustomField", "objects", ".object" },
		{ "CustomLabels", "labels", ".label" },
		{ "CustomObject", "objects", ".object" },
		{ "CustomObjectTranslation", "objectTranslations", ".objectTranslation" },
		{ "CustomPageWebLink", "weblinks", ".weblink" },
		{ "CustomSite", "sites", ".site" },
		{ "CustomTab", "tabs", ".tab" },
		{ "Dashboard", "dashboards", ".dashboard" },
		{ "DataCategoryGroup", "datacategorygroups", ".datacategorygroup" },
		{ "Document", "document", "" },
		{ "EmailTemplate", "email", ".email" },
		{ "EntitlementTemplate", "entitlementTemplates", ".entitlementTemplate" },
		{ "EscalationRule", "escalationRules", ".escalationRules" },
		{ "FieldSet", "objects", ".object" },
		{ "FlexiPage", "flexipages", ".flexipage" },
		{ "Group", "groups", ".group" },
		{ "HomePageComponent", "homePageComponents", ".homePageComponent" },
		{ "HomePageLayout", "homePageLayouts", ".homePageLayout" },
		{ "Layout", "layouts", ".layout" },
		{ "Letterhead", "letterhead", ".letterhead" },
		{ "ListView", "objects", ".object" },
		{ "NamedFilter", "objects", ".object" },
		{ "PermissionSet", "permissionsets", ".permissionset" },
		{ "Portal", "portals", ".portal" },
		{ "Profile", "profiles", ".profile" },
		{ "QuickAction", "quickActions", ".quickAction" },
		{ "RecordType", "objects", ".object" },
		{ "RemoteSiteSetting", "remoteSiteSettings", ".remoteSiteSetting" },
		{ "Report", "reports", ".report" },
		{ "ReportType", "reportTypes", ".reportType" },
		{ "Role", "roles", ".role" },
		{ "Scontrol", "scontrols", ".scontrol" },
		{ "Settings", "settings", ".settings" },
		{ "SharingReason", "objects", ".object" },
		{ "SharingRecalculation", "objects", ".object" },
		{ "StaticResource", "staticresources", ".resource" },
		{ "Translations", "translations", ".translation" },
		{ "ValidationRule", "objects", ".object" },
		{ "Weblink", "objects", ".object" },
		{ "Workflow", "workflows", ".workflow" }
		
		/*
		{ "AccountSettings", "settings", ".settings" },
		{ "ActivitiesSettings", "settings", ".settings" },
		{ "AddressSettings", "settings", ".settings" },
		{ "BusinessHoursSettings", "settings", ".settings" },
		{ "CaseSettings", "settings", ".settings" },
		{ "ChatterAnswersSettings", "settings", ".settings" },
		{ "CompanySettings", "settings", ".settings" },
		{ "ContractSettings", "settings", ".settings" },
		{ "EntitlementSettings", "settings", ".settings" },
		{ "ForecastingSettings", "settings", ".settings" },
		{ "IdeasSettings", "settings", ".settings" },
		{ "KnowledgeSettings", "settings", ".settings" },
		{ "LiveAgentSettings", "settings", ".settings" },
		{ "MobileSettings", "settings", ".settings" },
		{ "OpportunitySettings", "settings", ".settings" },
		{ "ProductSettings", "settings", ".settings" },
		{ "QuoteSettings", "settings", ".settings" },
		{ "SecuritySettings", "settings", ".settings" }
		*/
	};
	
	public static final String[][] FOLDER_META_MAP = {
		{ "analyticsnapshots", "AnalyticSnapshot", ".analyticsnapshot" },
		{ "applications", "CustomApplication", ".app" },
		{ "classes", "ApexClass", ".cls" },
		{ "connectedapps", "ConnectedApp", "*.connectedapp" },
		{ "components", "ApexComponent", ".component" },
		{ "dashboards", "Dashboard", ".dashboard" },
		{ "datacategorygroups", "DataCategoryGroup", ".datacategorygroup" },
		{ "documents", "Document", "" },
		{ "email", "EmailTemplate", ".email" },
		{ "entitlementTemplates", "EntitlementTemplate", ".entitlementTemplate" },
		{ "escalationRules", "EscalationRule", ".escalationRules" },
		{ "flexipages", "FlexiPage", ".flexipage" },
		{ "groups", "Group", ".group" },
		{ "homePageComponents", "HomePageComponent", ".homePageComponent" },
		{ "homePageLayouts", "HomePageLayout", ".homePageLayout" },
		{ "labels", "CustomLabels", ".label" },
		{ "layouts", "Layout", ".layout" },
		{ "letterhead", "Letterhead", ".letterhead" },
		{ "objectTranslations", "CustomObjectTranslation", ".objectTranslation" },
		{ "objects", "CustomObject", ".object" },
		{ "pages", "ApexPage", ".page" },
		{ "permissionsets", "PermissionSet", ".permissionset" },
		{ "portals", "Portal", ".portal" },
		{ "profiles", "Profile", ".profile" },
		{ "quickActions", "QuickAction", ".quickAction" },
		{ "remoteSiteSettings", "RemoteSiteSetting", ".remoteSiteSetting" },
		{ "reportTypes", "ReportType", ".reportType" },
		{ "reports", "Report", ".report" },
		{ "roles", "Role", ".role" },
		{ "scontrols", "Scontrol", ".scontrol" },
		{ "settings", "Settings", ".settings" },
		{ "sites", "CustomSite", ".site" },
		{ "staticresources", "StaticResource", ".resource" },
		{ "tabs", "CustomTab", ".tab" },
		{ "translations", "Translations", ".translation" },
		{ "triggers", "ApexTrigger", ".trigger" },
		{ "weblinks", "CustomPageWebLink", ".weblink" },
		{ "workflows", "Workflow", ".workflow" }
	};
	
	/**
	 *  Finds a specific metadata type member in a Document.
	 *  @param parentNode (Node)
	 *  @param metadataType (String)
	 *  @param member (String)
	 *  @return Node - whether the node was removed (true) or not (false)
	**/
	public static Node findMemberNode( Node parentNode, String metadataType, String member ) throws Exception {
		Node targetNode = null;
		if( XML_Util.doesNodeContainTextNode( parentNode, TAG_NAME, metadataType )){
			//System.out.println( "found node with " + metadataType );
			targetNode = XML_Util.findChildTextNode( parentNode, TAG_MEMBERS, member );
			//System.out.println( "targetNode:" + targetNode );
			
			if( !XML_Util.isEmptyNode( targetNode ) ){
				//System.out.println( "found node!:" + targetNode );
				return( targetNode );
			}
		}
		
		return( null );
	}
	
	public static void checkMetadataType( String metadataType ) throws BuildException {
		if( metadataType == null || ("").equals( metadataType )){
			throw( new BuildException( ERROR_META_TYPE_BLANK ));
		}
	}
	
	public static void checkMetaMember( String member ) throws BuildException {
		if( member == null || ("").equals( member )){
			throw( new BuildException( ERROR_META_MEMBER_BLANK ));
		}
	}
	
	public static void checkDelimiter( String delimiter ) throws BuildException {
		if( delimiter == null || ("").equals( delimiter )){
			throw( new BuildException( ERROR_EMPTY_DELIMITER ));
		}
	}
	
	/**
	 *  Converts a metadata type to the folder type
	 *  @param meta (String)
	 *  @return (String)
	 **/
	public static String convertMetaToFolder( String meta ){
		if( meta == null ){
			return( null );
		}
		meta = meta.toLowerCase();
		String val = null;
		for( int i = 0; i < META_MAP.length; i++ ){
			val = META_MAP[i][0].toLowerCase();
			if( val.equalsIgnoreCase( meta )){
				return( META_MAP[i][1] );
			}
		}
		return( "" );
	}
	
	/**
	 *  Converts a folder name to a metadata type
	 *  @param folder
	 *  @return (String)
	 **/
	public static String convertFolderToMeta( String folder ){
		return( convertFolderToMeta( folder, null ));
	}
	
	public static String convertFolderToMeta( String folder, String fileName ){
		if( folder == null ){
			return( null );
		}
		folder = folder.toLowerCase();
		String val = null;
		
		/*
		//-- Settings can be multiple types even though they are within the same folder
		//-- for now they can all be the same metadata type though.
		
		if( SETTINGS.equalsIgnoreCase( folder )){
			//-- TODO
			
			if( fileName != null ){
				fileName = fileName.trim();
				for( int i = 0; i < SETTINGS_MAP.length; i++ ){
					val = SETTINGS_MAP[i][0];
					if( val.equalsIgnoreCase( fileName )){
						//System.out.println( "found:" + SETTINGS_MAP[i][1] );
						return( SETTINGS_MAP[i][1] );
					}
				}
				
				System.out.println( "Settings file[" + fileName + "] does not appear to be a known Settings type:" + KNOWN_SETTINGS_TYPES );
			} else {
				//-- can't do anything for now
				System.out.println( "Ignoring [" + folder + "][" + fileName + "] as this folder includes multiple types." );
			}
		} else {
		*/
		
		for( int i = 0; i < FOLDER_META_MAP.length; i++ ){
			val = FOLDER_META_MAP[i][0].toLowerCase();
			if( val.equalsIgnoreCase( folder )){
				return( FOLDER_META_MAP[i][1] );
			}
		}
		return( "" );
	}
	
	/**
	 *  Converts a metadata type to an extension
	 *  @param meta (String)
	 *  @return (String)
	 **/
	public static String convertMetaToExtension( String meta ){
		if( meta == null ){
			return( null );
		}
		meta = meta.toLowerCase();
		String val = null;
		for( int i = 0; i < META_MAP.length; i++ ){
			val = META_MAP[i][0].toLowerCase();
			if( val.equalsIgnoreCase( meta )){
				return( META_MAP[i][2] );
			}
		}
		return( "" );
	}
	
	/**
	 *  Converts a folder name to a file extension
	 *  @param folder
	 *  @return (String)
	 **/
	public static String convertFolderToExtension( String folder ){
		return( convertFolderToExtension( folder, null ) );
	}
	
	public static String convertFolderToExtension( String folder, String fileName ){
		if( folder == null ){
			return( null );
		}
		folder = folder.toLowerCase();
		String val = null;
		
		//-- although settings can be multiple types, they all have the same extension.
		for( int i = 0; i < FOLDER_META_MAP.length; i++ ){
			val = FOLDER_META_MAP[i][0].toLowerCase();
			if( val.equalsIgnoreCase( folder )){
				return( FOLDER_META_MAP[i][2] );
			}
		}
		return( "" );
	}
	
	/**
	 *  Determines a list of all the meta types available
	 *  @return String[]
	**/
	public static String[] getAllMetaTypes(){
		LinkedHashSet<String> results = new LinkedHashSet<String>();
		for( int i = 0; i < META_MAP.length; i++ ){
			if( META_MAP[i][0] != null && !("").equals( META_MAP[i][0] ) ){
				results.add( META_MAP[i][0] );
			}
		}
		return( results.toArray( new String[results.size()] ));
	}
	
	/**
	 *  Determines a list of all the folders available
	 *  @return String[]
	**/
	public static String[] getAllFolderTypes(){
		LinkedHashSet<String> results = new LinkedHashSet<String>();
		for( int i = 0; i < META_MAP.length; i++ ){
			if( META_MAP[i][1] != null && !("").equals( META_MAP[i][1] ) ){
				results.add( META_MAP[i][1] );
			}
		}
		return( results.toArray( new String[results.size()] ));
	}
	
	public static String convertArrayToString( String[] a ){
		StringBuilder sb = new StringBuilder();
		String delimiter = ",";
		Boolean isFirst = true;
		
		if( a != null ){
			for( String s : a ){
				if( a != null && !("").equals(s) ){
					if( isFirst ){
						isFirst = false;
					} else {
						sb.append( delimiter );
					}
					sb.append( s );
				}
			}
		}
		
		return( sb.toString() );
	}
}
