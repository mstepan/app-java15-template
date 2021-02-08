package com.max.app.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public final class AESMain {

    private static final Logger LOG = LoggerFactory.getLogger(AESMain.class);

    public static void main(String[] args) throws Exception {

        String plainText = "Devoxx!!".repeat(13);

        final String algo = "AES/CBC/PKCS5Padding";
        final int ivLength = 16;

        // key length for AES can be 128/192/256
        final int keySize = 256;

        SecretKey key = generateKey(keySize);
        IvParameterSpec ivSpec = new IvParameterSpec(generateIV(ivLength));

        System.out.printf("plain: %s%n", plainText);

        String encoded = encode(plainText.getBytes(), algo, key, ivSpec);
        System.out.printf("encoded: %s%n", encoded);

        String decoded = decode(encoded, algo, key, ivSpec);
        System.out.printf("decoded: %s%n", decoded);

        System.out.printf("AES main completed. java version: %s%n", System.getProperty("java.version"));
    }

    private static String encode(byte[] data, String algo, SecretKey key, IvParameterSpec ivSpec)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encodedData = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encodedData);
    }

    private static String decode(String base64EncodedData, String algo, SecretKey key, IvParameterSpec ivSpec)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decodedData = cipher.doFinal(Base64.getDecoder().decode(base64EncodedData));

        return new String(decodedData, StandardCharsets.UTF_8);
    }

    private static final SecureRandom SECURE_RANDOM = getSecureRandom();

    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException ex) {
            LOG.info("Can't create STRONG SecureRandom, will fall back to default one.");
            return new SecureRandom();
        }
    }

    public static byte[] generateIV(int numBytes) {
        byte[] iv = new byte[numBytes];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    public static SecretKey generateKey(int keySizeInBits) throws GeneralSecurityException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySizeInBits, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    private static String base16Encode(byte[] data) {
        StringBuilder buf = new StringBuilder(2 * data.length);

        for (int singleByte : data) {
            int lower = singleByte & 0x0F;
            int higher = (singleByte >> 4) & 0x0F;
            buf.append(HEX[higher]).append(HEX[lower]);
        }

        return buf.toString();
    }


}
