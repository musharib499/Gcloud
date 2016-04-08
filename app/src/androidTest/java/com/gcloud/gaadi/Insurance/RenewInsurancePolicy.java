package com.gcloud.gaadi.Insurance;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.MySeekBar;
import com.gcloud.gaadi.insurance.NewInsuranceDashboard;
import com.gcloud.gaadi.ui.Utility;
import com.gcloud.gaadi.utils.CommonUtils;
import com.github.jjobes.slidedatetimepicker.CustomDatePicker;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.gcloud.gaadi.ui.Utility.withRecyclerView;
import static org.junit.Assume.assumeTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RenewInsurancePolicy {
    @Rule
    public ActivityTestRule<NewInsuranceDashboard> mTestRule =
            new ActivityTestRule<NewInsuranceDashboard>(NewInsuranceDashboard.class);

    MyIdleResource idlingResource;
    HashMap<String,String> value = new HashMap<String,String>() {
        {
            put("RegNo","UP14AS1545");
            put("RegFalse", "tywhejwjw");
            put("FullName","Dipanshu Garg");
            put("Mobile_number","9911350163");
            put("Email","dipanshu.garg@gaadi.com");
            put("Address","sddsfdsfsdsdfsdf");
            put("Pin","201002");
            put("nomineeName","dfgdfgdfg");
            put("age","1");
            put("AppointeFullName","Garg hgfhgfhgf");
            put("dateStart","18/Feb/2016");
            put("dateEnd","12/Feb/2017");
            put("EnginNumber","LN901931054321C");
            put("Chasesno","KA-01-C-4617");
        }

    };

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                MySeekBar seekBar = (MySeekBar) view;
                seekBar.setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(MySeekBar.class);
            }
        };
    }

    @Before
    public void registerIntentServiceIdlingResource() {
        idlingResource = new MyIdleResource();
    }

    @After
    public void unregisterIntentServiceIdlingResource() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    @MediumTest
    public void test7()  {

        //Renew Insurance Policy
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getRenewalCarsLayout)).perform(click());
        Utility.wait2Seconds();

        onView(withId(R.id.makeModel)).perform(typeText("Man"), closeSoftKeyboard());
        onView(withText("Manza")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.version)).perform(click());
        onView(withText("Aqua Safire")).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.tv_regDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.et_RegNum)).perform(typeText(value.get("RegNo")), closeSoftKeyboard());
        Utility.wait2Seconds();
        onView(withId(R.id.rb_ClaimPolicyYes)).perform(click());
        onView(withId(R.id.prevPolicyEndDate)).perform(ViewActions.scrollTo());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.yesCngLpg)).perform(click());
        onView(withId(R.id.prevPolicyEndDate)).perform(ViewActions.scrollTo());
        onView(withId(R.id.rb_companyFitted)).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.prevPolicyEndDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        Utility.wait2Seconds();
        Utility.wait2Seconds();
        //Filter test Case
        filter();

        Utility.wait2Seconds();
        Utility.wait2Seconds();

        onView(withRecyclerView(R.id.rv).atPositionOnView(0, R.id.buyNow)).perform(click());

        //customer Test Cases
        customerDetailCustomers();

        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        Utility.wait2Seconds();

        //More Details
        MoreDetails();

        Utility.wait2Seconds();


    }

    @Test
    @MediumTest
    public void test8()  {

        //Renew Insurance Policy
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getRenewalCarsLayout)).perform(click());
        Utility.wait2Seconds();

        onView(withId(R.id.makeModel)).perform(typeText("Man"), closeSoftKeyboard());
        onView(withText("Manza")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.version)).perform(click());
        onView(withText("Aqua Safire")).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.tv_regDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.et_RegNum)).perform(typeText(value.get("RegNo")), closeSoftKeyboard());
        Utility.wait2Seconds();

        onView(withId(R.id.rb_ClaimPolicyNo)).perform(click());
        onView(withId(R.id.prevPolicyEndDate)).perform(ViewActions.scrollTo());

        onView(withId(R.id.tv_NCB)).perform(click());
        onView(withText("20")).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.prevPolicyEndDate)).perform(ViewActions.scrollTo());
        Utility.wait2Seconds();

        onView(withId(R.id.noCngLpg)).perform(click());

        Utility.wait2Seconds();
        onView(withId(R.id.prevPolicyEndDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();

        //Filter test Case
        filter();

        Utility.wait2Seconds();
        Utility.wait2Seconds();

        onView(withRecyclerView(R.id.rv).atPositionOnView(0, R.id.buyNow)).perform(click());

        //customer Test Cases
        customerDetail();
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        Utility.wait2Seconds();
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        Utility.wait2Seconds();

        //More Details
         MoreDetails();



      /*  IdlingPolicies.setMasterPolicyTimeout(
                5, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(
                5, TimeUnit.MINUTES);

        Espresso.registerIdlingResources(idlingResource);*/
     /*   onView(withId(R.id.policyInsurer)).perform(click())*//*.check(matches(withHint("Previous Policy Insurer*")))*//*;


        onView(withText("National Insurance")).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.policyNumber)).perform(typeText(value.get("FullName")),closeSoftKeyboard());
        onView(withId(R.id.engineNo)).perform(typeText(value.get("EnginNumber")),closeSoftKeyboard());
        onView(withId(R.id.chasisNo)).perform(typeText(value.get("Chasesno")),closeSoftKeyboard());
        onView(withId(R.id.rb_carFinancedNo)).perform(click());
        onView(withId(R.id.spinnerCity)).perform(typeText("Aditya"), closeSoftKeyboard());
        onView(withText("Aditya Birla Finance")).inRoot(isPlatformPopup()).perform(click());

        Utility.wait2Seconds();*/

    }

    @Test
    @MediumTest
    public void negativeTestCase()  {

        //Renew Insurance Policy
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getRenewalCarsLayout)).perform(click());
        Utility.wait2Seconds();

        onView(withId(R.id.makeModel)).perform(typeText("Man"), closeSoftKeyboard());
        onView(withText("Manza")).inRoot(isPlatformPopup()).perform(click());

        // onView(withId(R.drawable.close_layer_dark)).check(matches(isDisplayed())).perform(click());
        //onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        //  onView(withId(R.id.makeModel)).perform(typeText("Man"), closeSoftKeyboard());
        //onView(withText("Manza")).inRoot(isPlatformPopup()).perform(click());

        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.version)).perform(click());
        onView(withText("Aqua Safire")).inRoot(isPlatformPopup()).perform(click());

        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.tv_regDate)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withText("Next")).check(matches(isDisplayed())).perform(click());


        onView(withId(R.id.et_RegNum)).perform(typeText(value.get("RegFalse")), closeSoftKeyboard());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        // onView(withId(R.id.et_RegNum)).perform(typeText(value.get("RegNo")), closeSoftKeyboard());
        //Utility.wait2Seconds();

        onView(withId(R.id.rb_ClaimPolicyYes)).perform(click());
        onView(withId(R.id.prevPolicyEndDate)).perform(ViewActions.scrollTo());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.yesCngLpg)).perform(click());
        onView(withId(R.id.prevPolicyEndDate)).perform(ViewActions.scrollTo());
        onView(withId(R.id.rb_companyFitted)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.prevPolicyEndDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.et_RegNum)).perform(clearText());
        onView(withId(R.id.et_RegNum)).perform(typeText(value.get("RegNo")), closeSoftKeyboard());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());

        Utility.wait2Seconds();
        Utility.wait2Seconds();

    }

    public void filter() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.seeb_bar)).perform(setProgress(2)).perform(click());
        onView(withText("Zero Depreciation Cover")).perform(click());
        onView(withText("\u20B9 5,000")).perform(click());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.seeb_bar)).perform(setProgress(2)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withText("APPLY")).perform(click());
    }

    public void customerDetail() {
        onView(withId(R.id.individual)).perform(click());
        onView(withId(R.id.etFirstName)).perform(typeText(value.get("FullName")), closeSoftKeyboard());
        onView(withId(R.id.etMobile)).perform(typeText(value.get("Mobile_number")), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(typeText(value.get("Email")), closeSoftKeyboard());
        onView(withId(R.id.etCustAddress)).perform(typeText(value.get("Address")), closeSoftKeyboard());
        onView(withId(R.id.radioMarried)).perform(ViewActions.scrollTo());
        onView(withId(R.id.spinnerCity)).perform(typeText("Ghaz"), closeSoftKeyboard());
        onView(withText("Ghaziabad")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.etPin)).perform(typeText(value.get("Pin")), closeSoftKeyboard());
        //onView(withId(R.id.radioMale)).perform(click());
        onView(withId(R.id.radioFemale)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        // onView(withId(R.id.radioSingle)).perform(click());
        onView(withId(R.id.spinnerIncome)).perform(ViewActions.scrollTo());
        onView(withId(R.id.radioMarried)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.etDateOfBirth)).perform(click());
        onView(withClassName(Matchers.equalTo(CustomDatePicker.class.getName()))).perform(PickerActions.setDate(2015, 1, 10));
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withText("OK")).perform(click());
        onView(withId(R.id.spinnerNomineeAge)).perform(ViewActions.scrollTo());
        onView(withId(R.id.spinnerOccupation)).perform(click());
        onView(withText("Self Employed")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinnerIncome)).perform(click());
        onView(withText("3 Lacs")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.etNominee)).perform(typeText(value.get("nomineeName")), closeSoftKeyboard());
        onView(withId(R.id.spinnerNomineeRelation)).perform(click());
        onView(withText("Son")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinnerNomineeAge)).perform(typeText(value.get("age")), closeSoftKeyboard());
        onView(withText("10")).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.spinnerAppointeeRelation)).perform(ViewActions.scrollTo());
        onView(withId(R.id.etAppointeeName)).perform(typeText(value.get("FullName")), closeSoftKeyboard());
        onView(withId(R.id.spinnerAppointeeRelation)).perform(click());
        onView(withText("Son")).inRoot(isPlatformPopup()).perform(click());

    }

    public void customerDetailCustomers() {
        onView(withId(R.id.company)).perform(click());
        onView(withId(R.id.etFirstName)).perform(typeText(value.get("FullName")), closeSoftKeyboard());
        onView(withId(R.id.etMobile)).perform(typeText(value.get("Mobile_number")), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(typeText(value.get("Email")), closeSoftKeyboard());
        onView(withId(R.id.etCustAddress)).perform(typeText(value.get("Address")), closeSoftKeyboard());
        onView(withId(R.id.etPin)).perform(ViewActions.scrollTo());
        Utility.wait2Seconds();
        onView(withId(R.id.spinnerCity)).perform(typeText("Ghaz"), closeSoftKeyboard());
        onView(withText("Ghaziabad")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.etPin)).perform(typeText(value.get("Pin")), closeSoftKeyboard());
        onView(withId(R.id.etNominee)).perform(ViewActions.scrollTo());
        onView(withId(R.id.etNominee)).perform(typeText(value.get("nomineeName")), closeSoftKeyboard());
        onView(withId(R.id.spinnerNomineeRelation)).perform(click());
        onView(withText("Son")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinnerNomineeAge)).perform(typeText(value.get("age")), closeSoftKeyboard());
        onView(withText("10")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinnerAppointeeRelation)).perform(ViewActions.scrollTo());
        onView(withId(R.id.etAppointeeName)).perform(typeText(value.get("FullName")), closeSoftKeyboard());
        onView(withId(R.id.spinnerAppointeeRelation)).perform(click());
        onView(withText("Son")).inRoot(isPlatformPopup()).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.etFirstName)).perform(ViewActions.scrollTo());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();

    }

    public void MoreDetails() {
        onView(withId(R.id.policyInsurer))/*.check(matches(isDisplayed()))*/.perform(click());
        onView(withText("National Insurance")).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.policyNumber)).perform(typeText(value.get("FullName")),closeSoftKeyboard());
        onView(withId(R.id.engineNo)).perform(typeText(value.get("EnginNumber")),closeSoftKeyboard());
        onView(withId(R.id.chasisNo)).perform(typeText(value.get("Chasesno")),closeSoftKeyboard());
        onView(withId(R.id.rb_carFinancedNo)).perform(click());
        onView(withId(R.id.spinnerCity)).perform(typeText("Aditya"), closeSoftKeyboard());
        onView(withText("Aditya Birla Finance")).inRoot(isPlatformPopup()).perform(click());
    }

    class MyIdleResource implements IdlingResource {

        ResourceCallback callback;
        private long startTime;

        MyIdleResource(){
            startTime = System.currentTimeMillis();
        }

        @Override
        public String getName() {
            return "MyIdleResource";
        }

        @Override
        public void registerIdleTransitionCallback(
                ResourceCallback resourceCallback) {
            this.callback = resourceCallback;
        }

        @Override
        public boolean isIdleNow() {

            boolean idle=false;

            if((startTime + 1000*60)<System.currentTimeMillis()){
                callback.onTransitionToIdle();
                idle=true;
            }

            return idle;
        }
    }

}
