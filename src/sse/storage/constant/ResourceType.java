package sse.storage.constant;

public enum ResourceType {
  PICTURE("picture"), POST("post"), UNKNOWN("unknown");

  private final String type;

  private ResourceType(String type) {
    this.type = type;
  }
  
  @Override
  public String toString() {
    return this.type;
  }
  
  public static void main(String[] args) {
    System.out.println(ResourceType.PICTURE);
  }

}
