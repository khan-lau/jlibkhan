/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice;

import java.util.*;
import java.util.Map.Entry;

import com.khan.AppMain.*;
import com.khan.db.*;
import com.khan.graph.*;
import com.khan.webservice.bean.*;




public class AreaManager {

  public AreaManager() {
  }


  /** 
   * �޸�����
   * 
   * @param ui UserInfo ��½�û���Ϣ
   * @param regionFlag ���α�־ 0Ϊ����,1Ϊ�����
   * @param regionName String ������
   * @param region ��������
   * 
   * @return int
   */
  public int modifyArea(UserInfo ui, int regionFlag, String regionName, double[][] region ){
    int result = 0;
    int area_id = 0;



    if(isExistPrivateRegion(ui, regionName) ) { //�޸�area
      area_id = deleteAreaDetail(ui, regionName);
    }else{ //���area
      area_id = insertArea(ui,regionName);
    }

    insertAreaShape(area_id, region); // ���� ����λ���� shape��¼

    AreaDetail[] ad_details = null;
    try{
      if( 0 == regionFlag) { //����

        Rect rect = new Rect(region[0][0], region[0][1], region[1][0], region[1][1]);

        HashMap<Integer, City> city_map = SysConf.getCitys();
        ad_details = getRectCrossRegionSendList(area_id, rect, city_map);
        SysConf.clearCitys(city_map);
        city_map = null;

      }else{ //�����
        Point[] points = new Point[region.length];
        for(int i=0; i<region.length; ++i) {
          points[i] = new Point(region[i][0], region[i][1]);
        }
        Polygon pg = new Polygon(points);

        HashMap<Integer, City> city_map = SysConf.getCitys();
        ad_details = getPolygonCrossRegionSendList(area_id, pg, city_map);

//System.out.println("ad_details.length:"+ad_details.length);

        SysConf.clearCitys(city_map);

        city_map = null;
      }

      insertAreaDetail(area_id, ad_details);

    }catch (Exception e){
      e.printStackTrace();
    }finally{
      ad_details = null;
    }
    return result;
  }


  /** 
   * ȡ���û���ѡ���������ķ����б�
   * 
   * @param message_id
   * @param rect
   * 
   * @return SendList[]
   */
  private AreaDetail[] getRectCrossRegionSendList(int area_id, Rect rect, HashMap<Integer, City> city_map){
    ArrayList<AreaDetail> ad_list = new ArrayList<AreaDetail>();

    for (Iterator itCity=city_map.entrySet().iterator(); itCity.hasNext(); ) {
      Map.Entry cityEntry = (Map.Entry)itCity.next(); 
      City ci = city_map.get( cityEntry.getKey() );

      if( ! rect.isRectCross(ci.getCityRect()) ) { //�����city ���ཻ

      } else if( ! rect.isRectIn(ci.getCityRect())  ){ //��� ��city�����ཻ, ��������

        for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
          Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
          Bsc bsc =  ci.getBscs().get( bscEntry.getKey() );

          if(! rect.isRectCross(bsc.getBscRect())) { //�����bsc ���ཻ

          } else if ( !rect.isRectIn(bsc.getBscRect()) ) { //��� ��bsc�����ཻ, ��������
            for(Iterator itCell=bsc.getCellList().entrySet().iterator(); itCell.hasNext(); ) {
              Map.Entry cellEntry = (Map.Entry)itCell.next(); 
              Cell cell = bsc.getCellList().get( cellEntry.getKey() );

              if( rect.isPointInRect( cell.getX(), cell.getY() ) ) {
                ad_list.add(new AreaDetail(area_id, cell.getID(), 0) );
              }

            }


          }else{ //��ȫ����bsc
            ad_list.add(new AreaDetail(area_id, bsc.getBscID(), 1) );
          }
        }


      } else{ // ��ȫ����city
        ad_list.add(new AreaDetail(area_id, ci.getCityID(), 2) );
      }

    }

    AreaDetail[] result  = new AreaDetail[ad_list.size()];
    for(int i=0; i<result.length; ++i) {
      result[i] = ad_list.get(i);
    }

    ad_list.clear();
    ad_list = null;

    return result;
  }


  /** 
   * ���������Ӧ�������
   * 
   * @param area_id
   * @param region
   * 
   * @return int
   */
  private int insertAreaShape(int area_id, double[][] region){
    String sql = "";
    int result = 0;

    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();

      for(int i=0; i<region.length; ++i) {
        sql = "Insert Into cbs_area_shape(area_id, point_index, x, y) "+
              "Values(" + area_id + ", " + i + ", " + region[i][0] + ", " + region[i][1] + ")";
        dcp.excuteSql(sql, dpc);
      }
    }finally{
      dcp.release(dpc);
    }

    return result;
  }


  /** 
   * �������������¼
   * 
   * @param ui
   * @param regionName
   * 
   * @return int
   */
  private int insertArea(UserInfo ui, String regionName){
    int result = 0;
    String area_id = null;

    String sql  = "Insert Into cbs_area(area_name, type, create_time, create_by) "+
                  "Values('" + regionName + "', 'private', now(), "+ui.getUserID()+" )";
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();
      dcp.excuteSql(sql, dpc);

      sql = "Select LAST_INSERT_ID()";
      data = dcp.getRecord(sql, dpc);

      if(data == null || data.length < 1) return -15;

      result = Integer.parseInt(data[0][0]);

    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }


  /** 
   * ȡ���û���ѡ����������ķ����б�
   * 
   * @param message_id
   * @param poly
   * 
   * @return SendList[]
   */
  private AreaDetail[] getPolygonCrossRegionSendList(int area_id, Polygon poly , HashMap<Integer, City> city_map){
    ArrayList<AreaDetail> ad_list = new ArrayList<AreaDetail>();

    Rect rect = poly.getEquivalentRect();

    for (Iterator itCity=city_map.entrySet().iterator(); itCity.hasNext(); ) {
      Map.Entry cityEntry = (Map.Entry)itCity.next(); 
      City ci = city_map.get( cityEntry.getKey() );

      if( rect.isRectCross(ci.getCityRect()) ) { //����ཻcity 

        if(poly.isRectInPolygon( ci.getCityRect() ) > 0) { //�������city

          ad_list.add(new AreaDetail(area_id, ci.getCityID(), 2) );

        }else{ //������city�����ཻ

          for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
            Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
            Bsc bsc =  ci.getBscs().get( bscEntry.getKey() );

            if( rect.isRectCross(bsc.getBscRect() ) ) { //����ཻbsc

              if( poly.isRectInPolygon(bsc.getBscRect() ) > 0 ) { //�������bsc
                ad_list.add(new AreaDetail(area_id, bsc.getBscID(), 1) );

              }else { //����,������ bsc�����ཻ

                for(Iterator itCell=bsc.getCellList().entrySet().iterator(); itCell.hasNext(); ) {
                  Map.Entry cellEntry = (Map.Entry)itCell.next(); 
                  Cell cell = bsc.getCellList().get( cellEntry.getKey() );

                  if( poly.isPointInPolygon( cell.getX(), cell.getY() ) > 0 ) {

                    ad_list.add(new AreaDetail(area_id, cell.getID(), 0) ); 
                  }
                }

              }

            }

          }

        }

      }
    }

    AreaDetail[] result  = new AreaDetail[ad_list.size()];
    for(int i=0; i<result.length; ++i) {
      result[i] = ad_list.get(i);
    }

    ad_list.clear();
    ad_list = null;

    return result;
  }





  /** 
   * ɾ������
   * 
   * @param ui UserInfo ��½�û���Ϣ
   * @param regionName String ������
   * 
   * @return int
   */
  public int deleteArea(UserInfo ui, String regionName){
    int result = 0;
    String area_id = null;

    String sql  = "SELECT area_id "+
                  "FROM cbs_area "+
                  "Where area_name = '"+ regionName 
                  +"' And type = 'private' And create_by = " + ui.getUserID();
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);

      if(data == null || data.length < 1 ) 
         result = -12;
      else{
        area_id = data[0][0];

        sql = "Delete From cbs_cell_in_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_bsc_in_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_city_in_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_area_shape Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);
      }
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }


  /** 
   * ����area����ϸ
   * 
   * @param area_id
   * @param area_details
   * 
   * @return int
   */
  private int insertAreaDetail(int area_id, AreaDetail[] area_details){
    int result = 0;

    String sql  = "";
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();

      for(int i=0; i<area_details.length; ++i) {
        switch (area_details[i].getType()) { //0:cell, 1:bsc, 2:city
        case 0:
          sql = "Insert Into cbs_cell_in_area(area_id, cell_id, update_time) Values(" + area_id + "," + area_details[i].getId() + " , now())";
          dcp.excuteSql(sql, dpc);
          break;
        case 1:
          sql = "Insert Into cbs_bsc_in_area(area_id, bsc_id, update_time) Values(" + area_id + "," + area_details[i].getId() + " , now())";
          dcp.excuteSql(sql, dpc);
          break;
        case 2:
          sql = "Insert Into cbs_city_in_area(area_id, city_id, update_time) Values(" + area_id + "," + area_details[i].getId() + " , now())";
          dcp.excuteSql(sql, dpc);
          break;
        default:
          break;
        }

      }

    }finally {
      dcp.release(dpc);
    }

    return result;

  }


  /** 
   * ɾ����������Ϣ
   * 
   * @param ui UserInfo ��½�û���Ϣ
   * @param regionName String ������
   * 
   * @return int
   */
  private int deleteAreaDetail(UserInfo ui, String regionName){
    int result = 0;
    String area_id = null;

    String sql  = "SELECT area_id "+
                  "FROM cbs_area "+
                  "Where area_name = '"+ regionName 
                  +"' And type = 'private' And create_by = " + ui.getUserID();
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);

      if(data == null || data.length < 1 ) 
         result = -12;
      else{
        area_id = data[0][0];

        sql = "Delete From cbs_cell_in_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_bsc_in_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_city_in_area Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

        sql = "Delete From cbs_area_shape Where area_id = " + area_id;
        dcp.excuteSql(sql, dpc);

      }
    }finally {
      data = null;
      dcp.release(dpc);
    }
    result = Integer.parseInt(area_id);
    return result;
  }



  /** 
   * ��ѯ����
   * 
   * @param ui UserInfo ��½�û���Ϣ
   * @param regionName String ������
   * 
   * @return double[][]
   */
  public double[][] queryArea(UserInfo ui, String regionName ){
    double[][] result = null;

    int area_id = getAreaID(ui,regionName);
    if ( -1 == area_id ) {
      return result;
    }

    Point[] pt = getRegionShape(area_id);
    result = new double[pt.length][2];
    for(int i=0; i<pt.length; ++i) {
      result[i][0] = pt[i].getX();
      result[i][1] = pt[i].getY();
    }
    pt = null;
    return result;
  }


  private Point[] getRegionShape(int areaId){
    Point[] result = null;
    String sql  = "SELECT x, y "
        +"FROM cbs_city_shape "
        +"Where area_id = " + areaId + " Order By point_index";
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      result = new Point[data.length];
      for(int i=0; i<data.length; ++i) {
        result[i] = new Point(Double.parseDouble(data[i][0]), Double.parseDouble(data[i][1]) );
      }

    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }


  public int getAreaID(UserInfo ui, String regionName){
    int result = -1;
    String sql  = "SELECT area_id "
        +"FROM cbs_area "
        +"Where area_name = '"+regionName+"' And  ( ( create_by = " + ui.getUserID() + " And type='private')"
        +" Or type = 'public'  )";
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);

      if(data != null && data.length > 0) 
        result = Integer.parseInt(data[0][0]);

    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }



  /** 
   * ���area������city
   * 
   * @param areaId
   * 
   * @return int[]
   */
  public int[] getCityInArea(int areaId){
    int[] result = null;
    String sql  = "SELECT city_id "
        +"FROM cbs_city_in_area "
        +"Where area_id = " + areaId;
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      result = new int[data.length];
      for(int i=0; i<data.length; ++i) {
        result[i] = Integer.parseInt(data[i][0]);
      }

    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }



  /** 
   * ��ȡָ��area������bsc
   * 
   * @param areaId
   * 
   * @return int[]
   */
  public int[] getBscInArea(int areaId){
    int[] result = null;
    String sql  = "SELECT bsc_id "
        +"FROM cbs_bsc_in_area "
        +"Where area_id = " + areaId;
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      result = new int[data.length];
      for(int i=0; i<data.length; ++i) {
        result[i] = Integer.parseInt(data[i][0]);
      }

    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }

  /** 
   * ��ȡָ��area������cell
   * 
   * @param areaId
   * 
   * @return Cell[]
   */
  public Cell[] getCellInArea(int areaId){
    Cell[] result = null;
    String sql  = "SELECT C.id, C.cell_id, C.bsc_id "
        +"FROM cbs_cell_in_area as A Left Join cbs_cell As C On A.cell_id = C.id "
        +"Where A.area_id = " + areaId;
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      result = new Cell[data.length];
      for(int i=0; i<data.length; ++i) {
        Bsc bsc = new Bsc(Integer.parseInt(data[i][2]), null, null, "", "", 0 );
        result[i] = new Cell(Integer.parseInt(data[i][0]), bsc, data[i][1], "", 0, 0, "", 0);
      }

    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }

  /** 
   * �Ƿ�����û���������˽������
   * 
   * @param ui UserInfo �û���Ϣ
   * @param regionName String ������
   * 
   * @return boolean
   */
  private boolean isExistPrivateRegion(UserInfo ui, String regionName){
    boolean result = false;
    String sql  = "SELECT area_id "
        +"FROM cbs_area "
        +"Where area_name = '"+regionName+"' And  ( ( create_by = " + ui.getUserID() + " And type='private') )";
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      if(data != null && data.length > 0) result = true;
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }


  /** 
   * �Ƿ�����û���������
   * 
   * @param ui  UserInfo �û���Ϣ
   * @param regionName String ������
   * 
   * @return boolean
   */
  public boolean isExistRegion(UserInfo ui, String regionName){
    boolean result = false;
    String sql  = "SELECT area_id "
        +"FROM cbs_area "
        +"Where area_name = '"+regionName+"' And  ( ( create_by = " + ui.getUserID() + " And type='private')"
        +" Or type = 'public'  )";
    
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();

    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      if(data != null && data.length > 0) result = true;
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return result;
  }





}




