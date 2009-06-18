package com.khan.db;

import java.sql.*;
/**
 * <p>Title: 数据库连接容器</p>
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
  /**容器中的连接*/
  public Connection con = null;

  /**连接池已使用时间*/
  public int nUsedTimes = 0;

  DBPoolCon() {
  }

  /**
   * 取得连接
   * @return Connection
   */
  public Connection get() {
    return con;
  }
}
