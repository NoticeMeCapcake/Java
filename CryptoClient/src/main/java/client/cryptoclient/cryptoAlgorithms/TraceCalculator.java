package client.cryptoclient.cryptoAlgorithms;

import java.math.BigInteger;
import java.util.HashMap;

public class TraceCalculator {
    private BigInteger p;
    private GFP2Element c = null;

    private HashMap<BigInteger, GFP2Element> c_map = new HashMap<>();

    public TraceCalculator(BigInteger p) {
        this.p = p;
    }
    //TODO: заменить на опшнал
    public GFP2Element getTrace(BigInteger n, GFP2Element c) {
        if (this.c == null || (this.c!=null && !this.c.myEquals(c))) {
            this.c = c;
            this.c_map.clear();
            c_map.put(BigInteger.ZERO, new GFP2Element(p, BigInteger.valueOf(3)));
            c_map.put(BigInteger.ONE, this.c);
        }

        GFP2Element ccc = getC(n);
//        for (var key: c_map.keySet().stream().sorted().toArray()) {
//            System.out.println(key + "=" + c_map.get(key));
//        }
        return ccc;
    }

    private GFP2Element getC(BigInteger n) {
        if (c_map.containsKey(n)) {
            return c_map.get(n);
        }

        BigInteger current = BigInteger.ONE;
        int nbits = n.bitLength();
        BigInteger runner = BigInteger.ONE.shiftLeft(nbits - 2);
        for (int i = 1; i < nbits; i++) {
            BigInteger bit = n.and(runner);
            BigInteger newN = current.shiftLeft(1).or(BigInteger.valueOf(bit.equals(BigInteger.ZERO) ? 0 : 1));
            if (!c_map.containsKey(newN)) {
                GFP2Element currentC = c_map.get(current);
                c_map.put(newN, (!bit.equals(BigInteger.ZERO)) ?
                        GFP2Element.magicOperation(getC(current.add(BigInteger.ONE)), this.c, currentC)
                                .sum(getC(current.subtract(BigInteger.ONE)).swap()) :
                        currentC.square().subtract(currentC.sum(currentC).swap()));
            }
            current = newN;
            runner = runner.shiftRight(1);
        }
        if (!c_map.containsKey(n)) {
            System.out.println("Not");
        }
        return c_map.get(n);
    }

}
