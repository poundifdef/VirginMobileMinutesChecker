package com.jaygoel.virginminuteschecker.tests;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.jaygoel.virginminuteschecker.WebsiteScraper;

import junit.framework.TestCase;

public class MinutesCheckerTests extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testParseInfo() {
		try {
	    FileInputStream fstream = new FileInputStream("test_credit.txt");
	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    
	    //Read File Line By Line
	    String line = br.readLine();
	      // Print the content on the console
	      //System.out.println (line);
	    //}
	    //Close the input stream
	    in.close();
	    
	    System.out.println(WebsiteScraper.parseInfo(line));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(WebsiteScraper.parseInfo(line));
		
		
		//fail("Not yet implemented");
	}

}
