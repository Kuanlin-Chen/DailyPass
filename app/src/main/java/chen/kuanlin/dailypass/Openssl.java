package chen.kuanlin.dailypass;

import android.util.Base64;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by kuanlin on 2017/6/2.
 */

public class Openssl {

    private String value = "ERROR";

    private static byte[] key = new byte[] { 0x60, 0x3d, (byte) 0xeb,
            0x10, 0x15, (byte) 0xca, 0x71, (byte) 0xbe, 0x2b, 0x73,
            (byte) 0xae, (byte) 0xf0, (byte) 0x85, 0x7d, 0x77, (byte) 0x81,
            0x1f, 0x35, 0x2c, 0x07, 0x3b, 0x61, 0x08, (byte) 0xd7, 0x2d,
            (byte) 0x98, 0x10, (byte) 0xa3, 0x09, 0x14, (byte) 0xdf,
            (byte) 0xf4 };
    private static byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

    public String Encrypt(String plaindata){
        try{
            AlgorithmParameterSpec algorithm = new IvParameterSpec(iv);
            SecretKeySpec secretkey = new SecretKeySpec(key, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "AndroidOpenSSL");
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, algorithm);

            value = Base64.encodeToString(cipher.doFinal(plaindata.getBytes("UTF-8")), Base64.DEFAULT);
            return value;
        }catch(Exception ex){
            return value;
        }
    }

    public String Decrypt(String cipherdata){
        try{
            AlgorithmParameterSpec algorithm = new IvParameterSpec(iv);
            SecretKeySpec secretkey = new SecretKeySpec(key, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "AndroidOpenSSL");
            cipher.init(Cipher.DECRYPT_MODE, secretkey, algorithm);

            byte[] bytearray = cipher.doFinal(Base64.decode(cipherdata, Base64.DEFAULT));
            value = new String(bytearray, "UTF-8");
            return value;
        }catch(Exception ex){
            return value;
        }
    }
}
