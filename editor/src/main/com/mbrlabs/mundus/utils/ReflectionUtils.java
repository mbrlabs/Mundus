/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.utils;

import com.mbrlabs.mundus.core.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Marcus Brummer
 * @version 21-01-2016
 */
public class ReflectionUtils {

    public static boolean hasMethodAnnotation(Method method, Class annotationClass) {
        final Annotation[] annotations = method.getAnnotations();
        for(Annotation a : annotations) {
            if(a.annotationType().equals(annotationClass)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasFieldAnnotation(Field field, Class annotationClass) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for(Annotation a : annotations) {
            if(a instanceof Inject) {
                return true;
            }
        }

        return false;
    }

}
