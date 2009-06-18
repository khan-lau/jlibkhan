/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.AppMain;

import java.io.*;
import java.util.HashMap;

import com.khan.datetime.*;
import com.khan.db.*;
import com.khan.file.*;
import com.khan.webservice.CbmOperation;
import com.khan.webservice.bean.*;



public class SysConf {
  public static String inifile = "conf/CBE.ini";
  private static SysConf conf = null;
  private static HashMap<Integer, City> citys = new HashMap<Integer, City>();
  
  private DBConPool dbPool = null;
  private int testChannel = 1000;
  private int httpPort = 0;
  

  private SysConf() {}

  private String loadinifile(String filename){
    BufferedReader in = null;
    String result = "";
    try{
      in = new BufferedReader(new FileReader(filename));
      String tmp = "";
      while((tmp = in.readLine()) != null) {
        result = result + tmp + "\n";
      }
    }catch (Exception e){
      System.out.println(SMPTime.getDateTime() +" 读取配置文件失败!");
    }finally{
      try{
        in.close();
        in = null;
      }catch (Exception e){
      }
    }
    return result;
  }

  private void loadConf(String filename){
    String inistr = loadinifile(filename);
    try{
      LoadIni li = new LoadIni(inistr);

      String port        = li.readINI("http.port");
      String testchannel = li.readINI("testChannel");
      String db_driver   = li.readINI("dbdriver");
      String db_url      = li.readINI("dburl");
      String db_user     = li.readINI("dbuser");
      String db_pw       = li.readINI("dbpw");

      int maxCon         = Integer.parseInt(li.readINI("maxCon"));
      dbPool             = new DBConPool(db_driver, db_url, db_user, db_pw, maxCon);
      httpPort           = Integer.parseInt(port);
      testChannel        = Integer.parseInt(testchannel);
    }catch (Exception e){
      System.out.println(SMPTime.getDateTime() + "Ini File parser error:" + e.getMessage());
    }
  }

  public static SysConf getInstance() {
    return getInstance(inifile);
  }

  public static SysConf getInstance(String filename) {
    if(conf == null) {
      conf = new SysConf();
      conf.loadConf(filename);
      return conf;
    }else
      return conf;
  }

  public int getTestChannel(){
    return testChannel;
  }

  public int getHttpPort(){
    return httpPort;
  }

  public DBConPool getDBConPool(){
    return dbPool;
  }

  /** 
   * 返回city-bsc-cell的树形结构, 内存处理方式
   * @return HashMap<Integer,City>
   */
  public static HashMap<Integer, City> getCitys(){
    CbmOperation cmo = new CbmOperation();
    return cmo.getCitys();
  }


  public static void clearCitys( HashMap<Integer, City> citys){
    CbmOperation cmo = new CbmOperation();
    cmo.clearCityList(citys);
  }

  /** 
   * 返回city-bsc-cell的树形结构, 数据库处理方式
   * @return HashMap<Integer,City>
   */
  /*
  public static HashMap<Integer, City> getCitys(){
    String sql  = "SELECT city_id, city_name, operator, status "
        +"FROM cbs_city "
        +"Where status = 'active'";
    String[][] citydata = null;
    String[][] bscdata = null;
    String[][] celldata = null;
    DBPoolCon dpc = null;
    DBConPool dcp = getInstance(SysConf.inifile).getDBConPool();
    try{
      dpc = dcp.get();
      citydata = dcp.getRecord(sql, dpc);

      for(int i = 0; i<citydata.length; ++i) {
        City ci = new City(Integer.parseInt(citydata[i][0]), citydata[i][1], citydata[i][2], citydata[i][3]);

        sql = "Select bsc_id, bsc_name From cbs_bsc Where status = 'active' And city_id = " + ci.getCityID();
        HashMap<Integer, Bsc> Bscs = new HashMap<Integer, Bsc>();
        bscdata = dcp.getRecord(sql, dpc);

        for(int j=0; j<bscdata.length; ++j) {
          Bsc bsc = new Bsc(Integer.parseInt(bscdata[j][0]), ci, 
                            new BscType(0, "", "", "", ""), bscdata[j][1], "active", 0);

          sql = "Select id, cell_id, x, y From cbs_cell Where status = 'active' And bsc_id = " + bsc.getBscID();
          HashMap<Integer, Cell> cells = new HashMap<Integer, Cell>();
          celldata = dcp.getRecord(sql, dpc);
          for(int k=0; k<celldata.length; ++k) {
            Cell cell = new Cell(Integer.parseInt(celldata[k][1]), bsc, celldata[k][1], "", 
                                 Double.parseDouble(celldata[k][2]), Double.parseDouble(celldata[k][3]), 
                                 "active" ,0);
            cells.put(cell.getID(), cell);
          }
 
          celldata = null;
          bsc.setCellList(cells);
          Bscs.put(bsc.getBscID(), bsc);
        }

        bscdata = null;
        ci.setBscs(Bscs);
        citys.put(ci.getCityID(), ci);
      }

      citydata = null;

    }catch(Exception e){
      e.printStackTrace();
    } finally {
      citydata = null;
      dcp.release(dpc);
      dcp = null;
    }
    return citys;
  }
*/


}





