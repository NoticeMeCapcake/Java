package crypto.des;

import java.io.*;
import java.util.Arrays;

public class SymmetricalCrypto {

    private final CryptoFunction algo;
    private final byte[] key;
    CipherModes mode;
    Mode modeRealization;
    private final byte[] initVector;

    protected SymmetricalCrypto(byte[] key, CipherModes mode, byte[] IV, CryptoFunction algo){
        this.key = Arrays.copyOfRange(key, 0, key.length);
        this.mode = mode;
        this.initVector = Arrays.copyOfRange(IV, 0, IV.length);
        this.algo = algo;
        this.algo.setRoundKeys(this.key);
        this.modeRealization = getModeRealization();
    }
    private Mode getModeRealization(){
        return switch (mode) {
            case ECB -> new ModeECB(algo);
            case CBC -> new ModeCBC(algo, initVector);
            case CFB -> new ModeCFB(algo, initVector);
            case OFB -> new ModeOFB(algo, initVector);
            case CTR -> new ModeCTR(algo, initVector);
            case RD -> new ModeRD(algo, initVector);
            case RDH -> new ModeRDH(algo, initVector);
            default -> null;
        };
    }

    public void encryptFile(String file, String outFile) {
        byte[] buffer = new byte[80000];
        int len;
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
            int length =  (this.mode == CipherModes.RD) ? 79984 : (this.mode == CipherModes.RDH) ? 79976 : 79992;
            while ((len = fileInputStream.read(buffer, 0, length)) > 0) {
                modeRealization.reset();
                int last = len % 8;
                Arrays.fill(buffer, len, len + 8 - last, (byte) (8 - last));
                fileOutputStream.write(modeRealization.encrypt(buffer, len + 8 - last), 0 , len + 8 - last);
            }
        }
        catch  (IOException e) {
            e.printStackTrace();
        }
    }
    public void decryptFile(String file, String outFile) {
        byte[] buffer = new byte[80000];
        int len;
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
            while ((len = fileInputStream.read(buffer)) > 0) {
                modeRealization.reset();
                byte[] newBuf = modeRealization.decrypt(buffer, len);
                var shift = ((this.mode == CipherModes.RD) ? 8 : (this.mode == CipherModes.RDH) ? 16 : 0);
                int paddedBytes = newBuf[len - 1 - shift] & 0xFF;
                fileOutputStream.write(newBuf, 0 , len - paddedBytes - shift);
            }
        }
        catch  (IOException e){
            e.printStackTrace();
        }
    }
}
