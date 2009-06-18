/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.graph;

import java.lang.Math;

public class Rect {
  private Point topLeft;
  private Point bottomRight;

  public Rect(Point topLeft, Point bottomRight) {
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
  }

  public Rect(double topX, double topY, double bottomX, double bottomY) {
    topLeft = new Point(topX, topY);
    bottomRight = new Point(bottomX, bottomY);
  }

  /** 
   * 判断矩形是否包含点p
   * @param p Point 点p的坐标
   * @return boolean
   */
  public boolean isPointInRect(double x, double y){
    Point p = new Point(x,y);

    return isPointInRect(p);
  }


  /** 
   * 判断矩形是否包含点p
   * @param p Point 点p的坐标
   * @return boolean
   */
  public boolean isPointInRect(Point p){
    // 矩形右边
    if(p.getX() > topLeft.getX() && p.getX() > bottomRight.getX()) 
      return false;

    //矩形左边
    if(p.getX() < topLeft.getX() && p.getX() < bottomRight.getX()) 
      return false;

    // 矩形上边
    if(p.getY() > topLeft.getY() && p.getY() > bottomRight.getY()) 
      return false;

    //矩形下边
    if(p.getY() < topLeft.getY() && p.getY() < bottomRight.getY()) 
      return false;

    return true;
  }


  /** 
   * 判断矩形是否包含 r矩形
   * @param r Rect 矩形坐标
   * @return boolean
   */
  public boolean isRectIn(Rect r){
    return isPointInRect( r.getTopLeft()) && isPointInRect( r.getBottomRight() );
  }



  /** 
   * 判断矩形是否与矩形r相交
   * @param r Rect 矩形坐标
   * @return boolean
   */
  public boolean isRectCross(Rect r){
    double r_minx = Math.min( r.getTopLeft().getX() , r.getBottomRight().getX() );
    double r_maxx = Math.max( r.getTopLeft().getX() , r.getBottomRight().getX() );
    double r_miny = Math.min( r.getTopLeft().getY() , r.getBottomRight().getY() );
    double r_maxy = Math.max( r.getTopLeft().getY() , r.getBottomRight().getY() );

    double minx = Math.min( getTopLeft().getX() , getBottomRight().getX() );
    double maxx = Math.max( getTopLeft().getX() , getBottomRight().getX() );
    double miny = Math.min( getTopLeft().getY() , getBottomRight().getY() );
    double maxy = Math.max( getTopLeft().getY() , getBottomRight().getY() );

    if(r_minx > maxx || r_maxx < minx) return false;
    if(r_miny > maxy || r_maxy < miny) return false;
    
    return true;
  }


  public void setTopLeft(Point p){
    topLeft = p;
  }

  public Point getTopLeft(){
    return topLeft;
  }

  public void setBottomRight(Point p){
    bottomRight = p;
  }

  public Point getBottomRight(){
    return bottomRight;
  }


  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("topLeft=[");
    buf.append(topLeft);
    buf.append("], bottomRight=[");
    buf.append(bottomRight);
    buf.append("]");

    return buf.toString();
  }
}





