package ra5.Fase4.Fita1.security;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Seguretat {

    private static final String ALGORISME = "AES";
    private static final String MODE_XIFRAT = "AES/GCM/NoPadding";

    private static final int MIDA_IV = 12;
    private static final int MIDA_TAG_BITS = 128;

    // FITA 1: Clau precompartida (simètrica) de 16 caràcters (128 bits)
    private static final String CLAU_SECRETA = "ClauSuperSegura!";
    private SecretKey clauAES;

    public Seguretat() {
        this.clauAES = new SecretKeySpec(CLAU_SECRETA.getBytes(), ALGORISME);
    }

    public String xifrar(String textEnClar) throws Exception {
        byte[] iv = new byte[MIDA_IV];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(MIDA_TAG_BITS, iv);

        Cipher cipher = Cipher.getInstance(MODE_XIFRAT);
        cipher.init(Cipher.ENCRYPT_MODE, clauAES, gcmSpec);

        byte[] textXifrat = cipher.doFinal(textEnClar.getBytes());

        byte[] missatgeFinal = new byte[MIDA_IV + textXifrat.length];
        System.arraycopy(iv, 0, missatgeFinal, 0, MIDA_IV);
        System.arraycopy(textXifrat, 0, missatgeFinal, MIDA_IV, textXifrat.length);

        return Base64.getEncoder().encodeToString(missatgeFinal);
    }

    public String desxifrar(String textXifratBase64) throws Exception {
        byte[] missatgeRebut = Base64.getDecoder().decode(textXifratBase64);

        byte[] iv = new byte[MIDA_IV];
        System.arraycopy(missatgeRebut, 0, iv, 0, MIDA_IV);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(MIDA_TAG_BITS, iv);

        byte[] textXifrat = new byte[missatgeRebut.length - MIDA_IV];
        System.arraycopy(missatgeRebut, MIDA_IV, textXifrat, 0, textXifrat.length);

        Cipher cipher = Cipher.getInstance(MODE_XIFRAT);
        cipher.init(Cipher.DECRYPT_MODE, clauAES, gcmSpec);

        byte[] textEnClar = cipher.doFinal(textXifrat);
        return new String(textEnClar);
    }
}
