package client.cryptoclient.cryptoAlgorithms;


import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.NoArgsConstructor;
import org.springframework.core.serializer.Serializer;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Random;
//@JsonDeserialize
//@JsonSerialize
@NoArgsConstructor
public class GFP2Element {
    public BigInteger p;
    public BigInteger a;
    public BigInteger b;

    private static final Random randomizer = new Random(LocalDateTime.now().getNano());

    public GFP2Element(BigInteger p, BigInteger value) {
        a = (value.negate().mod(p));
        b = a;
        this.p = p;
    }
    public GFP2Element(BigInteger p, BigInteger a, BigInteger b) {
        this.a = a.mod(p);
        this.b = b.mod(p);
        this.p = p;
    }
    public GFP2Element(BigInteger p) {
        this.p = p;
        randomize();
    }
    public void randomize() {
        do {
            this.a = new BigInteger(this.p.bitCount(), randomizer).mod(this.p);
            this.b = new BigInteger(this.p.bitCount(), randomizer).mod(this.p);
        } while (a.equals(b));
    }
    public GFP2Element sum(GFP2Element other) {
        return new GFP2Element(this.p, this.a.add(other.a), this.b.add(other.b));
    }
    public GFP2Element subtract(GFP2Element other) {
        return new GFP2Element(this.p, this.a.subtract(other.a), this.b.subtract(other.b));
    }
    public static GFP2Element magicOperation(GFP2Element x, GFP2Element y, GFP2Element z) {
        return new GFP2Element(x.p,
                z.a.multiply(y.a.subtract(x.b).subtract(y.b)).add(z.b.multiply(x.b.subtract(x.a).add(y.b))),
                z.a.multiply(x.a.subtract(x.b).add(y.a)).add(z.b.multiply(y.b.subtract(x.a).subtract(y.a))));
    }
    public GFP2Element square() {
        return new GFP2Element(this.p,
                this.b.multiply(this.b.subtract(this.a).subtract(this.a)),
                this.a.multiply(this.a.subtract(this.b).subtract(this.b)));
    }
    public GFP2Element swap() {
        return new GFP2Element(this.p, this.b, this.a);
    }
    public boolean fromGFP() {
        return this.a.equals(this.b);
    }
    public boolean myEquals(GFP2Element other) {
        return this.a.equals(other.a) && this.b.equals(other.b);
    }
    public BigInteger toBigInteger() {
        return this.a.add(this.b.multiply(p));
    }

    public String toString() {
        return "GFP2(" + this.a + ", " + this.b + ")";
    }

}