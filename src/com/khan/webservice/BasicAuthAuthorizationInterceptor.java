/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.cxf.binding.soap.interceptor.SoapHeaderInterceptor;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.*;
import org.apache.cxf.transport.http.URLConnectionInfo;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.mortbay.jetty.Request;

import com.khan.db.*;
import com.khan.AppMain.*;
import com.khan.webservice.bean.*;



public class BasicAuthAuthorizationInterceptor extends SoapHeaderInterceptor {

  @Override public void handleMessage(Message message) throws Fault {
    AuthorizationPolicy policy = message.get( AuthorizationPolicy.class );

    if (policy == null) {
      //System.out.println("Please input User Name");
      sendErrorResponse(message, HttpURLConnection.HTTP_UNAUTHORIZED);
      return;
    }

    //System.out.println("user:" + policy.getUserName() + " password:" +policy.getPassword());

    // Verify the password
    UserInfo[] ui = getUserInfo();
    for(int i = 0; i<ui.length; i++) {
      if(ui[i].getUserName().equals(policy.getUserName()) && ui[i].getPassword().equals(policy.getPassword())){
        updateUserLoginTime(ui[i].getUserID());

        ui = null;
        return;
      }
    }
    ui = null;

    System.out.println("Invalid username or password for user: "+ policy.getUserName());
    sendErrorResponse(message, HttpURLConnection.HTTP_FORBIDDEN);
  }


  private void updateUserLoginTime(int UserID){
    String sql = "Update cbs_user Set last_login_time = now() Where user_id="+UserID;

    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();
      dcp.excuteSql(sql, dpc);
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      dcp.release(dpc);
    }

  }

  private UserInfo[] getUserInfo(){
    String sql  = "SELECT user_id, username, password FROM cbs_user Where status = 'active'";

    String[][] data = null;
    DBPoolCon dpc = null;
    DBConPool dcp = SysConf.getInstance(SysConf.inifile).getDBConPool();
    UserInfo[] ui = null;
    try{
      dpc = dcp.get();
      data = dcp.getRecord(sql, dpc);
      ui = new UserInfo[data.length];
      for(int i = 0; i<ui.length; i++) {
        ui[i] = new UserInfo(Integer.parseInt( data[i][0]), data[i][1], data[i][2],
                                    "", 0, "",
                                     0, 0, "", 0);
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally {
      data = null;
      dcp.release(dpc);
    }

    return ui;
  }


  private void sendErrorResponse(Message message, int responseCode) {
    Message outMessage = getOutMessage(message);
    outMessage.put(Message.RESPONSE_CODE, responseCode);

    // Set the response headers
    Map<String, Object> responseHeaders = (Map)message.get( Message.PROTOCOL_HEADERS );
    //System.out.println(responseHeaders);
    if (responseHeaders != null) {
      responseHeaders.put( "WWW-Authenticate", Arrays.asList(new String[]{"Basic realm=realm"}) );
      responseHeaders.put( "Content-Length", Arrays.asList(new String[]{"0"}) );
    }

    message.getInterceptorChain().abort();
    try {
      getConduit(message).prepare(outMessage);
      close(outMessage);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }


  private Message getOutMessage(Message inMessage) {
    Exchange exchange = inMessage.getExchange();
    Message outMessage = exchange.getOutMessage();

    if (outMessage == null) {
      Endpoint endpoint = exchange.get(Endpoint.class);
      outMessage = endpoint.getBinding().createMessage();
      exchange.setOutMessage(outMessage);
    }
    outMessage.putAll(inMessage);
    return outMessage;
  }


  private Conduit getConduit(Message inMessage) throws IOException {
    Exchange exchange = inMessage.getExchange();
    EndpointReferenceType target = exchange.get( EndpointReferenceType.class);
    Conduit conduit = exchange.getDestination().getBackChannel( inMessage, null, target);
    exchange.setConduit(conduit);
    return conduit;
  }


  private void close(Message outMessage) throws IOException {
    OutputStream os = outMessage.getContent( OutputStream.class);
    os.flush();
    os.close();
  }
}


