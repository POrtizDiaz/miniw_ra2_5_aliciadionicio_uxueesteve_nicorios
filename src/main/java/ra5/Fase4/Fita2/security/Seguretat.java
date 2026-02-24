package ra5.Fase4.Fita2.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class Seguretat {

    // AES/GCM
    private static final String AES_ALGO = "AES";
    private static final String AES_CIPHER = "AES/GCM/NoPadding";
    private static final int MIDA_IV = 12;
    private static final int MIDA_TAG = 128;

    // RSA
    private static final String RSA_ALGO = "RSA";

    // --- MÉTODOS RSA ---
    public KeyPair generarParellClausRSA() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGO);
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public PublicKey stringToPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGO);
        return keyFactory.generatePublic(spec);
    }

    public String publicKeyToString(PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public byte[] xifrarRSA(byte[] dades, PublicKey clauPublica) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, clauPublica);
        return cipher.doFinal(dades);
    }

    public byte[] desxifrarRSA(byte[] dadesXifrades, PrivateKey clauPrivada) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, clauPrivada);
        return cipher.doFinal(dadesXifrades);
    }

    // --- MÉTODOS AES (Ahora reciben la clave como parámetro) ---
    public SecretKey generarClauAES() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGO);
        keyGen.init(128); // 128 bits
        return keyGen.generateKey();
    }

    public String xifrarAES(String textEnClar, SecretKey clauAES) throws Exception {
        byte[] iv = new byte[MIDA_IV];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(MIDA_TAG, iv);

        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, clauAES, gcmSpec);

        byte[] textXifrat = cipher.doFinal(textEnClar.getBytes());
        byte[] missatgeFinal = new byte[MIDA_IV + textXifrat.length];
        System.arraycopy(iv, 0, missatgeFinal, 0, MIDA_IV);
        System.arraycopy(textXifrat, 0, missatgeFinal, MIDA_IV, textXifrat.length);

        return Base64.getEncoder().encodeToString(missatgeFinal);
    }

    public String desxifrarAES(String textXifratBase64, SecretKey clauAES) throws Exception {
        byte[] missatgeRebut = Base64.getDecoder().decode(textXifratBase64);
        byte[] iv = new byte[MIDA_IV];
        System.arraycopy(missatgeRebut, 0, iv, 0, MIDA_IV);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(MIDA_TAG, iv);

        byte[] textXifrat = new byte[missatgeRebut.length - MIDA_IV];
        System.arraycopy(missatgeRebut, MIDA_IV, textXifrat, 0, textXifrat.length);

        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, clauAES, gcmSpec);

        return new String(cipher.doFinal(textXifrat));
    }

    // --- EXCELENCIA: SHA-256 para integridad ---
    public String generarHashSHA256(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}