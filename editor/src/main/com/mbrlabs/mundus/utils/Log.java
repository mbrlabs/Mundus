/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.core.HomeManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Marcus Brummer
 * @version 26-11-2015
 */
public class Log {
    public static final int DEBUG = 4;
    public static final int INFO = 3;
    public static final int WARN = 2;
    public static final int ERROR = 1;
    public static final int FATAL = 0;

    public static int LOG_LEVEL = DEBUG;

    private static File logFile;
    private static PrintWriter logFileWriter;
    private static SimpleDateFormat msgDateFormat = new SimpleDateFormat("[HH:mm]");

    public static void init () {
        System.setErr(new ErrorStreamInterceptor(System.err));
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.exception(throwable);
                Log.fatal("Uncaught exception occurred, error report will be saved");
                logFileWriter.flush();
            }
        });

        prepareLogFile();
    }

    public static void dispose () {
        info("Exiting");
        logFileWriter.close();
    }

    public static FileHandle getLogFile () {
        return Gdx.files.absolute(logFile.getAbsolutePath());
    }

    private static void prepareLogFile () {
        File logDirectory = new File(HomeManager.HOME_DIR, "logs");
        logDirectory.mkdir();

        SimpleDateFormat fileDateFormat = new SimpleDateFormat("yy-MM-dd");
        String fileName = fileDateFormat.format(new Date());

        try {
            logFile = new File(logDirectory, "mundus " + fileName + ".log");
            logFile.createNewFile();
            logFileWriter = new PrintWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            exception(e);
        }

        logFileWriter.println();
        info("Started: " + fileName);
    }

    public static String getLogStoragePath () {
        return logFile.getParent();
    }

    public static void debug (String msg) {
        if (LOG_LEVEL >= DEBUG) print("[Debug] " + msg);
    }

    public static void info (String msg) {
        if (LOG_LEVEL >= INFO) print("[Info] " + msg);
    }

    public static void warn (String msg) {
        if (LOG_LEVEL >= WARN) print("[Warning] " + msg);
    }

    public static void error (String msg) {
        if (LOG_LEVEL >= ERROR) printErr("[Error] " + msg);
    }

    public static void fatal (String msg) {
        if (LOG_LEVEL >= FATAL) printErr("[Fatal] " + msg);
    }

    //Log with tag

    public static void debug (String tag, String msg) {
        if (LOG_LEVEL >= DEBUG) print("[Debug][" + tag + "] " + msg);
    }

    public static void info (String tag, String msg) {
        if (LOG_LEVEL >= INFO) print("[Info][" + tag + "] " + msg);
    }

    public static void warn (String tag, String msg) {
        if (LOG_LEVEL >= WARN) print("[Warning][" + tag + "] " + msg);
    }

    public static void error (String tag, String msg) {
        if (LOG_LEVEL >= ERROR) printErr("[Error][" + tag + "] " + msg);
    }

    public static void fatal (String tag, String msg) {
        if (LOG_LEVEL >= FATAL) printErr("[Fatal][" + tag + "] " + msg);
    }

    private static void print (String msg) {
        msg = getTimestamp() + msg;
        logFileWriter.println(msg);
        logFileWriter.flush();
        System.out.println(msg);
    }

    private static void printErr (String msg) {
        msg = getTimestamp() + msg;
        System.err.println(msg);
    }

    public static void exception (Throwable e) {
        String stack = ExceptionUtils.getStackTrace(e);
        fatal(stack);
    }

    private static String getTimestamp () {
        return msgDateFormat.format(new Date());
    }

    private static class ErrorStreamInterceptor extends PrintStream {
        public ErrorStreamInterceptor (OutputStream out) {
            super(out, true);
        }

        @Override
        public void print (String s) {
            super.print(s);
            if (logFileWriter != null) logFileWriter.println(s);
        }
    }
}