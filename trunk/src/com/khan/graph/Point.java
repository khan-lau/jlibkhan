/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.graph;

public class Point {
  private double x;
  private double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void setX(double x){
    this.x = x;
  }

  public double getX(){
    return x;
  }

  public void setY(double y){
    this.y = y;
  }

  public double getY(){
    return y;
  }

  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("X=");
    buf.append(x);
    buf.append(", Y=");
    buf.append(y);
    buf.append("");

    return buf.toString();
  }

}





