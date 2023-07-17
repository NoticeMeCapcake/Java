package client.cryptoclient.cryptoAlgorithms;

import lombok.Getter;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Crypto {
    private CypherMode cypherMode;
    private Random randomizer = new Random(LocalDateTime.now().getNano());
    @Getter
    private byte[] IV;
    private Modes mode;
    private Magenta magenta;
    @Getter
    private float decryptProgress = 0;
    @Getter
    private float encryptProgress = 0;

    public Crypto(Modes mode, Magenta magenta, byte[] IV) {
        this.mode = mode;
        this.IV = IV;
        this.magenta = magenta;
        setMode();
    }
    public Crypto(Modes mode, Magenta magenta) {
        this.mode = mode;
        IV = new byte[16];
        randomizer.nextBytes(IV);
        this.magenta = magenta;
        setMode();
    }
    private void setMode() {
        switch (mode) {
            case ECB -> cypherMode = new ModeECB(magenta);
            case CBC -> cypherMode = new ModeCBC(magenta, IV);
            case CTR -> cypherMode = new ModeCTR(magenta, IV);
            case CFB -> cypherMode = new ModeCFB(magenta, IV);
            case RD -> cypherMode = new ModeRD(magenta, IV);
            case RDH -> cypherMode = new ModeRDH(magenta, IV);
            case OFB -> cypherMode = new ModeOFB(magenta, IV);
        }
    }
    public byte[] encryptFile(InputStream inputStream, long fileSize) {
        encryptProgress = 0;
        int maxProgress = (int) Math.ceil(((float)fileSize) / 1048576); //2^20 (1mb)
        byte[] data = new byte[0];
        byte[] buffer = new byte[1048576];
        int len;
        try {
            int maxLen = (this.mode == Modes.RD) ? 1048544 : (this.mode == Modes.RDH) ? 1048528 : 1048560;
            while ((len = inputStream.read(buffer, 0, maxLen)) > 0) {
                cypherMode.reset();
                int last = len % 16;
                Arrays.fill(buffer, len, len + 16 - last, (byte) (16 - last));
                System.out.println("in encrypt padded = " + (buffer[len + 1] & 0xff));
                byte[] encryptedBlock = cypherMode.encrypt(buffer, len + 16 - last);
                byte[] tmp = new byte[data.length + len + 16 - last + ((this.mode == Modes.RD) ? 16 : (this.mode == Modes.RDH) ? 32 : 0)];
                System.arraycopy(data, 0, tmp, 0, data.length);
                System.arraycopy(encryptedBlock, 0, tmp, data.length, len + 16 - last + ((this.mode == Modes.RD) ? 16 : (this.mode == Modes.RDH) ? 32 : 0));
                data = tmp;
                encryptProgress += (1f / maxProgress) * 100;
            }
        }
        catch  (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public byte[] decryptFile(InputStream inputStream, long fileSize) {
        decryptProgress = 0;
        int maxProgress = (int) Math.ceil(((float)fileSize) / 1048576); //2^20 (1mb)
        byte[] data = new byte[0];
        byte[] buffer = new byte[1048576];
        int len;
        try {
            while ((len = inputStream.read(buffer, 0, 1048576)) > 0) {
                cypherMode.reset();

                System.out.println((buffer[0] & 0xFF) + " " + len);
                byte[] decrypted = cypherMode.decrypt(buffer, len);
                int paddedBytes = decrypted[len - 1 - ((this.mode == Modes.RD) ? 16 : (this.mode == Modes.RDH) ? 32 : 0)] & 0xFF;
                System.out.println("in decrypt paddedBytes = " + paddedBytes);
                byte[] tmp = new byte[data.length + len - paddedBytes - ((this.mode == Modes.RD) ? 16 : (this.mode == Modes.RDH) ? 32 : 0)];
                System.arraycopy(data, 0, tmp, 0, data.length);
                System.arraycopy(decrypted, 0, tmp, data.length, len - paddedBytes -
                        ((this.mode == Modes.RD) ? 16 : (this.mode == Modes.RDH) ? 32 : 0));
                data = tmp;
                decryptProgress += (1f / maxProgress) * 100;
            }
        }
        catch  (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
