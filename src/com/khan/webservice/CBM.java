

package com.khan.webservice;

import java.util.Vector;

import javax.jws.*;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/** 
 * service interface
 * @author
 */

@WebService(name = "CBM",
            targetNamespace="http://cxf.apache.org"
            )
public interface CBM {

  @WebResult(name="MsgID") 
  public int sendCbmAT( //发布小区广播计划任务
      @WebParam(name="Channel") int channel,            //频道
      @WebParam(name="RegionFlag") int regionFlag,      //发送区域标志, 0为矩形,1为多边形
      @WebParam(name="ReginName") String regionName,    //区域名称,如果为NULL或者"", 则表示为临时区域
      @WebParam(name="Region") double[][] region,        //发送区域
      @WebParam(name="Level") int level,                //优先级 
      @WebParam(name="Geographical_Scope") int GS,      //地理范围
      @WebParam(name="Messge_Code") int msgCode,        //Messge_Code
      @WebParam(name="Messge_Update_Number") int msgUpdateNum, //Messge_Update_Number
      @WebParam(name="Format_Code") String fmt,         //字符串编码格式
      @WebParam(name="CbmMessage") String Cbm,          //广播消息内容
      @WebParam(name="StartTime") long startTime,       //开始发送时间, 20090812151230 表示2009-08-12 15:12:30, 即时发送为0
      @WebParam(name="Interval") int Interval,          //间隔时间,最小为1 分钟为单位
      @WebParam(name="EndTime") long endTime            //终止发送时间, 20090812151230 表示2009-08-12 15:12:30, 即时发送为0
      );

  @WebResult(name="MsgID") 
  public int sendCbm( //发送小区广播
      @WebParam(name="Channel") int channel,            //频道
      @WebParam(name="RegionFlag") int regionFlag,      //发送区域标志, 0为矩形,1为多边形
      @WebParam(name="ReginName") String regionName,    //区域名称,如果为NULL或者"", 则表示为临时区域
      @WebParam(name="Region") double[][] region,        //发送区域
      @WebParam(name="Level") int level,                //优先级 
      @WebParam(name="Geographical_Scope") int GS,      //地理范围
      @WebParam(name="Messge_Code") int msgCode,        //Messge_Code
      @WebParam(name="Messge_Update_Number") int msgUpdateNum, //Messge_Update_Number
      @WebParam(name="Format_Code") String fmt,         //字符串编码格式
      @WebParam(name="CbmMessage") String Cbm,          //广播消息内容
      @WebParam(name="StartTime") long startTime        //开始发送时间, 20090812151230 表示2009-08-12 15:12:30, 即时发送为0
      );



  @WebResult(name="Result")
  public int deleteArea(//删除区域
      @WebParam(name="ReginName") String regionName   //区域名称
      );

  @WebResult(name="Area")
  public double[][] queryArea(//查询区域
      @WebParam(name="ReginName") String regionName   //区域名称
      );

  @WebResult(name="Result")
  public int modifyArea(//增加修改区域
      @WebParam(name="RegionFlag") int regionFlag,     //发送区域标志, 0为矩形,1为多边形
      @WebParam(name="ReginName") String regionName,   //区域名称
      @WebParam(name="Region") double[][] region        //发送区域
      );

  @WebResult(name="Report")
  public String getReport(//管理区域
      @WebParam(name="MsgID") int MsgID                //1增加,2删除,3查询,4修改
      );

}



