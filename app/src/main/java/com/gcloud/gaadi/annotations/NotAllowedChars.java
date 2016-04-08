package com.gcloud.gaadi.annotations;

import com.gcloud.gaadi.annotations.rules.Rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ankitgarg on 16/06/15.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotAllowedChars {

    public int order();

    public String notAllowedChars() default "";

    public boolean trim() default true;

    public String message() default Rules.EMPTY_STRING;

    public int messageResId() default 0;
}
