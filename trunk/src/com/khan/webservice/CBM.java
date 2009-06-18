

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
  public int sendCbmAT( //����С���㲥�ƻ�����
      @WebParam(name="Channel") int channel,            //Ƶ��
      @WebParam(name="RegionFlag") int regionFlag,      //���������־, 0Ϊ����,1Ϊ�����
      @WebParam(name="ReginName") String regionName,    //��������,���ΪNULL����"", ���ʾΪ��ʱ����
      @WebParam(name="Region") double[][] region,        //��������
      @WebParam(name="Level") int level,                //���ȼ� 
      @WebParam(name="Geographical_Scope") int GS,      //����Χ
      @WebParam(name="Messge_Code") int msgCode,        //Messge_Code
      @WebParam(name="Messge_Update_Number") int msgUpdateNum, //Messge_Update_Number
      @WebParam(name="Format_Code") String fmt,         //�ַ��������ʽ
      @WebParam(name="CbmMessage") String Cbm,          //�㲥��Ϣ����
      @WebParam(name="StartTime") long startTime,       //��ʼ����ʱ��, 20090812151230 ��ʾ2009-08-12 15:12:30, ��ʱ����Ϊ0
      @WebParam(name="Interval") int Interval,          //���ʱ��,��СΪ1 ����Ϊ��λ
      @WebParam(name="EndTime") long endTime            //��ֹ����ʱ��, 20090812151230 ��ʾ2009-08-12 15:12:30, ��ʱ����Ϊ0
      );

  @WebResult(name="MsgID") 
  public int sendCbm( //����С���㲥
      @WebParam(name="Channel") int channel,            //Ƶ��
      @WebParam(name="RegionFlag") int regionFlag,      //���������־, 0Ϊ����,1Ϊ�����
      @WebParam(name="ReginName") String regionName,    //��������,���ΪNULL����"", ���ʾΪ��ʱ����
      @WebParam(name="Region") double[][] region,        //��������
      @WebParam(name="Level") int level,                //���ȼ� 
      @WebParam(name="Geographical_Scope") int GS,      //����Χ
      @WebParam(name="Messge_Code") int msgCode,        //Messge_Code
      @WebParam(name="Messge_Update_Number") int msgUpdateNum, //Messge_Update_Number
      @WebParam(name="Format_Code") String fmt,         //�ַ��������ʽ
      @WebParam(name="CbmMessage") String Cbm,          //�㲥��Ϣ����
      @WebParam(name="StartTime") long startTime        //��ʼ����ʱ��, 20090812151230 ��ʾ2009-08-12 15:12:30, ��ʱ����Ϊ0
      );



  @WebResult(name="Result")
  public int deleteArea(//ɾ������
      @WebParam(name="ReginName") String regionName   //��������
      );

  @WebResult(name="Area")
  public double[][] queryArea(//��ѯ����
      @WebParam(name="ReginName") String regionName   //��������
      );

  @WebResult(name="Result")
  public int modifyArea(//�����޸�����
      @WebParam(name="RegionFlag") int regionFlag,     //���������־, 0Ϊ����,1Ϊ�����
      @WebParam(name="ReginName") String regionName,   //��������
      @WebParam(name="Region") double[][] region        //��������
      );

  @WebResult(name="Report")
  public String getReport(//��������
      @WebParam(name="MsgID") int MsgID                //1����,2ɾ��,3��ѯ,4�޸�
      );

}



