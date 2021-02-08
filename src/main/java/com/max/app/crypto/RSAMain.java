package com.max.app.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;


public final class RSAMain {

    private static final Logger LOG = LoggerFactory.getLogger(RSAMain.class);

    public static void main(String[] args) throws Exception {

        String plainText = "Devoxx!!".repeat(5);

        final String algo = "RSA";

        // RSA key 1024 or 2048 or 4096
        final int keySize = 1024;

        KeyPair keyPair = generateKeyPair(keySize);

        System.out.printf("plain: %s%n", plainText);

        String encoded = encode(plainText.getBytes(), algo, keyPair.getPublic());
        System.out.printf("encoded: %s%n", encoded);

        String decoded = decode(encoded, algo, keyPair.getPrivate());
        System.out.printf("decoded: %s%n", decoded);

        System.out.printf("RSA main completed. java version: %s%n", System.getProperty("java.version"));
    }

    private static String encode(byte[] data, String algo, Key key)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encodedData = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encodedData);
    }

    private static String decode(String base64EncodedData, String algo, Key key)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = cipher.doFinal(Base64.getDecoder().decode(base64EncodedData));

        return new String(decodedData, StandardCharsets.UTF_8);
    }

    public static KeyPair generateKeyPair(int keySizeInBits) throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySizeInBits);
        return generator.generateKeyPair();
    }
}
