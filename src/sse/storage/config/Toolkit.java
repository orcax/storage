package sse.storage.config;

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
}
