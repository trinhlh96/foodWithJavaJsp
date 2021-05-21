package repository;

public class MyModelError extends Exception{
    public MyModelError() {
    }

    public MyModelError(String message) {
        super(message);
    }
}
