package com.gcloud.gaadi.ui;

import android.content.Context;
import android.os.IBinder;
import android.support.test.espresso.Root;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.gcloud.gaadi.ui.Utility.hasErrorText;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import com.gcloud.gaadi.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Created by vinodtakhar on 8/12/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SplashActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule =
            new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void test1LoginFailures() {

        Context targetContext = InstrumentationRegistry.getTargetContext();

        //blank email
        onView(withId(R.id.userEmail)).perform(typeText(""), closeSoftKeyboard());

        onView(withId(R.id.userPassword)).perform(typeText("vinod"), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.userEmail)).check(matches(hasErrorText(targetContext.getResources().getString(R.string.invalid_email_id))));

        //invalid email
        onView(withId(R.id.userEmail)).perform(clearText());
        onView(withId(R.id.userPassword)).perform(clearText());

        onView(withId(R.id.userEmail)).perform(typeText("vinod"), closeSoftKeyboard());

        onView(withId(R.id.userPassword)).perform(typeText(""), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.userEmail)).check(matches(hasErrorText(targetContext.getResources().getString(R.string.invalid_email_id))));

        //invalid password
        onView(withId(R.id.userEmail)).perform(clearText());
        onView(withId(R.id.userPassword)).perform(clearText());

        onView(withId(R.id.userEmail)).perform(typeText("vinod@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.userPassword)).perform(typeText(""), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.userPassword)).check(matches(hasErrorText(targetContext.getResources().getString(R.string.invalid_password))));

        //both valid but incorrect
        onView(withId(R.id.userEmail)).perform(clearText());
        onView(withId(R.id.userPassword)).perform(clearText());

        onView(withId(R.id.userEmail)).perform(typeText("vinod@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.userPassword)).perform(typeText("abc"), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        isToastMessageDisplayed("Invalid username or password!!");

        //both  valid and correct
        onView(withId(R.id.userEmail)).perform(clearText());
        onView(withId(R.id.userPassword)).perform(clearText());

        onView(withId(R.id.userEmail)).perform(typeText("saroj.sahoo@gaadi.com"), closeSoftKeyboard());

        onView(withId(R.id.userPassword)).perform(typeText("sarojsahoo"), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.container)).check(matches(isDisplayed()));
    }

   /* @Test
    public void test2LoginSuccess(){

        onView(withId(R.id.userEmail)).perform(clearText());
        onView(withId(R.id.userPassword)).perform(clearText());

        onView(withId(R.id.userEmail)).perform(typeText("saroj.sahoo@gaadi.com"), closeSoftKeyboard());

        onView(withId(R.id.userPassword)).perform(typeText("sarojsahoo"), closeSoftKeyboard());

        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.container)).check(matches(isDisplayed()));

//        onView(withClassName(equalTo("MainActivity"))).check(matches(isDisplayed()));
    }*/

    public void isToastMessageDisplayed(String textId) {
        onView(withText(textId)).inRoot(new Utility.ToastMatcher()).perform(click());
    }


}