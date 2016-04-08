package com.gcloud.gaadi.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

import org.hamcrest.CoreMatchers;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assume.assumeTrue;

/**
 * Created by ankitgarg on 29/12/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddLeadActivityTest {

    @Rule
    public ActivityTestRule<LeadAddActivity> mActivityRule =
            new ActivityTestRule<LeadAddActivity>(LeadAddActivity.class);

    HashMap<String, String> invalidData = new HashMap<String, String>() {
        {
            put("leadName",    "Ankit Garg");
            put("leadEmail",   "ankit.garg@gaadi.com");
            put("leadMobile",  "9971126267");
            put("leadSource",  "CarDekho");
            put("leadStatus",  "Cold");
            put("budgetFrom",  "5 Lacs");
            put("budgetTo",    "2 Lacs");
        }
    };

    HashMap<String, String> validData = new HashMap<String, String>() {
        {
            put("leadName",    "Ankit Garg");
            put("leadEmail",   "ankit.garg@gaadi.com");
            put("leadMobile",  "9971126267");
            put("leadSource",  "CarDekho");
            put("leadStatus",  "Warm");
            put("budgetFrom",  "2 Lacs");
            put("budgetTo",    "5 Lacs");
        }
    };


    @Test
    @LargeTest
    public void test1AddLeadInvalidBudgetRange() {

        Context targetContext = InstrumentationRegistry.getTargetContext();

        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));

        onView(withId(R.id.et_fullName)).perform(typeText(invalidData.get("leadName")), closeSoftKeyboard());
        onView(withId(R.id.et_email)).perform(typeText(invalidData.get("leadEmail")), closeSoftKeyboard());
        onView(withId(R.id.et_mobileNum)).perform(typeText(invalidData.get("leadMobile")), closeSoftKeyboard());

        onView(withId(R.id.statusCold)).perform(click());

        onView(withId(R.id.budget)).perform(click());

        onData(hasToString(CoreMatchers.containsString("'" + invalidData.get("budgetFrom") + "'"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.budget)).check(matches(withText(invalidData.get("budgetFrom"))));


        onView(withId(R.id.budgetto)).perform(click());

        onData(hasToString(CoreMatchers.containsString("'" + invalidData.get("budgetTo") + "'"))).inRoot(isPlatformPopup()).perform(click());

        onView(withText("Budget from cannot be greater than budget to.")).inRoot(new Utility.ToastMatcher()).perform(click());

        Utility.wait2Seconds();

    }

    @Test
    @LargeTest
    public void test2AddLead() {
        Context targetContext = InstrumentationRegistry.getTargetContext();

        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));

        onView(withId(R.id.et_fullName)).perform(typeText(validData.get("leadName")), closeSoftKeyboard());
        onView(withId(R.id.et_email)).perform(typeText(validData.get("leadEmail")), closeSoftKeyboard());
        onView(withId(R.id.et_mobileNum)).perform(typeText(validData.get("leadMobile")), closeSoftKeyboard());

        onView(withText(validData.get("leadSource"))).perform(scrollTo(), click());
        onView(withId(R.id.statusWarm)).perform(click());

        onView(withId(R.id.budget)).perform(click());

        onData(hasToString(CoreMatchers.containsString("'" + validData.get("budgetFrom") + "'"))).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.budget)).check(matches(withText(validData.get("budgetFrom"))));


        onView(withId(R.id.budgetto)).perform(click());

        onData(hasToString(CoreMatchers.containsString("'" + validData.get("budgetTo") + "'"))).inRoot(isPlatformPopup()).perform(click());

        Utility.wait2Seconds();

        //onView(withId(R.id.followupDate)).perform(scrollTo(), click());
        //onData(withClassName(Matchers.equalTo(DatePickerDialog.class.getName()))).perform(DatePickerDialog.newInstance(targetContext, 2015, 11, 30));

        //onView(withText("Budget from cannot be greater than budget to.")).inRoot(new Utility.ToastMatcher()).perform(click());


    }
}
