package controller;

public class Tools {

	public static String toHex(byte[] bytes) {
		  
		// converte byte para hexadecimal
		StringBuffer buf = new StringBuffer();
	    for(int i = 0; i < bytes.length; i++) {
	    	String hex = Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1);
	    	buf.append((hex.length() < 2 ? "0" : "") + hex);
	    }
	
	    return buf.toString();
	}
}
