package com.khan.datetime;

import java.util.*;
import java.text.*;

/**
 * ʱ�������
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SMPTime {

  public static long getDateTime() {
    Calendar now = null;
    long date = 0;
    try {
      now = Calendar.getInstance();
      date = now.get(Calendar.SECOND)
             + 100L * now.get(Calendar.MINUTE)
             + 10000L * now.get(Calendar.HOUR_OF_DAY)
             + 1000000L * now.get(Calendar.DATE)
             + 100000000L * (now.get(Calendar.MONTH) + 1)
             + 10000000000L * now.get(Calendar.YEAR);
    } finally {
      now = null;
    }
    return date;
  }

  public static String getSqlServerDateString(long datetime) {
    return getYear(datetime) + "-"
      + getMonth(datetime) + "-"
      + getDay(datetime) + " "
      + getHour(datetime) + ":"
      + getMinute(datetime) + ":"
      + getSecond(datetime);
  }

  /**
   * ȡ�õ�ǰʱ��������ʽ
   * @return long
   */

  public static long getNow() {
    return getDateTime();
  }


  /**��������ʱ����������*/
  public static long getDatePart(long time1, long time2) {
    Calendar now = null;
    Calendar now1 = null;
    long time = 0;
    try {
      now = getCalendar(time1);
      now1 = getCalendar(time2);
      time = (now.getTimeInMillis() - now1.getTimeInMillis()) / 1000;
    } finally {
      now = null;
      now1 = null;
    }
    return time;
  }

  /** 
   * ����ʱ��time1 ����seconds����ʱ��
   * 
   * @param time1 long ʱ���long��ʽ
   * @param seconds int ����
   * 
   * @return long
   */
  public static long getDateAdd(long time, int seconds) {
    Calendar now = null;

    long datetime = 0;
    try {
      now = getCalendar(time);
      
      now.add(Calendar.SECOND, seconds);
      datetime = getDateTime(now, true);
    } finally {
      now = null;
    }
    return datetime;
  }



  public static long getYearMonth(){
    Calendar now = null;
    long date = 0;
    try {
      now = Calendar.getInstance();
      date = (now.get(Calendar.MONTH) + 1)
             + 100L * now.get(Calendar.YEAR);
    } finally {
      now = null;
    }
    return date;
  }


  public static long getDay(boolean hour) {
    Calendar now = null;
    long date = 0;
    try {
      now = Calendar.getInstance();
      date = now.get(Calendar.DATE)
             + 100L * (now.get(Calendar.MONTH) + 1)
             + 10000L * now.get(Calendar.YEAR);
    } finally {
      now = null;
    }
    return date;
  }

  public static long getTime(boolean hour) {
    Calendar now = null;
    long data = 0;
    try {
      now = Calendar.getInstance();
      data = now.get(Calendar.SECOND)
             + 100L * now.get(Calendar.MINUTE)
             + 10000L * now.get(Calendar.HOUR_OF_DAY);
    } finally {
      now = null;
    }
    return data;

  }

  /**
   * ȡ��ָ��ʱ���������ʽ
   * @param year int
   * @param month int
   * @param day int
   * @param hour int
   * @param minute int
   * @param second int
   * @return long
   */
  public static long getDateTime(int year, int month, int day, int hour, int minute, int second) {
    return second + 100L * minute + 10000L * hour + 1000000L * day + 100000000L * month + 10000000000L * year;
  }


  /**
   * ת�� Calendar ��ʽʱ��Ϊ long��ʽ
   * @param date Calendar
   * @param year boolean
   * @return long
   */
  public static long getDateTime(Calendar date, boolean year) {
    int nYear = 0;
    if (year == true) {
      nYear = date.get(Calendar.YEAR);
    }
    return getDateTime(nYear, date.get(Calendar.MONTH) + 1, date.get(Calendar.DATE), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
  }


  public static long getDateTime(long timeInSecond) {
    Calendar date = null;
    long  d = 0;
    try{
      date = Calendar.getInstance();
      date.setTimeInMillis(timeInSecond * 1000);
      d = getDateTime(date, true);
    }finally{
      date = null;
    }
    return d;
  }

  /**
   * ȡ�õ�ǰʱ��
   * @param year boolean �Ƿ�������
   * @return long
   */
  public static long getDateTime(boolean year) {
    Calendar rightNow = null;
    long d = 0;
    try{
      rightNow = Calendar.getInstance();
      d = getDateTime(rightNow, year);
    }finally{
      rightNow = null;
    }
    return d;
  }

  /**
   * ת��long��ʱ��Ϊjava Calendarʱ����ʽ
   * @param time long
   * @return Calendar
   */
  public static Calendar getCalendar(long time) {
    Calendar now = Calendar.getInstance();
    now.set(getYear(time), getMonth(time), getDay(time), getHour(time), getMinute(time), getSecond(time));
    return now;
  }

  /**
   * ȡ�õ�ǰ�·ݵ�һ�������
   * @param year boolean
   * @return long
   */
  public static long getMonthStart(boolean year) {
    return getMonthStart(Calendar.getInstance(), year);
  }

  /**
   * ȡ�õ�ǰ�µ�һ��
   * @param now Calendar
   * @param year boolean �Ƿ�������
   * @return long
   */
  public static long getMonthStart(Calendar now, boolean year) {
    int nYear = 0;
    if (year == true) {
      nYear = now.get(Calendar.YEAR);
    }

    return getDateTime(nYear, now.get(Calendar.MONTH) + 1, 0, 0, 0, 0);
  }

  /**
   * ȡ��ʱ���е����
   * @param date long
   * @return int
   */
  public static int getYear(long date) {
    return (int) (date / 10000000000L);
  }

  /**
   * ȡ��ʱ���е��·�
   * @param date long
   * @return int
   */
  public static int getMonth(long date) {
    return (int) ((date / 100000000L) % 100);
  }

  /**
   * ȡ��ʱ���е���
   * @param date long
   * @return int
   */
  public static int getDay(long date) {
    return (int) ((date / 1000000L) % 100);
  }

  /**
   * ȡ��ʱ���е�Сʱ
   * @param date long
   * @return int
   */
  public static int getHour(long date) {
    return (int) ((date / 10000L) % 100);
  }

  /**
   * ȡ��ʱ���еķ���
   * @param date long
   * @return int
   */
  public static int getMinute(long date) {
    return (int) ((date / 100L) % 100);
  }

  /**
   * ȡ�������е���
   * @param date long
   * @return int
   */
  public static int getSecond(long date) {
    return (int) (date % 100);
  }

  /**
   * ȡ�õ�ǰʱ���е���
   * @return int
   */
  public static int getSecond() {
    Calendar now = Calendar.getInstance();
    return now.get(Calendar.SECOND);
  }

  /**
   * �����û����
   * @param birth long
   * @param time long
   * @return int
   */
  public static int getAge(long birth, long time) {
    return getYear(time - birth);
  }

  public static long getNextDateTime() {
    Calendar now = null;
    long d = 0;
    try{
      now = Calendar.getInstance();
      now.add(Calendar.DATE, 1);
      d = now.get(Calendar.SECOND)
             + 100L * now.get(Calendar.MINUTE)
             + 10000L * now.get(Calendar.HOUR_OF_DAY)
             + 1000000L * now.get(Calendar.DATE)
             + 100000000L * (now.get(Calendar.MONTH) + 1)
             + 10000000000L * now.get(Calendar.YEAR);
    }finally{
      now = null;
    }
    return d;
  }

  public static long getNextDay() {
    Calendar now = null;
    long d = 0;
    try{
      now = Calendar.getInstance();
      now.add(Calendar.DATE, 1);
      d = now.get(Calendar.DATE)
      + 100L * (now.get(Calendar.MONTH) + 1)
      + 10000L * now.get(Calendar.YEAR);
    }finally{
      now = null;
    }
    return d;
  }

  public static long getDay() {
    Calendar now = null;
    long d = 0;
    try{
      now = Calendar.getInstance();
      d =  now.get(Calendar.DATE)
      + 100L * (now.get(Calendar.MONTH) + 1)
      + 10000L * now.get(Calendar.YEAR);
    }finally{
      now  = null;
    }
    return d;
  }

  public static boolean isValidDate(long lDate){
    String timestr = String.valueOf(lDate);

    if (timestr.length() != 14) return false;
    timestr  = getYear(lDate) +"-"+ getMonth(lDate)+"-"+  getDay(lDate)+" "
        + getHour(lDate)+":"+ getMinute(lDate)+":"+ getSecond(lDate);
    //System.out.println(timestr);
    try{  
      return DateFormat.getDateInstance().parse(timestr)!=null;  
    }catch(ParseException e){  
      return false;  
    }   
  }

  public static boolean isValidDate(String sDate){
    try{  
      return DateFormat.getDateInstance().parse(sDate)!=null;  
    }catch(ParseException e){  
      return false;  
    }   
  }

  public static Date str2Date(String sDate){
    try{  
      return DateFormat.getDateInstance().parse(sDate);  
    }catch(ParseException e){  
      return null;  
    }   
  }


  public static long getDataTime(Date date){
    Calendar now = null;
    long d = 0;
    try{
      now = Calendar.getInstance();
      now.setTime(date);
      d = now.get(Calendar.SECOND)
             + 100L * now.get(Calendar.MINUTE)
             + 10000L * now.get(Calendar.HOUR_OF_DAY)
             + 1000000L * now.get(Calendar.DATE)
             + 100000000L * (now.get(Calendar.MONTH) + 1)
             + 10000000000L * now.get(Calendar.YEAR);
    }finally{
      now  = null;
    }
    return d;
  }

  /**
   * �����û�����
   * @param age int
   * @param birth long
   * @param time long
   * @return long
   */
  public static long getBirth(int age, long birth, long time) {
    return birth + 10000000000L * (getAge(birth, time) - age);
  }

  public static void main(String[] args) { //test
    System.out.println(getDatePart(20061206020701L, 20061206020638L));
  }


}


