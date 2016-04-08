package com.gcloud.gaadi.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.SmallTest;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assume.assumeTrue;

/**
 * Created by kanishroshan on 6/1/16.
 */
public class GaadiWarrantyActivityTest {

    @Rule
    public ActivityTestRule<GaadiWarrantyActivity> mActivityRule =
            new ActivityTestRule<>(GaadiWarrantyActivity.class);


    @Test
    @SmallTest
    public void testonevalidViewselection() {

        Context targetContext = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));
        onData(anything()).inAdapterView(withId(R.id.certifiedcarslist)).atPosition(0).perform(click());
        Utility.waitSeconds(5);
        validissuewarrantyTest();
        inValidissuewarrantyTest();
    }


    private void validissuewarrantyTest() {
        //FillingWarrantyForm
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("WarrantyType", "TM-Comp (6 Months)");
        data.put("WarrantyT", "TM-E & T (6 Months)");
        data.put("Odometereading", "2000");
        data.put("price", "200000");
        data.put("customername", "JOHN SNOW");
        data.put("customermobile", "9023879337");
        data.put("customeremailid", "kanishroshan@hotmail.com");
        data.put("customeraddress", "park street");
        data.put("cityHint", "Del");
        data.put("city", "Delhi");
        data.put("confirm", "OK");

        //select warranty type TM
        onView(withId(R.id.wt_warrantytype)).perform(click());
//        Utility.wait2Seconds();

        onView(withText(data.get("WarrantyT"))).inRoot(isPlatformPopup()).perform(click());
        //onView(withId(R.id.wt_warrantytype)).check(matches(withText(data.get("WarrantyT"))));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //setodometerreading2000
        onView(withId(R.id.odometerreading)).perform(click());
        onView(withId(R.id.odometerreading)).perform(typeText(data.get("Odometereading")), closeSoftKeyboard());
        //price
        onView(withId(R.id.price)).perform(typeText(data.get("price")), closeSoftKeyboard());

        onView(withId(R.id.cusName)).perform(typeText(data.get("customername")), closeSoftKeyboard());
        onView(withId(R.id.custMobileNumber)).perform(typeText(data.get("customermobile")), closeSoftKeyboard());
        onView(withId(R.id.custEmailAddress)).perform(typeText(data.get("customeremailid")), closeSoftKeyboard());
        onView(withId(R.id.custaddress)).perform(typeText(data.get("customeraddress")), closeSoftKeyboard());
        //City
        onView(withId(R.id.registrationCity)).perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.registrationCity)).perform(typeText(data.get("cityHint")), closeSoftKeyboard());

        onView(withText(data.get("city"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.registrationCity)).check(matches(withText(data.get("city"))));
        onView(withId(R.id.footerLayout)).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("confirm"))).perform(click());

    }

    private void inValidissuewarrantyTest() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("WarrantyType", "TM-Comp (6 Months)");
        data.put("WarrantyT", "TM-E & T (6 Months)");
        data.put("Odometereading", "2000");
        data.put("customeremailid", "kanishroshan@hotmail.com");
        data.put("price", "200000");
        data.put("customername", "invalid person");
        data.put("customermobile", "9023877");
        data.put("customeraddress", "parkstreet");
        data.put("cityHint", "Del");
        data.put("city", "Delhi");
        data.put("confirm", "OK");
        onData(anything()).inAdapterView(withId(R.id.certifiedcarslist)).atPosition(0).perform(click());
        Utility.waitSeconds(3);

        //select warranty type TM
        onView(withId(R.id.wt_warrantytype)).perform(click());
//        Utility.wait2Seconds();

        onView(withText(data.get("WarrantyT"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.wt_warrantytype)).check(matches(withText(data.get("WarrantyT"))));

        //setodometerreading2000
        onView(withId(R.id.odometerreading)).perform(click());
        onView(withId(R.id.odometerreading)).perform(typeText(data.get("Odometereading")), closeSoftKeyboard());
        //price
        onView(withId(R.id.price)).perform(typeText(data.get("price")), closeSoftKeyboard());

        onView(withId(R.id.cusName)).perform(typeText(data.get("customername")), closeSoftKeyboard());
        onView(withId(R.id.custMobileNumber)).perform(typeText(data.get("customermobile")), closeSoftKeyboard());
        onView(withId(R.id.custEmailAddress)).perform(typeText(data.get("customeremailid")), closeSoftKeyboard());
        onView(withId(R.id.custaddress)).perform(typeText(data.get("customeraddress")), closeSoftKeyboard());
        //City
        onView(withId(R.id.registrationCity)).perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.registrationCity)).perform(typeText(data.get("cityHint")), closeSoftKeyboard());

        onView(withText(data.get("city"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.registrationCity)).check(matches(withText(data.get("city"))));
        onView(withId(R.id.footerLayout)).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("confirm"))).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("confirm"))).perform(click());

    }

    @Test
    @SmallTest
    public void filterTest() {


        Context targetContext = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));
        Utility.waitSeconds(10);
        onView(withId(R.id.fab)).perform(click());

        filterValidTest();




    }

    private void filterValidTest() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("MAKE", "MAKE");
        data.put("CAR","BMW");
        data.put("MODEL", "MODEL");
        data.put("model","Civic");
        data.put("WARRANTY STATUS", "WARRANTY STATUS");
        data.put("Status","Active");
        data.put("WARRANTY TYPE", "WARRANTY TYPE");
        data.put("WarrantyT", "TM-E & T (6 Months)");

        //filteringMAKE
        onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
      //  onView(withText(data.get("CAR"))).check(ViewAssertions.matches(withText(data.get("CAR")))).perform(click());
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());


        //MODEl
        onView(withText(data.get("MODEL"))).check(ViewAssertions.matches(withText(data.get("MODEL")))).perform(click());
        onView(withText(data.get("model"))).check(ViewAssertions.matches(withText(data.get("model")))).perform(click());
        //WARRANTY STATUS
        onView(withText(data.get("WARRANTY STATUS"))).check(ViewAssertions.matches(withText(data.get("WARRANTY STATUS")))).perform(click());
        onView(withText(data.get("Status"))).check(ViewAssertions.matches(withText(data.get("Status")))).perform(click());
        //WARRANTY TYPE
        onView(withText(data.get("WARRANTY TYPE"))).check(ViewAssertions.matches(withText(data.get("WARRANTY TYPE")))).perform(click());
        onView(withText(data.get("WarrantyT"))).check(ViewAssertions.matches(withText(data.get("WarrantyT")))).perform(click());
        //APPLY FILTER
        onView(withId(R.id.apply_filter)).perform(click());

    }
    @Test
    @SmallTest
    public void issueWarrantyTabTest(){
        Context targetContext = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));
        //onData(anything()).inAdapterView(withId(R.id.certifiedcarslist)).atPosition(0).perform(click());
      //  onView(startsWith("Issue"))
        Utility.waitSeconds(5);

        validissuewarrantyTest();
        inValidissuewarrantyTest();


    }

}






