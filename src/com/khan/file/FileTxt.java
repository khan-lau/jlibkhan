/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.file;

import java.io.*;



public class FileTxt {
  private FileWriter _fw = null;
  private FileReader _fr = null;


  public FileTxt(String path , boolean append, boolean isRead) {
    open(path, append, isRead);
  }


  public FileTxt(String path, boolean isRead ) {
    open(path, false, isRead);
  }

  public boolean open(String path , boolean append, boolean isRead){
    try{
      if(isRead) {
        _fr = new FileReader(path);
        _fw = null;
      }else{
        _fw = new FileWriter(path, append);
        _fr = null;
      }
    }catch (Exception e){
      System.out.println("FileTxt:open error:" + e.getMessage());
      return false;
    }
    return true;
  }


  public void write(String str){
    try{
      if(_fr != null) {
        _fr.close();
        _fr = null;
      }
      _fw.write(str, 0, str.length());
      _fw.flush();
    }catch (Exception e){
      System.out.println("FileTxt:write error:" + e.getMessage());
    }
  }

  public String read( String path ){
    String result = null;
    try{
      if(_fw != null) {
        _fw.close();
        _fw = null;
      }

      int len = (int)fileSize(path);
      char[] data = new char[len];
      
      _fr.read(data, 0, len);
      result = new String(data);
      data  = null;
    }catch (Exception e){
      System.out.println("FileTxt:read error:" + e.getMessage());
      return result;
    }
    return result;
  }


  public boolean close(){
    try{
      if(null != _fw) {
        _fw.close();
        _fw = null;
      }
      if(_fr != null) {
        _fr.close();
        _fr = null;
      }
    }catch (Exception e){
      System.out.println("FileTxt:close error:" + e.getMessage());
      return false;
    }
    return true;
  }

  //判断文件或者目录是否存在
  public static boolean isPathExists(String path) {
    File file = null;
    boolean f = false;
    try {
      file = new File(path);
      f = file.exists();
    } finally {
      file = null;
    }
    return f;
  }

  //取文件大小
  public static long fileSize(String path) {
    File file = null;
    long len = 0;
    try {
      file = new File(path);
      len = file.length();
    } finally {
      file = null;
    }
    return len;
  }


  //删除文件或路径
  public static boolean delete(String path){
    File file = null;
    boolean result = false;
    try {
      file =new File(path);
      result = file.delete();
    } finally {
      file = null;
    }
    return result;
  }

  //判断当前文件是否为目录
  public static boolean isDirectory(String path){
    File file = null;
    boolean result = false;
    try {
      file =new File(path);
      result = file.isDirectory();
    } finally {
      file = null;
    }
    return result;
  }

  //创建一个目录
  public static boolean mkDir(String path) {
    File file = null;
    boolean result = false;
    try {
      file =new File(path);
      result = file.mkdir();
    } finally {
      file = null;
    }
    return result;
  }


  public static boolean rename(String srcPath, String dstPath){
    File file = null;
    boolean result = false;
    try {
      file =new File(srcPath);
      result = file.renameTo(new File(dstPath));
    } finally {
      file = null;
    }
    return result;
  }

}





