package com.gcloud.gaadi.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.CommonUtils;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assume.assumeTrue;

/**
 * Created by vinodtakhar on 14/12/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test1CorrectScreenSelection() {

        Context targetContext = InstrumentationRegistry.getTargetContext();

        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));

        //view stock
        onView(withId(R.id.viewStock)).perform(click());

        onView(withText(R.string.title_activity_stocksactivity)).check(matches(isDisplayed()));

        pressBack();

        //add stock
        onView(withId(R.id.addStock)).perform(click());

        onView(allOf(withId(-1),withText(R.string.add_stock))).check(matches(isDisplayed()));

//        pressBack();
    }
}
