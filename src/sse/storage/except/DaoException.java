package sse.storage.except;

@SuppressWarnings("serial")
public class DaoException extends Exception {
    private String errorMsg;

    public DaoException() {

    }

    public DaoException(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    @Override
    public void printStackTrace() {
        System.out.println("\n[ERROR] " + errorMsg);
        super.printStackTrace();
    }

}
