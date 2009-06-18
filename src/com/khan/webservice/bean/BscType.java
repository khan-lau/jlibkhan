/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.webservice.bean;

public class BscType {
  private int TypeID;
  private String Manufacturer;
  private String Product;
  private String Version;
  private String Protocol;

  public BscType(int type_id, String manufacturer, String product, String version, String protocol) {
    TypeID = type_id; 
    Manufacturer = manufacturer;
    Product = product;
    Version = version;
    Protocol = protocol;
  }

  public void setTypeID(int type_id){
    TypeID = type_id;
  }
  public int getTypeID(){
    return TypeID;
  }

  public void setManufacturer(String manufacturer){
    Manufacturer = manufacturer;
  }
  public String setManufacturer(){
    return Manufacturer;
  }

  public void setProduct(String product){
    Product = product;
  }
  public String getProduct(){
    return Product;
  }

  public void setVersion(String version){
    Version = version;
  }
  public String getVersion(){
    return Version;
  }

  public void setProtocol(String protocol){
    Protocol = protocol;
  }
  public String getProtocol(){
    return Protocol;
  }

  public String toString(){
    StringBuffer buf = new StringBuffer();
    buf.append("TypeID=[");
    buf.append(TypeID);
    buf.append("], Manufacturer=[");
    buf.append(Manufacturer);
    buf.append("], Product=[");
    buf.append(Product);
    buf.append("], Version=[");
    buf.append(Version);
    buf.append("], Protocol=[");
    buf.append(Protocol);
    buf.append("]");
    return buf.toString();
  }

}





