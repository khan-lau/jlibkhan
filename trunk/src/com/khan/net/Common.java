package com.khan.net;

import java.io.*;
import java.net.*;
import java.util.*;


public class Common {



  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      System.err.println("sleep interruptedException!");
    }
  }


  public static int rand() {
    Random r = new Random();
    return (int) (r.nextLong() % 10000);
  }
  
  

  public static byte byteMask0Left(int i){
    return (byte)(0xFF/(Math.pow(2, i % 9 )));
  }

  public static byte byteMask0Right(int i){
    return (byte)~(byte)(Math.pow(2, i % 9 ) - 1);
  }

  public static String IntFormat(int value, int len) {
    String mask = "";
    for (int i = 0; i < len; i++) {
      mask = mask + "0";
    }
    java.text.DecimalFormat df = new java.text.DecimalFormat(String.valueOf(mask));
    return df.format(value);
  }

  public static String IntFormat(long value, int len) {
    String mask = "";
    for (int i = 0; i < len; i++) {
      mask = mask + "0";
    }
    java.text.DecimalFormat df = new java.text.DecimalFormat(String.valueOf(mask));
    return df.format(value);
  }


  public static String Byte2BinString(byte b){
    String[] tmp = {
        "0000","0001","0010","0011","0100","0101","0110","0111",
        "1000","1001","1010","1011","1100","1101","1110","1111"
    };

    byte t1 = (byte)( b & 0x0F );
    byte t2 = (byte)( (b >> 4) &0x0F);

    String result = tmp[t2] + tmp[t1];
    tmp = null;
    return result;
  }

  /**
   *  将byte[]数据按2进制格式打印出来供调试
   * @param data byte[] 
   * @param split string 分隔符 
   */
  public static void PrintDataBIN(byte[] data, String split) {
    if (data == null) {
      return;
    }
    for (int i = 0; i < data.length; i++) {
      System.out.print(split + Byte2BinString(data[i]));
    }
    System.out.println();
  }

  /**
   *  将byte[]数据按2进制格式打印出来供调试
   * @param data byte[]
   */
  public static void PrintDataBIN(byte[] data){
    if (data == null) {
      return;
    }
    for (int i = 0; i < data.length; i++) {
      System.out.print( Byte2BinString(data[i]) + " ");
    }
    System.out.println();
  }
  /**
   * 将一个字节数据转换成 '1F' 这样的对应16进制字符串
   * @param b byte 字节数据
   * @return String
   */
  public static String Byte2String(byte b) {
    String value = "";
    char[] str = null;
    try{
      str = new char[2];
      str[0] = Byte2HexChar((byte) ((b >> 4) & 0x0F));
      str[1] = Byte2HexChar((byte) (b & 0x0F));
      value = String.valueOf(str);
    }finally{
      str = null;
    }
    return value;
  }


  
  /**
   * 将一个字数据转换成 'AA1F' 这样的对应16进制字符串
   * @param b byte 字节数据
   * @return String
   */
  public static String Short2String(short b) {
    String value = "";
    char[] str = null;
    try{
      str = new char[4];
      str[0] = Byte2HexChar((byte) ((b >> 12) & 0x0F));
      str[1] = Byte2HexChar((byte) ((b >> 8) & 0x0F));
      str[2] = Byte2HexChar((byte) ((b >> 4) & 0x0F));
      str[3] = Byte2HexChar((byte) (b & 0x0F));
      value = String.valueOf(str);
    }finally{
      str = null;
    }
    return value;
  }


  /**
   * 将一个10进制的数字转换成16进制的字符
   * @param value byte
   * @return char 否则返回对应的16进制字符,转换失败返回0
   */
  private static char Byte2HexChar(byte value) {
    if ( value > 0x0f) return 0;
    
    char result[] ={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    char v = result[value];
    result = null;
    return v;
  }


  public static String toHexString(byte[] data) {
    String tmp = "";
    for (int i = 0; i < data.length; i++) {
      tmp = tmp + Byte2String(data[i]);
    }
    return tmp;
  }

  public static void PrintDataHex(byte[] data, String split) {
    if (data == null) {
      return;
    }
    for (int i = 0; i < data.length; i++) {
      System.out.print(split + Byte2String(data[i]));
    }
    System.out.println();
  }

  /**
   *  将byte[]数据按16进制格式打印出来供调试
   * @param data byte[]
   */
  public static void PrintDataHex(byte[] data) {
    if (data == null) {
      return;
    }
    for (int i = 0; i < data.length; i++) {
      System.out.print("0x" + Byte2String(data[i]) + " ");
    }
    System.out.println();
  }

  // 将iSource转为长度为iArrayLen的byte数组，低位是高字节－－见代码中举例
  public static byte[] toByteArrayHigh(int iSource) {
    int iArrayLen = Integer.SIZE / 8;
    byte[] bLocalArr = new byte[iArrayLen];
    for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
      bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
    }
    return bLocalArr;
  }


  // 将iSource转为长度为iArrayLen的byte数组，低位是低字节－－见代码中举例
  public static byte[] toByteArrayLow(int iSource) {
    int iArrayLen = Integer.SIZE / 8;
    byte[] bLocalArr = new byte[iArrayLen];
    int j = 0;
    for (int i = iArrayLen - 1; i >= 0; i--) {
      bLocalArr[i] = (byte) ((iSource >> (8 * j++)) & 0xFF);
    }
    return bLocalArr;
  }


  // 将iSource转为长度为iArrayLen的byte数组，低位是高字节－－见代码中举例
  public static byte[] toByteArrayHigh(short iSource) {
    int iArrayLen = Short.SIZE / 8;
    byte[] bLocalArr = new byte[iArrayLen];
    for (int i = 0;  i < iArrayLen; i++) {
      bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
    }
    return bLocalArr;
  }

  // 将iSource转为长度为iArrayLen的byte数组，低位是低字节－－见代码中举例
  public static byte[] toByteArrayLow(short iSource) {
    int iArrayLen = Short.SIZE / 8;
    byte[] bLocalArr = new byte[iArrayLen];
    int j = 0;
    for (int i = iArrayLen - 1; i >= 0; i--) {
      bLocalArr[i] = (byte) ((iSource >> (8 * j++)) & 0xFF);
    }
    return bLocalArr;
  }

  public static byte[] toByteArray(int iSource) {
    return toByteArrayLow(iSource);
  }

  public static byte[] toByteArray(short iSource) {
    return toByteArrayLow(iSource);
  }

  /**
   * 将byte[2]转换成short int
   * @param data byte[]
   * @param offset int
   * @return short
   */
  public static short getWord(byte[] data, int offset) {
    if (offset < 0 || offset > (data.length - 1)) {
      return 0;
    }
    return (short) (((data[offset] << 8) & 0xFF00)
                    + (data[offset + 1] & 0x00FF));
  }


  public static void FillChar(byte[] data, byte value) {
    for (int i = 0; i < data.length; i++) {
      data[i] = value;
    }
  }

  /**
   * 将byte[4]转换成int
   * @param data byte[]
   * @param offset int
   * @return int
   */
  public static int getDWord(byte[] data, int offset) {
    if (offset < 0 || offset > (data.length - 1)) {
      return 0;
    }
    return (int) (((data[offset] << 24) & 0xFF000000)
                  + ((data[offset + 1] << 16) & 0x00FF0000)
                  + ((data[offset + 2] << 8) & 0x0000FF00)
                  + (data[offset + 3] & 0x000000FF));

  }


}