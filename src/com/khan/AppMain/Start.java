/**
* TODO: Base64±‡¬ÎΩ‚¬Î¿‡
* 
* @author   Administrator
*/
package com.khan.AppMain;

import java.io.*;
import java.util.*;

import javax.xml.ws.*;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.jaxws.*;

import com.khan.datetime.*;

import com.khan.webservice.*;

import com.khan.graph.*;



public class Start {

  public static SysConf sysconf = SysConf.getInstance(SysConf.inifile);



  public void startServer(String address, Object object){
    //Server server = serverFactoryBean.create();
    //server.getEndpoint().getInInterceptor().add(myInterceptor);

    Endpoint endpoint = Endpoint.create(object);
    endpoint.publish(address); 
  }



  public void startServer(String address, Class classr){
    ServerFactoryBean sf = new ServerFactoryBean();
    
    sf.setServiceClass(classr);
    try{
        sf.setServiceBean(classr.getClassLoader().loadClass(classr.getName()).newInstance());
    }catch (Exception e){
        e.printStackTrace();
    }
    sf.setAddress(address);

    BasicAuthAuthorizationInterceptor myInterceptor = new BasicAuthAuthorizationInterceptor();
    sf.getInInterceptors().add(myInterceptor);
    sf.getOutInterceptors().add(myInterceptor);
    
    Server server = sf.create();
  }



  public static void main(String[] args) {
/*
    Point polygon[] ={ 
        new Point(113.251782, 23.30491),
        new Point(113.224392, 23.184905),
        new Point(113.310474, 23.150874),
        new Point(113.40047, 23.161621),
        new Point(113.521768, 23.211772),
        new Point(113.463074, 23.308492),
        new Point(113.347646, 23.347896),
        new Point(113.251782, 23.30491)
    };
    Polygon poly = new Polygon(polygon);
    Point p = new Point(113.300240, 23.232130);

    System.out.println(poly.isPointInPolygon(p) );

 */



    Start s = new Start();
    try{
      s = new Start();
      int port = s.sysconf.getHttpPort();
      String address = "http://localhost:" + port + "/CBM";
      System.out.println("Starting Server in :" + address);

      CBMImpl implementor = new CBMImpl();
      s.startServer(address, implementor);

      //s.startServer(address, CBMImpl.class);

    }catch (Exception e){
      System.out.println(SMPTime.getDateTime()+ ":" +  e.getMessage());
      e.printStackTrace();
    }finally{
      s = null;
    }


  }





}
