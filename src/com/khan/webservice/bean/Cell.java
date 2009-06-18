/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

public class Cell {
  private String[] Status_enum = {"active","inactive"};

  private Bsc Cell_Bsc;
  private int ID;
  private String CellID;
  private String CellName;
  private double X;
  private double Y;
  private byte Status;
  private int CreateBy;

  public Cell(int id, Bsc bsc, String cell_id, String cell_name, 
              double x, double y, String status, int create_by) 
  {
    ID = id;
    Cell_Bsc = bsc;
    CellID = cell_id;
    CellName = cell_name;
    X = x;
    Y = y;
    setStatus(status);
    CreateBy = create_by;
  }

  public void setID(int id){
    ID = id;
  }

  public int getID(){
    return ID;
  }

  public void setCellBsc(Bsc bsc){
    Cell_Bsc = bsc;
  }
  public Bsc getCellBsc(){
    return Cell_Bsc;
  }

  public void setCellID(String cell_id){
    CellID = cell_id;
  }
  public String getCellID(){
    return CellID;
  }

  public void setCellName(String cell_name){
    CellName = cell_name;
  }
  public String getCellName(){
    return CellName;
  }

  public void setX(double x){
    X = x;
  }
  public double getX(){
    return X;
  }

  public void setY(double y){
    Y = y;
  }
  public double getY(){
    return Y;
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

  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("ID=[");
    buf.append(ID);
    buf.append("],Cell_Bsc=[");
    buf.append(Cell_Bsc);
    buf.append("], CellID=[");
    buf.append(CellID);
    buf.append("], CellName=[");
    buf.append(CellName);
    buf.append("], X=[");
    buf.append(X);
    buf.append("], Y=[");
    buf.append(Y);
    buf.append("], Status=["); 
    buf.append(getStatus());
    buf.append("], CreateBy=[");
    buf.append(CreateBy);
    buf.append("]");
    return buf.toString();
  }
}





