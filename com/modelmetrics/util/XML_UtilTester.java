package com.modelmetrics.util;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

public class XML_UtilTester {
	
	public XML_UtilTester(){
	}
	
	@Before
	public void initTest(){
	}
	
	@Test
	public void testWorking(){
		//assertEquals( "test sanity", "2+2", "love" );
		assertEquals( "test sanity", true, true );
	}
	
	//-- unsure how to add in an xml document for testing currently
	
	public static junit.framework.Test suite(){
		return( new JUnit4TestAdapter( XML_UtilTester.class ));
	}
}
