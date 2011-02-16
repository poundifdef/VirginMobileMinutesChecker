package com.jaygoel.virginminuteschecker;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.jaygoel.virginminuteschecker.MinutesCheckerTest \
 * com.jaygoel.virginminuteschecker.tests/android.test.InstrumentationTestRunner
 */
public class MinutesCheckerTest extends ActivityInstrumentationTestCase2<MinutesChecker> {

    public MinutesCheckerTest() {
        super("com.jaygoel.virginminuteschecker", MinutesChecker.class);
    }

}
