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

package com.mbrlabs.mundus.editor.events;

import com.mbrlabs.mundus.editor.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple Event bus via reflection.
 *
 * Subscribers need to provide a public method, annotated with @Subscribe and 1
 * parameter as event type.
 *
 * Inspired by the Otto Event Bus for Android.
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
// TODO improve/test performance might not be that great
public class EventBus {

    private class EventBusExcetion extends RuntimeException {
        private EventBusExcetion(String s) {
            super(s);
        }
    }

    private List<Object> subscribers;

    public EventBus() {
        subscribers = new LinkedList<>();
    }

    public void register(Object subscriber) {
        subscribers.add(subscriber);
    }

    public void unregister(Object subscriber) {
        subscribers.remove(subscriber);
    }

    public void post(Object event) {
        try {
            final Class eventType = event.getClass();
            for (Object subscriber : subscribers.toArray()) {
                for (Method method : subscriber.getClass().getDeclaredMethods()) {
                    if (isSubscriber(method)) {
                        if (method.getParameterTypes().length != 1) {
                            throw new EventBusExcetion("Size of parameter list of method " + method.getName() + " in "
                                    + subscriber.getClass().getName() + " must be 1");
                        }

                        if (method.getParameterTypes()[0].equals(eventType)) {
                            // System.out.println(subscriber.getClass().getName());
                            method.invoke(subscriber, eventType.cast(event));
                        }
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean isSubscriber(Method method) {
        // check if @Subscribe is directly used in class
        boolean isSub = ReflectionUtils.hasMethodAnnotation(method, Subscribe.class);
        if (isSub) return true;

        // check if implemented interfaces of this class have a @Subscribe
        // annotation
        Class[] interfaces = method.getDeclaringClass().getInterfaces();
        for (Class i : interfaces) {
            try {
                Method interfaceMethod = i.getMethod(method.getName(), method.getParameterTypes());
                if (interfaceMethod != null) {
                    isSub = ReflectionUtils.hasMethodAnnotation(interfaceMethod, Subscribe.class);
                    if (isSub) return true;
                }
            } catch (NoSuchMethodException e) {
                // silently ignore -> this interface simply does not declare
                // such a method
            }
        }

        return false;
    }

}
