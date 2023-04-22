package crypto.des;

public interface CryptoFunction {
    public byte[] encrypt(byte[] array) throws KeyNotSetException;
    public byte[] decrypt(byte[] array) throws KeyNotSetException;
    public void setRoundKeys(byte[] key);
}
