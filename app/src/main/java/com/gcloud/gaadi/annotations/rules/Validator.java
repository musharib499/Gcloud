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

import android.os.AsyncTask;
import android.view.View;
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
import com.gcloud.gaadi.annotations.RequiredNameWithDash;
import com.gcloud.gaadi.annotations.Select;
import com.gcloud.gaadi.annotations.TextRule;
import com.gcloud.gaadi.annotations.TotalFloor;
import com.gcloud.gaadi.annotations.ValueChecker;
import com.gcloud.gaadi.utils.GCLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A processor that checks all the {@link Rule}s against their {@link android.view.View}s.
 *
 * @author Ankit Garg <ankit.garg@gaadi.com>
 */
public class Validator {
    // Debug
    static final String TAG = "Validator";
    static final boolean DEBUG = true;

    private Object mController;
    private boolean mAnnotationsProcessed;
    private List<ViewRulePair> mViewsAndRules;
    private Map<String, Object> mProperties;
    private AsyncTask<Void, Void, ViewRulePair> mAsyncValidationTask;
    private ValidationListener mValidationListener;

    /**
     * Private constructor. Cannot be instantiated.
     */
    private Validator() {
        mAnnotationsProcessed = false;
        mViewsAndRules = new ArrayList<ViewRulePair>();
        mProperties = new HashMap<String, Object>();
    }

    /**
     * Creates a new {@link com.gcloud.gaadi.annotations.rules.Validator}.
     *
     * @param controller The instance that holds references to the Views that are
     *                   being validated. Usually an {@code Activity} or a {@code Fragment}. Also accepts
     *                   controller instances that have annotated {@code View} references.
     */
    public Validator(Object controller) {
        this();
        if (controller == null) {
            throw new IllegalArgumentException("'controller' cannot be null");
        }
        mController = controller;
    }

    /**
     * Interface definition for a callback to be invoked when {@code validate()} is called.
     */
    public interface ValidationListener {

        /**
         * Called when all the {@link Rule}s added to this Validator are valid.
         */
        public void onValidationSucceeded();

        /**
         * Called if any of the {@link Rule}s fail.
         *
         * @param failedView The {@link android.view.View} that did not pass validation.
         * @param failedRule The failed {@link Rule} associated with the {@link android.view.View}.
         */
        public void onValidationFailed(View failedView, Rule<?> failedRule);

        /**
         * Called when single item was validated successfully. This method may be helpful when
         * some error status was set up when validation failed and you need to remove it after
         * error was fixed.
         *
         * @param succeededView The {@link android.view.View} that was successfully validated.
         */
        public void onViewValidationSucceeded(View succeededView);
    }

    /**
     * Add a {@link android.view.View} and it's associated {@link Rule} to the Validator.
     *
     * @param view The {@link android.view.View} to be validated.
     * @param rule The {@link Rule} associated with the view.
     * @throws IllegalArgumentException If {@code rule} is {@code null}.
     */
    public void put(View view, Rule<?> rule) {
        if (rule == null) {
            throw new IllegalArgumentException("'rule' cannot be null");
        }

        mViewsAndRules.add(new ViewRulePair(view, rule));
    }

    /**
     * Convenience method for adding multiple {@link Rule}s for a single {@link android.view.View}.
     *
     * @param view  The {@link android.view.View} to be validated.
     * @param rules {@link java.util.List} of {@link Rule}s associated with the view.
     * @throws IllegalArgumentException If {@code rules} is {@code null}.
     */
    public void put(View view, List<Rule<?>> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("\'rules\' cannot be null");
        }

        for (Rule<?> rule : rules) {
            put(view, rule);
        }
    }

    /**
     * Convenience method for adding just {@link Rule}s to the Validator.
     *
     * @param rule A {@link Rule}, usually composite or custom.
     */
    public void put(Rule<?> rule) {
        put(null, rule);
    }

    /**
     * Validate all the {@link Rule}s against their {@link android.view.View}s.
     *
     * @throws IllegalStateException If a {@link com.gcloud.gaadi.annotations.rules.Validator.ValidationListener} is not registered.
     */
    public synchronized void validate() {
        if (mValidationListener == null) {
            throw new IllegalStateException("Set a " + ValidationListener.class.getSimpleName() +
                    " before attempting to validate.");
        }

        ViewRulePair failedViewRulePair = validateAllRules();
        if (failedViewRulePair == null) {
            mValidationListener.onValidationSucceeded();
        } else {
            mValidationListener.onValidationFailed(failedViewRulePair.view, failedViewRulePair.rule);
        }
    }

    /**
     * Asynchronously validates all the {@link Rule}s against their {@link android.view.View}s. Subsequent calls
     * to this method will cancel any pending asynchronous validations and start a new one.
     *
     * @throws IllegalStateException If a {@link com.gcloud.gaadi.annotations.rules.Validator.ValidationListener} is not registered.
     */
    public void validateAsync() {
        if (mValidationListener == null) {
            throw new IllegalStateException("Set a " + ValidationListener.class.getSimpleName() +
                    " before attempting to validate.");
        }

        // Cancel the existing task
        if (mAsyncValidationTask != null) {
            mAsyncValidationTask.cancel(true);
            mAsyncValidationTask = null;
        }

        // Start a new one ;)
        mAsyncValidationTask = new AsyncTask<Void, Void, ViewRulePair>() {

            @Override
            protected ViewRulePair doInBackground(Void... params) {
                return validateAllRules();
            }

            @Override
            protected void onPostExecute(ViewRulePair pair) {
                if (pair == null) {
                    mValidationListener.onValidationSucceeded();
                } else {
                    mValidationListener.onValidationFailed(pair.view, pair.rule);
                }

                mAsyncValidationTask = null;
            }

            @Override
            protected void onCancelled() {
                mAsyncValidationTask = null;
            }
        };

        mAsyncValidationTask.execute((Void[]) null);
    }

    /**
     * Used to find if the asynchronous validation task is running, useful only when you run the
     * Validator in asynchronous mode using the {@code validateAsync} method.
     *
     * @return True if the asynchronous task is running, false otherwise.
     */
    public boolean isValidating() {
        return mAsyncValidationTask != null &&
                mAsyncValidationTask.getStatus() != AsyncTask.Status.FINISHED;
    }

    /**
     * Cancels the asynchronous validation task if running, useful only when you run the
     * Validator in asynchronous mode using the {@code validateAsync} method.
     *
     * @return True if the asynchronous task was cancelled.
     */
    public boolean cancelAsync() {
        boolean cancelled = false;
        if (mAsyncValidationTask != null) {
            cancelled = mAsyncValidationTask.cancel(true);
            mAsyncValidationTask = null;
        }

        return cancelled;
    }

    /**
     * Returns the callback registered for this Validator.
     *
     * @return The callback, or null if one is not registered.
     */
    public ValidationListener getValidationListener() {
        return mValidationListener;
    }

    /**
     * Register a callback to be invoked when {@code validate()} is called.
     *
     * @param validationListener The callback that will run.
     */
    public void setValidationListener(ValidationListener validationListener) {
        this.mValidationListener = validationListener;
    }

    /**
     * Updates a property value if it exists, else creates a new one.
     *
     * @param name  The property name.
     * @param value Value of the property.
     * @throws IllegalArgumentException If {@code name} is {@code null}.
     */
    public void setProperty(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("\'name\' cannot be null");
        }

        mProperties.put(name, value);
    }

    /**
     * Retrieves the value of the given property.
     *
     * @param name The property name.
     * @return Value of the property or {@code null} if the property does not exist.
     * @throws IllegalArgumentException If {@code name} is {@code null}.
     */
    public Object getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("\'name\' cannot be null");
        }

        return mProperties.get(name);
    }

    /**
     * Removes the property from this Validator.
     *
     * @param name The property name.
     * @return The value of the removed property or {@code null} if the property was not found.
     */
    public Object removeProperty(String name) {
        return name != null ? mProperties.remove(name) : null;
    }

    /**
     * Checks if the specified property exists in this Validator.
     *
     * @param name The property name.
     * @return True if the property exists.
     */
    public boolean containsProperty(String name) {
        return name != null ? mProperties.containsKey(name) : false;
    }

    /**
     * Removes all properties from this Validator.
     */
    public void removeAllProperties() {
        mProperties.clear();
    }

    /**
     * Removes all the rules for the matching {@link android.view.View}
     *
     * @param view The {@code View} whose rules must be removed.
     */
    public void removeRulesFor(View view) {
        if (view == null) {
            throw new IllegalArgumentException("'view' cannot be null");
        }

        int index = 0;
        while (index < mViewsAndRules.size()) {
            ViewRulePair pair = mViewsAndRules.get(index);
            if (pair.view == view) {
                mViewsAndRules.remove(index);
                continue;
            }

            index++;
        }
    }

    /**
     * Validates all rules added to this Validator.
     *
     * @return {@code null} if all {@code Rule}s are valid, else returns the failed
     * {@code ViewRulePair}.
     */
    private ViewRulePair validateAllRules() {
//        if (!mAnnotationsProcessed) {
        mViewsAndRules.clear();
        createRulesFromAnnotations(getSaripaarAnnotatedFields());
//            mAnnotationsProcessed = true;
//        }

        if (mViewsAndRules.size() == 0) {
            GCLog.e("No rules found. Passing validation by default.");
            return null;
        }

        ViewRulePair failedViewRulePair = null;
        for (ViewRulePair pair : mViewsAndRules) {
            if (pair == null) continue;

            // Validate views only if they are visible and enabled
            if (pair.view != null) {
                if (!pair.view.isShown() || !pair.view.isEnabled()) continue;
            }

            if (!pair.rule.isValid(pair.view)) {

                failedViewRulePair = pair;
                break;
            } else {
                mValidationListener.onViewValidationSucceeded(pair.view);
            }
        }

        // MyLog.e(TAG, "Failed rule: " + failedViewRulePair.rule.getFailureMessage() + ": " + failedViewRulePair.view.toString());
        return failedViewRulePair;
    }

    private void createRulesFromAnnotations(List<AnnotationFieldPair> annotationFieldPairs) {
        TextView passwordTextView = null;
        TextView confirmPasswordTextView = null;
        TextView propertyFloorTextView = null;
        TextView totalFloorTextView = null;

        for (AnnotationFieldPair pair : annotationFieldPairs) {

            // Password
            if (pair.annotation.annotationType().equals(Password.class)) {
                if (passwordTextView == null) {
                    passwordTextView = (TextView) getView(pair.field);
                } else {
                    throw new IllegalStateException("You cannot annotate " +
                            "two fields in the same Activity with @Password.");
                }
            }

            // Confirm password
            if (pair.annotation.annotationType().equals(ConfirmPassword.class)) {
                if (passwordTextView == null) {
                    throw new IllegalStateException("A @Password annotated field is required " +
                            "before you can use @ConfirmPassword.");
                } else if (confirmPasswordTextView != null) {
                    throw new IllegalStateException("You cannot annotate " +
                            "two fields in the same Activity with @ConfirmPassword.");
                } else if (confirmPasswordTextView == null) {
                    confirmPasswordTextView = (TextView) getView(pair.field);
                }
            }

            //Property Floor
            if (pair.annotation.annotationType().equals(PropertyFloor.class)) {
                if (propertyFloorTextView == null) {
                    propertyFloorTextView = (TextView) getView(pair.field);
                } else {
                    throw new IllegalStateException("You cannot annotate " +
                            "two fields in the same Activity with @Password.");
                }
            }

            //Total Floors
            if (pair.annotation.annotationType().equals(TotalFloor.class)) {

                if (propertyFloorTextView == null) {
                    throw new IllegalStateException("A @PropertyFloor annotated field is required " +
                            "before you can use @TotalFloor.");
                } else if (totalFloorTextView != null) {
                    throw new IllegalStateException("You cannot annotate " +
                            "two fields in the same Activity with @TotalFloor");
                } else if (totalFloorTextView == null) {
                    totalFloorTextView = (TextView) getView(pair.field);
                }
            }

            // Others
            ViewRulePair viewRulePair = null;
            if (pair.annotation.annotationType().equals(ConfirmPassword.class)) {
                viewRulePair = getViewAndRule(pair.field, pair.annotation, passwordTextView);
            } else {
                viewRulePair = getViewAndRule(pair.field, pair.annotation);
            }

            if (pair.annotation.annotationType().equals(TotalFloor.class)) {

                viewRulePair = getViewAndRule(pair.field, pair.annotation, propertyFloorTextView);
            } else {
                viewRulePair = getViewAndRule(pair.field, pair.annotation);
            }

            if (viewRulePair != null) {
                if (DEBUG) {
                    GCLog.d(String.format("Added @%s rule for %s.",
                            pair.annotation.annotationType().getSimpleName(),
                            pair.field.getName()));
                }
                mViewsAndRules.add(viewRulePair);
            }
        }
    }

    private ViewRulePair getViewAndRule(Field field, Annotation annotation, Object... params) {
        View view = getView(field);
        if (view == null) {
            GCLog.w(TAG, String.format("Your %s - %s is null. Please check your field assignment(s).",
                    field.getType().getSimpleName(), field.getName()));
            return null;
        }

        Rule<?> rule = null;
        if (params != null && params.length > 0) {
            rule = AnnotationToRuleConverter.getRule(field, view, annotation, params);
        } else {
            rule = AnnotationToRuleConverter.getRule(field, view, annotation);
        }

        return rule != null ? new ViewRulePair(view, rule) : null;
    }

    private View getView(Field field) {
        try {
            field.setAccessible(true);
            Object instance = mController;

            return (View) field.get(instance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<AnnotationFieldPair> getSaripaarAnnotatedFields() {
        List<AnnotationFieldPair> annotationFieldPairs = new ArrayList<AnnotationFieldPair>();
        List<Field> fieldsWithAnnotations = getViewFieldsWithAnnotations();

        for (Field field : fieldsWithAnnotations) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (isSaripaarAnnotation(annotation)) {
                    annotationFieldPairs.add(new AnnotationFieldPair(annotation, field));
                }
            }
        }

        Collections.sort(annotationFieldPairs, new AnnotationFieldPairCompartor());

        return annotationFieldPairs;
    }

    private List<Field> getViewFieldsWithAnnotations() {
        List<Field> fieldsWithAnnotations = new ArrayList<Field>();
        List<Field> viewFields = getAllViewFields();
        for (Field field : viewFields) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            fieldsWithAnnotations.add(field);
        }
        return fieldsWithAnnotations;
    }

    private List<Field> getAllViewFields() {
        List<Field> viewFields = new ArrayList<Field>();

        // Declared fields
        Class<?> superClass = null;
        if (mController != null) {
            viewFields.addAll(getDeclaredViewFields(mController.getClass()));
            superClass = mController.getClass().getSuperclass();
        }

        // Inherited fields
        while (superClass != null && !superClass.equals(Object.class)) {
            List<Field> declaredViewFields = getDeclaredViewFields(superClass);
            if (declaredViewFields.size() > 0) {
                viewFields.addAll(declaredViewFields);
            }
            superClass = superClass.getSuperclass();
        }

        return viewFields;
    }

    private List<Field> getDeclaredViewFields(Class<?> clazz) {
        List<Field> viewFields = new ArrayList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            if (View.class.isAssignableFrom(f.getType())) {
                viewFields.add(f);
            }
        }
        return viewFields;
    }

    private boolean isSaripaarAnnotation(Annotation annotation) {
        Class<?> annotationType = annotation.annotationType();
        return annotationType.equals(Checked.class) ||
                annotationType.equals(ConfirmPassword.class) ||
                annotationType.equals(Email.class) ||
                annotationType.equals(IpAddress.class) ||
                annotationType.equals(NumberRule.class) ||
                annotationType.equals(Password.class) ||
                annotationType.equals(Regex.class) ||
                annotationType.equals(Required.class) ||
                annotationType.equals(Select.class) ||
                annotationType.equals(TextRule.class) ||
                annotationType.equals(RadioGrp.class) ||
                annotationType.equals(FirstCharValidation.class) ||
                annotationType.equals(MobileNumber.class) ||
                annotationType.equals(TotalFloor.class) ||
                annotationType.equals(PropertyFloor.class) ||
                annotationType.equals(NoContactNumber.class) ||
                annotationType.equals(ValueChecker.class) ||
                annotationType.equals(RequiredName.class) ||
                annotationType.equals(NotAllowedChars.class) ||
                annotationType.equals(RequiredNameWithDash.class) ||
                annotationType.equals(CardNumber.class);
    }

    /**
     * @author ankitgarg
     * @created 10-Jan-2014 12:59:01 PM
     */
    private class ViewRulePair {
        public View view;
        public Rule rule;

        public ViewRulePair(View view, Rule<?> rule) {
            this.view = view;
            this.rule = rule;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "ViewRulePair [view=" + view + ", rule=" + rule.getFailureMessage() + "]";
        }

    }

    private class AnnotationFieldPair {
        public Annotation annotation;
        public Field field;

        public AnnotationFieldPair(Annotation annotation, Field field) {
            this.annotation = annotation;
            this.field = field;
        }
    }

    private class AnnotationFieldPairCompartor implements Comparator<AnnotationFieldPair> {

        @Override
        public int compare(AnnotationFieldPair lhs, AnnotationFieldPair rhs) {
            int lhsOrder = getAnnotationOrder(lhs.annotation);
            int rhsOrder = getAnnotationOrder(rhs.annotation);
            return lhsOrder < rhsOrder ? -1 : lhsOrder == rhsOrder ? 0 : 1;
        }

        private int getAnnotationOrder(Annotation annotation) {
            Class<?> annotatedClass = annotation.annotationType();
            if (annotatedClass.equals(Checked.class)) {
                return ((Checked) annotation).order();

            } else if (annotatedClass.equals(ConfirmPassword.class)) {
                return ((ConfirmPassword) annotation).order();

            } else if (annotatedClass.equals(Email.class)) {
                return ((Email) annotation).order();

            } else if (annotatedClass.equals(IpAddress.class)) {
                return ((IpAddress) annotation).order();

            } else if (annotatedClass.equals(NumberRule.class)) {
                return ((NumberRule) annotation).order();

            } else if (annotatedClass.equals(Password.class)) {
                return ((Password) annotation).order();

            } else if (annotatedClass.equals(Regex.class)) {
                return ((Regex) annotation).order();

            } else if (annotatedClass.equals(Required.class)) {
                return ((Required) annotation).order();

            } else if (annotatedClass.equals(Select.class)) {
                return ((Select) annotation).order();

            } else if (annotatedClass.equals(TextRule.class)) {
                return ((TextRule) annotation).order();

            } else if (annotatedClass.equals(RadioGrp.class)) {
                return ((RadioGrp) annotation).order();

            } else if (annotatedClass.equals(MobileNumber.class)) {
                return ((MobileNumber) annotation).order();

            } else if (annotatedClass.equals(FirstCharValidation.class)) {
                return ((FirstCharValidation) annotation).order();

            } else if (annotatedClass.equals(PropertyFloor.class)) {
                return ((PropertyFloor) annotation).order();

            } else if (annotatedClass.equals(TotalFloor.class)) {
                return ((TotalFloor) annotation).order();

            } else if (annotatedClass.equals(NoContactNumber.class)) {
                return ((NoContactNumber) annotation).order();

            } else if (annotatedClass.equals(CardNumber.class)) {
                return ((CardNumber) annotation).order();

            } else if (annotatedClass.equals(ValueChecker.class)) {
                return ((ValueChecker) annotation).order();

            } else if (annotatedClass.equals(RequiredName.class)) {
                return ((RequiredName) annotation).order();

            } else if (annotatedClass.equals(RequiredNameWithDash.class)) {
                return ((RequiredNameWithDash) annotation).order();

            } else if (annotatedClass.equals(NotAllowedChars.class)) {
                return ((NotAllowedChars) annotation).order();

            } else {
                throw new IllegalArgumentException(String.format("%s is not a Saripaar annotation",
                        annotatedClass.getName()));
            }
        }
    }

}
