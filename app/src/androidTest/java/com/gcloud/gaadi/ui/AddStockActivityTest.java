package com.gcloud.gaadi.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assume.assumeTrue;

/**
 * Created by vinodtakhar on 15/12/15.
 */
public class AddStockActivityTest {
    @Rule
    public ActivityTestRule<StockAddActivity> mActivityRule =
            new ActivityTestRule<>(StockAddActivity.class);

    @Test
    @LargeTest
    public void test1StockAdd() {

        Context targetContext = InstrumentationRegistry.getTargetContext();

        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));

        HashMap<String,String> data=new HashMap<String,String>();
        data.put("makeModelHint","ma");
        data.put("model","Matiz");
        data.put("version","SD");
        data.put("color","Red");
        data.put("year","2014");
        data.put("month","Mar");
        data.put("kms","2,000");
        data.put("numOwner","Fourth");
        data.put("cityHint","Del");
        data.put("city","Delhi");
        data.put("regNumber","5435435u3");
        data.put("price","34,000");

        //select swift (model)
        onView(withId(R.id.makeModel)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(typeText(data.get("makeModelHint")), closeSoftKeyboard());

        onView(withText(data.get("model"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.makeModel)).check(matches(withText(data.get("model"))));

        //select version
        onView(withId(R.id.version)).perform(click());

        onView(withText(data.get("version"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.version)).check(matches(withText(data.get("version"))));

        //color
        onView(withId(R.id.stockcolorlayout)).perform(click());

        onView(withText(data.get("color"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.stockColor)).check(matches(withText(data.get("color"))));

        //year
        onView(withId(R.id.stockYear)).perform(click());

        onView(withText(data.get("year"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.stockYear)).check(matches(withText(data.get("year"))));

        //month
        onView(withId(R.id.stockMonth)).perform(click());

        onView(withText(data.get("month"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.stockMonth)).check(matches(withText(data.get("month"))));

        //kms driven
        onView(withId(R.id.kmsDriven)).perform(typeText(data.get("kms")), closeSoftKeyboard());
        onView(withId(R.id.kmsDriven)).check(matches(withText(data.get("kms"))));

        //number of owners
        onView(withId(R.id.numOwners)).perform(click());

        onView(withText(data.get("numOwner"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.numOwners)).check(matches(withText(data.get("numOwner"))));

        //registration city
        onView(withId(R.id.registrationCity)).perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.registrationCity)).perform(typeText(data.get("cityHint")), closeSoftKeyboard());

        onView(withText(data.get("city"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.registrationCity)).check(matches(withText(data.get("city"))));

        //kms driven
        onView(withId(R.id.registrationNumber)).perform(typeText(data.get("regNumber")), closeSoftKeyboard());
        onView(withId(R.id.registrationNumber)).check(matches(withText(data.get("regNumber"))));

        //stock price
        onView(withId(R.id.stockPrice)).perform(typeText(data.get("price")), closeSoftKeyboard());
        onView(withId(R.id.stockPrice)).check(matches(withText(data.get("price"))));

        //trigger
        onView(withId(R.id.addGaadi)).perform(click());

        onView(withText("Stock added successfully.")).inRoot(new Utility.ToastMatcher()).perform(click());
    }
}
