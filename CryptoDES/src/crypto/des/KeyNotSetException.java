package crypto.des;

public class KeyNotSetException extends Exception{
    public KeyNotSetException(String msg){
        super(msg);
    }
}
