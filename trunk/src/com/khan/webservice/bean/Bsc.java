/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

import java.util.*;
import com.khan.graph.*;


public class Bsc {
  private String[] Status_enum = {"active","inactive"};

  private int BscID;
  private City Bsc_City;
  private BscType Bsc_Type;
  private String BscName;
  private byte Status;
  private int CreateBy;
  private HashMap<Integer, Cell> CellList;

  public Bsc(int bsc_id, City bsc_city, BscType bsc_type, String bsc_name, String status, int create_by) {
    BscID = bsc_id;
    Bsc_City = bsc_city;
    Bsc_Type = bsc_type;
    BscName = bsc_name;
    setStatus(status);
    CreateBy = create_by;
  }


  public void setBscID(int bsc_id){
    BscID = bsc_id;
  }
  public int getBscID(){
    return BscID;
  }

  public void setBscCity(City bsc_city){
    Bsc_City = bsc_city;
  }
  public City  getBscCity(){
    return Bsc_City;
  }

  public void getBscType(BscType bsc_type){
    Bsc_Type = bsc_type;
  }
  public BscType getBscType(){
    return Bsc_Type;
  }

  private void setBscName(String bsc_name){
    BscName = bsc_name;
  }

  private String getBscName(){
    return BscName;
  }

  public void setStatus(String status){
    for(int i=0; i<Status_enum.length; i++) {
      if(status.equals( Status_enum[i]) ) Status = (byte)i;
    }
  }

  public String getStatus(){
    return Status_enum[Status];
  }

  public void setCreateBy(int create_by){
    CreateBy = create_by;
  }

  public int getCreateBy(){
    return CreateBy;
  }

  public void setCellList(HashMap<Integer, Cell> celllist){
    CellList = celllist;
  }

  public HashMap<Integer, Cell> getCellList(){
    return CellList;
  }

   /** 
    * 查询bsc最大覆盖矩形范围
    * @param bsc
    * @return Rect
    */
   public Rect getBscRect(){
     double topX = 0;
     double topY = 0;
     double bottomX = 0;
     double bottomY = 0;

     for(Iterator itCell=getCellList().entrySet().iterator(); itCell.hasNext(); ) {
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
    return new Rect(topX, topY, bottomX, bottomY);

  }



  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("BscID=[");
    buf.append(BscID);
    buf.append("], Bsc_City=[");
    buf.append(Bsc_City);
    buf.append("], Bsc_Type=[");
    buf.append(Bsc_Type);
    buf.append("], BscName=[");
    buf.append(BscName);
    buf.append("], Status=[");
    buf.append(getStatus());
    buf.append("], CreateBy=[");
    buf.append(CreateBy);
    buf.append("]");
    return buf.toString();
  }
}





