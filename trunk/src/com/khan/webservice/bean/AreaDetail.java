/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

public class AreaDetail {

  private int AreaId; //area_id
  private int Id;     //id
  private int Type = 0;  //ÀàÐÍ 0:cell, 1:bsc, 2:city


  public AreaDetail(int area_id, int id, int type) {
    AreaId = area_id;
    Id = id;
    if( type > -1 && type < 3) {
      Type = type;
    }
  }

  public int getAreaId(){
    return AreaId;
  }

  public int getId(){
    return Id;
  }

  public int getType(){
    return Type;
  }

}





