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

package com.gcloud.gaadi.annotations;

import com.gcloud.gaadi.annotations.rules.Rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate first char validation. It follows the logic on what all
 * first characters are not allowed.
 *
 * @author Ankit Garg <ankit.garg@gaadi.com>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstCharValidation {
    public int order();

    public String notAllowedFirstChars() default "";

    public boolean trim() default true;

    public String message() default Rules.EMPTY_STRING;

    public int messageResId() default 0;
}
