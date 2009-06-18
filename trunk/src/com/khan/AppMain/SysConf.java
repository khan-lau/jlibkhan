/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.AppMain;

import java.io.*;
import java.util.HashMap;

import com.khan.datetime.*;
import com.khan.db.*;
import com.khan.file.*;




public class SysConf {
  public static String inifile = "conf/CBE.ini";
  private static SysConf conf = null;

  
  private DBConPool dbPool = null;
  private int testChannel = 1000;
  private int httpPort = 0;
  

  private SysConf() {}

  private String loadinifile(String filename){
    BufferedReader in = null;
    String result = "";
    try{
      in = new BufferedReader(new FileReader(filename));
      String tmp = "";
      while((tmp = in.readLine()) != null) {
        result = result + tmp + "\n";
      }
    }catch (Exception e){
      System.out.println(SMPTime.getDateTime() +" ∂¡»°≈‰÷√Œƒº˛ ß∞‹!");
    }finally{
      try{
        in.close();
        in = null;
      }catch (Exception e){
      }
    }
    return result;
  }

  private void loadConf(String filename){
    String inistr = loadinifile(filename);
    try{
      LoadIni li = new LoadIni(inistr);

      String port        = li.readINI("http.port");
      String testchannel = li.readINI("testChannel");
      String db_driver   = li.readINI("dbdriver");
      String db_url      = li.readINI("dburl");
      String db_user     = li.readINI("dbuser");
      String db_pw       = li.readINI("dbpw");

      int maxCon         = Integer.parseInt(li.readINI("maxCon"));
      dbPool             = new DBConPool(db_driver, db_url, db_user, db_pw, maxCon);
      httpPort           = Integer.parseInt(port);
      testChannel        = Integer.parseInt(testchannel);
    }catch (Exception e){
      System.out.println(SMPTime.getDateTime() + "Ini File parser error:" + e.getMessage());
    }
  }

  public static SysConf getInstance() {
    return getInstance(inifile);
  }

  public static SysConf getInstance(String filename) {
    if(conf == null) {
      conf = new SysConf();
      conf.loadConf(filename);
      return conf;
    }else
      return conf;
  }



}





