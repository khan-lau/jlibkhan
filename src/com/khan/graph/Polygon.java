package com.khan.graph;




public class Polygon{

  Point[] points = null; 

  public Polygon(Point[] points){
    this.points = points;
  }


  /** 
   * �жϵ��Ƿ��ڶ���η�Χ�� 
   * @param p Point ������ 
   * @return int �����������ཻ ż��Ϊ���ཻ ż��Ϊ0
   */
  public int isPointInPolygon(Point p){
    if (points == null || points.length < 2) return -1;

    if (points.length == 2){//����
      if( points[0].getX() == points[1].getX() || points[0].getY() == points[1].getY() ) //����һ���Ϸ��ľ���
        return -2;

      Rect r = new Rect(points[0], points[1]);
      return r.isPointInRect(p) ? 1 : 0;
    }

    //�����
    int crosses=0;
    for(int i=0; i < points.length - 1; ++i){
      crosses += HaveCross(p.getX(), p.getY(), 
                           points[i].getX(), points[i].getY(), 
                           points[i+1].getX(), points[i+1].getY()
                           );
//System.out.println("line:[" + points[i] +",|"+ points[i+1]+ "]  \t" +p + " " + " " +crosses + " i:" + i);
    }

    //ͷβ���պϵĶ������Ҫ���⴦��
    if (points[0].getX() != points[points.length -1].getX() 
        || points[0].getY() != points[points.length -1].getY())
    {
      crosses += HaveCross(p.getX(), p.getY(), 
                           points[0].getX(), points[0].getY(), 
                           points[points.length -1].getX(), points[points.length -1].getY()
                           );
//System.out.println(p + " " + " " +crosses);
    }



    //������Ϊż��, ���ཻ
    if (crosses % 2 == 0)
      return 0;

    return crosses;
  }

  /** 
   * �жϵ��Ƿ��ڶ���η�Χ��
   * 
   * @param x double ���x����
   * @param y double ���y����
   * 
   * @return int
   */
  public int isPointInPolygon(double x, double y){
    Point p = new Point(x, y);
    return isPointInPolygon(p);
  }


  /** 
   * �����Ƿ��ڶ���η�Χ��
   * @param r Rect ��������
   * @return int 1Ϊ����, 0Ϊ������
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
   * ȡ�øö���εĵ�Ч����,��ȡ���(x,y) ����С(x,y)����, 
   * ��һ������ 
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



  /**  �Ե�(x,y)Ϊԭ��, ���һ�һ������, �ж������Ƿ����߶�(x1,y1 ;  x2,y2)�ཻ
   * @param xp ���x����
   * @param yp ���y����
   * @param xl1 �ߵ�x1����
   * @param yl1 �ߵ�y1����
   * @param xl2 �ߵ�x2����
   * @param yl2 �ߵ�y2����
   * @return int 0:���ཻ, 1�ཻ
   */
  static public int HaveCross(double xp, double yp, double xl1, double yl1, double xl2, double yl2){
    //�߶�(x1,y1;  x2,y2) ���(xp,yp)����������ˮƽ, �޷��ཻ
    if(yl1 == yl2) return 0;

    //������ֺ��߶ζ����ཻ�����
    yp = yp * 2 + 0.0000001;
    yl1 = yl1 * 2;
    yl2 = yl2 * 2;
//System.out.println("yp:" +yp + " yl1:" + yl1 + " yl2:" + yl2);
    if(yp < yl1 && yp < yl2) return 0; //�����߶��·�
    if(yp > yl1 && yp > yl2) return 0; //�����߶��Ϸ�

    double dx = xl2 - xl1;
    double dy = yl2 - yl1;

    //����ˮƽ��y���߶ν����x����
    double crossX = (xl1 + dx * (yp - yl1) / dy);
//System.out.println(crossX);
    //�����ڵ�(xp, yp)���ұ�, ���ߺ��߶��ཻ
    if(crossX >= xp) return 1;

    return 0;
  }

}


