package Utils;

public class ErrorHandler {

    public void printError(String msg) {
        System.out.println((char) 27 + "[31mError: " + (char) 27 + "[0m" + "\u001B[35m" + msg + "\u001B[0m");
    }
}
