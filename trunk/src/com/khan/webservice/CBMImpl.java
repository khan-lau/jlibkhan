

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
      "����",                                       //0
      "Channel������Χ",                            //-1
      "RegionFlag������Χ",                         //-2
      "Regionָ���������޷�����һ�����λ�����", //-3
      "Geographical Scopeָ��Ϊһ�������ֵ",       //-4
      "CbmMessageû��ָ��",                         //-5
      "startTimeֻ��Ϊ0��YYYYMMDDHHmmss��ʽ������", //-6
      "endTimeֻ��Ϊ0��YYYYMMDDHHmmss��ʽ������",   //-7
      "RegionFlag��ʾΪһ������,��region�޷����",  //-8
      "RegionFlag��ʾΪһ�������,��region�޷����",//-9
      "RegionFlag��ʾΪһ������,��region���ֱ��",  //-10
      "regionName����Ϊ��",                         //-11
      "regionName������",                           //-12
      "Message�а����Ƿ��ؼ���",                    //-13
      "Fmtָ���ı����ʽ��֧��",                    //-14
      "ϵͳ����,ȡMessageIDʧ��",                   //-15
      "MsgID������",                                //-16
      "regionNameָ�������򲻴���"                  //-17
  };

  /**����С���㲥�ƻ�����*/
  public int sendCbmAT(int channel, int regionFlag, String regionName, 
                     double[][] region, int level, int GS, 
                     int msgCode, int msgUpdateNum, String fmt, 
                     String Cbm, long startTime, int interval, 
                     long endTime )
  {
    int result = 0;

    if(channel < 0 || channel > 65535)
       return -1;

    /**@todo ��֤�û�Ȩ��*/
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    result = checkRegion(ui, regionFlag, regionName, region);
    if(result < 0) 
      return result;

    if(GS < 0 || GS > 3)
      return -4;
    if(Cbm == null || Cbm.equals(""))
      return -5;

    if(startTime != 0 && !SMPTime.isValidDate( startTime ) ) return -6; //����ʱ�䲻�Ϸ�
    if(endTime != 0 && !SMPTime.isValidDate( endTime ) ) return -7; //����ʱ�䲻�Ϸ�



    //System.out.println("getUserInfo:" + ui);

    //��֤��Ϣ���ȼ���, ���ܴ����û�������ȼ�
    level  = (level > ui.getUserLevel()) ? ui.getUserLevel() : level;

    result = co.sendCbmAT(ui,channel,regionFlag,regionName,region,level,GS,msgCode,msgUpdateNum,fmt, Cbm, startTime, interval, endTime);

    return result;
  }




  /**����С���㲥*/
  public int sendCbm(int channel, int regionFlag, String regionName, 
                     double[][] region, int level, int GS, 
                     int msgCode, int msgUpdateNum, String fmt, 
                     String Cbm, long startTime
                     )
  {
    int result = 0;

    if(channel < 0 || channel > 65535)
       return -1;

    /**@todo ��֤�û�Ȩ��*/
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    result = checkRegion(ui, regionFlag,regionName,region);
    if(result < 0) 
      return result;

    if(GS < 0 || GS > 3)
      return -4;
    if(Cbm == null || Cbm.equals(""))
      return -5;

    if(startTime != 0 && !SMPTime.isValidDate( startTime ) ) return -6; //����ʱ�䲻�Ϸ�


    //System.out.println("getUserInfo:" + ui);

    //��֤��Ϣ���ȼ���, ���ܴ����û�������ȼ�
    level  = (level > ui.getUserLevel()) ? ui.getUserLevel() : level;

    result = co.sendCbm(ui,channel,regionFlag,regionName,region,level,GS,msgCode,msgUpdateNum,fmt, Cbm, startTime);

    return result;
  }



  /**ɾ������*/
  public int deleteArea( String regionName){
    int result = 0;
    if(regionName == null || regionName.equals("")) return -10;

    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    AreaManager am = new AreaManager();
    result = am.deleteArea(ui, regionName);
    return result;
  }


  /**��ѯ����*/
  public double[][] queryArea( String regionName ){
    double[][] result = null;

    if(regionName == null || regionName.equals("")) return result;
    /**@todo �ж�region�Ƿ����, ������Ϊ�޸�, ����Ϊ���*/
    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());

    AreaManager am = new AreaManager();
    result = am.queryArea(ui,regionName);
    return result;
  }


  /**�޸�����*/
  public int modifyArea(int regionFlag, String regionName, double[][] region ){
    int result = 0;
    
    byte flag = 0x00; //��4bit ��־���λ��Ƕ����, ��4bit ��־��ʱ�����ǹ̶�����
    if(regionFlag != 0 && regionFlag != 1) return -2;
    flag = (regionFlag == 0) ? (byte)0x00 : (byte)0x10;

    if( (region == null) || (region.length) < 2 || (region[0].length != 2) ) return -3;
    if( ((flag & 0x10) == 0x00) && region.length != 2)  return -8; //������Ϊ����, ��ֻ����2�������
    if( ((flag & 0x10) == 0x10) && region.length < 3)   return -9; //������Ϊ�����, ��������3�������
    if( ( ((flag & 0x10) == 0x00) && region.length == 2 ) && ((region[0][0] == region[1][0]) || (region[0][1] == region[1][1])) ) 
      return -10; //������Ϊ����, x���겻����Ȼ�y���겻����
    if(regionName == null ) return -11;

    CbmOperation co = new CbmOperation();
    UserInfo ui = co.getUserInfo(getLoginUser());


    AreaManager am = new AreaManager();
    result = am.modifyArea(ui,regionFlag,regionName,region);
    return result;
  }


  /**��ȡ״̬����   */
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

      byte flag = 0x00; //��4bit ��־���λ��Ƕ����, ��4bit ��־��ʱ�����ǹ̶�����

      if(regionFlag != 0 && regionFlag != 1) return -2;
      flag = (regionFlag == 0) ? (byte)0x00 : (byte)0x10;

      if( (region == null) || (region.length) < 2 || (region[0].length != 2) ) return -3;
      if( ((flag & 0x10) == 0x00) && region.length != 2)  return -8; //������Ϊ����, ��ֻ����2�������
      if( ((flag & 0x10) == 0x10) && region.length < 3)   return -9; //������Ϊ�����, ��������3�������
      if( ( ((flag & 0x10) == 0x00) && region.length == 2 ) && ((region[0][0] == region[1][0]) || (region[0][1] == region[1][1])) ) 
        return -10; //������Ϊ����, x���겻����Ȼ�y���겻����

    }else{
      AreaManager am = new AreaManager();
      int area_id = am.getAreaID(ui, regionName);
      if(area_id < 0)  return -17; //ָ�����򲻴���
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
XML���ݰ���ʽ* 
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
