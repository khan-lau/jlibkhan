package com.khan.net;


import sun.net.ftp.*;
import sun.net.*;
import java.io.*;
import java.util.*;
import java.util.*;


/**
 * <p>Title:FTP处理类 </p>
 *
 * <p>Description:实现FTP上传下载 </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MyFtpClient {
  FtpClient aftp;

  int ch;
  String hostname = "";
  private String path = "/";


  public MyFtpClient() {
  }


  public FtpClient connect(String hostname, int port, String uid, String pwd) {
    this.hostname = hostname;
    System.out.println("正在连接" + hostname + "，请等待.....");
    try {
      aftp = new FtpClient(hostname, port);
      aftp.login(uid, pwd);
      aftp.binary();
      //aftp.openPortDataConnection(); 被动模式
      System.out.println("连接主机:" + hostname + "成功!");
    } catch (FtpLoginException e) {
      System.out.println("登陆主机:" + hostname + "失败!请检查用户名或密码是否正确：" + e);
      return null;
    } catch (IOException e) {
      System.out.println("连接主机:" + hostname + "失败!请检查端口是否正确：" + e);
      return null;
    } catch (SecurityException e) {
      System.out.println("无权限与主机:" + hostname + "连接!请检查是否有访问权限：" + e);
      return null;
    }

    return aftp;
  }


  /**
   *  从ftp下载文件
   * @param DestName String 下载文件存放的路径名, 包含文件名
   * @param SourceName String 下载源文件的路径名, 包含文件名
   * @return boolean
   */
  public boolean downloadFile(String DestName, String SourceName) {
    boolean result = true;

    String DestDir = DestName.substring(0, DestName.lastIndexOf('/'));
    if (!DestDir.endsWith("/")) {
      DestDir = DestDir + "/";
    }
    if (aftp != null) {
      System.out.println("正在下载文件" + SourceName + ",请等待....");
      File fi = null;
      TelnetInputStream fget = null;
      DataInputStream puts = null;
      try {
        int ch;
        fi = new File(DestDir + DestName.substring(DestName.lastIndexOf('/') + 1));
        RandomAccessFile getFile = new RandomAccessFile(fi, "rw");
        getFile.seek(0);
        fget = aftp.get(SourceName);
        puts = new DataInputStream(fget);
        while ((ch = puts.read()) >= 0) {
          getFile.write(ch);
        }
        fget.close();
        getFile.close();

        System.out.println("下载" + SourceName + "文件到" + DestDir + "目录成功!");
      } catch (IOException e) {
        System.out.println("下载" + SourceName + "文件到" + DestDir + "目录失败!" + e);
        result = false;
      }finally{
        fi = null;
        fget = null;
        puts = null;
      }
    } else {
      result = false;
    }
    return result;
  }


  public boolean uploadFile(String DestName, String SourceName) {
    boolean result = true;
    if (aftp != null) {
      System.out.println("正在上传文件" + SourceName + ",请等待....");
      RandomAccessFile sendFile = null;
      TelnetOutputStream outs = null;
      DataOutputStream outputs = null;
      try {
        sendFile = new RandomAccessFile(SourceName, "r");
        sendFile.seek(0);
        outs = aftp.put(DestName);
        outputs = new DataOutputStream(outs);
        while (sendFile.getFilePointer() < sendFile.length()) {
          ch = sendFile.read();
          outputs.write(ch);
        }
        outs.close();
        sendFile.close();

        System.out.println("上传" + SourceName + "文件成功!");
      } catch (IOException e) {
        System.out.println("上传" + SourceName + "文件失败!" + e);
        result = false;
      }finally{
        sendFile = null;
        outs = null;
        outputs = null;
      }
    } else {
      result = false;
    }
    return result;
  }


  public static void deleFile(String DestName) {
    File file = null;
    try{
      file = new File(DestName);
      file.delete();
      System.out.println("已经成功删除" + DestName + "文件！");
    }finally{
      file = null;
    }
  }


  public void showFileContents(String strdir) {
    StringBuffer buf = null;
    TelnetInputStream ins = null;
    try {
      buf = new StringBuffer();
      aftp.cd(strdir);
      ins = aftp.list();
      while ((ch = ins.read()) >= 0) {
        buf.append((char) ch);
      }
      System.out.println(buf.toString());
      ins.close();
    } catch (IOException e) {
    } finally{
      buf = null;
      ins = null;
    }
  }


  public ArrayList getNameList(String RWFileDir) throws IOException {

    BufferedReader dr = new BufferedReader(new InputStreamReader(aftp.nameList("")));
    ArrayList<String> al = new ArrayList<String>();
    String s = "";
    while ((s = dr.readLine()) != null) {
      al.add(s);
      s = s.substring(13, s.length());
      isFile(s);
      downloadFile(RWFileDir, s);
      File fileDelF = new File(s);
      fileDelF.delete();
    }
    dr = null;
    return al;
  }

  public ArrayList getFileList() throws IOException {
    BufferedReader dr = new BufferedReader(new InputStreamReader(aftp.list()));
    ArrayList<String> al = new ArrayList<String>();
    String s = "";
    while ((s = dr.readLine()) != null) {
      al.add(s);
    }
    dr = null;
    return al;
  }


  public void setPath(String path) throws IOException {
    if (aftp == null) {
      this.path = path;
    } else {
      aftp.cd(path);
    }
  }


  public boolean isDir(String line) {
    return ((String) parseLine(line).get(0)).indexOf("d") != -1;
  }


  public boolean isFile(String line) {
    return !isDir(line);
  }


  private ArrayList parseLine(String line) {
    ArrayList<String> s1 = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(line, " ");
    while (st.hasMoreTokens()) {
      s1.add(st.nextToken());
    }
    st = null;
    return s1;
  }


  public void stop() {
    try {
      if (aftp != null) {
        aftp.closeServer();
        System.out.println("与主机" + hostname + "连接已断开!");
      }
    } catch (IOException e) {
      System.out.println("与主机" + hostname + "断开连接失败!" + e);
    }finally{
     aftp = null;
    }
  }


}


