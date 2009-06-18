

/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice;



import javax.annotation.Resource;
import javax.jws.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.jaxws.context.WrappedMessageContext;

import org.apache.cxf.message.Message;

import com.khan.AppMain.*;
import com.khan.datetime.*;
import com.khan.db.*;
import com.khan.webservice.bean.*;


@org.apache.cxf.interceptor.InInterceptors (interceptors = {"com.khan.webservice.BasicAuthAuthorizationInterceptor" })
@org.apache.cxf.interceptor.OutInterceptors (interceptors = {"com.khan.webservice.BasicAuthAuthorizationInterceptor" })


@WebService(endpointInterface = "com.khan.webservice.CBM", 
            targetNamespace = "http://cxf.apache.org",
            portName = "CBM", 
            serviceName = "CBM"
            )


public class CBMImpl implements CBM {
@Resource
  WebServiceContext wsContext;




  private String[] m_sendCbm_ErrorMsg = {
      "正常",                                       //0
      "Channel超出范围",                            //-1
      "RegionFlag超出范围",                         //-2
      "Region指明的坐标无法描绘出一个矩形或多边形", //-3
      "Geographical Scope指明为一个错误的值",       //-4
      "CbmMessage没有指定",                         //-5
      "startTime只能为0或YYYYMMDDHHmmss格式的日期", //-6
      "endTime只能为0或YYYYMMDDHHmmss格式的日期",   //-7
      "RegionFlag标示为一个矩形,但region无法描绘",  //-8
      "RegionFlag标示为一个多边形,但region无法描绘",//-9
      "RegionFlag标示为一个矩形,但region描绘直线",  //-10
      "regionName不能为空",                         //-11
      "regionName不存在",                           //-12
      "Message中包含非法关键字",                    //-13
      "Fmt指定的编码格式不支持",                    //-14
      "系统错误,取MessageID失败",                   //-15
      "MsgID不存在",                                //-16
      "regionName指定的区域不存在"                  //-17
  };

  /**发布小区广播计划任务*/
  public int sendCbmAT(int channel, int regionFlag, String regionName, 
                     double[][] region, int level, int GS, 
                     int msgCode, int msgUpdateNum, String fmt, 
                     String Cbm, long startTime, int interval, 
                     long endTime )
  {
    int result = 0;

    if(channel < 0 || channel > 65535)
       return -1;

    /**@todo 验证用户权限*/
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    result = checkRegion(ui, regionFlag, regionName, region);
    if(result < 0) 
      return result;

    if(GS < 0 || GS > 3)
      return -4;
    if(Cbm == null || Cbm.equals(""))
      return -5;

    if(startTime != 0 && !SMPTime.isValidDate( startTime ) ) return -6; //发送时间不合法
    if(endTime != 0 && !SMPTime.isValidDate( endTime ) ) return -7; //发送时间不合法



    //System.out.println("getUserInfo:" + ui);

    //验证消息优先级别, 不能大于用户最大优先级
    level  = (level > ui.getUserLevel()) ? ui.getUserLevel() : level;

    result = co.sendCbmAT(ui,channel,regionFlag,regionName,region,level,GS,msgCode,msgUpdateNum,fmt, Cbm, startTime, interval, endTime);

    return result;
  }




  /**发送小区广播*/
  public int sendCbm(int channel, int regionFlag, String regionName, 
                     double[][] region, int level, int GS, 
                     int msgCode, int msgUpdateNum, String fmt, 
                     String Cbm, long startTime
                     )
  {
    int result = 0;

    if(channel < 0 || channel > 65535)
       return -1;

    /**@todo 验证用户权限*/
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    result = checkRegion(ui, regionFlag,regionName,region);
    if(result < 0) 
      return result;

    if(GS < 0 || GS > 3)
      return -4;
    if(Cbm == null || Cbm.equals(""))
      return -5;

    if(startTime != 0 && !SMPTime.isValidDate( startTime ) ) return -6; //发送时间不合法


    //System.out.println("getUserInfo:" + ui);

    //验证消息优先级别, 不能大于用户最大优先级
    level  = (level > ui.getUserLevel()) ? ui.getUserLevel() : level;

    result = co.sendCbm(ui,channel,regionFlag,regionName,region,level,GS,msgCode,msgUpdateNum,fmt, Cbm, startTime);

    return result;
  }



  /**删除区域*/
  public int deleteArea( String regionName){
    int result = 0;
    if(regionName == null || regionName.equals("")) return -10;

    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    AreaManager am = new AreaManager();
    result = am.deleteArea(ui, regionName);
    return result;
  }


  /**查询区域*/
  public double[][] queryArea( String regionName ){
    double[][] result = null;

    if(regionName == null || regionName.equals("")) return result;
    /**@todo 判断region是否存在, 存在则为修改, 否则为添加*/
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    AreaManager am = new AreaManager();
    result = am.queryArea(ui,regionName);
    return result;
  }


  /**修改区域*/
  public int modifyArea(int regionFlag, String regionName, double[][] region ){
    int result = 0;
    
    byte flag = 0x00; //高4bit 标志矩形还是多边形, 低4bit 标志临时区域还是固定区域
    if(regionFlag != 0 && regionFlag != 1) return -2;
    flag = (regionFlag == 0) ? (byte)0x00 : (byte)0x10;

    if( (region == null) || (region.length) < 2 || (region[0].length != 2) ) return -3;
    if( ((flag & 0x10) == 0x00) && region.length != 2)  return -8; //如果标记为矩形, 则只能有2个坐标点
    if( ((flag & 0x10) == 0x10) && region.length < 3)   return -9; //如果标记为多边形, 则最少有3个坐标点
    if( ( ((flag & 0x10) == 0x00) && region.length == 2 ) && ((region[0][0] == region[1][0]) || (region[0][1] == region[1][1])) ) 
      return -10; //如果标记为矩形, x坐标不能相等或y坐标不能相
    if(regionName == null ) return -11;

    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());


    AreaManager am = new AreaManager();
    result = am.modifyArea(ui,regionFlag,regionName,region);
    return result;
  }


  /**获取状态报告   */
  public String getReport(int MsgID ){
    String result = "";
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());
    result = co.getReport(ui, MsgID);

    return result;
  }








  private int checkRegion(UserInfo ui, int regionFlag, String regionName, double[][] region){
    int result = 0;

    if(regionName == null ) {

      byte flag = 0x00; //高4bit 标志矩形还是多边形, 低4bit 标志临时区域还是固定区域

      if(regionFlag != 0 && regionFlag != 1) return -2;
      flag = (regionFlag == 0) ? (byte)0x00 : (byte)0x10;

      if( (region == null) || (region.length) < 2 || (region[0].length != 2) ) return -3;
      if( ((flag & 0x10) == 0x00) && region.length != 2)  return -8; //如果标记为矩形, 则只能有2个坐标点
      if( ((flag & 0x10) == 0x10) && region.length < 3)   return -9; //如果标记为多边形, 则最少有3个坐标点
      if( ( ((flag & 0x10) == 0x00) && region.length == 2 ) && ((region[0][0] == region[1][0]) || (region[0][1] == region[1][1])) ) 
        return -10; //如果标记为矩形, x坐标不能相等或y坐标不能相

    }else{
      AreaManager am = new AreaManager();
      int area_id = am.getAreaID(ui, regionName);
      if(area_id < 0)  return -17; //指定区域不存在
    }

    return result;
  }



  private String getLoginUser(){
    String str = null;
    try{
      MessageContext mContext = wsContext.getMessageContext(); 
      WrappedMessageContext wmc = (WrappedMessageContext)mContext;
      Message m = wmc.getWrappedMessage();
      AuthorizationPolicy policy = m.get( AuthorizationPolicy.class );
      str = policy.getUserName();
    }catch (Exception e){
      return str;
    }
    return str;
  }






}




/**
XML数据包格式* 
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cxf="http://cxf.apache.org">
   <soapenv:Header/>
   <soapenv:Body>
      <cxf:sendCbm>
         <Channel>9</Channel>
         <RegionFlag>0</RegionFlag>
         <!--Optional:-->
         <ReginName>test</ReginName>
         <!--Zero or more repetitions:-->

         <Region>
            <item>11.23</item>
            <item>12.27</item>
         </Region>

         <Region>
            <item>15.23</item>
            <item>17.27</item>
         </Region>

         <Geographical_Scope>1</Geographical_Scope>
         <!--Optional:-->
         <CbmMessage>test message</CbmMessage>
         <SendTime>20090427154501</SendTime>
      </cxf:sendCbm>
   </soapenv:Body>
</soapenv:Envelope> 
 */
