package com.gcloud.gaadi.ui;

import android.annotation.TargetApi;
import android.os.Build;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Created by vinodtakhar on 15/12/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SplashActivityTest.class,
        MainActivityTest.class,
})
public class UiTestSuite /*extends TestSuite*/{
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public static Test suite(){
//        Class[] testClasses = { SplashActivityTest.class, MainActivityTest.class };
//        TestSuite suite= new TestSuite(testClasses);
//        return suite;
//    }
}
