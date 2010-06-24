package com.khan.db;

import java.sql.*;
import java.util.*;
import com.khan.file.Log;

/**
 *
 * <p>Title:���ӳ��� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DBConPool {

  public static final String MYSQL_DRIVER    = "com.mysql.jdbc.Driver";
  public static final String POSTGRES_DRIVER = "org.postgresql.Driver";
  public static final String MSSQL_DRIVER    = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
  public static final String ORACEL_DRIVER   = "oracle.jdbc.driver.OracleDriver";

  private Log log = null;//new Log("DBConPollLog.out");
  private Log err = null;//new Log("DBConPollLog.err");

  protected LinkedList<DBPoolCon> listCon = new LinkedList<DBPoolCon>(); // һ��������ӳص�����

  protected String DBUrl    = null;
  protected String DBUser   = null;
  protected String DBPasswd = null;
  protected String DbDriver = null;
  protected String PoolName = null;

  /**���������*/
  protected int nMaxCon = 0;


  /**��ǰ����������*/
  protected int nConNum = 0;




  public DBConPool(String PoolName, String DbDriver, String DBUrl, String DBUser, String DBPasswd, int nMaxCon) {
    this.PoolName = PoolName;
    log = new Log(PoolName+".out");
    err = new Log(PoolName+".err");

    this.DBUrl = DBUrl;
    this.DBUser = DBUser;
    this.DBPasswd = DBPasswd;

    this.nMaxCon = nMaxCon;
    this.DbDriver = DbDriver;
    try {
      Class.forName(DbDriver);
    } catch (ClassNotFoundException e) {
      err.logOut("װ�����ݿ���������!", e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**������
   * @param DbDriver String
   * @param DBUrl String
   * @param DBUser String
   * @param DBPasswd String
   * @param nMaxCon int
   * @param nMaxUsedTime int*/
  public DBConPool(String DbDriver, String DBUrl, String DBUser, String DBPasswd, int nMaxCon) {
    this.PoolName = "DBConPollLog";
    log = new Log(PoolName+".out");
    err = new Log(PoolName+".err");

    this.DBUrl = DBUrl;
    this.DBUser = DBUser;
    this.DBPasswd = DBPasswd;

    this.nMaxCon = nMaxCon;
    this.DbDriver = DbDriver;
    try {
      Class.forName(DbDriver);
    } catch (ClassNotFoundException e) {
      err.logOut("װ�����ݿ���������!", e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**�����ӳ���ȡ�����ӣ��̰߳�ȫ������
   * @return DBPoolCon ���ص����ݿ���������
   * */
  synchronized public DBPoolCon get() {
    DBPoolCon dbp = null;

    for (int i = 0; i < listCon.size(); i++) { //������ʱ,ֱ��ȡ������
      dbp = (DBPoolCon) listCon.removeFirst();
      if (login(dbp)) { //������ӵ���Ч��
        return dbp;
      }else {           //����ʧЧ��ر�����
        try{
          dbp.con.close();
          dbp = null;

        }catch (Exception e){
          err.logOut("DBConPool close Statement error", e);
        }
      }
    }

    if (nConNum < nMaxCon) { //���û�ж�������,�����ӳ�δ��,�½�һ������
      return createCon();
    }

    err.logOut("Get DBPoolCon error, too many con: " + nConNum + " max:" + nMaxCon);
    return null;
  }

  /**�ͷ�����, �����ӷŻس���
   * @param pCon  DBPoolCon*/
  synchronized public void release(DBPoolCon pCon) {
    if (null != pCon) {
      listCon.add(pCon);
    }
  }

  /**������������
   * @return DBPoolCon ����һ����������*/
  protected DBPoolCon createCon() {
    DBPoolCon pCon = new DBPoolCon();
    try {
      pCon.con = DriverManager.getConnection(DBUrl, DBUser, DBPasswd);
      pCon.con.setAutoCommit(true);
      nConNum++;
      log.logOut("Create Connection, con num:" + nConNum + " max:" + nMaxCon);
    } catch (Exception e) {
      err.logOut("Create Connection is error", e);
      pCon= null;
    }
    return pCon;
  }

  public String[][] getRecord(String sqlStr, DBPoolCon con){
    int fieldCount = 0;
    ArrayList<String[]>  dataSource  = new ArrayList<String[]>() ;

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = con.get().createStatement();
      rs = stmt.executeQuery(sqlStr);
      while (rs.next()) {
        fieldCount = rs.getMetaData().getColumnCount();
        String fields[] = new String[fieldCount];
        for (int j = 1; j <= fieldCount; j++) {
          fields[j - 1] = rs.getString(j);
        }
        dataSource.add(fields);
        fields = null;
      }
    } catch (Exception e) {
      System.out.println("error msg:"+ e.getMessage() + " statement:"+sqlStr);
    } finally {
      try {
        if (stmt != null) stmt.close();
        if (rs != null)   rs.close();
      } catch (Exception e) {
        err.logOut("DBConPool close Statement error");
      }
      stmt = null;
      rs = null;
    }

    String[][] strs = new String[dataSource.size()][fieldCount];
    int i = 0;
    while (dataSource.size() > 0) {
      String fields[] = (String[]) (dataSource.remove(0));
      for (int j = 0; j < fieldCount; j++) {
        strs[i][j] = fields[j];
      }
      i += 1;
      fields = null;
    }
    dataSource = null;
    return strs;
  }

  public int excuteSql(String sqlStr, DBPoolCon con){
    Statement stmt = null;
    int result = 0;
    try {
      stmt = con.get().createStatement();
      result = stmt.executeUpdate(sqlStr);
    } catch (SQLException e) {
      System.out.println("error msg:"+ e.getMessage() + " statement:"+sqlStr);
    } finally {
      try {
        if (stmt != null) stmt.close();
      } catch (Exception e) {
        err.logOut("DBConPool close Statement error");
      }
      stmt = null;
    }
    return result;
  }


  public void commit(Connection conn){
    try{
      conn.commit();
    }catch (Exception e){
      err.logOut("DBConPool commit error!" + e.getMessage());
    }
  }

  public void setAutoCommit(Connection conn, boolean value){
    try{
      conn.setAutoCommit(value);
    }catch (Exception e){
      err.logOut("DBConPool setAutoCommit error!" + e.getMessage());
    }
  }

  public void rollback(Connection conn){
    try{
      conn.rollback();
    }catch (Exception e){
      err.logOut("DBConPool rollback error!" + e.getMessage());
    }
  }

  protected boolean login(DBPoolCon con) {
    boolean result = true;
    String sqlstr = "select 1 ";

    if (DbDriver.indexOf("oracle") != -1) {
      sqlstr = "select 1 from dual";
    }
    
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = con.get().createStatement();
      rs = stmt.executeQuery(sqlstr);
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      err.logOut("DBConPool TimeOut Checked! begin reConnection!");
      return false;
    } finally{
      try{
        if(rs != null) rs.close();
        if(stmt != null) stmt.close();
      }catch(Exception e){
        err.logOut("DBConPool checked error");
      }finally{
        rs = null;
        stmt = null;
      }
    }

    return result;
  }


  /**������
   * �����ӳ��е��������ӹر�*/
  protected void finalize() {
    try {
      while (listCon.size() > 0) {
        DBPoolCon pCon = (DBPoolCon) listCon.removeFirst();
        pCon.con.close();
        pCon = null;
      }

      log = null;
      err = null;
    } catch (Exception e) {
      err.logOut("Release DBConPool, Close DBConPool", e);
    }
  }
}

