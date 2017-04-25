/*
 * http://www.programcreek.com/java-api-examples/index.php?api=com.sun.crypto.provider.SunJCE
 */
package cryptostack.example;

import java.io.*; 
import java.security.*; 
import javax.crypto.*; 
import javax.crypto.spec.*; 
import com.sun.crypto.provider.SunJCE; 
 
public class SunJceDES { 
    public static void main(String[] args) throws Exception { 
 
        Provider prov = new com.sun.crypto.provider.SunJCE(); 
        Security.addProvider(prov); 
 
        SecureRandom sr = new SecureRandom(); 
 
        // Create new DES key. 
        KeyGenerator kg = KeyGenerator.getInstance("DES"); 
        kg.init(sr); 
        Key key = kg.generateKey(); 
 
        // Generate an IV. 
        byte[] iv_bytes = new byte[8]; 
        sr.nextBytes(iv_bytes); 
        IvParameterSpec iv = new IvParameterSpec(iv_bytes); 
 
        // Create the consumer 
        Cipher decrypter = Cipher.getInstance("DES/CFB8/NoPadding"); 
        decrypter.init(Cipher.DECRYPT_MODE, key, iv); 
        PipedInputStream consumer = new PipedInputStream(); 
        InputStream in = new CipherInputStream(consumer, decrypter); 
 
        // Create the producer 
        Cipher encrypter = Cipher.getInstance("DES/CFB8/NoPadding"); 
        encrypter.init(Cipher.ENCRYPT_MODE, key, iv); 
        PipedOutputStream producer = new PipedOutputStream(); 
        OutputStream out = new CipherOutputStream(producer, encrypter); 
 
        producer.connect(consumer); // connect pipe 
 
        byte[] plaintext = "abcdef".getBytes(); 
        for (int i = 0; i < plaintext.length; i++) { 
            out.write(plaintext[i]); 
            out.flush(); 
            int b = in.read(); 
            String original = new String(plaintext, i, 1); 
            String result = new String(new byte[] { (byte)b }); 
            System.out.println("  " + original + " -> " + result); 
        } 
    } 
}