package crypto.des;

public interface KeyExpansion {
    public byte[][] expandKey(byte[] key);
}
