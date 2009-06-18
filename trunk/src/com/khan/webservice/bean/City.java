/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

import java.util.*;
import com.khan.graph.*;

public class City {

  private String[] Status_enum = {"active","inactive"};

  private int CityID;
  private String CityName;
  private String Operator;
  private byte Status;
  private HashMap<Integer, Bsc> Bscs;

  public City(int city_id, String city_name, String operat, String status) { 
    CityID = city_id;
    CityName = city_name;
    Operator = operat;

    setStatus(status);
  }


  public void setCityID(int city_id){
    CityID = city_id;
  }

  public int getCityID(){
    return CityID;
  }

  public void setCityName(String city_name){
    CityName = city_name;
  }

  public String getCityName(){
    return CityName;
  }

  public void setOperator(String operat){
    Operator = operat;
  }

  public String getOperator(){
    return Operator;
  }

  public void setStatus(String status){
    for(int i=0; i<Status_enum.length; i++) {
      if(status.equals( Status_enum[i]) ) Status = (byte)i;
    }
  }

  public String getStatus(){
    return Status_enum[Status];
  }

  public void setBscs(HashMap<Integer, Bsc> bscs){
    Bscs = bscs;
  }

  public HashMap<Integer, Bsc> getBscs(){
    return Bscs;
  }

  /** 
   * 查询city最大覆盖矩形范围
   * @param city 
   * @return Rect
   */
  public Rect getCityRect(){
    double topX = 0;
    double topY = 0;
    double bottomX = 0;
    double bottomY = 0;

    for(Iterator itBsc=getBscs().entrySet().iterator(); itBsc.hasNext(); ){
      Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
      Bsc bsc=(Bsc)bscEntry.getValue();

      for(Iterator itCell=bsc.getCellList().entrySet().iterator(); itCell.hasNext(); ) {
        Map.Entry celEntry = (Map.Entry)itCell.next(); 
        Cell cell = (Cell)celEntry.getValue();

//System.out.println(cell.getCellID() +":x="+ cell.getX()+" y="+cell.getY());

        if(0 == topX) topX = cell.getX();
        if(0 == topY) topY = cell.getY();
        if(0 == bottomX) bottomX = cell.getX();
        if(0 == bottomY) bottomY = cell.getY();

        if(cell.getX() <  topX) topX = cell.getX();
        if(cell.getX() > bottomX) bottomX = cell.getX();

        if(cell.getY() < topY) topY = cell.getY();
        if(cell.getY() > bottomY)  bottomY = cell.getY();

//System.out.println( new Rect(topX, topY, bottomX, bottomY));
//System.out.println();



      }
    }

    return new Rect(topX, topY, bottomX, bottomY);
  }



  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("CityID=[");
    buf.append(CityID);
    buf.append("], CityName=[");
    buf.append(CityName);
    buf.append("], Operator=[");
    buf.append(Operator);
    buf.append("], Status=[");
    buf.append(getStatus());
    buf.append("]");
    return buf.toString();
  }
}





