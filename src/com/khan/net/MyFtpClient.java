package com.khan.net;


import sun.net.ftp.*;
import sun.net.*;
import java.io.*;
import java.util.*;
import java.util.*;


/**
 * <p>Title:FTP������ </p>
 *
 * <p>Description:ʵ��FTP�ϴ����� </p>
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
    System.out.println("��������" + hostname + "����ȴ�.....");
    try {
      aftp = new FtpClient(hostname, port);
      aftp.login(uid, pwd);
      aftp.binary();
      //aftp.openPortDataConnection(); ����ģʽ
      System.out.println("��������:" + hostname + "�ɹ�!");
    } catch (FtpLoginException e) {
      System.out.println("��½����:" + hostname + "ʧ��!�����û����������Ƿ���ȷ��" + e);
      return null;
    } catch (IOException e) {
      System.out.println("��������:" + hostname + "ʧ��!����˿��Ƿ���ȷ��" + e);
      return null;
    } catch (SecurityException e) {
      System.out.println("��Ȩ��������:" + hostname + "����!�����Ƿ��з���Ȩ�ޣ�" + e);
      return null;
    }

    return aftp;
  }


  /**
   *  ��ftp�����ļ�
   * @param DestName String �����ļ���ŵ�·����, �����ļ���
   * @param SourceName String ����Դ�ļ���·����, �����ļ���
   * @return boolean
   */
  public boolean downloadFile(String DestName, String SourceName) {
    boolean result = true;

    String DestDir = DestName.substring(0, DestName.lastIndexOf('/'));
    if (!DestDir.endsWith("/")) {
      DestDir = DestDir + "/";
    }
    if (aftp != null) {
      System.out.println("���������ļ�" + SourceName + ",��ȴ�....");
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

        System.out.println("����" + SourceName + "�ļ���" + DestDir + "Ŀ¼�ɹ�!");
      } catch (IOException e) {
        System.out.println("����" + SourceName + "�ļ���" + DestDir + "Ŀ¼ʧ��!" + e);
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
      System.out.println("�����ϴ��ļ�" + SourceName + ",��ȴ�....");
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

        System.out.println("�ϴ�" + SourceName + "�ļ��ɹ�!");
      } catch (IOException e) {
        System.out.println("�ϴ�" + SourceName + "�ļ�ʧ��!" + e);
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
      System.out.println("�Ѿ��ɹ�ɾ��" + DestName + "�ļ���");
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
        System.out.println("������" + hostname + "�����ѶϿ�!");
      }
    } catch (IOException e) {
      System.out.println("������" + hostname + "�Ͽ�����ʧ��!" + e);
    }finally{
     aftp = null;
    }
  }


}


