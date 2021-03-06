package com.android.zjctools.utils;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;
public class ZLog {

    /**
     * 借用牛逼哄哄的 lzan13 代码
     */

    // 这里设置默认的 Tag
    private static String mTag = "zjcTools";

    // 日志级别，默认为 ERROR
    private static int mLevel = Level.ERROR;
    // 是否保存日志到文件 默认为 false
    private static boolean isEnableSave = false;
    // 最大保存日志文件个数
    private static int mMaxCount = 3;
    // 日志文件路径
    private static String mLogDir;
    private static String mLogPath;
    // 日志信息队列
    private static LinkedBlockingQueue<String> mQueue;
    private static Thread mSaveThread;

    /**
     * 初始化日志 Tag，即设置自己的TAG
     */
    public static void setLogTag(String tag) {
        mTag = tag;
    }

    /**
     * 设置 Debug 输出级别{@link Level}
     */
    public static void setDebug(int level) {
        mLevel = level;
    }

    /**
     * 设置是否保存日志到文件
     */
    public static void setEnableSave(boolean enable, int count) {
        isEnableSave = enable;
        mMaxCount = count;
        if (isEnableSave) {
            // 判断是否存在 sdcard
            if (ZFile.hasSdcard()) {
                mLogDir = ZFile.getFilesFromSDCard() + "logs/";
            } else {
                mLogDir = ZFile.getFilesFromData() + "logs/";
            }
            mLogPath = mLogDir + ZDate.filenameDate() + ".log";
            ZFile.createFile(mLogPath);
            mQueue = new LinkedBlockingQueue<>();
            startSaveThread();

            // 异步处理
            ZSystem.runTask(() -> { checkLog();});
        } else {
            if (mSaveThread != null) {
                mSaveThread.interrupt();
                mSaveThread = null;
            }
            if (mQueue != null) {
                mQueue.clear();
                mQueue = null;
            }
        }
    }

    /**
     * 使用格式化的方式输出 Verbose 日志信息
     *
     * @param msg  需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void v(String msg, Object... args) {
        if (mLevel <= Level.VERBOSE) {
            print(Level.VERBOSE, args.length == 0 ? msg : String.format(msg, args));
        }
    }

    /**
     * 使用格式化的方式输出 Debug 日志信息
     *
     * @param msg  需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void d(String msg, Object... args) {
        if (mLevel <= Level.DEBUG) {
            print(Level.DEBUG, args.length == 0 ? msg : String.format(msg, args));
        }
    }

    /**
     * 使用格式化的方式输出 Info 日志信息
     *
     * @param msg  需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void i(String msg, Object... args) {
        if (mLevel <= Level.INFO) {
            print(Level.INFO, args.length == 0 ? msg : String.format(msg, args));
        }
    }

    /**
     * 使用格式化的方式输出 Error 日志信息
     *
     * @param msg  需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void e(String msg, Object... args) {
        if (mLevel <= Level.ERROR) {
            print(Level.ERROR, args.length == 0 ? msg : String.format(msg, args));
        }
    }

    /**
     * 输出 Verbose 日志信息
     *
     * @param message 日志内容
     */
    public static void v(String message) {
        if (mLevel <= Level.VERBOSE) {
            print(Level.VERBOSE, message);
        }
    }


    /**
     * 输出日志为 json 格式
     * @param msg ，如请求参数，返回数据等
     * @param logType
     */
    public static void json(String msg,String logType){
        try {
            String message="";
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
            print(mLevel,logType+":"+message);
        }catch (Exception e){
            print(mLevel,logType+"json:error"+e.getMessage());
        }



    }
    /**
     * 输出 Debug 日志信息
     *
     * @param message 日志内容
     */
    public static void d(String message) {
        if (mLevel <= Level.DEBUG) {
            print(Level.DEBUG, message);
        }
    }

    /**
     * 输出 Info 日志信息
     *
     * @param message 日志内容
     */
    public static void i(String message) {
        if (mLevel <= Level.INFO) {
            print(Level.INFO, message);
        }
    }

    /**
     * 输出 Error 日志信息
     *
     * @param message 日志内容
     */
    public static void e(String message) {
        if (mLevel <= Level.ERROR) {
            print(Level.ERROR, message);
        }
    }

    /**
     * 输出日志
     *
     * @param level   日志级别
     * @param message 日志内容
     */
    private static void print(int level, String message) {
        log(level, "┆ Thread:" + getThreadInfo() + " - " + generateLog());
        log(level, "┆ " + message);
        log(level, "┆──────────────────────────────────────────────────────────────────────────");
    }

    /**
     * 统一处理日志输出
     *
     * @param level   日志级别
     * @param message 日志内容
     */
    private static void log(int level, String message) {
        switch (level) {
            case Level.VERBOSE:
                Log.v(mTag, message);
                break;
            case Level.DEBUG:
                Log.d(mTag, message);
                break;
            case Level.INFO:
                Log.i(mTag, message);
                break;
            case Level.ERROR:
                Log.e(mTag, message);
                break;
        }
        if (isEnableSave && mQueue != null) {
            try {
                mQueue.put(String.format("%s %s %s", ZDate.currentUTCDateTime(), mTag, message));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始写入文件，这里必须在子线程操作
     */
    private static void startSaveThread() {
        mSaveThread = new Thread(() -> {
            while (isEnableSave && mQueue != null) {
                try {
                    String logInfo = mQueue.take();
                    writeLog(logInfo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mSaveThread.start();
    }

    /**
     * 检查日志文件，主要是限制文件个数和大小
     */
    private static void checkLog() {
        File logDir = new File(mLogDir);
        FilenameFilter filter = (File dir, String name) -> {
            // 过滤日志文件
            if (ZFile.parseSuffix(name).equals(".log")) {
                return true;
            }
            return false;
        };
        // 获取文件并按照最后修改时间排序
        File[] files = logDir.listFiles(filter);
        sortFiles(files);
        // 这里从 1 开始，第一个日志文件还要写入，因此不处理
        for (int i = 1; i < files.length; i++) {
            File file = files[i];
            int dot = file.getName().lastIndexOf(".");
            String zipPath = mLogDir + file.getName().substring(0, dot + 1) + "zip";
            if (i < mMaxCount) {
                ZFile.zipFile(file.getAbsolutePath(), zipPath);
            }
            ZFile.deleteFile(file.getAbsolutePath());
        }

        /**
         * 处理压缩过的文件
         */
        filter = (File dir, String name) -> {
            // 过滤日志文件
            if (ZFile.parseSuffix(name).equals(".zip")) {
                return true;
            }
            return false;
        };
        // 获取文件并按照最后修改时间排序
        files = logDir.listFiles(filter);
        sortFiles(files);
        for (int i = 2; i < files.length; i++) {
            ZFile.deleteFile(files[i].getAbsolutePath());
        }
    }

    /**
     * 按照时间降序排列文件集合
     */
    private static void sortFiles(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                // 降序排列
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0) {
                    return -1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
    }

    /**
     * 打开日志文件并写入日志
     */
    private static void writeLog(String message) {
        try {
            File file = new File(mLogPath);
            // 第二个参数代表是不是要接上文件中原来的数据，不进行覆盖
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(message);
            bufWriter.newLine();

            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据堆栈信息定位 Log，生成 Log 为 类名 + 方法名 + 行数
     *
     * @return 返回 Log
     */
    private static String generateLog() {
        StackTraceElement element = getCallerStackTraceElement();

        String log = "%s.%s (%s:%d)";
        // 获取堆栈信息中调用当前方法的类名
        String className = element.getClassName();
        // 截取简单类名
        className = className.substring(className.lastIndexOf(".") + 1);
        // 格式化 log 内容
        log = String.format(log, className, element.getMethodName(), element.getFileName(), element.getLineNumber());
        return log;
    }

    /**
     * StackTrace 用栈的形式保存了方法的调用信息；可用 Thread.currentThread().getStackTrace() 方法得到当前线程的 StackTrace 信息；
     * 该方法返回的是一个 StackTraceElement 数组；
     *
     * 在 StackTraceElement 数组下标为[2]的元素中保存了当前方法的所属文件名，当前方法所属的类名，以及该方法的名字；除此以外还可以获取方法调用的行数；
     * 在 StackTraceElement 数组下标为[3]的元素中保存了当前方法的调用者的信息和它调用时的代码行数；
     */
    private static StackTraceElement getCallerStackTraceElement() {
        // 所以这里选择第三个元素，用来获取调用当前方法的类和方法名以及行数
        return Thread.currentThread().getStackTrace()[6];
    }

    /**
     * 获取线程信息
     */
    private static String getThreadInfo() {
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        return threadName + " - " + threadId;
    }

    /**
     * 获取类文件全名称
     */
    private static String getClassFileName() {
        return getCallerStackTraceElement().getFileName();
    }

    /**
     * 日志级别
     */
    public interface Level {
        int VERBOSE = 0;
        int DEBUG = 1;
        int INFO = 2;
        int ERROR = 3;
        int NONE = 4;
    }
}
