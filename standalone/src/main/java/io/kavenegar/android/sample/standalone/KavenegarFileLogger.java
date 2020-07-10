package io.kavenegar.android.sample.standalone;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.kavenegar.sdk.call.log.Logger;

public class KavenegarFileLogger implements Logger {
    private int logLevel;

    private SimpleDateFormat dateFormat;
    private File directory;
    File currentLogFile;

    public KavenegarFileLogger(int logLevel) {
        this();
        this.logLevel = logLevel;
    }

    public KavenegarFileLogger() {
        this.logLevel = 4;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        this.directory = MyApplication.context.get().getExternalFilesDir("logs");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    File getFile() {
        String tag = getTag();
        if (currentLogFile == null || !currentLogFile.getName().equals(tag + ".txt")) {
            File logFile = new File(directory, tag + ".txt");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            currentLogFile = logFile;
        }
        return currentLogFile;
    }

    void appendLog(String text) {
        File logFile = getFile();
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTag() {
        if (MyApplication.getCurrentCallId() != null) {
            return MyApplication.getCurrentCallId();
        } else {
            return "No-Active-Call";
        }
    }


    public boolean isLoggable(String tag, int level) {
        return this.logLevel <= level;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }


    private String getDate() {
        String date = dateFormat.format(new Date());
        return date;
    }

    private String stringifyException(Throwable throwable) {
        if (throwable == null) return "";
        String stackTrace = Log.getStackTraceString(throwable);
        return "[" + stackTrace + "]";
    }

    public void debug(String tag, String text, Throwable throwable) {
        if (this.isLoggable(tag, 3)) {
            appendLog(String.format("[%s] [info] [%s] [%s] %s", getDate(), tag, text, stringifyException(throwable)));
        }

    }

    public void verbose(String tag, String text, Throwable throwable) {
        if (this.isLoggable(tag, 2)) {
            appendLog(String.format("[%s] [verbose] [%s] [%s] %s", getDate(), tag, text, stringifyException(throwable)));
        }

    }

    public void info(String tag, String text, Throwable throwable) {
        if (this.isLoggable(tag, 4)) {
            appendLog(String.format("[%s] [info] [%s] [%s] %s", getDate(), tag, text, stringifyException(throwable)));
        }

    }

    ///data/user/0/io.kavenegar.call
    public void warn(String tag, String text, Throwable throwable) {
        if (this.isLoggable(tag, 5)) {
            appendLog(String.format("[%s] [warn] [%s] [%s] %s", getDate(), tag, text, stringifyException(throwable)));
        }

    }

    public void error(String tag, String text, Throwable throwable) {
        if (this.isLoggable(tag, 6)) {
            appendLog(String.format("[%s] [error] [%s] [%s] %s", getDate(), tag, text, stringifyException(throwable)));
        }

    }

    public void debug(String tag, String text) {
        this.debug(tag, text, null);
    }

    public void verbose(String tag, String text) {
        this.verbose(tag, text, null);
    }

    public void info(String tag, String text) {
        this.info(tag, text, null);
    }

    public void warn(String tag, String text) {
        this.warn(tag, text, null);
    }

    public void error(String tag, String text) {
        this.error(tag, text, null);
    }
}
