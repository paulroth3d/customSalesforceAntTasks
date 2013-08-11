package com.modelmetrics.util;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

public class PackageUtilTester {
	
	
	public PackageUtilTester(){
	}
	
	@Before
	public void initTest(){
	}
	
	@After
	public void cleanUp(){}
	
	@Test
	public void testWorking(){
		//assertEquals( "test sanity", "2+2", "love" );
		assertEquals( "test sanity", true, true );
	}
	
	@Test
	public void testGetAllMetaTypes(){
		String[] results = PackageUtil.getAllMetaTypes();
		assertEquals( "all items must be returned", PackageUtil.META_MAP.length, results.length );
		//System.out.println( results.toString() );
	}
	
	//-- unsure how to add in an xml document for testing currently
	
	public static junit.framework.Test suite(){
		return( new JUnit4TestAdapter( XML_UtilTester.class ));
	}
}
