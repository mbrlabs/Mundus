package com.mbrlabs.mundus.events;

import com.mbrlabs.mundus.utils.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple Event bus via reflection.
 *
 * Subscibers need to provide a public method, annotated with @Subscribe and
 * 1 parameter as event type.
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
            for(Object subscriber : subscribers) {
                for(Method method : subscriber.getClass().getDeclaredMethods()) {
                    if(isSubscriber(method)) {
                        if(method.getParameterCount() != 1) {
                            throw new EventBusExcetion("Size of parameter list of method " + method.getName() +
                                    " in " + subscriber.getClass().getName() + " must be 1");
                        }

                        if(method.getParameters()[0].getType().equals(eventType)) {
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
        final Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().equals(Subscribe.class)) {
                return true;
            }
        }
        return false;
    }

}
