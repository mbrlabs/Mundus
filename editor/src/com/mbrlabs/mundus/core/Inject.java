package com.mbrlabs.mundus.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Marcus Brummer
 * @version 10-12-2015
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    // TODO id paramenter for injecting by value...injectable classes must have id.
}
