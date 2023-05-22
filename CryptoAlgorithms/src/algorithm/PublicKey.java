package algorithm;

import java.math.BigInteger;

public class PublicKey {
    public BigInteger p;
    public BigInteger q;
    public GFP2Element trace;
    public PublicKey(BigInteger p, BigInteger q, GFP2Element trace) {
        this.p = p;
        this.q = q;
        this.trace = trace;
    }
}
