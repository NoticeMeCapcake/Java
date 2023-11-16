package client.cryptoclient.cryptoAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ModeECB implements CypherMode {
    private Magenta magenta;
    public ModeECB(Magenta magenta) {
        this.magenta = magenta;
    }
    @Override
    public byte[] encrypt(byte[] data, int len) {
        List<Future<byte[]>> encryptTasks = new ArrayList<>();
        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < len; i += 16) {
                byte[] block = Arrays.copyOfRange(data, i, i + 16);
                encryptTasks.add(executorService.submit(() -> magenta.encodeBlock(block)));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < len / 16; i += 1) {
            try {
                byte[] block = encryptTasks.get(i).get();
                System.arraycopy(block, 0, data, i * 16, 16);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data, int len) {
        List<Future<byte[]>> decryptTasks = new ArrayList<>();

        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < len; i += 16) {
                byte[] block = Arrays.copyOfRange(data, i, i + 16);
                decryptTasks.add(executorService.submit(() -> magenta.decodeBlock(block)));
            }

            for (int i = 0; i < len / 16; i++) {
                byte[] block = decryptTasks.get(i).get();
                System.arraycopy(block, 0, data, i * 16, 16);
            }
            return data;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void reset() {}
}
