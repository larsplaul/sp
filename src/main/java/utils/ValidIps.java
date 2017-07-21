package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a list of public IP's used by the school
 * The assumption will be, if a request commes from one of these ip's, it commes from the school
 * This will let at student register without an code
 */
public class ValidIps {
  private static Map<String,Boolean> validIps = new HashMap(); 
  
  //Should only be called from DeploymentConfiguration
  public static void setValidIps(String ipsAsString){
    String[] ips = ipsAsString.split(",");
    for(String ip : ips){
      validIps.put(ip, Boolean.TRUE);
    }
  }
  
  public static boolean isAValidIP(String ip){
    return validIps.get(ip) != null; 
  }
  
}
