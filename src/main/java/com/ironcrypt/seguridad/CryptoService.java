package com.ironcrypt.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoService {

    private static final String ALGORITMO_TRANSFORMACION = "AES/GCM/NoPadding";
    private static final int LONGITUD_TAG_BITS = 128;
    private static final int LONGITUD_IV_BYTES = 12;
    private static final int LONGITUD_SALT_BYTES = 16;
    private static final int ITERACIONES_PBKDF2 = 65536;
    private static final int LONGITUD_CLAVE_BITS = 256;

    public static class ResultadoCifrado {
        private final String textoCifrado;
        private final String ivNonce;
        private final String salt;

        public ResultadoCifrado(String textoCifrado, String ivNonce, String salt) {
            this.textoCifrado = textoCifrado;
            this.ivNonce = ivNonce;
            this.salt = salt;
        }

        public String getTextoCifrado() { return textoCifrado; }
        public String getIvNonce() { return ivNonce; }
        public String getSalt() { return salt; }
    }

    public static ResultadoCifrado cifrar(String passwordPlana, String passphrase) throws Exception {
        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        random.nextBytes(salt);

        byte[] iv = new byte[LONGITUD_IV_BYTES];
        random.nextBytes(iv);

        SecretKey secretKey = derivarClave(passphrase, salt);

        Cipher cipher = Cipher.getInstance(ALGORITMO_TRANSFORMACION);
        GCMParameterSpec spec = new GCMParameterSpec(LONGITUD_TAG_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] bytesCifrados = cipher.doFinal(passwordPlana.getBytes(StandardCharsets.UTF_8));

        return new ResultadoCifrado(
            Base64.getEncoder().encodeToString(bytesCifrados),
            Base64.getEncoder().encodeToString(iv),
            Base64.getEncoder().encodeToString(salt)
        );
    }

    public static String descifrar(String textoCifradoBase64, String ivBase64, 
                                   String saltBase64, String passphrase) throws Exception {
        byte[] bytesCifrados = Base64.getDecoder().decode(textoCifradoBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] salt = Base64.getDecoder().decode(saltBase64);

        SecretKey secretKey = derivarClave(passphrase, salt);

        Cipher cipher = Cipher.getInstance(ALGORITMO_TRANSFORMACION);
        GCMParameterSpec spec = new GCMParameterSpec(LONGITUD_TAG_BITS, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] bytesDescifrados = cipher.doFinal(bytesCifrados);
        return new String(bytesDescifrados, StandardCharsets.UTF_8);
    }

    private static SecretKey derivarClave(String passphrase, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERACIONES_PBKDF2, LONGITUD_CLAVE_BITS);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] encodedKey = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(encodedKey, "AES");
    }
}
