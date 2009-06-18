package com.khan.file;


import java.io.*;
import java.util.*;
import com.khan.datetime.*;

public class Log {
  protected String sFileName; //文件名
  protected PrintWriter pwLog = null;


  /**
   * 访问日志记录
   * @param fileName String 文件名
   */
  public Log(String fileName) {
    sFileName = fileName;
    //  bakLog();
  }

  /**
   * 取得文件最后修改时间
   * @return long
   */
  public long fileIsNow() {
    Calendar now = Calendar.getInstance();
    long i = 0;
    try {
      now.setTimeInMillis(new File(sFileName).lastModified());
      i = now.get(Calendar.DATE) + 100L * (now.get(Calendar.MONTH) + 1) + 10000L * now.get(Calendar.YEAR);
    } finally {
      now = null;
    }
    return i;
  }


  /**
   * 输出日志
   * @param out String
   */
  synchronized public void logOut(String out) {
    if (fileIsNow() != SMPTime.getDay(false)) {
      close();
      bakLog();
    }

    if (pwLog == null) {
      openWriter(true);
    }

    String msg = SMPTime.getDateTime(true) + ":" + Thread.currentThread().getName() + ":" + out;
    pwLog.println(msg);
  }

  /**
   * 输出错误信息
   * @param out String
   * @param e Exception
   */
  synchronized public void logOut(String out, Exception e) {
    //System.out.println(" " + fileIsNow() + " " + SMPTime.getDay(false));
    if (fileIsNow() != SMPTime.getDay(false)) {
      close();
      bakLog();
    }

    if (pwLog == null) {
      openWriter(true);
    }

    String msg = SMPTime.getDateTime(true) + ":" + Thread.currentThread().getName() + ":" + out;
    pwLog.println(msg);

    pwLog.println(e.getMessage());
    e.printStackTrace(pwLog);
  }

  protected void openWriter(boolean append) {
    try {
      pwLog = new PrintWriter(new FileWriter(sFileName, append), true);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
  }


  /**
   * 关闭文件
   */
  public void close() {
    if (pwLog != null) {
      pwLog.close();
      pwLog = null;
    }
  }

  /**
   * 备份日志文件
   */
  protected void bakLog() {
    String toFile = "log" + File.separator + sFileName + fileIsNow();
    FileTxt.rename(sFileName, toFile);
  }

  protected void finalize() {
    close();
  }

  public static void main(String[] args) {
    //Log logout = new Log("out");
    //Log logerr = new Log("err");
    //  logout.fileIsNow();
    //  logerr.fileIsNow();
    //System.out.println( logerr.fileSize("http://www.sina.com.cn/index.html"));
    //System.out.println("out:"+logout.fileIsNow()+" now:"+SMPTime.getDay(false));
    //while (true) {
    //  logout.logOut("test");
    //  logerr.logOut("err");
    //  com.khan.util.common.sleep(1000);
    //}
  }
}

