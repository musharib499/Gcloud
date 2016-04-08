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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assume.assumeTrue;

/**
 * Created by vinodtakhar on 16/12/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ViewStockTest {

    @Rule
    public ActivityTestRule<StocksActivity> mActivityRule =
            new ActivityTestRule<>(StocksActivity.class);

    @Test
    public void test1CorrectScreenSelection() {

        Context targetContext = InstrumentationRegistry.getTargetContext();

        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));

//        onData(withRowString("modelVersion","Miata Base")).inAdapterView(allOf(withId(R.id.stocksList), isDisplayed())).perform(scrollTo());

        onData(hasToString(containsString("Miata Base"))).inAdapterView(allOf(withId(R.id.stocksList), isDisplayed())).check(matches(isDisplayed()));//perform(click());

//        onData(hasToString(containsString("M?iata Base"))).inAdapterView(withId(R.id.stocksList)).perform(click());
    }
}
