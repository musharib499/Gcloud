package com.gcloud.gaadi.Insurance;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.NumberPicker;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.insurance.MySeekBar;
import com.gcloud.gaadi.insurance.NewInsuranceDashboard;
import com.gcloud.gaadi.ui.Utility;
import com.gcloud.gaadi.utils.CommonUtils;
import com.gcloud.gaadi.utils.GCLog;
import com.github.jjobes.slidedatetimepicker.CustomDatePicker;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
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
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assume.assumeTrue;

/**
 * Created by dipanshugarg on 7/1/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InsuranceDashboardTest {
    @Rule
    public ActivityTestRule<NewInsuranceDashboard> mTestRule =
            new ActivityTestRule<NewInsuranceDashboard>(NewInsuranceDashboard.class);

    HashMap<String,String> data=new HashMap<String,String>(){
        { put("REG NO", "REG NO");
            put("MAKE", "MAKE");
            put("MODEL", "MODEL");
            put("FUEL TYPE", "FUEL TYPE");
            put("YEAR", "YEAR");
            put("PRICE", "PRICE");
            put("KM", "KM");
            put("CERTIFIED", "CERTIFIED");}};

    HashMap<String,String> value = new HashMap<String,String>() {
        {
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
            put("RegNo","UP14AS1566");

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

    public static ViewAction setDate(final int year, final int monthOfYear, final int dayOfMonth) {

        return new ViewAction() {

            @Override
            public void perform(UiController uiController, View view) {
                final CustomDatePicker dayPickerView = (CustomDatePicker) view;

                try {
//                    Field f = null; //NoSuchFieldException
//                    f = DayPickerView.class.getDeclaredField("mController");
//                    f.setAccessible(true);
//                    CustomCalender controller = (CustomCalender) f.get(dayPickerView); //IllegalAccessException
//                    controller.setSimpleDateFormate(dayOfMonth + monthOfYear + year + "");

                    dayPickerView.updateDate(year, monthOfYear, dayOfMonth);
                    dayPickerView.invalidate();

                    // Create an instance of the id class
                    Class<?> idClass = Class.forName("com.android.internal.R$id");

                    // Get the fields that store the resource IDs for the month, day and year NumberPickers
                    Field dayField = idClass.getField("day");
                    Field monthField = idClass.getField("month");
                    Field yearField = idClass.getField("year");

                    // Use the resource IDs to get references to the month, day and year NumberPickers
                    NumberPicker dayNumberPicker = (NumberPicker) view.findViewById(dayField.getInt(null));
                    NumberPicker monthNumberPicker = (NumberPicker) view.findViewById(monthField.getInt(null));
                    NumberPicker yearNumberPicker = (NumberPicker) view.findViewById(yearField.getInt(null));

                    dayNumberPicker.setValue(22);
                    monthNumberPicker.setValue(01);
                    yearNumberPicker.setValue(2016);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String getDescription() {
                return "set date";
            }

            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(CustomDatePicker.class), isDisplayed());
            }
        };

    }

    @Test
    @MediumTest
    public void test1() {
        //All Cases Unit Test
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.totalCases)).perform(click());
        Utility.wait2Seconds();
        pressBack();
    }

    @Test
    @MediumTest
    public void test2() {
        //Inspected Car Layout Test case
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getInspectedCarsListLayout)).perform(click());
        Utility.wait2Seconds();
        pressBack();
    }

    @Test
    @MediumTest
    public void test3() {
        //Filter Test Case apply Cancel and Reset
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getInspectedCarsListLayout)).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.fab)).perform(click());
        onView(withText("Filter")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
        //onView(withText(R.string.ford)).perform(click());
        onView(withText(R.string.chevrolet)).perform(click());
        onView(withText(data.get("MODEL"))).check(ViewAssertions.matches(withText(data.get("MODEL")))).perform(click());
        //onView(withText("Figo")).perform(click());
        onView(withText("Beat")).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("FUEL TYPE"))).check(ViewAssertions.matches(withText(data.get("FUEL TYPE")))).perform(click());
        //onView(withText("Diesel")).perform(click());
        //onView(withText("LPG")).perform(click());
        onView(withText("Petrol")).perform(click());
        Utility.wait2Seconds();

        onView(withText(data.get("YEAR"))).check(ViewAssertions.matches(withText(data.get("YEAR")))).perform(click());
        //onView(withText("2015")).perform(click());
        //onView(withText("2014")).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("PRICE"))).check(ViewAssertions.matches(withText(data.get("PRICE")))).perform(click());
        //onView(withText("1 Lac - 2 Lacs")).perform(click());
        // onView(withText("2 Lacs - 3 Lacs")).perform(click());
        Utility.wait2Seconds();
        //onView(withText(data.get("KM"))).check(ViewAssertions.matches(withText(data.get("KM")))).perform(click());
        // onView(withText("Below 20,000 Kms")).perform(click());
        onView(withId(R.id.resetFilter)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.filter_cancel)).perform(click());

        Utility.wait2Seconds();
    }

    @Test
    @MediumTest
    public void test4() {

        //Filter Test Case
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getInspectedCarsListLayout)).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.fab)).perform(click());
        onView(withText("Filter")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
        //onView(withText(R.string.ford)).perform(click());
        onView(withText(R.string.chevrolet)).perform(click());
        onView(withText(data.get("MODEL"))).check(ViewAssertions.matches(withText(data.get("MODEL")))).perform(click());
        //onView(withText("Figo")).perform(click());
        onView(withText("Beat")).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("FUEL TYPE"))).check(ViewAssertions.matches(withText(data.get("FUEL TYPE")))).perform(click());
        //onView(withText("Diesel")).perform(click());
        //onView(withText("LPG")).perform(click());
        onView(withText("Petrol")).perform(click());
        Utility.wait2Seconds();

        onView(withText(data.get("YEAR"))).check(ViewAssertions.matches(withText(data.get("YEAR")))).perform(click());
        //onView(withText("2015")).perform(click());
        //onView(withText("2014")).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("PRICE"))).check(ViewAssertions.matches(withText(data.get("PRICE")))).perform(click());
        //onView(withText("1 Lac - 2 Lacs")).perform(click());
        // onView(withText("2 Lacs - 3 Lacs")).perform(click());
        Utility.wait2Seconds();
        //onView(withText(data.get("KM"))).check(ViewAssertions.matches(withText(data.get("KM")))).perform(click());
        // onView(withText("Below 20,000 Kms")).perform(click());

        onView(withId(R.id.apply_filter)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        ClickonRecyclerView();



    }

    public void ClickonRecyclerView() {
        onView(withRecyclerView(R.id.cardList).atPositionOnView(0, R.id.stockImage)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        Utility.wait2Seconds();
    }

    @Test
    @MediumTest
    public void test5()  {
        // In case of vehicle owner individual selected
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getInspectedCarsListLayout)).perform(click());
        Utility.wait2Seconds();
        onView(withRecyclerView(R.id.cardList).atPositionOnView(1, R.id.stockImage)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.seeb_bar)).perform(setProgress(2)).perform(click());
        onView(withText("Zero Depreciation Cover")).perform(click());
        onView(withText("\u20B9 5,000")).perform(click());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.seeb_bar)).perform(setProgress(2)).perform(click());
        //onView(withText("\u20B9 5,000")).perform(click());     //  onView(withId(R.id.fab)).perform(click());

   /*     onView(withText("Filter")).check(ViewAssertions.matches(isDisplayed()));
      onView(withText(data.get("REQ NO"))).check(ViewAssertions.matches(withText(data.get("REQ NO")))).perform(click());
        onView(withId(R.id.reg_no)).perform(typeText(data.get("RequestId")),closeSoftKeyboard());
*/

       /* onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
        onView(withText(data.get("REGNO"))).perform(typeText(data.get("RequestId")), closeSoftKeyboard());
        onView(withText("APPLY")).perform(click());
*/
      /*  onView(withText(data.get("MAKE"))).check(ViewAssertions.matches(withText(data.get("MAKE")))).perform(click());
        //onView(withText(R.string.ford)).perform(click());
        onView(withText(R.string.chevrolet)).perform(click());
        onView(withText(data.get("MODEL"))).check(ViewAssertions.matches(withText(data.get("MODEL")))).perform(click());
        //onView(withText("Figo")).perform(click());
        onView(withText("Beat")).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("FUEL TYPE"))).check(ViewAssertions.matches(withText(data.get("FUEL TYPE")))).perform(click());
        //onView(withText("Diesel")).perform(click());
        //onView(withText("LPG")).perform(click());
        onView(withText("Petrol")).perform(click());
        Utility.wait2Seconds();

        onView(withText(data.get("YEAR"))).check(ViewAssertions.matches(withText(data.get("YEAR")))).perform(click());
        //onView(withText("2015")).perform(click());
        //onView(withText("2014")).perform(click());
        Utility.wait2Seconds();
        onView(withText(data.get("PRICE"))).check(ViewAssertions.matches(withText(data.get("PRICE")))).perform(click());
        //onView(withText("1 Lac - 2 Lacs")).perform(click());
        // onView(withText("2 Lacs - 3 Lacs")).perform(click());
        Utility.wait2Seconds();
        //onView(withText(data.get("KM"))).check(ViewAssertions.matches(withText(data.get("KM")))).perform(click());
        // onView(withText("Below 20,000 Kms")).perform(click());
        onView(withId(R.id.resetFilter)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.filter_cancel)).perform(click());

        Utility.wait2Seconds();*/
        onView(withText("APPLY")).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        //onView(withId(R.id.dropDownArrow)).perform(click());
        onView(withRecyclerView(R.id.rv).atPositionOnView(0, R.id.buyNow)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.individual)).perform(click());
        onView(withId(R.id.etFirstName)).perform(typeText(value.get("FullName")), closeSoftKeyboard());
        onView(withId(R.id.etMobile)).perform(typeText(value.get("Mobile_number")), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(typeText(value.get("Email")), closeSoftKeyboard());
        onView(withId(R.id.etCustAddress)).perform(typeText(value.get("Address")), closeSoftKeyboard());
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
        onView(withId(R.id.etAppointeeName)).perform(typeText(value.get("nomineeName")), closeSoftKeyboard());
        onView(withId(R.id.spinnerAppointeeRelation)).perform(click());
        onView(withText("Son")).inRoot(isPlatformPopup()).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.etFirstName)).perform(ViewActions.scrollTo());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());
        Utility.wait2Seconds();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        GCLog.e("dipanshu", currentDateTimeString + "");
        GCLog.e("dipanshu", "" + currentDateTimeString.substring(0, 2));
        onView(withId(R.id.policyStartDate)).perform(click());
        onView(withClassName(Matchers.equalTo(CustomDatePicker.class.getName()))).perform(PickerActions.setDate(2015, 1, 10));
        onView(withText("OK")).perform(click());
//        int value = Integer.parseInt(currentDateTimeString.substring(0, 2).trim());
//        if(value <= 14) {
//            // onView(withText(value)).perform(ViewActions.swipeUp());
//            // onView(withText(value)).perform(withCustomConstraints(swipeUp(), isDisplayingAtLeast(16)));
//        }
//        onView(withText("OK")).perform(click());



       /* onView(withId(R.id.policyStartDate)).perform(typeText(value.get("dateStart")), closeSoftKeyboard());
        onView(withId(R.id.policyEndDateText)).perform(typeText(value.get("dateEnd")), closeSoftKeyboard());
        onView(withId(R.id.tv_regDate)).perform(click());
        onView(withText("1")).inRoot(isPlatformPopup()).perform(click());*/
    }

    @Test
    @MediumTest
    public void test6()  {
        // In case of vehicle owner Company selected
        Context context = InstrumentationRegistry.getTargetContext();
        assumeTrue(CommonUtils.getBooleanSharedPreference(context, Constants.USER_LOGGEDIN, false));
        onView(withId(R.id.getInspectedCarsListLayout)).perform(click());
        Utility.wait2Seconds();
        onView(withRecyclerView(R.id.cardList).atPositionOnView(1, R.id.stockImage)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.seeb_bar)).perform(setProgress(2)).perform(click());
        onView(withText("Zero Depreciation Cover")).perform(click());
        onView(withText("\u20B9 5,000")).perform(click());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.seeb_bar)).perform(setProgress(2)).perform(click());
        //onView(withText("\u20B9 5,000")).perform(click());
        onView(withText("APPLY")).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        //onView(withId(R.id.dropDownArrow)).perform(click());
        onView(withRecyclerView(R.id.rv).atPositionOnView(0, R.id.buyNow)).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();
        onView(withId(R.id.company)).perform(click());
        onView(withId(R.id.etFirstName)).perform(typeText(value.get("FullName")), closeSoftKeyboard());
        onView(withId(R.id.etMobile)).perform(typeText(value.get("Mobile_number")), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(typeText(value.get("Email")), closeSoftKeyboard());
        onView(withId(R.id.etCustAddress)).perform(typeText(value.get("Address")), closeSoftKeyboard());
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
        onView(withId(R.id.etAppointeeName)).perform(typeText(value.get("nomineeName")), closeSoftKeyboard());
        onView(withId(R.id.spinnerAppointeeRelation)).perform(click());
        onView(withText("Son")).inRoot(isPlatformPopup()).perform(click());
        Utility.wait2Seconds();
        onView(withId(R.id.etFirstName)).perform(ViewActions.scrollTo());
        onView(withText("Next")).check(matches(isDisplayed())).perform(click());
        Utility.wait2Seconds();
        Utility.wait2Seconds();


    }
}
