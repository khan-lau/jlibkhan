/**
* TODO: Add class description
* 
* @author   Administrator
*/
package com.khan.net;

import com.khan.net.Common;

public class Bit7 {

  static char[] Bit7DecodeTable = {
    //    0       1       2       3       4       5       6       7       8       9       A       B       C       D       E       F
    /*0*/ 0x40,   0xA3,   0x24,   0xA5,   0xE8,   0xE9,   0xF9,   0xEC,   0xF2,   0xC7,   0x0A,   0xD8,   0xF8,   0x0D,   0xC5,   0xE5,
    /*1*/ 0x0394, 0x5F,   0x03A6, 0x0393, 0x039B, 0x03A9, 0x03A0, 0x03A8, 0x03A3, 0x0398, 0x039E, 0x1B,   0xC6,   0xE6,   0xDF,   0xA9,
    /*2*/ 0x20,   0x21,   0x22,   0x23,   0xA4,   0x25,   0x26,   0x27,   0x28,   0x29,   0x2A,   0x2B,   0x2C,   0x2D,   0x2E,   0x2F,
    /*3*/ 0x30,   0x31,   0x32,   0x33,   0x34,   0x35,   0x36,   0x37,   0x38,   0x39,   0x3A,   0x3B,   0x3C,   0x3D,   0x3E,   0x3F,
    /*4*/ 0xA1,   0x41,   0x42,   0x43,   0x44,   0x45,   0x46,   0x47,   0x48,   0x49,   0x4A,   0x4B,   0x4C,   0x4D,   0x4E,   0x4F,
    /*5*/ 0x50,   0x51,   0x52,   0x53,   0x54,   0x55,   0x56,   0x57,   0x58,   0x59,   0x5A,   0xC4,   0xD6,   0xD1,   0xDC,   0xA7,
    /*6*/ 0xBF,   0x61,   0x62,   0x63,   0x64,   0x65,   0x66,   0x67,   0x68,   0x69,   0x6A,   0x6B,   0x6C,   0x6D,   0x6E,   0x6F,
    /*7*/ 0x70,   0x71,   0x72,   0x73,   0x74,   0x75,   0x76,   0x77,   0x78,   0x79,   0x7A,   0xE4,   0xF6,   0xF1,   0xFC,   0xE0
  };


  static char[] Bit7EncodeTable = {//0xFF为不存在
    //    0       1       2       3       4       5       6       7       8       9       A       B       C       D       E       F
    /*0*/ 0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0x0A,   0xFF,   0xFF,   0x0D,   0xFF,   0xFF,
    /*1*/ 0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0x1B,   0xFF,   0xFF,   0xFF,   0xFF,
    /*2*/ 0x20,   0x21,   0x22,   0x23,   0x02,   0x25,   0x26,   0x27,   0x28,   0x29,   0x2A,   0x2B,   0x2C,   0x2D,   0x2E,   0x2F,
    /*3*/ 0x30,   0x31,   0x32,   0x33,   0x34,   0x35,   0x36,   0x37,   0x38,   0x39,   0x3A,   0x3B,   0x3C,   0x3D,   0x3E,   0x3F,
    /*4*/ 0x00,   0x41,   0x42,   0x43,   0x44,   0x45,   0x46,   0x47,   0x48,   0x49,   0x4A,   0x4B,   0x4C,   0x4D,   0x4E,   0x4F,
    /*5*/ 0x50,   0x51,   0x52,   0x53,   0x54,   0x55,   0x56,   0x57,   0x58,   0x59,   0x5A,   0xFF,   0xFF,   0xFF,   0xFF,   0x11,
    /*6*/ 0xFF,   0x61,   0x62,   0x63,   0x64,   0x65,   0x66,   0x67,   0x68,   0x69,   0x6A,   0x6B,   0x6C,   0x6D,   0x6E,   0x6F,
    /*7*/ 0x70,   0x71,   0x72,   0x73,   0x74,   0x75,   0x76,   0x77,   0x78,   0x79,   0x7A,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,

    /*8*/ 0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,
    /*9*/ 0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,
    /*A*/ 0xFF,   0x40,   0xFF,   0x01,   0x24,   0x03,   0xFF,   0x5F,   0xFF,   0x1F,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,
    /*B*/ 0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0x60,
    /*C*/ 0xFF,   0xFF,   0xFF,   0xFF,   0x5B,   0x0E,   0x1C,   0x09,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,   0xFF,
    /*D*/ 0xFF,   0x5D,   0xFF,   0xFF,   0xFF,   0xFF,   0x5C,   0xFF,   0x0B,   0xFF,   0xFF,   0xFF,   0x5E,   0xFF,   0xFF,   0x1E,
    /*E*/ 0x7F,   0xFF,   0xFF,   0xFF,   0x7B,   0x0F,   0x1D,   0xFF,   0x04,   0x05,   0xFF,   0xFF,   0x07,   0xFF,   0xFF,   0xFF,
    /*F*/ 0xFF,   0x7D,   0x08,   0xFF,   0xFF,   0xFF,   0x7C,   0xFF,   0x0C,   0x06,   0xFF,   0xFF,   0x7E,   0xFF,   0xFF,   0xFF
  }; 

  private Bit7() {
  }

  public static int Bit7Length(String str){
    if(str == null || str.equals("")) return 0;
    int len = str.length();
    return len / 8 * 7 + (len % 8);
  }



  public static byte[] Encode7Bit (String str){
    if(str == null || str.equals("")) return null;
    byte[] data = null;
    data = str.getBytes();
    for(int i = 0; i<data.length; ++i) {
      byte h = (byte)Bit7EncodeTable[(int)data[i]];
      if(h == (byte)0xFF) {
        h = 0x20;
      }
      data[i] = h;
    }
    int len  = data.length;
    int step = len / 8;
    int residue = len % 8;

//System.out.println("7bit length:"+Bit7Length(str));
    int k = 0;

    for(int i=0; i<step; ++i) {
//System.out.println("length >=8");
      for(int j=0; j<7; ++j) {
        byte b2 = data[(i*8)+j+1];

        byte swap =(byte)( b2 & (int)(Math.pow(2,(j+1))-1));
        swap = (byte)(swap << (8-(j+1)) );
        data[(i*8)+j] = (byte)(swap | data[(i*8)+j]);

        b2 = (byte)( b2 >> (j+1) );
        data[(i*8)+j+1] = b2;
        if(j+1==7) {
          data[(i*8)+j+1] = 0x00;
        }
      }
    }
    int offset = len - residue ;
    
    for(int i=0; i<residue; ++i) {
      if( i+1 < residue) {
        byte b2 = data[offset+i+1];
        byte swap =(byte)( b2 & (int)(Math.pow(2,(i+1))-1));
        swap = (byte)(swap << (8-(i+1)));
        data[offset+i] = (byte)(swap | data[offset+i]);
        b2 = (byte)(b2 >> (i+1));
        data[offset+i+1] = b2;
      }else if(i+1 == residue){ //末尾字节高7bit为0 则填充0x0D
//System.out.println(" data["+(offset+i)+"]:" + data[offset+i]);
        byte b1 = data[offset+i];
        if(b1 == 0x01) {
          data[offset+i] = (byte)(b1 | 0x1A); //0x1A 为0x0D左移1位
        }
      }
    }
//Common.PrintDataHex(data);
    byte[] result = new byte[Bit7Length(str)];
    for(int i = 0; i<step; ++i) {
      System.arraycopy(data, i*8, result, i*7, 7 );
    }
//System.out.println((len - residue) +" | "+(step*7) +" | " + (result.length%8));
    System.arraycopy(data, len - residue, result, step*7, result.length%8 );
//Common.PrintDataHex(result);
    data = null;
    return result;
  }

  public static String Decode7Bit(byte[] data){
    if (data == null || data.length < 1) return null;
    StringBuffer sb = new StringBuffer();
    int step = 0;
    byte swap = 0x00;
    while( ++step < data.length +1) {
      byte b1 = data[step - 1];
      byte v = 0;

      v = (byte)(  (  (b1 & Common.byteMask0Left((step-1)%7 +1)) << ((step-1)%7) ) | swap  );
//      System.out.println("char:"+ (char)v + " :" + Common.Byte2BinString(v) + " -- " + Common.Byte2BinString(b1));
      if(v > 0x7F) {//判断字节有效范围
        return null;
      }
      sb.append(Bit7DecodeTable[(int)v]);
      if((step)%7 == 0 ) {//刚刚好第7的倍数个字节时
        swap = (byte)( (b1 >> ( 7 - ((step-1) % 7) )) & 0x7F );
//        System.out.println( "swap hex:" + Common.Byte2String(swap) + " bin:" +Common.Byte2BinString(swap) );//
        sb.append(Bit7DecodeTable[(int)swap]);
        if(swap > 0x7F) { //判断字节有效范围
          return null;
        }
        swap =0x00;
      }else{
        swap = (byte)( (b1 >> ( 7 - ((step-1) % 7) )) & (~Common.byteMask0Right(step%7 )) );
      }
//System.out.println("decode:"+sb.toString()+"\n");
    }

    return sb.toString();  
  }



  public static void main(String[] args){
 /*   String str = "hellohe";
    System.out.println("encode:"+str);

    byte[] data = Encode7Bit(str);
    Common.PrintDataHex(data);
    String tmp = Bit7.Decode7Bit(data);
    System.out.print("decode:"+tmp+"\n");
    data = null;
*/


    String a = "中文";
    String s  = null;
    byte[] b = null;

String msg = "中文";
String fmt = "gbk";



    try{
      s = new String( a.getBytes(), "GBK" );
      b = s.getBytes("utf8");
    Common.PrintDataHex(b, "");

       b = s.getBytes("iso-10646-ucs-2");
      Common.PrintDataHex(b, "");

       b = new String( msg.getBytes(fmt)).getBytes( "iso-10646-ucs-2");
    Common.PrintDataHex(b, "");

    }catch (Exception e){
      e.printStackTrace();
    }



    //String str = "Base64代码翻译转换-程序员编译反编译在线工具. 2";
    //String result = Base64.Encode(str.getBytes());
    //System.out.println(result);
    //byte[] data = Base64.Decode(result);
    //System.out.println(new String(data));
  }

}





