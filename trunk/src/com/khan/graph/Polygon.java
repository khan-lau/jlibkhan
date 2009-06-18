package com.khan.graph;




public class Polygon{

  Point[] points = null; 

  public Polygon(Point[] points){
    this.points = points;
  }


  /** 
   * 判断点是否在多边形范围内 
   * @param p Point 点坐标 
   * @return int 返回奇数则相交 偶数为不相交 偶数为0
   */
  public int isPointInPolygon(Point p){
    if (points == null || points.length < 2) return -1;

    if (points.length == 2){//矩形
      if( points[0].getX() == points[1].getX() || points[0].getY() == points[1].getY() ) //不是一个合法的矩形
        return -2;

      Rect r = new Rect(points[0], points[1]);
      return r.isPointInRect(p) ? 1 : 0;
    }

    //多边形
    int crosses=0;
    for(int i=0; i < points.length - 1; ++i){
      crosses += HaveCross(p.getX(), p.getY(), 
                           points[i].getX(), points[i].getY(), 
                           points[i+1].getX(), points[i+1].getY()
                           );
//System.out.println("line:[" + points[i] +",|"+ points[i+1]+ "]  \t" +p + " " + " " +crosses + " i:" + i);
    }

    //头尾不闭合的多边形需要额外处理
    if (points[0].getX() != points[points.length -1].getX() 
        || points[0].getY() != points[points.length -1].getY())
    {
      crosses += HaveCross(p.getX(), p.getY(), 
                           points[0].getX(), points[0].getY(), 
                           points[points.length -1].getX(), points[points.length -1].getY()
                           );
//System.out.println(p + " " + " " +crosses);
    }



    //交点数为偶数, 则不相交
    if (crosses % 2 == 0)
      return 0;

    return crosses;
  }

  /** 
   * 判断点是否在多边形范围内
   * 
   * @param x double 点的x坐标
   * @param y double 点的y坐标
   * 
   * @return int
   */
  public int isPointInPolygon(double x, double y){
    Point p = new Point(x, y);
    return isPointInPolygon(p);
  }


  /** 
   * 矩形是否在多边形范围内
   * @param r Rect 矩形坐标
   * @return int 1为包含, 0为不包含
   */
  public int isRectInPolygon(Rect r){
    int crosses1 = isPointInPolygon(r.getTopLeft().getX(), r.getTopLeft().getY());
    if (crosses1 < 1)
      return 0;

    int crosses2 = isPointInPolygon(r.getTopLeft().getX(), r.getBottomRight().getY());
    if (crosses2 < 1)
      return 0;

    if (isPointInPolygon(r.getBottomRight().getX(), r.getTopLeft().getY()) != crosses1)
      return 0;

    if (isPointInPolygon(r.getBottomRight().getX(), r.getBottomRight().getY()) != crosses2)
      return 0;
	
    return 1;
  }


  /** 
   * 取得该多边形的等效矩形,即取最大(x,y) 与最小(x,y)坐标, 
   * 画一个矩形 
   * @return Rect
   */
  public Rect getEquivalentRect(){
    double topX = 0;
    double topY = 0;
    double bottomX = 0;
    double bottomY = 0;

    for(int i=0; i< points.length; ++i ) {
      if(0 == topX) topX = points[i].getX();
      if(0 == topY) topY = points[i].getY();
      if(0 == bottomX) bottomX = points[i].getX();
      if(0 == bottomY) bottomY = points[i].getY();

      if(points[i].getX() <  topX) topX = points[i].getX();
      if(points[i].getX() > bottomX) bottomX = points[i].getX();

      if(points[i].getY() < topY) topY = points[i].getY();
      if(points[i].getY() > bottomY)  bottomY = points[i].getY();
    }

    return new Rect(topX, topY, bottomX, bottomY);
  }



  /**  以点(x,y)为原点, 向右画一条射线, 判断射线是否与线段(x1,y1 ;  x2,y2)相交
   * @param xp 点的x坐标
   * @param yp 点的y坐标
   * @param xl1 线的x1坐标
   * @param yl1 线的y1坐标
   * @param xl2 线的x2坐标
   * @param yl2 线的y2坐标
   * @return int 0:不相交, 1相交
   */
  static public int HaveCross(double xp, double yp, double xl1, double yl1, double xl2, double yl2){
    //线段(x1,y1;  x2,y2) 与点(xp,yp)引出的射线水平, 无法相交
    if(yl1 == yl2) return 0;

    //避免出现和线段顶点相交的情况
    yp = yp * 2 + 0.0000001;
    yl1 = yl1 * 2;
    yl2 = yl2 * 2;
//System.out.println("yp:" +yp + " yl1:" + yl1 + " yl2:" + yl2);
    if(yp < yl1 && yp < yl2) return 0; //点在线段下方
    if(yp > yl1 && yp > yl2) return 0; //点在线段上方

    double dx = xl2 - xl1;
    double dy = yl2 - yl1;

    //计算水平线y和线段交点的x坐标
    double crossX = (xl1 + dx * (yp - yl1) / dy);
//System.out.println(crossX);
    //交点在点(xp, yp)的右边, 射线和线段相交
    if(crossX >= xp) return 1;

    return 0;
  }

}


