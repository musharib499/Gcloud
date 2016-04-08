package com.gcloud.gaadi.annotations;

import com.gcloud.gaadi.annotations.rules.Rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ankitgarg on 17/2/14.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoContactNumber {
    public int order();

    public int minLength() default 0;

    public boolean trim() default true;

    public String message() default Rules.EMPTY_STRING;

    public int messageResId() default 0;
}
