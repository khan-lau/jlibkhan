package com.khan.db;

import java.sql.*;
/**
 * <p>Title: ���ݿ���������</p>
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

public class DBPoolCon {
  /**�����е�����*/
  public Connection con = null;

  /**���ӳ���ʹ��ʱ��*/
  public int nUsedTimes = 0;

  DBPoolCon() {
  }

  /**
   * ȡ������
   * @return Connection
   */
  public Connection get() {
    return con;
  }
}
