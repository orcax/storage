package sse.storage.constant;

public class Toolkit {

  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }
  
  public static boolean isEmpty(String... strs) {
    for(String str : strs) {
      if (isEmpty(str)) {
        return true;
      }
    }
    return false;
  }
  
  public static void info(String msg) {
    System.out.println("[INFO] " + msg);
  }
  
  public static void error(String msg) {
    System.out.println("[ERROR] " + msg);
  }
}
