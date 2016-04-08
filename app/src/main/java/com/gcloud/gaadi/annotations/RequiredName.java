package com.gcloud.gaadi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Vaibhav on 6/8/2015.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredName {
    public int order();

    public boolean trim() default true;

    public String message() default "Should not contain space and special chars";

    public int messageResId() default 0;
}
