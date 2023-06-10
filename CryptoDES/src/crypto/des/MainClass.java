package crypto.des;

public class MainClass {


    public static void main(String[] args){
        byte[] key = DESHelper.generateRandomArray(8);

        byte[] initVector = DESHelper.generateRandomArray(8);

        var crypto = new SymmetricalCrypto(key, CipherModes.CTR, initVector, new FeistelNetwork(new DESKeyExpansion(), new DESEncryptor()));
        crypto.encryptFile("C:\\Users\\Danon\\Desktop\\CryptoDES\\src\\crypto\\des\\test.pdf", "C:\\Users\\Danon\\Desktop\\CryptoDES\\src\\crypto\\des\\out3");
        crypto.decryptFile("C:\\Users\\Danon\\Desktop\\CryptoDES\\src\\crypto\\des\\out3", "C:\\Users\\Danon\\Desktop\\CryptoDES\\src\\crypto\\des\\out4.pdf");

    }
}
