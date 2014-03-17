package sse.storage.except;

@SuppressWarnings("serial")
public class ResourceException extends Exception {
    private String errorMsg;

    public ResourceException() {

    }

    public ResourceException(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    @Override
    public void printStackTrace() {
        System.out.println("\n[ERROR] " + errorMsg);
        super.printStackTrace();
    }

}
