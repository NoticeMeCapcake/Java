package server.cryptoserver.cryptoAlgorithms;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class Magenta {
    private short[] sBox;
    private byte[][] keyOrder;
    byte[] key;

    private static int[] permutationInBlock = {0, 2, 4, 6, 8, 10, 12, 14, 1, 3, 5, 7, 9, 11, 13, 15};
    public Magenta(byte[] key) {
        System.arraycopy(key, 0, this.key = new byte[key.length], 0, key.length);
        setKeyOrder();
        generateSBox();
    }

    private void setKeyOrder() {
        int keyLength = key.length;
        if (keyLength == 16) {
            byte[] K1 = Arrays.copyOfRange(key, 0, 8);
            byte[] K2 = Arrays.copyOfRange(key, 8, 16);
            this.keyOrder = new byte[][]{
                    K1, K1, K2, K2, K1, K1
            };
        }
        else if (keyLength == 24) {
            byte[] K1 = Arrays.copyOfRange(key, 0, 8);
            byte[] K2 = Arrays.copyOfRange(key, 8, 16);
            byte[] K3 = Arrays.copyOfRange(key, 16, 24);
            this.keyOrder = new byte[][] {
                    K1, K2, K3, K3, K2, K1
            };
        }
        else {//if (keyLength == 32)
            byte[] K1 = Arrays.copyOfRange(key, 0, 8);
            byte[] K2 = Arrays.copyOfRange(key, 8, 16);
            byte[] K3 = Arrays.copyOfRange(key, 16, 24);
            byte[] K4 = Arrays.copyOfRange(key, 24, 32);
            this.keyOrder = new byte[][] {
                    K1, K2, K3, K4, K4, K3, K2, K1
            };
        }

    }

    private static long fromBytesToLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    private void generateSBox() {
        short el = 1;
        sBox = new short[256];
        sBox[0] = 1;
        for (int i = 1; i < 255; i++) {
            el <<= 1;
            if (el > 255) {
                el = (short) ((0xff & el) ^ 101);
            }
            sBox[i] = el;
        }
        sBox[255] = 0;
        System.out.println(Arrays.toString(sBox));
    }

    public byte[] encodeBlock(byte[] block) {
        byte[] encodedBlock = new byte[block.length];
        System.arraycopy(block, 0, encodedBlock, 0, block.length);
        for (var K : keyOrder) {
            encodedBlock = FK(K, encodedBlock);
        }
        return encodedBlock;
    }

    public byte[] decodeBlock(byte[] encodedBlock) {
        return V(encodeBlock(V(encodedBlock)));
    }

    private byte[] V(byte[] bytes) {
        byte[] res = new byte[bytes.length];
        System.arraycopy(bytes, 8, res, 0, 8);
        System.arraycopy(bytes, 0, res, 8, 8);
        return res;
    }

    private byte[] FK(byte[] k, byte[] encodedBlock) { // len block 16 bytes
        byte[] x1 = Arrays.copyOfRange(encodedBlock, 0, 8);
        byte[] x2 = Arrays.copyOfRange(encodedBlock, 8, 16);
        byte[] concat = new byte[16];
        System.arraycopy(x2, 0, concat, 0, 8);
        System.arraycopy(k, 0, concat, 8, 8);
        byte[] tmp = F(concat);
        byte[] res = new byte[8];
        for (int i = 0; i < 8; i++) {
            res[i] = (byte) (tmp[i] ^ x1[i]);
        }
        System.arraycopy(x2, 0, concat, 0, 8);
        System.arraycopy(res, 0, concat, 8, 8);
        return concat;

    }

    private byte[] F(byte[] block) {
        byte[] res = S(C(3, block));

        return Arrays.copyOfRange(res, 8, 16);
    }

    private byte[] S(byte[] block) {
        byte[] res = new byte[block.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = block[permutationInBlock[i]];
        }
        return res;
    }

    private byte[] C(int i, byte[] block) {
        if (i == 1) {
            return T(block);
        }
        byte[] temp = S(C(i - 1, block));

        byte[] res = xorBytes(block, temp);

        return T(res);
    }

    private byte[] T(byte[] block) {
        byte[] res = block.clone();
        for (int i = 0; i < 4; i++) {
            res = P(res);
        }
        return res;
    }

    private byte[] P(byte[] block) {

        byte[] res = new byte[block.length];
        for (int i = 0; i < 8; i++) {
            byte[] tmp = PE(block[i], block[i + 8]);
            res[2*i] = tmp[0];
            res[2*i + 1] = tmp[1];
        }
        return res;
    }

    private byte[] PE(byte b1, byte b2) {
        byte[] res = new byte[2];
        res[0] = A(b1, b2);
        res[1] = A(b2, b1);
        return res;
    }

    private byte A(byte b1, byte b2) {
        return (byte) (b1 ^ f(b2));
    }

    private byte f(byte b) {
        return (byte) sBox[b&0xFF];
    }

    private byte[] xorBytes(byte[] block1, byte[] block2) {
        byte[] res = new byte[block1.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) (block1[i] ^ block2[i]);
        }
        return res;
    }

}
