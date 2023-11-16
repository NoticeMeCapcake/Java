package crypto.des;

public class DESEncryptor implements Encryptor {
    @Override
    public byte[] encryptBlock(byte[] block, byte[] roundKey) { // на вход блок 4 байта
        byte[] arrAfterExpansion = DESHelper.permutationBits(block, DESHelper.E); //расширение до 6 байт
        for(int i = 0; i < arrAfterExpansion.length; i++){ // xor с ключом
            arrAfterExpansion[i] ^= roundKey[i];
        }
        byte[] arrAfterSbox = new byte[4];
        int count = 0;
        byte val = 0;
        //замена Sbox
        for(byte b: arrAfterExpansion){
            for(int i = 0; i < 8; i++) {
                val = (byte) ((val << 1) | ((b & 0b1000_0000) >>> 7));
                count++;
                if (count % 6 == 0) {
                    arrAfterSbox[(count / 6 - 1) / 2] = (byte) ((arrAfterSbox[(count / 6 - 1) / 2] << 4) | (DESHelper.replaceWithSbox(val, DESHelper.S[count / 6 - 1]) & 0xf));
                    val = 0;
                }
                b = (byte) (b << 1);
            }
        }
        //перестановка P
        arrAfterSbox = DESHelper.permutationBits(arrAfterSbox, DESHelper.pPermutationBlock);
        return arrAfterSbox;
    }
}
