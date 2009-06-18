package com.khan.db;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.util.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DBMng {
  private DBMng() {}

  /**
   * 取得jndi配置的连接池中的连接
   * @return Connection
   */

  public static Connection getConnection(String dbStr) {
    Connection con = null;
    try { 
      Context env = (Context)new InitialContext().lookup("java:comp/env");
      //InitialContext env = (Context)new InitialContext().lookup("java:comp/env");
      DataSource source = (DataSource) env.lookup(dbStr);
      con = source.getConnection();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return con;
  }



    static public int excuteSql(String sqlstr, String JndiName) {
        Connection conn = null;
        Statement stmt = null;
        int result = 0;
        try {
            conn = DBMng.getConnection(JndiName);
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sqlstr);
        } catch (SQLException e) {
            System.out.println("error msg: "+ e.getMessage() + " statement:"+sqlstr);
            //e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
            conn = null;
        }
        return result;
    }


    /**
     * 执行带结果集的sql语句
     * @param sqlstr String sql语句
     * @return String[][] 结果集
     */
    static public String[][] getRecord(String sqlstr, String JndiName) {
        int fieldCount = 0;
        ArrayList<String[]>  dataSource  = new ArrayList<String[]>() ;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBMng.getConnection(JndiName);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlstr);
            while (rs.next()) {
                fieldCount = rs.getMetaData().getColumnCount();
                String fields[] = new String[fieldCount];
                for (int j = 1; j <= fieldCount; j++) {
                    fields[j - 1] = rs.getString(j);
                }
                dataSource.add(fields);
                fields = null;
            }
        } catch (SQLException e) {
            System.out.println("error msg: "+ e.getMessage() + " statement:"+sqlstr);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (rs != null)   rs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
            rs = null;
            conn = null;
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

}
