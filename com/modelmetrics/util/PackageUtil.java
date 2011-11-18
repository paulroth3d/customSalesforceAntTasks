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
	
	public static final String TAG_TYPES = "types";
	public static final String TAG_MEMBERS = "members";
	public static final String TAG_NAME = "name";
	
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
		if( ("ActionOverride").equals( meta ) ){
			return( "objects" );
		} else if( ("AnalyticSnapshot").equals( meta ) ){
			return( "analyticsnapshots" );
		} else if( ("ApexClass").equals( meta ) ){
			return( "classes" );
		} else if( ("ApexComponent").equals( meta ) ){
			return( "components" );
		} else if( ("ApexPage").equals( meta ) ){
			return( "pages" );
		} else if( ("ApexTrigger").equals( meta ) ){
			return( "triggers" );
		} else if( ("ArticleType").equals( meta ) ){
			return( "objects" );
		} else if( ("BusinessProcess").equals( meta ) ){
			return( "objects" );
		} else if( ("CustomApplication").equals( meta ) ){
			return( "applications" );
		} else if( ("CustomField").equals( meta ) ){
			return( "objects" );
		} else if( ("CustomLabels").equals( meta ) ){
			return( "labels" );
		} else if( ("CustomObject").equals( meta ) ){
			return( "objects" );
		} else if( ("CustomObjectTranslation").equals( meta ) ){
			return( "objectTranslations" );
		} else if( ("CustomPageWebLink").equals( meta ) ){
			return( "weblinks" );
		} else if( ("CustomSite").equals( meta ) ){
			return( "sites" );
		} else if( ("CustomTab").equals( meta ) ){
			return( "tabs" );
		} else if( ("Dashboard").equals( meta ) ){
			return( "dashboards" );
		} else if( ("DataCategoryGroup").equals( meta ) ){
			return( "datacategorygroups" );
		} else if( ("Document").equals( meta ) ){
			return( "document" );
		} else if( ("EmailTemplate").equals( meta ) ){
			return( "email" );
		} else if( ("EntitlementTemplate").equals( meta ) ){
			return( "entitlementTemplates" );
		} else if( ("FieldSet").equals( meta ) ){
			return( "objects" );
		} else if( ("HomePageComponent").equals( meta ) ){
			return( "homePageComponents" );
		} else if( ("HomePageLayout").equals( meta ) ){
			return( "homePageLayouts" );
		} else if( ("Layout").equals( meta ) ){
			return( "layouts" );
		} else if( ("Letterhead").equals( meta ) ){
			return( "letterhead" );
		} else if( ("ListView").equals( meta ) ){
			return( "objects" );
		} else if( ("NamedFilter").equals( meta ) ){
			return( "objects" );
		} else if( ("PermissionSet").equals( meta ) ){
			return( "permissionsets" );
		} else if( ("Portal").equals( meta ) ){
			return( "portals" );
		} else if( ("Profile").equals( meta ) ){
			return( "profiles" );
		} else if( ("RecordType").equals( meta ) ){
			return( "objects" );
		} else if( ("RemoteSiteSetting").equals( meta ) ){
			return( "remoteSiteSettings" );
		} else if( ("Report").equals( meta ) ){
			return( "reports" );
		} else if( ("ReportType").equals( meta ) ){
			return( "reportTypes" );
		} else if( ("Scontrol").equals( meta ) ){
			return( "scontrols" );
		} else if( ("SharingReason").equals( meta ) ){
			return( "objects" );
		} else if( ("SharingRecalculation").equals( meta ) ){
			return( "objects" );
		} else if( ("StaticResource").equals( meta ) ){
			return( "staticResources" );
		} else if( ("Translations").equals( meta ) ){
			return( "translations" );
		} else if( ("ValidationRule").equals( meta ) ){
			return( "objects" );
		} else if( ("Weblink").equals( meta ) ){
			return( "objects" );
		} else if( ("Workflow").equals( meta ) ){
			return( "workflows" );
		} else {
			return( "" );
		}
	}
	
	/**
	 *  Converts a folder name to a metadata type
	 *  @param folder
	 *  @return (String)
	 **/
	public static String convertFolderToMeta( String folder ){
		if( ("analyticsnapshots").equals( folder ) ){
			return( "AnalyticSnapshot" );
		} else if( ("applications").equals( folder ) ){
			return( "CustomApplication" );
		} else if( ("classes").equals( folder ) ){
			return( "ApexClass" );
		} else if( ("components").equals( folder ) ){
			return( "ApexComponent" );
		} else if( ("dashboards").equals( folder ) ){
			return( "Dashboard" );
		} else if( ("datacategorygroups").equals( folder ) ){
			return( "DataCategoryGroup" );
		} else if( ("document").equals( folder ) ){
			return( "Document" );
		} else if( ("email").equals( folder ) ){
			return( "EmailTemplate" );
		} else if( ("entitlementTemplates").equals( folder ) ){
			return( "EntitlementTemplate" );
		} else if( ("homePageComponents").equals( folder ) ){
			return( "HomePageComponent" );
		} else if( ("homePageLayouts").equals( folder ) ){
			return( "HomePageLayout" );
		} else if( ("labels").equals( folder ) ){
			return( "CustomLabels" );
		} else if( ("layouts").equals( folder ) ){
			return( "Layout" );
		} else if( ("letterhead").equals( folder ) ){
			return( "Letterhead" );
		} else if( ("objectTranslations").equals( folder ) ){
			return( "CustomObjectTranslation" );
		} else if( ("objects").equals( folder ) ){
			return( "CustomObject" );
		} else if( ("pages").equals( folder ) ){
			return( "ApexPage" );
		} else if( ("permissionsets").equals( folder ) ){
			return( "PermissionSet" );
		} else if( ("portals").equals( folder ) ){
			return( "Portal" );
		} else if( ("profiles").equals( folder ) ){
			return( "Profile" );
		} else if( ("remoteSiteSettings").equals( folder ) ){
			return( "RemoteSiteSetting" );
		} else if( ("reportTypes").equals( folder ) ){
			return( "ReportType" );
		} else if( ("reports").equals( folder ) ){
			return( "Report" );
		} else if( ("scontrols").equals( folder ) ){
			return( "Scontrol" );
		} else if( ("sites").equals( folder ) ){
			return( "CustomSite" );
		} else if( ("staticResources").equals( folder ) ){
			return( "StaticResource" );
		} else if( ("tabs").equals( folder ) ){
			return( "CustomTab" );
		} else if( ("translations").equals( folder ) ){
			return( "Translations" );
		} else if( ("triggers").equals( folder ) ){
			return( "ApexTrigger" );
		} else if( ("weblinks").equals( folder ) ){
			return( "CustomPageWebLink" );
		} else if( ("workflows").equals( folder ) ){
			return( "Workflow" );
		} else {
			return( "" );
		} 
	}
}
