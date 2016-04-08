package com.gcloud.gaadi.ui;

import android.os.IBinder;
import android.support.test.espresso.Root;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;



import com.gcloud.gaadi.Insurance.RecyclerViewMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkArgument;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by vinodtakhar on 16/12/15.
 */
public class Utility {
//    public class NotRunningOnWindows implements IgnoreCondition {
//        public boolean isSatisfied() {
//            return !System.getProperty( "os.name" ).startsWith( "Windows" );
//        }
//    }

    public static Matcher<View> hasErrorText(final String expectedError) {
        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: " + expectedError);
            }

            @Override
            protected boolean matchesSafely(View view) {

                if (!(view instanceof EditText)) {
                    return false;
                }

                EditText editText = (EditText) view;

                return expectedError.equals(editText.getError());
            }
        };
    }


    public static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    // windowToken == appToken means this window isn't contained by any other windows.
                    // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                    return true;
                }
            }
            return false;
        }

    }

    public static Matcher<Object> withItemText(String itemText) {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        checkArgument(!(itemText.equals(null)));
        return withItemText(equalTo(itemText));
    }

    public static Matcher<Object> withItemText(final Matcher<String> matcherText) {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        checkNotNull(matcherText);
        return new TypeSafeMatcher<Object>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("expected text: " + matcherText);
            }

            @Override
            public void describeMismatchSafely(Object item, Description mismatchDescription) {
                mismatchDescription.appendText("actual text: " + item.toString());
            }

            @Override
            public boolean matchesSafely(Object item) {
                return matcherText.equals(item);
            }
        };
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {

        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static void wait2Seconds(){
        final CountDownLatch signal = new CountDownLatch(1);
        try {
            signal.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void waitSeconds(int wait){
        final CountDownLatch signal = new CountDownLatch(1);
        try {
            signal.await(wait, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
