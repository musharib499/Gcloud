package com.gcloud.gaadi.finance_test_cases;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.ui.Finance.GaadiFinanceActivity;
import com.gcloud.gaadi.ui.Utility;
import com.gcloud.gaadi.utils.CommonUtils;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.gcloud.gaadi.ui.Utility.withRecyclerView;
import static org.junit.Assume.assumeTrue;

/**
 * Created by priyarawat on 11/1/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GaadiFinanceActivityTest {

    @Rule
   public ActivityTestRule<GaadiFinanceActivity> testRule = new ActivityTestRule<GaadiFinanceActivity>(GaadiFinanceActivity.class);
    @Test
    public void test1AddNewCase()
    {
        Context targetContext = InstrumentationRegistry.getTargetContext();

        assumeTrue(CommonUtils.getBooleanSharedPreference(targetContext, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.rlNewCasesLayout)).perform(click());
        onView(withRecyclerView(R.id.recyclerView)
                .atPositionOnView(1, R.id.ivStockImage)).perform(click());
        Utility.wait2Seconds();
    }
  /*  @Test
    public void test2performClickOnAvailableCarsList()
    {
       // Context targetContext = InstrumentationRegistry.getTargetContext();

        onView(withRecyclerView(R.id.recyclerView)
                .atPositionOnView(1, R.id.ivStockImage)).perform(click());

        Utility.wait2Seconds();
    }*/

}
