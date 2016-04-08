package com.gcloud.gaadi.annotations;

import com.gcloud.gaadi.annotations.rules.Rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ankitgarg on 28/05/15.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueChecker {
    public int order();

    public int minValue() default Integer.MIN_VALUE;

    public int maxValue() default Integer.MAX_VALUE;

    public boolean trim() default true;

    public String replaceChars() default Rules.REPLACE_CHARS;

    public String message() default Rules.EMPTY_STRING;

    public int messageResId() default 0;
}
