package sse.storage.except;

@SuppressWarnings("serial")
public class SyncException extends Exception {
    private String errorMsg;

    public SyncException() {

    }

    public SyncException(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    @Override
    public void printStackTrace() {
        System.out.println("\n[ERROR] " + errorMsg);
        super.printStackTrace();
    }

}
