/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

public class UserInfo {
  private String[] UserType_enum = {"root","admin","client"};
  private String[] Status_enum = {"active","inactive"};

  private int UserID = 0;
  private String UserName = "";
  private String Password = "";
  private int UserType = -1;
  private int UserLevel = 0;
  private byte Status = -1;
  private long CreateTime = 0L;
  private int CreateBy = 1;
  private String Description = "";
  private long LastLoginTime = 0L;

  private UserInfo(){}

  public UserInfo(int user_id, String user_name, String password, String user_type, 
                  int user_level, String status, long create_time, int create_by,
                  String description, long last_login_time) 
  {
    UserID          = user_id;
    UserName        = user_name;
    Password        = password;
    UserLevel       = user_level;
    CreateTime      = create_time;
    CreateBy        = create_by;
    Description     = description;
    LastLoginTime   = last_login_time;

    setUserType(user_type);
    setStatus(status);
  }

  public void setUserID(int user_id){
    UserID = user_id;
  }

  public int getUserID (){
    return UserID;
  }

  public void setUserName(String user_name){
    UserName = user_name;
  }

  public String getUserName(){
    return UserName;
  }

  public void setPassword(String password){
    Password = password;
  }

  public String getPassword (){
    return Password;
  }

  public void setUserType(String user_type){
    for(int i=0; i<UserType_enum.length; i++) {
      if(user_type.equals( UserType_enum[i]) ) UserType = i;
    }
  }
  public String getUserType (){
    return UserType_enum[UserType];
  }

  public int getUserLevel(){
    return UserLevel;
  }


  public void setStatus(String status){
    for(int i=0; i<Status_enum.length; i++) {
      if(status.equals( Status_enum[i]) ) Status = (byte)i;
    }
  }
  public String getStatus(){
    return Status_enum[Status];
  }

  public long getCreateTime(){
    return CreateTime;
  }

  public int getCreateBy(){
    return CreateBy;
  }

  public String getDescription(){
    return Description;
  }

  public long getLastLoginTime(){
    return LastLoginTime;
  }


  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("UserID=[");
    buf.append(UserID);
    buf.append("], UserName=[");
    buf.append(UserName);
    buf.append("], Password=[");
    buf.append(Password);
    buf.append("], UserType=[");
    buf.append(getUserType());
    buf.append("], UserLevel=[");
    buf.append(UserLevel);
    buf.append("], Status=[");
    buf.append(getStatus());
    buf.append("], CreateTime=[");
    buf.append(CreateTime);
    buf.append("], CreateBy=[");
    buf.append(CreateBy);
    buf.append("], Description=[");
    buf.append(Description);
    buf.append("], LastLoginTime=[");
    buf.append(LastLoginTime);
    buf.append("]");
    return buf.toString();
  }
}





