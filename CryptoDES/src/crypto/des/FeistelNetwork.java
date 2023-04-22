package crypto.des;

public
 class FeistelNetwork implements CryptoFunction {
    KeyExpansion expansion;
    Encryptor encryptor;
    byte[][] roundKeys;

    public FeistelNetwork(KeyExpansion expansion, Encryptor encryptor) {
        this.expansion = expansion;
        this.encryptor = encryptor;
    }

    @Override
    public byte[] encrypt(byte[] array) throws KeyNotSetException {
        if (roundKeys == null)
            throw new KeyNotSetException("Не настроены ключи!");
        array = DESHelper.permutationBits(array, DESHelper.IP);
        byte[] prevLeft = DESHelper.getBits(array, 0, 32);
        byte[] prevRight = DESHelper.getBits(array, 32, 32);
        byte[] left = new byte[4];
        byte[] right = new byte[4];
        for (int i = 0; i < 16; i++){
            right = encryptor.encryptBlock(prevRight, roundKeys[i]);
            for (int j = 0; j < 4; j++) {
                right[j] = (byte) (right[j] ^ prevLeft[j]);
            }
            prevLeft = prevRight;
            left = prevRight;
            prevRight = right;
        }
        return glue(left, right);
    }

    @Override
    public byte[] decrypt(byte[] array) throws KeyNotSetException {
        if (roundKeys == null)
            throw new KeyNotSetException("Не настроены ключи!");
        array = DESHelper.permutationBits(array, DESHelper.IP);
        byte[] prevLeft = DESHelper.getBits(array, 0, 32);
        byte[] prevRight = DESHelper.getBits(array, 32, 32);
        byte[] left = new byte[4];
        byte[] right = new byte[4];
        for (int i = 0; i < 16; i++){
            left = encryptor.encryptBlock(prevLeft, roundKeys[15 - i]); //используем ключи в обратном порядке
            for (int j = 0; j < 4; j++){
                left[j] = (byte) (left[j] ^ prevRight[j]);
            }
            prevRight = prevLeft;
            right = prevLeft;
            prevLeft = left;
        }
        return glue(left, right);
    }

    private byte[] glue(byte[] left, byte[] right) {
        byte[] res = new byte[left.length + right.length];
        System.arraycopy(left, 0, res, 0, left.length);
        System.arraycopy(right, 0, res, left.length, right.length);
        return res;
    }

    @Override
    public void setRoundKeys(byte[] key) {
        roundKeys = expansion.expandKey(key);
    }
}
