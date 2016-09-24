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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.core.registry.Registry;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log messages with different log levels.
 * To save performance during runtime, string concatination is only run
 * if log level is present.
 * Use for example: Log.log(Log.INFO, "Msg with param {} and param {}", "param1", "param2");
 * {} will be replaced with parameter in left to right order.
 * 
 * @author Marcus Brummer, codenigma
 * @version 23-09-2015
 */
public class Log {

    public static final int TRACE = 5;
    public static final int DEBUG = 4;
    public static final int INFO = 3;
    public static final int WARN = 2;
    public static final int ERROR = 1;
    public static final int FATAL = 0;

    public static int LOG_LEVEL = TRACE;

    private static File logFile;
    private static PrintWriter logFileWriter;
    private static SimpleDateFormat msgDateFormat = new SimpleDateFormat("[HH:mm]");

    public static void init() {
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

    public static void dispose() {
        info("Exiting");
        logFileWriter.close();
    }

    public static FileHandle getLogFile() {
        return Gdx.files.absolute(logFile.getAbsolutePath());
    }

    private static void prepareLogFile() {
        File logDirectory = new File(Registry.LOGS_DIR);
        logDirectory.mkdir();

        SimpleDateFormat fileDateFormat = new SimpleDateFormat("yy-MM-dd");
        String fileName = fileDateFormat.format(new Date());
        fileName = "mundus " + fileName + ".log";
        
        try {
            logFile = new File(logDirectory, fileName);
            logFile.createNewFile();
            logFileWriter = new PrintWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            exception(e);
        }

        logFileWriter.println();
        info("Logging activated. Log file [{}] created. ", fileName);
    }

    public static String getLogStoragePath() {
        return logFile.getParent();
    }

    public static void trace(String msg, Object... params) {
        if (LOG_LEVEL >= TRACE) {
            msg = completeMsg(msg, params);
            print("[Trace] " + msg);
        }
    }
    public static void debug(String msg, Object... params) {
        if (LOG_LEVEL >= DEBUG) {
            msg = completeMsg(msg, params);
            print("[Debug] " + msg);
        }
    }

    public static void info(String msg, Object... params) {
        if (LOG_LEVEL >= INFO) {
            msg = completeMsg(msg, params);
            print("[Info] " + msg);
        }
    }

    public static void warn(String msg, Object... params) {
        if (LOG_LEVEL >= WARN) {
            msg = completeMsg(msg, params);
            print("[Warning] " + msg);
        }
    }

    public static void error(String msg, Object... params) {
        if (LOG_LEVEL >= ERROR) {
            msg = completeMsg(msg, params);
            printErr("[Error] " + msg);
        }
    }

    public static void fatal(String msg, Object... params) {
        if (LOG_LEVEL >= FATAL) {
            msg = completeMsg(msg, params);
            printErr("[Fatal] " + msg);
        }
    }

    //Log with tag
    public static void traceTag(String tag, String msg, Object... params) {
        if (LOG_LEVEL >= TRACE) {
            msg = completeMsg(msg, params);
            print("[Trace][" + tag + "] " + msg);
        }
    }
    public static void debugTag(String tag, String msg, Object... params) {
        if (LOG_LEVEL >= DEBUG) {
            msg = completeMsg(msg, params);
            print("[Debug][" + tag + "] " + msg);
        }
    }

    public static void infoTag(String tag, String msg, Object... params) {
        if (LOG_LEVEL >= INFO) {
            msg = completeMsg(msg, params);
            print("[Info][" + tag + "] " + msg);
        }
    }

    public static void warnTag(String tag, String msg, Object... params) {
        if (LOG_LEVEL >= WARN) {
            msg = completeMsg(msg, params);
            print("[Warning][" + tag + "] " + msg);
        }
    }

    public static void errorTag(String tag, String msg, Object... params) {
        if (LOG_LEVEL >= ERROR) {
            msg = completeMsg(msg, params);
            printErr("[Error][" + tag + "] " + msg);
        }
    }

    public static void fatalTag(String tag, String msg, Object... params) {
        if (LOG_LEVEL >= FATAL) {
            msg = completeMsg(msg, params);
            printErr("[Fatal][" + tag + "] " + msg);
        }
    }

    private static void print(String msg) {
        msg = getTimestamp() + msg;
        logFileWriter.println(msg);
        logFileWriter.flush();
        System.out.println(msg);
    }

    private static void printErr(String msg) {
        msg = getTimestamp() + msg;
        System.err.println(msg);
    }

    public static void exception(Throwable e) {
        String stack = ExceptionUtils.getStackTrace(e);
        fatal(stack);
    }

    private static String getTimestamp() {
        return msgDateFormat.format(new Date());
    }

    private static class ErrorStreamInterceptor extends PrintStream {

        public ErrorStreamInterceptor(OutputStream out) {
            super(out, true);
        }

        @Override
        public void print(String s) {
            super.print(s);
            if (logFileWriter != null) {
                logFileWriter.println(s);
            }
        }
    }

    private static String completeMsg(String msg, Object... params) {
        for(Object p : params)
            msg = msg.replaceFirst("\\{\\}", String.valueOf(p));
        return msg;
    }
    
    /**
     * To save memory values will be concat to strings only when log level really is set
     * Use {} in the msg as wild card
     * @param logLevel
     * @param msg
     * @param params 
     */
    public static void log(int logLevel, String msg, Object... params) {
        switch (logLevel) {
            case TRACE:
                trace(msg, params);
                break;
            case DEBUG:
                debug(msg, params);
                break;
            case INFO:
                info(msg, params);
                break;
            case WARN:
                warn(msg, params);
                break;
            case ERROR:
                error(msg, params);
                break;
            case FATAL:
                fatal(msg, params);
                break;
            default:
                error("Log level " + logLevel + " is not supported!");
        }
    }

    /**
     * Log msg with tag
     * To save memory values will be concat to strings only when log level really is set
     * Use {} in the msg as wild card
     * @param logLevel
     * @param msg
     * @param params 
     */
    public static void logTag(int logLevel, String tag, String msg, Object... params) {
        switch (logLevel) {
            case TRACE:
                traceTag(tag, msg, params);
                break;
            case DEBUG:
                debugTag(tag, msg, params);
                break;
            case INFO:
                infoTag(tag, msg, params);
                break;
            case WARN:
                warnTag(tag, msg, params);
                break;
            case ERROR:
                errorTag(tag, msg, params);
                break;
            case FATAL:
                fatalTag(tag, msg, params);
                break;
            default:
                error("Log level " + logLevel + " is not supported!");
        }
    }

    public final static void printHeadLine(int logLevel, String title) {
        log(logLevel, "|                  > {} <", title);
    }

    public final static void printUpperSeperationLine(int logLevel) {
        log(logLevel, "↓‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾BEGIN‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾↓");
    }

    public final static void printLowerSeperationLine(int logLevel) {
        log(logLevel, "↑_______________________END_______________________↑");
    }
}
