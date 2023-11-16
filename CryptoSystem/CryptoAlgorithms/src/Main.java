import algorithm.Magenta;
import algorithm.XTR;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");
        XTR xtr = new XTR(128);
        xtr.AlGamalScheme();

        Magenta magenta = new Magenta(new byte[] {12, 43, 62, 76, (byte) 211, 32, 87,
                (byte) 240, 93, 45, 21, 78, 40, 90, 45, 112});
        byte[] enc = magenta.encodeBlock(new byte[] {(byte) 132, 13, (byte) 222, 64, 120, 21, (byte) 227,
                (byte) 144, 23, 47, 76, 71, 77, 26, 74, 127});
        byte[] dec = magenta.decodeBlock(enc);
        System.out.println(Arrays.toString(dec));
    }
}