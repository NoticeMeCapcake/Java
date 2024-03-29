package client.cryptoclient.cryptoAlgorithms;

public class ModeOFB implements CypherMode {
    private byte[] IV;
    private Magenta magenta;
    public ModeOFB(Magenta magenta, byte[] IV) {
        this.magenta = magenta;
        this.IV = IV;
    }
    @Override
    public byte[] encrypt(byte[] data, int len) {
        byte[] prevBlock = IV;
        for (int i = 0; i < data.length; i+=16) {
            byte[] block = magenta.encodeBlock(prevBlock);
            prevBlock = xor(data, block, i);
            System.arraycopy(prevBlock, 0, data, i, 16);
            prevBlock = block;
        }
        return data;
    }

    private byte[] xor(byte[] a, byte[] b, int pos) {
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = (byte) (a[pos + i] ^ b[i]);
        }
        return result;
    }

    @Override
    public byte[] decrypt(byte[] data, int len) {
        byte[] prevBlock = IV;
        for (int i = 0; i < data.length; i+=16) {
            prevBlock = magenta.encodeBlock(prevBlock);
            System.arraycopy(xor(data, prevBlock, i), 0, data, i, 16);
        }
        return data;
    }
    @Override
    public void reset() {}
}
