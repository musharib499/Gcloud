/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.gcloud.gaadi.annotations.rules;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.gcloud.gaadi.annotations.CardNumber;
import com.gcloud.gaadi.annotations.Checked;
import com.gcloud.gaadi.annotations.ConfirmPassword;
import com.gcloud.gaadi.annotations.Email;
import com.gcloud.gaadi.annotations.FirstCharValidation;
import com.gcloud.gaadi.annotations.IpAddress;
import com.gcloud.gaadi.annotations.MobileNumber;
import com.gcloud.gaadi.annotations.NoContactNumber;
import com.gcloud.gaadi.annotations.NotAllowedChars;
import com.gcloud.gaadi.annotations.NumberRule;
import com.gcloud.gaadi.annotations.Password;
import com.gcloud.gaadi.annotations.PropertyFloor;
import com.gcloud.gaadi.annotations.RadioGrp;
import com.gcloud.gaadi.annotations.Regex;
import com.gcloud.gaadi.annotations.Required;
import com.gcloud.gaadi.annotations.RequiredName;
import com.gcloud.gaadi.annotations.Select;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.TotalFloor;
import com.gcloud.gaadi.annotations.ValueChecker;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.utils.GCLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Class contains {@code static} methods that return appropriate {@link Rule}s
 * for Saripaar annotations.
 *
 * @author Ankit Garg <ankit.garg@gaadi.com>
 */
class AnnotationToRuleConverter {
    // Debug
    static final String TAG = "AnnotationToRuleConverter";

    // Constants
    static final String WARN_TEXT = "%s - @%s can only be applied to TextView and "
            + "its subclasses.";
    static final String WARN_CHECKABLE = "%s - @%s can only be applied to Checkable, "
            + "its implementations and subclasses.";

    static final String WARN_SPINNER = "%s - @%s can only be applied to Spinner, "
            + "its implementations and subclasses.";

    public static Rule<?> getRule(Field field, View view, Annotation annotation) {
        Class<?> annotationType = annotation.annotationType();
        if (Checked.class.equals(annotationType)) {
            return getCheckedRule(field, view, (Checked) annotation);
        } else if (Required.class.equals(annotationType)) {
            return getRequiredRule(field, view, (Required) annotation);
        } else if (RequiredName.class.equals(annotationType)) {
            return getRequiredNameRule(field, view, (RequiredName) annotation);
        } else if (TextRule.class.equals(annotationType)) {
            return getTextRule(field, view, (TextRule) annotation);
        } else if (Regex.class.equals(annotationType)) {
            return getRegexRule(field, view, (Regex) annotation);
        } else if (NumberRule.class.equals(annotationType)) {
            return getNumberRule(field, view, (NumberRule) annotation);
        } else if (Password.class.equals(annotationType)) {
            return getPasswordRule(field, view, (Password) annotation);
        } else if (Email.class.equals(annotationType)) {
            return getEmailRule(field, view, (Email) annotation);
        } else if (IpAddress.class.equals(annotationType)) {
            return getIpAddressRule(field, view, (IpAddress) annotation);
        } else if (Select.class.equals(annotation)) {
            return getSelectRule(field, view, (Select) annotation);
        } else if (RadioGrp.class.equals(annotationType)) {
            return getSelectedRule(field, view, (RadioGrp) annotation);
        } else if (MobileNumber.class.equals(annotationType)) {
            return getMobileNumberRule(field, view, (MobileNumber) annotation);
        } else if (FirstCharValidation.class.equals(annotationType)) {
            return getFirstCharRule(field, view, (FirstCharValidation) annotation);
        } else if (NoContactNumber.class.equals(annotationType)) {
            return noContactNumberRule(field, view, (NoContactNumber) annotation);
        } else if (PropertyFloor.class.equals(annotationType)) {
            return getPropertyFloorRule(field, view, (PropertyFloor) annotation);
        } else if (CardNumber.class.equals(annotationType)) {
            return getCardNumberRule(field, view, (CardNumber) annotation);
        } else if (ValueChecker.class.equals(annotationType)) {
            return getValueCheckerRule(field, view, (ValueChecker) annotation);
        } else if (NotAllowedChars.class.equals(annotationType)) {
            return getNotAllowedCharsRule(field, view, (NotAllowedChars) annotation);
        }

        return null;
    }

    private static Rule<View> getNotAllowedCharsRule(Field field, View view, NotAllowedChars annotation) {
        if (!EditText.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), EditText.class.getSimpleName()));
            return null;
        }
        GCLog.e("not allowed chars rule called");

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = annotation.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : annotation.message();


        rules.add(Rules.validChars(null, annotation.notAllowedChars(), annotation.trim()));

        Rule<?>[] ruleyArray = new Rule<?>[rules.size()];
        rules.toArray(ruleyArray);

        return Rules.and(message, ruleyArray);
    }

    private static Rule<View> getValueCheckerRule(Field field, View view, ValueChecker annotation) {
        if (!EditText.class.isAssignableFrom(view.getClass())) {
            GCLog.w(Constants.TAG, String.format(WARN_TEXT, field.getName(), EditText.class.getSimpleName()));
            return null;
        }
        GCLog.e("value checker rule");

        List<Rule<?>> rules = new ArrayList<>();
        int messageResId = annotation.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : annotation.message();
        if (annotation.minValue() != Integer.MIN_VALUE) {
            rules.add(Rules.commaMinValueCheck(null, annotation.minValue(), annotation.replaceChars(), annotation.trim()));
        }

        if (annotation.maxValue() != Integer.MAX_VALUE) {
            rules.add(Rules.commaMaxValueCheck(null, annotation.maxValue(), annotation.replaceChars(), annotation.trim()));
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);
        return Rules.and(message, ruleArray);
    }

    private static Rule<View> getCardNumberRule(Field field, View view, CardNumber annotation) {
        if (!EditText.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), EditText.class.getSimpleName()));
            return null;
        }
        GCLog.e("Card number rule called");

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = annotation.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : annotation.message();

        rules.add(Rules.minLength(null, annotation.minLength(), annotation.trim()));
        rules.add(Rules.maxLength(null, annotation.maxLength(), annotation.trim()));
        rules.add(Rules.cardCheck(null, ((TextView) view).getText().toString(), annotation.trim()));

        Rule<?>[] ruleyArray = new Rule<?>[rules.size()];
        rules.toArray(ruleyArray);

        return Rules.and(message, ruleyArray);
    }

    private static Rule<View> noContactNumberRule(Field field, View view, NoContactNumber annotation) {

        if (!EditText.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), EditText.class.getSimpleName()));
            return null;
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = annotation.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : annotation.message();

    /*if (annotation.minLength() >= Constants.NUMBER_IN_OPEN_FIELD_LENGTH) {
      rules.add(Rules.minLength(null, annotation.minLength(), annotation.trim()));
    }*/

        rules.add(Rules.regex(null, "\\d{" + annotation.minLength() + ",}", annotation.trim()));

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);
    }

    private static Rule<Spinner> getSelectRule(Field field, View view,
                                               Select select) {
        if (!Spinner.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_SPINNER, field.getName(),
                            Spinner.class.getSimpleName()));
            return null;
        }

        int messageResId = select.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : select.message();

        int unexpectedSelection = select.defaultSelection();

        return Rules.spinnerNotEq(message, unexpectedSelection);
    }

    public static Rule<?> getRule(Field field, View view,
                                  Annotation annotation, Object... params) {
        Class<?> annotationType = annotation.annotationType();

        if (ConfirmPassword.class.equals(annotationType)) {
            TextView passwordTextView = (TextView) params[0];
            return getConfirmPasswordRule(field, view,
                    (ConfirmPassword) annotation, passwordTextView);
        }

        if (TotalFloor.class.equals(annotationType)) {

            TextView propertyFloorTextView = (TextView) params[0];
            return getTotalFloorsRule(field, view, (TotalFloor) annotation, propertyFloorTextView);
        }

        return (params == null || params.length == 0) ? getRule(field, view,
                annotation) : null;
    }

    private static Rule<TextView> getRequiredRule(Field field, View view,
                                                  Required required) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), Required.class.getSimpleName()));
            return null;
        }

        int messageResId = required.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : required.message();

        return Rules.required(message, required.trim());
    }

    private static Rule<TextView> getRequiredNameRule(Field field, View view,
                                                      RequiredName required) {

        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), RequiredName.class.getSimpleName()));
            return null;
        }

        int messageResId = required.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : required.message();

        return Rules.requiredName(message, required.trim());
    }

    private static Rule<View> getPropertyFloorRule(Field field, View view, PropertyFloor propFloorRule) {

        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), PropertyFloor.class.getSimpleName()));
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = propFloorRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : "PROP FLOOR";

        if (propFloorRule.minLength() > 0) {
            rules.add(Rules.minLength(null, propFloorRule.minLength(), propFloorRule.trim()));
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);
    }

    private static Rule<View> getTextRule(Field field, View view,
                                          TextRule textRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_TEXT, field.getName(),
                            TextRule.class.getSimpleName()));
            return null;
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = textRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : textRule.message();

        if (textRule.minLength() > 0) {
            rules.add(Rules.minLength(null, textRule.minLength(),
                    textRule.trim()));
        }
        if (textRule.maxLength() != Integer.MAX_VALUE) {
            rules.add(Rules.maxLength(null, textRule.maxLength(),
                    textRule.trim()));
        }

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);
    }

    private static Rule<View> getFirstCharRule(Field field, View view,
                                               FirstCharValidation charRule) {


        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(),
                    FirstCharValidation.class.getSimpleName()));
            return null;
        }
        String text = charRule.trim() ? ((TextView) view).getText().toString()
                .trim() : ((TextView) view).getText().toString();

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = charRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : charRule.message();

        rules.add(Rules.notAllowedFirstChar(null,
                charRule.notAllowedFirstChars(), charRule.trim()));
        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);

    }

    private static Rule<View> getMobileNumberRule(Field field, View view,
                                                  MobileNumber mobileRule) {

        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(),
                    MobileNumber.class.getSimpleName()));
            return null;
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = mobileRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : mobileRule.message();

        if (mobileRule.minLength() != Integer.MIN_VALUE) {
            rules.add(Rules.minLength(null, mobileRule.minLength(),
                    mobileRule.trim()));
        }
        if (mobileRule.maxLength() != Integer.MAX_VALUE) {
            rules.add(Rules.maxLength(null, mobileRule.maxLength(),
                    mobileRule.trim()));
        }

        rules.add(Rules.mobileNumber(message, mobileRule.trim()));
        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);
        return Rules.and(message, ruleArray);
    }

    private static Rule<View> getEmailRule(Field field, View view, Email email) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_TEXT, field.getName(),
                            Regex.class.getSimpleName()));
            return null;
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = email.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : email.message();

        rules.add(Rules.minLength(null, email.minLength(), email.trim()));
        if (email.minLength() == 0 && (((TextView) view).getText().toString().length() == 0)) {
            return Rules.or(null, rules.get(0));
        }


        // rules.add(Rules.eq(null, Rules.EMPTY_STRING));
        //rules.add(Rules.required(message, true));
        rules.add(Rules.regex(message, Rules.REGEX_EMAIL, true));

        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.and(message, ruleArray);
    }

    private static Rule<TextView> getRegexRule(Field field, View view,
                                               Regex regexRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_TEXT, field.getName(),
                            Regex.class.getSimpleName()));
            return null;
        }

        Context context = view.getContext();
        int messageResId = regexRule.messageResId();
        String message = messageResId != 0 ? context.getString(messageResId)
                : regexRule.message();

        int patternResId = regexRule.patternResId();
        String pattern = patternResId != 0 ? view.getContext().getString(
                patternResId) : regexRule.pattern();

        return Rules.regex(message, pattern, regexRule.trim());
    }

    private static Rule<View> getNumberRule(Field field, View view,
                                            NumberRule numberRule) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_TEXT, field.getName(),
                            NumberRule.class.getSimpleName()));
            return null;
        } else if (numberRule.type() == null) {
            throw new IllegalArgumentException(String.format(
                    "@%s.type() cannot be null.",
                    NumberRule.class.getSimpleName()));
        }

        List<Rule<?>> rules = new ArrayList<Rule<?>>();
        int messageResId = numberRule.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : numberRule.message();

        switch (numberRule.type()) {
            case INTEGER:
            case LONG:
                Rules.regex(null, Rules.REGEX_INTEGER, true);
                break;
            case FLOAT:
            case DOUBLE:
                Rules.regex(null, Rules.REGEX_DECIMAL, true);
                break;
        }

    /*if (numberRule.isNull() == true) {

      rules.add(Rules.minLength(null, 0, true));

    }*/

        if (numberRule.lt() != Double.MIN_VALUE) {
            String ltNumber = String.valueOf(numberRule.lt());
            double number = Double.parseDouble(ltNumber);
            switch (numberRule.type()) {
                case INTEGER:
                    rules.add(Rules.lt(null, ((int) number)));
                    break;
                case LONG:
                    rules.add(Rules.lt(null, ((long) number)));
                    break;
                case FLOAT:
                    rules.add(Rules.lt(null, Float.parseFloat(ltNumber)));
                    break;
                case DOUBLE:
                    rules.add(Rules.lt(null, Double.parseDouble(ltNumber)));
                    break;
            }
        }
        if (numberRule.gt() != Double.MAX_VALUE) {
            String gtNumber = String.valueOf(numberRule.gt());
            double number = Double.parseDouble(gtNumber);
            switch (numberRule.type()) {
                case INTEGER:
                    rules.add(Rules.gt(null, ((int) number)));
                    break;
                case LONG:
                    rules.add(Rules.gt(null, ((long) number)));
                    break;
                case FLOAT:
                    rules.add(Rules.gt(null, Float.parseFloat(gtNumber)));
                    break;
                case DOUBLE:
                    rules.add(Rules.gt(null, Double.parseDouble(gtNumber)));
                    break;
            }
        }
        if (numberRule.eq() != Double.MAX_VALUE) {
            String eqNumber = String.valueOf(numberRule.eq());
            double number = Double.parseDouble(eqNumber);
            switch (numberRule.type()) {
                case INTEGER:
                    rules.add(Rules.eq(null, ((int) number)));
                    break;
                case LONG:
                    rules.add(Rules.eq(null, ((long) number)));
                    break;
                case FLOAT:
                    rules.add(Rules.eq(null, Float.parseFloat(eqNumber)));
                    break;
                case DOUBLE:
                    rules.add(Rules.eq(null, Double.parseDouble(eqNumber)));
                    break;
            }
        }


        Rule<?>[] ruleArray = new Rule<?>[rules.size()];
        rules.toArray(ruleArray);

        return Rules.or(message, ruleArray);
    }

    private static Rule<TextView> getPasswordRule(Field field, View view,
                                                  Password password) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_TEXT, field.getName(),
                            Password.class.getSimpleName()));
            return null;
        }

        int messageResId = password.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : password.message();

        return Rules.required(message, false);
    }

    private static Rule<TextView> getConfirmPasswordRule(Field field,
                                                         View view, ConfirmPassword confirmPassword,
                                                         TextView passwordTextView) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(),
                    ConfirmPassword.class.getSimpleName()));
            return null;
        }

        int messageResId = confirmPassword.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : confirmPassword.message();

        return Rules.eq(message, passwordTextView);
    }

    private static Rule<TextView> getTotalFloorsRule(Field field,
                                                     View view,
                                                     TotalFloor totalFloor,
                                                     TextView propertyFloorTextView) {

        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_TEXT, field.getName(), TotalFloor.class.getSimpleName()));
            return null;
        }

        int messageResId = totalFloor.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(messageResId) : "Invalid tot Floors";

        return Rules.gt(message, propertyFloorTextView);
    }

    private static Rule<View> getIpAddressRule(Field field, View view,
                                               IpAddress ipAddress) {
        if (!TextView.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG,
                    String.format(WARN_TEXT, field.getName(),
                            IpAddress.class.getSimpleName()));
            return null;
        }

        int messageResId = ipAddress.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : ipAddress.message();

        return Rules.or(message, Rules.eq(null, Rules.EMPTY_STRING),
                Rules.regex(message, Rules.REGEX_IP_ADDRESS, true));
    }

    private static <T extends View & Checkable> Rule<T> getCheckedRule(
            Field field, View view, Checked checked) {

        if (!Checkable.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_CHECKABLE, field.getName(),
                    Checked.class.getSimpleName()));
            return null;
        }

        int messageResId = checked.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : checked.message();

        return Rules.checked(message, checked.checked());
    }

    private static <T extends RadioGroup> Rule<T> getSelectedRule(
            Field field, View view, RadioGrp radioGroup) {

        if (!RadioGroup.class.isAssignableFrom(view.getClass())) {
            GCLog.w(TAG, String.format(WARN_CHECKABLE, field.getName(),
                    RadioGrp.class.getSimpleName()));
        }

        int messageResId = radioGroup.messageResId();
        String message = messageResId != 0 ? view.getContext().getString(
                messageResId) : radioGroup.message();

        return Rules.isRadioButtonSelected(message);
    }

}
