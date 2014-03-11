package sse.storage.except;

@SuppressWarnings("serial")
public class ConfigInitException extends Exception {

  private String errorMsg;

  public ConfigInitException() {

  }

  public ConfigInitException(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  @Override
  public void printStackTrace() {
    System.out.println("\n[ERROR] " + errorMsg);
    super.printStackTrace();
  }
}
