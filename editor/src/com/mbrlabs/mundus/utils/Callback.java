package com.mbrlabs.mundus.utils;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public interface Callback<T> {

    public void done(T result);

    public void error(String msg);

}
