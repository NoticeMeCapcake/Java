package server.cryptoserver.cryptoAlgorithms;

import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class XTR {
    SimplicityTest test = new MillerRubin();
    private static Random randomizer = new Random(LocalDateTime.now().getNano());

    @Getter
    private PublicKey publicKey;
    @Getter
    private BigInteger privateKey;
    private int bitCount;
    public XTR(int bitCount) {
        this.bitCount = bitCount;
        generateKey();
    }

    private void generateKey() {
        BigInteger r, q, p, k;
        System.out.println("Generating key r q");
        do {
            System.out.println("Generating r");
            r = new BigInteger(bitCount / 2, randomizer);
            q = r.multiply(r).subtract(r).add(BigInteger.ONE);
        } while (!q.mod(BigInteger.valueOf(12)).equals(BigInteger.valueOf(7)) || !test.isPrime(q, 100));
        System.out.println(" r = " + r);
        System.out.println("Generating key k p");
        do {
            k = new BigInteger(bitCount / 2, randomizer);
            p = r.add(k.multiply(q));
        } while (!test.isPrime(p, 100) || ! p.mod(BigInteger.valueOf(3)).equals(BigInteger.valueOf(2)));
        System.out.println("k = " + k);
        System.out.println("Generated key");
        BigInteger quotient = p.multiply(p).subtract(p).add(BigInteger.ONE).divide(q);
        GFP2Element c = new GFP2Element(p);
        GFP2Element three = new GFP2Element(p, BigInteger.valueOf(3));
        GFP2Element trace;

        System.out.println("Generating trace");
        TraceCalculator tracer = new TraceCalculator(p);

        while (true) {
            GFP2Element newC = tracer.getTrace(p.add(BigInteger.ONE), c);
            if (!newC.fromGFP()) {
                trace = tracer.getTrace(quotient, c);
                System.out.println(trace);
                if (!trace.myEquals(three)) {
                    break;
                }
            }
            c = new GFP2Element(p);
        }
        System.out.println("Generated trace");

        BigInteger secretK = new BigInteger(bitCount / 4, randomizer);
        System.out.println("K " + secretK);
        privateKey = secretK;
        GFP2Element traceGK = tracer.getTrace(secretK, trace);
        this.publicKey = new PublicKey(p, q, trace, traceGK);
    }

    public void AlGamalScheme() {
        if (Optional.ofNullable(publicKey).isEmpty()) {
            generateKey();
        }

        TraceCalculator tracer = new TraceCalculator(publicKey.p);
        BigInteger secretK = new BigInteger(bitCount / 4, randomizer);
        System.out.println("K " + secretK);
        GFP2Element traceGK = tracer.getTrace(secretK, publicKey.trace);
        BigInteger b;
        BigInteger upperLimit = publicKey.q.subtract(BigInteger.TWO);
        System.out.println("");
        do {
            b = new BigInteger(upperLimit.bitLength() - 1, randomizer);
        } while (b.compareTo(upperLimit) >= 0 || b.compareTo(BigInteger.ONE) <= 0);
        System.out.println("Generated b");
        GFP2Element traceGB = tracer.getTrace(b, publicKey.trace);
        GFP2Element traceGBK = tracer.getTrace(b, traceGK);

        BigInteger message = new BigInteger(bitCount / 2, randomizer);
        System.out.println("message " + message);
        BigInteger cipher = message.xor(traceGBK.toBigInteger());
        System.out.println("cipher " + cipher);
        GFP2Element decryptKey = tracer.getTrace(secretK, traceGB);
        BigInteger decryptedMsg = decryptKey.toBigInteger().xor(cipher);
        System.out.println("decryptedMsg " + decryptedMsg);
    }

    public byte[] decryptKey(byte[] key, GFP2Element traceGB) {
        TraceCalculator tracer = new TraceCalculator(publicKey.p);
        BigInteger cipher = new BigInteger(key);
        GFP2Element decryptKey = tracer.getTrace(privateKey, traceGB);
        BigInteger decryptedMsg = decryptKey.toBigInteger().xor(cipher);
        System.out.println("decryptedMsg " + Arrays.toString(decryptedMsg.toByteArray()));
        return decryptedMsg.toByteArray();
    }

    public static BigInteger generateB(PublicKey publicKey) {
        BigInteger upperLimit = publicKey.q.subtract(BigInteger.TWO);
        BigInteger b;
        do {
            b = new BigInteger(upperLimit.bitLength() - 1, randomizer);
        } while (b.compareTo(upperLimit) >= 0 || b.compareTo(BigInteger.ONE) <= 0);
        System.out.println("Generated b");
        return b;
    }
    public static GFP2Element generateTraceGB(PublicKey publicKey, BigInteger b) {
        TraceCalculator tracer = new TraceCalculator(publicKey.p);
        return tracer.getTrace(b, publicKey.trace);
    }

    public static byte[] encryptKey(byte[] key, PublicKey publicKey, BigInteger b) {
        TraceCalculator tracer = new TraceCalculator(publicKey.p);
        GFP2Element traceGBK = tracer.getTrace(b, publicKey.traceGK);
        BigInteger message = new BigInteger(key);
        System.out.println("message " + message);
        BigInteger cipher = message.xor(traceGBK.toBigInteger());
        System.out.println(cipher.bitCount());
        return cipher.toByteArray();
    }

}
