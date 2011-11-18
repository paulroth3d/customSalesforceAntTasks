package com.coolblue.meta.pack;

import com.coolblue.meta.pack.*;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

import java.io.*;

public class PackageMetadataGroupTest {
	
	public static final String APEX_CLASSES = "apexClasses";
	public static final String PACKAGE = "package";
	
	public PackageMetadataGroup inst;
	
	public PackageMetadataGroupTest(){
		this.inst = new PackageMetadataGroup( "apexClasses" );
	}
	
	@Before
	public void initTest(){
	}
	
	//@BeforeClass, @Before, @Test, @After, @AfterClass
	@Test
	public void testWorking(){
		assertEquals( "there should not be an exception thrown for valid text files:", true,true );
	}
	
	@Test
	public void testType(){
		String expected = APEX_CLASSES;
		String result = this.inst.getType();
		assertEquals( "type does not return expected:", expected, result );
	}
	
	public static junit.framework.Test suite(){
		return( new JUnit4TestAdapter( PackageMetadataGroupTest.class ));
	}
}
