/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice;

import java.util.*;

import com.khan.AppMain.*;
import com.khan.datetime.SMPTime;
import com.khan.db.*;
import com.khan.graph.*;
import com.khan.net.*;
import com.khan.webservice.bean.*;


public class CbmOperation {

  public CbmOperation() {
  }

  public String getReport(UserInfo ui, int MsgID ){
    String result = "";
    String sql  = "SELECT bsc_id, send_time, sent_count, failure_count, failure_list "
                + "FROM cbs_message_send_list  "
                + "Where message_id = " + MsgID;
    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      if(data == null || data.length < 1) {
        result = "error = -16";
      }else{
        for(int i=0; i<data.length; ++i) {
          result = result + data[1]+"-"+ data[0]+":�ɹ���="+data[2]+";ʧ����="+data[3]+"\n";
        }
      }

    }finally {
      data = null;
      dcp.release(dpc);
    }
    return result;
  }


  /** 
   * ����С���㲥
   * @param ui UserInfo         �û���Ϣ
   * @param channel int         Ƶ��   0--65535
   * @param regionFlag int      ������������ 0--1 0:����  1:�����
   * @param regionName  String  ����������
   * @param region float[][]    ������������
   * @param level int           ���ȼ�
   * @param GS int              Э���� ���ͷ�Χ �ֲ�����bscȫ��
   * @param msgCode int         
   * @param msgUpdateNum int    
   * @param fmt String          �ַ���������
   * @param Cbm String          С���㲥��Ϣ����
   * @param startTime long       ����ʱ��
   * 
   * @return int
   */
  public int sendCbmAT(UserInfo ui, int channel, int regionFlag, String regionName, 
                     double[][] region, int level, int GS, 
                     int msgCode, int msgUpdateNum, String fmt, 
                     String cbm, long startTime, int Interval, 
                     long endTime)
  {
    int result = 0;
    AreaManager am = new AreaManager();
    cbm = cbm.trim();

    if( authFilter(cbm) ) { //��������Ƿ��ؼ���
      return -13;
    }

    if(! checkFmt(fmt) ) { //�����ʽ��֧��
      return -14;
    }

    String sql = "";
    int msgid = 0;

    int data_coding_scheme =  fmt.equals("7bit") ? 40 : 48 ;

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();


      //����Ҫ���͵Ĺ㲥����
      sql = "Insert Into cbs_message(message_channel, message_gs, message_code, message_update_number, "
          +                          "data_coding_scheme, create_time, create_by, priority) "
          + "Values(" + channel + "," + GS + "," + msgCode + "," + msgUpdateNum +", " 
          + data_coding_scheme + ", now(), " + ui.getUserID() + ", 'normal')";

      dcp.excuteSql(sql, dpc);

      sql = "Select LAST_INSERT_ID()";
      data = dcp.getRecord(sql, dpc);

      if(data == null || data.length < 1) return -15;

      msgid = Integer.parseInt(data[0][0]);

      //����Ҫ���͵�cbm��Ϣ, ����ָ����ʽ����, ����ҳ����cbs_message_page
      String[] cbm_split = enCodeCbm(fmt, cbm);
      for(int i=0; i<cbm_split.length; ++i) {
        sql = "Insert Into  cbs_message_page(message_id, page_no, page_content) Values( "  + msgid + ", " + i + ", '" + cbm_split[i] + "')";
        dcp.excuteSql(sql, dpc);
      }

      SendList[] send_list = null;

      if(null != regionName && !regionName.equals("") && am.isExistRegion(ui, regionName)) {//�̶�������
        int area_id = am.getAreaID(ui, regionName);

        send_list = getAreaSendList(msgid, area_id, am);
      }else if(0 == regionFlag){ //��ʱ������η���
        Rect rect = new Rect(region[0][0], region[0][1],  region[1][0], region[1][1]);

        HashMap<Integer, City> city_map = getCitys();
        send_list = getRectCrossRegionSendList(msgid, rect, city_map);
        clearCityList(city_map);
        city_map = null;

      }else if(1 == regionFlag){//��ʱ�������η���
        Point[] points = new Point[region.length];
        for(int i=0; i<region.length; ++i) {
          points[i] = new Point(region[i][0], region[i][1]);
        }
        Polygon pg = new Polygon(points);
        points = null;

        HashMap<Integer, City> city_map = getCitys();
        send_list = getPolygonCrossRegionSendList(msgid, pg, city_map);
        clearCityList(city_map);
        city_map = null;

      }else{ //���ǹ̶�����, ������ʱ����, ������ʱ����� �����
        return -2;
      }

      for(int i=0; i<send_list.length; ++i) {

        sql = "Insert into cbs_message_cell_list(message_id, bsc_id, cell_count, cell_list) "+
              "Values(" +msgid+ ", " +send_list[i].getBscId()+ "," +send_list[i].getCellCount()+ ",'" +send_list[i].getCellList()+ "')";
        dcp.excuteSql(sql, dpc);

        int timep = (int)SMPTime.getDatePart(endTime, startTime) / 60; //starttime ��endtimme ���������
        int step = (0 == timep / Interval) ? 1 : timep / Interval;

        //ʱ��η��������Ų�
        for(int j = 0; j<step; ++j) {
            sql = "Insert Into cbs_message_send_list(message_id, bsc_id, send_time, level) "+
                  "Values("+msgid+","+send_list[i].getBscId()+","+SMPTime.getDateAdd(startTime, step*60 )+","+level+")";
            dcp.excuteSql(sql, dpc);
        }
      }
      send_list = null;

    }catch (Exception e){
      e.printStackTrace();
    }finally{
      data = null;
      dcp.release(dpc);
    }

    return msgid;
  }




  /** 
   * ����С���㲥
   * @param ui UserInfo         �û���Ϣ
   * @param channel int         Ƶ��   0--65535
   * @param regionFlag int      ������������ 0--1 0:����  1:�����
   * @param regionName  String  ����������
   * @param region float[][]    ������������
   * @param level int           ���ȼ�
   * @param GS int              Э���� ���ͷ�Χ �ֲ�����bscȫ��
   * @param msgCode int         
   * @param msgUpdateNum int    
   * @param fmt String          �ַ���������
   * @param Cbm String          С���㲥��Ϣ����
   * @param startTime long       ����ʱ��
   * 
   * @return int
   */
  public int sendCbm(UserInfo ui, int channel, int regionFlag, String regionName, 
                     double[][] region, int level, int GS, 
                     int msgCode, int msgUpdateNum, String fmt, 
                     String cbm, long startTime)
  {
    int result = 0;
    AreaManager am = new AreaManager();
    cbm = cbm.trim();

    if( authFilter(cbm) ) { //��������Ƿ��ؼ���
      return -13;
    }

    if(! checkFmt(fmt) ) { //�����ʽ��֧��
      return -14;
    }

    String sql = "";
    int msgid = 0;

    int data_coding_scheme =  fmt.equals("7bit") ? 0x40 : 0x48 ;

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();


      //����Ҫ���͵Ĺ㲥����
      sql = "Insert Into cbs_message(message_channel, message_gs, message_code, message_update_number, "
          +                          "data_coding_scheme, create_time, create_by, priority) "
          + "Values(" + channel + "," + GS + "," + msgCode + "," + msgUpdateNum +", " 
          + data_coding_scheme + ", now(), " + ui.getUserID() + ", 'normal')";
    
      dcp.excuteSql(sql, dpc);

      sql = "Select LAST_INSERT_ID()";
      data = dcp.getRecord(sql, dpc);

      if(data == null || data.length < 1) return -15;

      msgid = Integer.parseInt(data[0][0]);
      result = msgid;

      //����Ҫ���͵�cbm��Ϣ, ����ָ����ʽ����, ����ҳ����cbs_message_page
      String[] cbm_split = enCodeCbm(fmt, cbm);
      for(int i=0; i<cbm_split.length; ++i) {
        sql = "Insert Into  cbs_message_page(message_id, page_no, page_content) Values( "  + msgid + ", " + i + ", '" + cbm_split[i] + "')";
        dcp.excuteSql(sql, dpc);
      }
    
      SendList[] send_list = null;

      if(null != regionName && !regionName.equals("") && am.isExistRegion(ui, regionName)) {//�̶�������
        int area_id = am.getAreaID(ui, regionName);

        send_list = getAreaSendList(msgid, area_id, am);
      }else if(0 == regionFlag){ //��ʱ������η���


        Rect rect = new Rect(region[0][0], region[0][1],  region[1][0], region[1][1]);

        HashMap<Integer, City> city_map = getCitys();
        send_list = getRectCrossRegionSendList(msgid, rect, city_map);
        clearCityList(city_map);
        city_map = null;

      }else if(1 == regionFlag){//��ʱ�������η���
        Point[] points = new Point[region.length];
        for(int i=0; i<region.length; ++i) {
          points[i] = new Point(region[i][0], region[i][1]);
        }
        Polygon pg = new Polygon(points);
        points = null;

        HashMap<Integer, City> city_map = getCitys();
        send_list = getPolygonCrossRegionSendList(msgid, pg, city_map);
        clearCityList(city_map);
        city_map = null;

      }else{ //���ǹ̶�����, ������ʱ����, ������ʱ����� �����
        return -2;
      }

      for(int i=0; i<send_list.length; ++i) {

        sql = "Insert into cbs_message_cell_list(message_id, bsc_id, cell_count, cell_list) "+
              "Values(" +msgid+ ", " +send_list[i].getBscId()+ "," +send_list[i].getCellCount()+ ",'" +send_list[i].getCellList()+ "')";
        dcp.excuteSql(sql, dpc);
        sql = "Insert Into cbs_message_send_list(message_id, status, bsc_id, send_time, level) "+
              "Values("+msgid+", 'queue', "+send_list[i].getBscId()+","+startTime+","+level+")";
        dcp.excuteSql(sql, dpc);
      }
      send_list = null;
    
    }catch (Exception e){
      e.printStackTrace();
    }finally{
      data = null;
      dcp.release(dpc);
    }

    return result;
  }


  /** 
   * ȡ�ù̶�����ķ����б�
   * 
   * @param message_id
   * @param area_id 
   * @param am
   * 
   * @return SendList[]
   */
  private SendList[] getAreaSendList(int message_id, int area_id, AreaManager am){

    int[] cityids = am.getCityInArea(area_id);
    int[] bscids = am.getBscInArea(area_id);
    Cell[] cellids = am.getCellInArea(area_id);

    SendList[] sendBscList = makeSendBscList(message_id, cityids, bscids) ;
    SendList[] sendCellList = makeSendCellList(message_id, cellids);

    SendList[] result  = new SendList[sendBscList.length + sendCellList.length];

    int step = 0;
    for(int i=0; i<sendBscList.length; ++i) {
      result[step++] = sendBscList[i];
    }

    for(int i=0; i<sendCellList.length; ++i) {
      result[step++] = sendCellList[i];
    }

    cityids = null;
    bscids = null;
    cellids = null;

    sendBscList = null;
    sendCellList = null;

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
  private SendList[] getPolygonCrossRegionSendList(int message_id, Polygon poly , HashMap<Integer, City> city_map){
    
    HashMap<Integer, SendList> sendList_map = new HashMap<Integer, SendList>();

    Rect rect = poly.getEquivalentRect();

    for (Iterator itCity=city_map.entrySet().iterator(); itCity.hasNext(); ) {
      Map.Entry cityEntry = (Map.Entry)itCity.next(); 
      City ci = city_map.get( cityEntry.getKey() );

      Rect city_rect = ci.getCityRect();

      if( rect.isRectCross(city_rect ) ) { //����ཻcity 

        if(poly.isRectInPolygon( city_rect ) > 0) { //�������city

//System.out.println( " include city:" + ci );

          for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
            Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
            Bsc bsc =  ci.getBscs().get( bscEntry.getKey() );
            sendList_map.put(bsc.getBscID(), new SendList(message_id, bsc.getBscID(), 0, "")  );
          }

        }else{ //������city�����ཻ

//System.out.println( " cross city:" + ci   );
//int i=0;
          for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
            Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
            Bsc bsc =  ci.getBscs().get( bscEntry.getKey() );

            Rect bsc_rect = bsc.getBscRect() ;
            if( rect.isRectCross(bsc_rect) ) { //����ཻbsc

              if( poly.isRectInPolygon(bsc_rect ) > 0 ) { //�������bsc

//System.out.println( " include bsc:" + bsc );

                sendList_map.put(bsc.getBscID(), new SendList(message_id, bsc.getBscID(), 0, "")  );

              }else { //����,������ bsc�����ཻ
                int cells = 0;
                StringBuffer sb = new StringBuffer("");

//System.out.println( " cross bsc:" + bsc  + " bsc cell:" + bsc.getCellList().size());

                for(Iterator itCell=bsc.getCellList().entrySet().iterator(); itCell.hasNext(); ) {
                  Map.Entry cellEntry = (Map.Entry)itCell.next(); 
                  Cell cell = bsc.getCellList().get( cellEntry.getKey() );
//System.out.println(poly.isPointInPolygon( cell.getX(), cell.getY() ));
                  if( poly.isPointInPolygon( cell.getX(), cell.getY() ) > 0 ) {
//System.out.println(cell);
                    cells += 1;
                    sb.append(cell.getCellID() + ",");
                  }
                }

                if (cells > 0) {  //�����ѯ��һ�����߶���ڶ����poly�����cell
                  //ɾ�����һ������
//System.out.println(i++ +" :" + sb.substring(0, sb.length() -1));
//System.out.println();
                  sendList_map.put(bsc.getBscID(), new SendList(message_id, bsc.getBscID(), cells, sb.substring(0, sb.length() -1) )  );
                }

              }



            }

          }

        }//end if  include city

      }
    }

    int i=0;
    SendList[] result = new SendList[sendList_map.size()];
    for(Iterator itSendList=sendList_map.entrySet().iterator(); itSendList.hasNext(); ) {
      Map.Entry sendListEntry = (Map.Entry)itSendList.next(); 
      SendList send_list =  sendList_map.get( sendListEntry.getKey() );

      result[i++] = send_list;
    }

    sendList_map.clear();
    sendList_map = null;
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
  private SendList[] getRectCrossRegionSendList(int message_id, Rect rect , HashMap<Integer, City> city_map){

    HashMap<Integer, SendList> sendList_map = new HashMap<Integer, SendList>();

    for (Iterator itCity=city_map.entrySet().iterator(); itCity.hasNext(); ) {
      Map.Entry cityEntry = (Map.Entry)itCity.next(); 
      City ci = city_map.get( cityEntry.getKey() );

      Rect city_rect = ci.getCityRect();

      if(  rect.isRectCross(city_rect) && rect.isRectIn(city_rect)  ) { //����ཻ������city
//System.out.println( " include city:" + ci );
        for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
          Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
          Bsc bsc =  ci.getBscs().get( bscEntry.getKey() );
          sendList_map.put(bsc.getBscID(), new SendList(message_id, bsc.getBscID(), 0, "")  );
        }

      }else if(  rect.isRectCross(city_rect) && !rect.isRectIn(city_rect)   ) { //������Ϊ�����ཻ, �������ж�city����BSC
//System.out.println( " cross city:" + ci   );

        for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
          Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
          Bsc bsc =  ci.getBscs().get( bscEntry.getKey() );

          Rect bsc_rect = bsc.getBscRect();

          if( rect.isRectCross(bsc_rect) && rect.isRectIn(bsc_rect)  ) { //����ཻ������bsc
//System.out.println( " include bsc:" + bsc );
            sendList_map.put(bsc.getBscID(), new SendList(message_id, bsc.getBscID(), 0, "")  );

          }else if( rect.isRectCross(bsc_rect) && !rect.isRectIn(bsc_rect) ){ //��������ཻ,������ж�bsc����cell
            int cells = 0;
            StringBuffer sb = new StringBuffer("");
//System.out.println( " cross bsc:" + bsc  + " bsc cell:" + bsc.getCellList().size());

            for(Iterator itCell=bsc.getCellList().entrySet().iterator(); itCell.hasNext(); ) {
              Map.Entry cellEntry = (Map.Entry)itCell.next(); 
              Cell cell = bsc.getCellList().get( cellEntry.getKey() );
//System.out.println(i++ +" :" +cell);
//System.out.println();
              if( rect.isPointInRect( cell.getX(), cell.getY() ) ) {
                cells += 1;
                sb.append(cell.getCellID() + ",");
              }
            }

            if (cells > 0) { //�����ѯ��һ�����߶���ڶ����poly�����cell
              sendList_map.put(bsc.getBscID(), new SendList(message_id, bsc.getBscID(), cells, sb.substring(0, sb.length() -1).toString() )  );
            }
          }
        }
      }

    }

    int i=0;
    SendList[] result = new SendList[sendList_map.size()];
    for(Iterator itSendList=sendList_map.entrySet().iterator(); itSendList.hasNext(); ) {
      Map.Entry sendListEntry = (Map.Entry)itSendList.next(); 
      SendList send_list =  sendList_map.get( sendListEntry.getKey() );

      result[i++] = send_list;
    }
    sendList_map.clear();
    sendList_map = null;
    return result;

  }



  private boolean checkFmt(String fmt) {
    String[] FmtTable = {"utf8", "7bit", "iso-10646-ucs-2", "gbk" };
    boolean flag = false;
    for(int i = 0; i<FmtTable.length; ++i) {
      if(FmtTable[i].equals(fmt)) {
        flag = true;
        break;
      }
    }
    FmtTable = null;
    return flag;
  }



  private String[] enCodeCbm(String fmt, String msg){
System.out.println("msg:"+msg);
    String str = "";
    if ( checkFmt(fmt) ) {
      if(fmt.equals("7bit")) {
        str = Common.toHexString( Bit7.Encode7Bit(msg) );
      }else{
        try{
//System.out.println("str:"+new String(msg.getBytes(fmt)));
          str = Common.toHexString(new String( msg.getBytes(fmt)).getBytes("iso-10646-ucs-2"));
System.out.println("str:"+str);
        }catch (Exception e){
          e.printStackTrace();//
          return null;
        }
      }

      int pud_len = 164;
      int step = (str.length() % pud_len) > 0 ? (str.length() / pud_len) + 1 : str.length() / pud_len;
      String[] result = new String[step];
      for(int i=0; i < step; ++i) {
         result[i] = str.substring(i*pud_len, (i==step-1) ? (str.length() % pud_len) : pud_len  );
      }

      return result;
    }

    return null;
  }


  /** 
   * �ж��û���Ϣ�Ƿ�����Ƿ��ؼ���
   * @param msg ��Ϣ����
   * @return boolean
   */
  public boolean authFilter(String msg){
    String sql  = "SELECT filter_id "
        +"FROM cbs_message_auth_filter "
        +"Where keyword like '%" +msg+ "%'";

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    boolean result = false;
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
   * ��cbs_cell_in_area���еļ�¼ת��Ϊ�����б�
   * 
   * @param message_id ������Ϣid
   * @param cellids cellid�б�
   * 
   * @return SendList[]
   */
  private SendList[] makeSendCellList(int message_id, Cell[] cellids){
    HashMap<Integer, SendList> send_map = new HashMap<Integer, SendList>();
    SendList[] send_list = null;

   try{
    for(int i=0; i<cellids.length; ++i) {

      SendList senditem = send_map.remove( cellids[i].getCellBsc().getBscID() );
      //�ж��Ƿ��Ѿ��ڷ����б���
      if(senditem == null) {
        send_map.put(  cellids[i].getCellBsc().getBscID(),  
                       new SendList( message_id, cellids[i].getCellBsc().getBscID(), 1, cellids[i].getCellID() )
                    );
      } else {

        senditem.setCellCount(senditem.getCellCount() + 1); 
        senditem.setCellList(senditem.getCellList() + ","+  cellids[i].getCellID());

        send_map.put(  cellids[i].getCellBsc().getBscID(),  senditem );
      }
    }


    send_list = new SendList[send_map.size()];
 
      int i = 0;
      for(Iterator itSendMap=send_map.entrySet().iterator(); itSendMap.hasNext(); ) {
        Map.Entry sendMapEntry = (Map.Entry)itSendMap.next(); 
        send_list[i++] = send_map.get( sendMapEntry.getKey() );
        itSendMap.remove(); //��Map����Collection��ʱ�򣬲�Ҫ�����ǵ�APIֱ���޸ļ��ϵ�����
      }
    }catch (Exception e){
      e.printStackTrace();
    }finally{
      send_map.clear();
      send_map = null;
    }

    return send_list;
  }


  /** 
   * ��cbs_city_in_area����cbs_bsc_in_area���е�����ת��Ϊ�����б�
   * 
   * @param message_id int ��Ϣid
   * @param cityids int[] ��ص�city_id
   * @param bscids int[] ��ص�bsc_id
   * 
   * @return SendList[]
   */
  private SendList[] makeSendBscList(int message_id, int[] cityids, int[] bscids){
    HashMap<Integer, City> city_map = getCitys();
    int bscs = 0;
    for(int i = 0; i<cityids.length; ++i) {
      City ci = city_map.get(cityids[i]);
      bscs += ci.getBscs().size();
    }
    bscs += bscids.length ;
    SendList[] result = new SendList[bscs];
    int step = 0;
    for(int i = 0; i<cityids.length; ++i) {
      City ci = city_map.get(cityids[i]);

      for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
        Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
        result[step++] = new SendList(message_id, ((Bsc)bscEntry.getValue()).getBscID(), 0, "" );
      }
    }

    for(int i=0; i<bscids.length; ++i) {
      result[step++] = new SendList(message_id, bscids[i], 0, "" ); 
    }

    clearCityList(city_map);
    city_map = null;
    return result;
  }



  public void clearCityList(HashMap<Integer, City> city_list){
    try{
      for(Iterator itCity=city_list.entrySet().iterator(); itCity.hasNext(); ){
        Map.Entry cityEntry = (Map.Entry)itCity.next(); 
        City ci = city_list.get( cityEntry.getKey() );
        itCity.remove();
        for(Iterator itBsc=ci.getBscs().entrySet().iterator(); itBsc.hasNext(); ) {
          Map.Entry bscEntry = (Map.Entry)itBsc.next(); 
          Bsc bsc = ci.getBscs().get( bscEntry.getKey() );
          itBsc.remove();
          bsc.getCellList().clear();
          bsc = null;
        }
    
        ci.getBscs().clear();
        ci = null;
      }
    
      city_list.clear();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  /** 
   * ����city-bsc-cell�����νṹ, �ڴ洦��ʽ
   * @return HashMap<Integer,City>
   */
  public HashMap<Integer, City> getCitys(){
    HashMap<Integer, City> city_list = null;

    BscType[] bsc_types = null;
    City[] citys = null;
    Bsc[] bscs = null;
    Cell[] cells = null;
    try{
      citys = getCityInfo();
      bsc_types = getBscTypeInfo();
      bscs = getBscInfo(bsc_types, citys);
      cells = getCellInfo(bscs);

      for(int i=0; i<bscs.length; ++i) {
        HashMap<Integer, Cell> cell_map = new HashMap<Integer, Cell>();
        for(int j=0; j<cells.length; ++j) {

//System.out.println("bscs[i]:" + bscs[i]);
//System.out.println("cells[j].getCellBsc():" + cells[j].getCellBsc());

          if(bscs[i].getBscID() == cells[j].getCellBsc().getBscID() ) {
            cell_map.put(cells[j].getID(),  cells[j]);
//System.out.println(bscs[i].getBscID() + " = " +cells[j]);
//System.out.println();
          }
        }
        bscs[i].setCellList(cell_map);
      }

      for(int i=0; i<citys.length; ++i) {
        HashMap<Integer, Bsc> bsc_list = new HashMap<Integer, Bsc>();
        for(int j=0; j<bscs.length; ++j) {
          if(citys[i].getCityID() == bscs[j].getBscCity().getCityID() ) {
            bsc_list.put(bscs[j].getBscID(), bscs[j] );
          }
        }
        citys[i].setBscs(bsc_list);
      }

      city_list = new HashMap<Integer, City>();
      for(int i=0; i<citys.length; ++i) {
        city_list.put(citys[i].getCityID(), citys[i]);
      }
    }catch (Exception e){
      e.printStackTrace();
    }finally{
      cells = null;
      bscs = null;
      bsc_types = null;
      citys = null;
    }
    return city_list;
  }


  /** 
   * ȡ����city���б�
   * @return City[]
   */
  private City[] getCityInfo(){
    String sql  = "SELECT city_id, city_name, operator, status "
        +"FROM cbs_city "
        +"Where status = 'active' or status = 'inactive' ";

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    City[] ci = null;
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      ci = new City[data.length];
      for(int i = 0;i < data.length; i++) {
        ci[i] = new City(Integer.parseInt(data[i][0]), data[i][1], data[i][2], data[i][3]);
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return ci;
  }



  /** 
   * ȡ����BscType���б�
   * @return BscType[]
   */
  private BscType[] getBscTypeInfo(){
    String sql  = "SELECT type_id, manufacturer, product, version, protocol "
        +"FROM cbs_bsc_type ";

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    BscType[] bti = null;
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      bti = new BscType[data.length];
      for(int i = 0;i < data.length; i++) {
        bti[i] = new BscType(Integer.parseInt(data[i][0]), data[i][1], 
                             data[i][2], data[i][3],data[i][4]);
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return bti;
  }



  /** 
   * ȡ����bsc���б�
   * @param bsc_type_info  ����bsctype�б�
   * @param city_info      ����city�б�
   * @return Bsc[]
   */
  private Bsc[] getBscInfo(BscType[] bsc_type_info, City[] city_info){
    String sql  = "SELECT bsc_id, city_id, type_id, bsc_name, status, create_by "
        +"FROM cbs_bsc "
        +"Where status = 'active' or status = 'inactive' ";

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    Bsc[] bi = null;
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      bi = new Bsc[data.length];
      for(int i = 0;i < data.length; i++) {
        int city_id = Integer.parseInt(data[i][1]);
        int type_id = Integer.parseInt(data[i][2]);
        City ct = getCity(city_info, city_id);
        if (ct != null) {
          BscType bt = getBscType(bsc_type_info, type_id);
          bi[i] = new Bsc(Integer.parseInt(data[i][0]), ct, bt, 
                          data[i][3], data[i][4], Integer.parseInt(data[i][5]));
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }
    
    return bi;
  }


  /** 
   * ȡ���л�վ�б�
   * 
   * @param bsc_info ����bsc�б�
   * 
   * @return Cell[]
   */
  private Cell[] getCellInfo(Bsc[] bsc_info){
    String sql  = "SELECT id, bsc_id, cell_id, cell_name, x, y, status, create_by "
        +"FROM cbs_cell "
        +"Where status = 'active' And x is not null And y is not null" ;

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    Cell[] bti = null;
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      bti = new Cell[data.length];
      for(int i = 0;i < data.length; i++) {
        int bsc_id = Integer.parseInt(data[i][1]);
        Bsc bsc = getBsc(bsc_info, bsc_id);

        if(bsc != null) {
          bti[i] = new Cell(Integer.parseInt(data[i][0]), bsc, data[i][2], data[i][3], 
                            Double.parseDouble(data[i][4]), Double.parseDouble(data[i][5]),
                            data[i][6], Integer.parseInt(data[i][7]));
        }

      }
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return bti;
  }

  /** 
   * ȡ�û���Ϣ
   * @param UserName �û���
   * @return UserInfo
   */
  public UserInfo getUserInfo(String UserName){
    String sql  = "SELECT user_id, username, password, user_type, user_level,status, create_by "
        +"FROM cbs_user "
        +"Where status = 'active' And username = '"+UserName+"'";

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    UserInfo uid = null;
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      if(data.length >0) {
        uid = new UserInfo(Integer.parseInt( data[0][0]), data[0][1], data[0][2],
                           data[0][3], Integer.parseInt(data[0][4]), data[0][5], 
                           0L, Integer.parseInt(data[0][6]), "", 0L);
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return uid;
  }


  private Bsc getBsc(Bsc[] bsc, int bsc_id){
    if (bsc == null) return null;

    for(int i=0; i<bsc.length; i++) {
      if(bsc[i].getBscID() == bsc_id) {
        return bsc[i];
      }
    }

    return null;
  }

  private City getCity(City[] city, int city_id){
    if (city == null) return null;

    for(int i=0; i<city.length; i++) {
      if(city[i].getCityID() == city_id) {
        return city[i];
      }
    }

    return null;
  }


  private BscType getBscType(BscType[] bsc_type, int type_id){
    if (bsc_type == null) return null;
    
    for(int i=0; i<bsc_type.length; i++) {
      if(bsc_type[i].getTypeID() == type_id) {
        return bsc_type[i];
      }
    }

    return null;
  }

}





