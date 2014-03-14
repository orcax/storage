package sse.storage.except;

@SuppressWarnings("serial")
public class BlockException extends Exception {
    private String errorMsg;

    public BlockException() {

    }

    public BlockException(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    @Override
    public void printStackTrace() {
        System.out.println("\n[ERROR] " + errorMsg);
        super.printStackTrace();
    }

}
