package com.khan.file;



import java.util.*;
import java.io.*;


public class LoadIni {

  private Properties prop;
  private String IniString;
    
  public LoadIni(String IniString) {
    prop = new Properties();
    this.IniString = IniString;
  }




  public String  wrintINI(String key, String values) throws  Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    prop.store(out, "header");
    out.close();
    out = null;
    IniString = new String( out.toByteArray());
    return IniString;
  }
  
  
  public String readINI(String key) throws  Exception {
    ByteArrayInputStream in   =   new ByteArrayInputStream(  IniString.getBytes() );
    prop.load(in);
    in.close();
    in = null;
    
    return prop.getProperty(key);
  }

  public Properties getProperties(){
    return prop;
  }


  protected void finalize() {
    dostory();
  }


  protected void dostory(){
    prop = null;
  }

  public static void main(String[] args) {
    String inistr = "\n" +"[PARAMETER]\n" +"return=0\n" +"sessionid=188821\n";
    LoadIni inistream = new LoadIni(inistr);        
    try {
      System.out.println(inistream.readINI("return"));
      System.out.println(inistream.readINI("sessionid"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
