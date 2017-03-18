package objectstack;

import com.sansec.jce.provider.SwxaProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SwxaCrypto {
	
	public SwxaCrypto() {
		Security.addProvider( new SwxaProvider("swsds.ini"));
	}
	
	private int getInput(String promptString) {
		System.out.print(promptString);
		int out = -1;
		try {
			String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
			if("".equals(str)) {
				out = -1;
			} else {
				out = Integer.parseInt(str);
			}
		} catch (Exception e) {
			out = -2;
		}
		
		return out;
    }	

	private KeyPair kp = null;
	private String transformation = "SM2";
	private String algorithm = "SM2";
	private SecretKeySpec sks = null;
	private PublicKey pubkey = null;

	public SwxaCrypto generateSM2KeyPair() {
		int keynum = -1;
		while ((keynum < 1) || (keynum > 100)) {
			keynum = getInput("Please Input the KeyNumber (1-100) :", 3);
		}
		System.out.print("Create Internal SM2 Key " + ':' + " KeyIndex " + keynum + ' ' + " ... ");
		
		kp = null;
		transformation = "SM2";
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("SM2", "SwxaJCE");
			kpg.initialize(keynum<<16);
			kp = kpg.genKeyPair();
			if (kp == null) {
				System.out.println("failed!");
			} else {
				System.out.println("succeed");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this;
	}
	
	public byte[] exportPublicKey() throws CryptoException {
		if (null == kp) throw new CryptoException("KeyPair not ready");
		byte[] result = this.kp.GetPublic().GetEncoded();
		System.out.println("Cipher Text: " + new Base64().getEncoder().encode(result));
		return result;
	}
	
	public SwxaCrypto importKey(byte[] key, string algorithm) throws IllegalArgumentException {
		this.algorithm = algorithm;
		sks = new SecretKeySpec(key, algorithm);
		return this;
	}
	
	public SwxaCrypto importSM2PublicKey(byte[] key) throws InvalidKeySpecException {
		this.importKey(key, "SM2");
		pubkey = KeyFactory.generatePublic(this.sks);
		return this;
	}
	
    public void EncryptSM2(InputStream in, OutputStream out) {
		transformation = "SM2";
		Cipher cipher = null;

		BufferedOutputStream bufferOS = null;
		BufferedInputStream bufferIS = null;

		try {
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);
			int buffsize = 8;
			byte[] buffer = new byte[buffsize];
			
			bufferOS = new BufferedOutputStream(new CipherOutputStream(out,	cipher), buffsize);
			bufferIS = new BufferedInputStream(in, buffsize);
			int len = 0;
			while ((len = bufferIS.read(buffer)) > 0) {
				System.out.println("\r\nbufferIS.read length="+len);
				bufferOS.write(buffer, 0, len);
			}
		} catch (Exception e) {
			System.out.println(transformation+ " Mode Encrypt and Decrypt ERROR!");
			e.printStackTrace();
		} finally {
			if (bufferOS != null) {
				try {
					bufferOS.close();
				} catch (IOException e) {
				}
			}
			if (bufferIS != null) {
				try {
					bufferIS.close();
				} catch (IOException e) {
				}
			}
		}
    }
    
	public void DecryptSM2(InputStream in, OutputStream out) {
		String alg = "SM2";
		int keylength = 256;

		String transformation = "SM2";
		Key key = null;

		Cipher cipher = null;

		BufferedOutputStream bufferOS = null;
		BufferedInputStream bufferIS = null;

		try {

			
			System.out.println("-----------------------------------------------------------------------------------");
			// 定义解密Cipher类对象
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			// 初始化Cipher类对象
			cipherDec.init(Cipher.DECRYPT_MODE, key);
			
			int buffsize = 8;
			byte[] buffer = new byte[buffsize];
			
			bufferOS = new BufferedOutputStream(new CipherOutputStream(out, cipher), buffsize);
			bufferIS = new BufferedInputStream(in, buffsize);
			int len = 0;
			while ((len = bufferIS.read(buffer)) > 0) {
				System.out.println("\r\n  Decrypt bufferIS.read length="+len);
				bufferOS.write(buffer, 0, len);
			}
		} catch (IOException e) {
			//throw new PKIException("Encrypt failure", e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferOS != null) {
				try {
					bufferOS.close();
				} catch (IOException e) {
				}
			}
			if (bufferIS != null) {
				try {
					bufferIS.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static final String toHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
}