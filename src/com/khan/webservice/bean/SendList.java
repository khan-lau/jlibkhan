/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

public class SendList {

  private int MessageId ;
  private int BscId;
  private int CellCount;
  private String CellList;

  public SendList(int message_id, int bsc_id, int cell_count, String cell_list) {
    MessageId = message_id;
    BscId = bsc_id;
    CellCount = cell_count;
    CellList = cell_list;
  }

  public void setMessageId(int message_id){
    MessageId = message_id;
  }

  public int getMessageId(){
    return MessageId;
  }

  public void setBscId(int bsc_id){
    BscId = bsc_id;
  }

  public int getBscId(){
    return BscId;
  }

  public void setCellCount(int cell_count){
    CellCount = cell_count;
  }

  public int getCellCount(){
    return CellCount;
  }

  public void setCellList(String cell_list){
    CellList = cell_list;
  }

  public String getCellList(){
    return CellList;
  }

}





