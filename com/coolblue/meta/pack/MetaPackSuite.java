package com.coolblue.meta.pack;
/*
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	WordTokenizer.class
  })
public class UtilitySuite {}
*/

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.*;

public class MetaPackSuite {
	public static junit.framework.TestSuite suite(){
		TestSuite suite= new TestSuite();
		suite.addTest(new JUnit4TestAdapter(PackageMetadataGroupTest.class));
		return(suite);
	}
}
