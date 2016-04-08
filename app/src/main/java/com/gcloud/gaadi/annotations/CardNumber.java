package com.gcloud.gaadi.annotations;

import com.gcloud.gaadi.annotations.rules.Rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ankit on 16/10/14.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CardNumber {
    public int order();

    public int minLength() default 12;

    public int maxLength() default 16;

    public boolean trim() default true;

    public String message() default Rules.EMPTY_STRING;

    public int messageResId() default 0;
}
