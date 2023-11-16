package server.cryptoserver.cryptoAlgorithms;

import java.math.BigInteger;

public class PublicKey {
    public BigInteger p;
    public BigInteger q;
    public GFP2Element trace;
    public GFP2Element traceGK;
    public PublicKey(BigInteger p, BigInteger q, GFP2Element trace, GFP2Element traceGK) {
        this.p = p;
        this.q = q;
        this.trace = trace;
        this.traceGK = traceGK;
    }
}
