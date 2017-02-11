package com.itheima.mobilesafe.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Utils {

	/**需要加密码的密码
	 * @param pwd   需要加密码的密码
	 * @return      md5加密后的字符串
	 */
	public static String encoder(String pwd) {
		try {
			//加盐处理
			pwd = pwd + "mobilsafe";
			//指定加密算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5");
		    //将需要加密的字符串转换成byte类型的数组，然后进行随机hash过程
		    byte[] bs = digest.digest(pwd.getBytes());
		    
		    StringBuffer sb = new StringBuffer();
		    //循环遍历bs,然后让其生成32位字符串，固定写法
		    for (byte b : bs) {
				int i = b & 0xff;
				//int类型的i需要转换成16进制字符
			    String hexString = Integer.toHexString(i);
		        if(hexString.length() < 2){
		        	
		        	hexString = "0" + hexString;
		        	
		        }
		        sb.append(hexString);
		    }
			
//		    System.out.println(sb.toString());
		    return sb.toString();
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		
		return null;
	}

}
