package client.cryptoclient.cryptoAlgorithms;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ModeRDH implements CypherMode {
    private Magenta magenta;
    private byte[] initializationVec;
    private BigInteger delta;
    private BigInteger initial;
    private BigInteger shift;

    public ModeRDH(Magenta magenta, byte[] IV){
        this.magenta = magenta;
        this.initializationVec = IV;
        this.initial = new BigInteger(initializationVec);
        this.delta = new BigInteger(IV, IV.length/2, IV.length/2);
        shift = BigInteger.ONE.shiftLeft(128);
    }

    private void xor(byte[] a, byte[] b, int pos) {
        for (int i = 0; i < 16; i++) {
            a[i + pos] = (byte) (a[pos + i] ^ b[i]);
        }
    }
    @Override
    public byte[] encrypt(byte[] buffer, int len) {
        int index = 0;
        System.out.println("Стар шифроания");
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(processors);
        List<Future<byte[]>> encryptedBlocksFutures = new LinkedList<>();
        System.out.println("INIT = " + Arrays.toString(initial.toByteArray()) + " : " + initial.toByteArray().length);
        encryptedBlocksFutures.add(service.submit(() -> magenta.encodeBlock(initial.toByteArray())));
        int hashCodeVec = initial.hashCode();
        ByteBuffer byteBufferHash = ByteBuffer.allocate(16);
        byteBufferHash.putInt(hashCodeVec);
        encryptedBlocksFutures.add(service.submit(() -> magenta.encodeBlock(initial.xor(new BigInteger(byteBufferHash.array())).toByteArray())));

        for (int i = 0; i < len; i += 16) {
            byte[] initArray = initial.toByteArray();
            xor(buffer, initArray, index);
            index += 16;
            initial = initial.add(delta).mod(shift);
        }
        System.out.println("Стар шифроания1");
        for (int i = 0; i < len; i += 16) {
            byte[] newBuf = Arrays.copyOfRange(buffer, i, i + 16);
            encryptedBlocksFutures.add(service.submit(() -> magenta.encodeBlock(newBuf)));
        }
        service.shutdown();

        return getBytes(encryptedBlocksFutures, buffer);
    }

    private byte[] getBytes(List<Future<byte[]>> encryptedBlocksFutures, byte[] resBuf) {
        int index = 0;
        try {
            for (var futureBufToWrite : encryptedBlocksFutures) {
                byte[] encryptedBuf = futureBufToWrite.get();
                for (int i = 0; i < 16; i++) {
                    resBuf[index++] = encryptedBuf[i];
                }
            }
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return resBuf;
    }

    @Override
    public byte[] decrypt(byte[] buffer, int len) {
        int index = 0;
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(processors);
        List<Future<byte[]>> decryptedBlocksFutures = new LinkedList<>();
        for (int i = 32; i < len; i += 16) {
            byte[] newBuf = Arrays.copyOfRange(buffer, i, i + 16);
            decryptedBlocksFutures.add(service.submit(() -> magenta.decodeBlock(newBuf)));
        }
        service.shutdown();
        getBytes(decryptedBlocksFutures, buffer);
        for (int i = 0; i < len; i += 16) {
            byte[] initArray = initial.toByteArray();
            xor(buffer, initArray, index);
            index += 16;
            initial = initial.add(delta).mod(shift);
        }

        return buffer;
    }

    @Override
    public void reset() {
        this.initial = new BigInteger(this.initializationVec);
    }
}
