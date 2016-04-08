package com.gcloud.gaadi.Insurance;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.MediumTest;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.NewInsuranceDashboard;
import com.gcloud.gaadi.ui.Utility;
import com.gcloud.gaadi.utils.CommonUtils;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assume.assumeTrue;
/**
 * Created by dipanshugarg on 6/1/16.
 */

public class AllCaseActivityTest {
    @Rule
    public ActivityTestRule<NewInsuranceDashboard> mTestRule =
            new ActivityTestRule<NewInsuranceDashboard>(NewInsuranceDashboard.class);

    HashMap<String,String> data=new HashMap<String,String>(){
        { put("RequestId", "123");
            put("REGNO", "REG NO");
            put("MAKE", "MAKE");
            put("MODEL", "MODEL");
            put("STATUS", "STATUS");
            put("InsuranceType","INSURANCE TYPE");
            put("Insurer","INSURER");
        }};


    @Test
    @MediumTest
    public void testAllCase() {
        Context  context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        Utility.wait2Seconds();
        onView(withId(R.id.totalCases)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.fab)).perform(click());
        Utility.wait2Seconds();
        onView(allOf(withId(R.id.reg_no), isDisplayed())).perform(typeText(data.get("RequestId")), closeSoftKeyboard());
        onView(withText("APPLY FILTER")).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.fab)).perform(click());
        onView(withText("RESET")).perform(click());
        onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
        onView(withText(R.string.chevrolet)).perform(click());
        onView(withText(data.get("MODEL"))).check(ViewAssertions.matches(withText(data.get("MODEL")))).perform(click());
        onView(withText("Beat")).perform(click());
        onView(withText(data.get("STATUS"))).check(ViewAssertions.matches(withText(data.get("STATUS")))).perform(click());
        onView(withText("Booked")).perform(click());
        onView(withText(data.get("InsuranceType"))).check(ViewAssertions.matches(withText(data.get("InsuranceType")))).perform(click());
        onView(withText("Inspected")).perform(click());
        onView(withText(data.get("InsuranceType"))).check(ViewAssertions.matches(withText(data.get("InsuranceType")))).perform(click());
        onView(withText("Inspected")).perform(click());
        onView(withText(data.get("Insurer"))).check(ViewAssertions.matches(withText(data.get("Insurer")))).perform(click());
        onView(withText("Bharti Axa")).perform(click());
        onView(withText("APPLY FILTER")).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withText("RESET")).perform(click());
        onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
        onView(withText(R.string.chevrolet)).perform(click());
        onView(withText(data.get("MODEL"))).check(ViewAssertions.matches(withText(data.get("MODEL")))).perform(click());
        onView(withText("Beat")).perform(click());
        onView(withText(data.get("STATUS"))).check(ViewAssertions.matches(withText(data.get("STATUS")))).perform(click());
        onView(withText("Booked")).perform(click());
        //onView(withText(data.get("InsuranceType"))).check(Vi  Utility.wait2Seconds();ewAssertions.matches(withText(data.get("InsuranceType")))).perform(click());
        //onView(withText("Inspected")).perform(click());
        //onView(withText(data.get("InsuranceType"))).check(ViewAssertions.matches(withText(data.get("InsuranceType")))).perform(click());
        //onView(withText("Inspected")).perform(click());
        onView(withText("APPLY FILTER")).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        Utility.wait2Seconds();

    }
}
