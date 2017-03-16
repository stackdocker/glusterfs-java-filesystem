package objectstack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sansec.jce.provider.SwxaProvider;

import sun.misc.BASE64Encoder;
import sw.jce.util.InUtil;

public class SwxaCrypto {
	
	public SwxaCrypto() {
		Security.addProvider( new SwxaProvider());
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
	
	public void GenerateKeypair() {
		String transformation = "SM2";
		KeyPair kp = null;
		int keynum = -1;
		while ((keynum < 1) || (keynum > 100)) {
			keynum = getInput("Please Input the KeyNumber (1-100) :", 3);
		}
		System.out.print("Create Internal SM2 Key " + ':' + " KeyIndex " + keynum + ' ' + " ... ");
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
	}
	
    public void EncryptSM2() {
		byte[] plain = "hello world".getBytes();
		
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			byte[] tTemp = cipher.doFinal(plain);
			if (tTemp == null) {
				System.out.println(transformation+ " Mode Encrypt ERROR! Return value is NULL!");
			} else {
				cipher = Cipher.getInstance(transformation, "SwxaJCE");
				cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
				byte[] tResult = cipher.doFinal(tTemp);

				if (tResult == null) {
					System.out.println(transformation+ " Mode Decrypt ERROR! Return value is NULL!");
				}
				if (new String(plain).equals(new String(tResult)))
					System.out.println(transformation+ " Mode Encrypt and Decrypt Success!");
				else
					System.out.println(transformation+ " Mode Encrypt and Decrypt ERROR!");
			}
		} catch (Exception e) {
			System.out.println(transformation+ " Mode Encrypt and Decrypt ERROR!");
			e.printStackTrace();
		}    	
    }
    
	public void DecryptSM2(byte[] keycode, InputStream in, OutputStream out) {
		String alg = "SM2";
		int keylength = 256;

		String transformation = "SM2";
		Key key = null;

		Cipher cipher = null;
		Cipher cipherDec = null;

		BufferedOutputStream bufferOS = null;
		BufferedInputStream bufferIS = null;
		BufferedOutputStream bufferOS1 = null;
		BufferedInputStream bufferIS1 = null;

		try {
			KeyGenerator kg = KeyGenerator.getInstance(alg, "SwxaJCE");
			kg.init(keylength);
			key = kg.generateKey();  
			if (key == null) {
				System.err.println("fail！");
			} else {
				// 生成密钥成功。
				System.out.println("ok！");
			}
			//byte[]  keyCode = key.getEncoded();
			byte[]  keyCode = {(byte)0x69,(byte)0x44,(byte)0xB0,(byte)0x77,(byte)0x5A,(byte)0xD5,(byte)0xE6,(byte)0x9E,(byte)0x2D,(byte)0x7B,(byte)0x59,(byte)0x41,(byte)0x50,(byte)0xD1,(byte)0xD5,(byte)0x07};
		    System.out.println("keyCode====");
		    
			for (int i = 0; i < keyCode.length; i++) {
				System.out.print(toHex(keyCode[i]));
			};
			System.out.println("----------keyCode.length="+keyCode.length);
			
			newSecetKey = new SecretKeySpec(keyCode,alg);  //(keyCode,"SM4")
			System.out.println("----SecretKeySpec(keyCode,"+alg+")------");

			key = newSecretKey;
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			System.out.println("transformation="+transformation);
			//CBC 模式传入向量参数
			if (transformation.contains("CBC")) {
				System.out.println("(transformation.contains(CBC)) ......is true");
				IvParameterSpec iIvParameterSpec=null;
				if(transformation.contains("DES")){
					iIvParameterSpec = new IvParameterSpec("00000000".getBytes());
				}else{
					iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				}
				cipher.init(Cipher.ENCRYPT_MODE, key,iIvParameterSpec);
			}else if (transformation.contains("CTR")) {
				System.out.println("(transformation.contains(CTR)) ......is true");
				IvParameterSpec iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				cipher.init(Cipher.ENCRYPT_MODE, key,iIvParameterSpec);
			} else{
				cipher.init(Cipher.ENCRYPT_MODE, key);
			}
			
			int buffsize = 8;
			FileOutputStream out = new FileOutputStream(outputFileName);
			FileInputStream   in = new FileInputStream(inputFileName);
			byte[] buffer = new byte[buffsize];
			
			bufferOS = new BufferedOutputStream(new CipherOutputStream(out,	cipher), buffsize);
			bufferIS = new BufferedInputStream(in, buffsize);
			int len = 0;
			while ((len = bufferIS.read(buffer)) > 0) {
				System.out.println("\r\nbufferIS.read length="+len);
				bufferOS.write(buffer, 0, len);
			}
			
			System.out.println("-----------------------------------------------------------------------------------");
			// 定义解密Cipher类对象
			cipherDec = Cipher.getInstance(transformation, "SwxaJCE");
			// 初始化Cipher类对象
			if (transformation.contains("CBC")) {
				System.out.println("(transformation.contains(CBC)) ......is true");
				IvParameterSpec iIvParameterSpec=null;
				if(transformation.contains("DES")){
					iIvParameterSpec = new IvParameterSpec("00000000".getBytes());
				}else{
					iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				}
				cipherDec.init(Cipher.DECRYPT_MODE, key,iIvParameterSpec);
			}else if (transformation.contains("CTR")) {
				System.out.println("(transformation.contains(CTR)) ......is true");
				IvParameterSpec iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				cipherDec.init(Cipher.DECRYPT_MODE, key,iIvParameterSpec);
			} else{
				cipherDec.init(Cipher.DECRYPT_MODE, key);
			}
			
			int buffsize1 = 8;
			FileOutputStream out1 = new FileOutputStream(inputFileName+"1");
			FileInputStream   in1 = new FileInputStream(outputFileName);
			byte[] buffer1 = new byte[buffsize1];
			
			bufferOS1 = new BufferedOutputStream(new CipherOutputStream(out1, cipherDec), buffsize1);
			bufferIS1 = new BufferedInputStream(in1, buffsize1);
			int len = 0;
			while ((len = bufferIS1.read(buffer1)) > 0) {
				System.out.println("\r\n  Decrypt bufferIS.read length="+len);
				bufferOS1.write(buffer1, 0, len);
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
			if (bufferOS1 != null) {
				try {
					bufferOS1.close();
				} catch (IOException e) {
				}
			}
			if (bufferIS1 != null) {
				try {
					bufferIS1.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static final String toHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
	
	public static void testSM2Cipher() throws Exception {
		KeyPair kp = testGenSM2Key();
		String transformation = "";
		byte[] plain = "hello".getBytes();
		Cipher cipher = null;
		transformation = "SM2";
		cipher = Cipher.getInstance(transformation, "SwxaJCE");
		cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
		byte[] tTemp = cipher.doFinal(plain);
		if (tTemp == null) {
			System.out.println(transformation+ " Mode Encrypt ERROR! Return value is NULL!");
		} else {
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
			byte[] tResult = cipher.doFinal(tTemp);

			if (tResult == null) {
				System.out.println(transformation+ " Mode Decrypt ERROR! Return value is NULL!");
			}
			if (new String(plain).equals(new String(tResult)))
				System.out.println(transformation+ " Mode Encrypt and Decrypt Success!");
			else
				System.out.println(transformation+ " Mode Encrypt and Decrypt ERROR!");
		}
	}
	
	public static KeyPair testGenSM2Key() throws Exception {
		KeyPair kp = null;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("SM2", "SwxaJCE");
		kpg.initialize(2<<16);
		
		kp = kpg.genKeyPair();
		if (kp == null) {
			System.out.println("SM2 keypair not generated");
		} else {
			System.out.println(kp.getPublic());
			System.out.println(kp.getPrivate());
		}
		
		return kp;
	}
	
	/**
	 * 外部密钥带加解密, 流方式处理<br>
	 */
	public static void testExternal_Stream(String alg, String mode, String padding) {
		
		String transformation = alg+"/"+mode+"/"+padding;
		System.out.println("算法模式："+transformation);
		byte[] plain = "hello world!".getBytes();
		Key key = null;
		int keylength = -1;
		keylength = InUtil.createSymmetryKeySize();
		SecretKey  newSecetKey = null;
		
		System.out.print("Create External "+alg+" Key " + ':' + " KeyLength[" + keylength + "] ... ");
		try {
			KeyGenerator kg = KeyGenerator.getInstance(alg, "SwxaJCE");
			kg.init(keylength);
			key = kg.generateKey();  
			if (key == null) {
				System.err.println("fail！");
			} else {
				// 生成密钥成功。
				System.out.println("ok！");
			}
			//byte[]  keyCode = key.getEncoded();
			byte[]  keyCode = {(byte)0x69,(byte)0x44,(byte)0xB0,(byte)0x77,(byte)0x5A,(byte)0xD5,(byte)0xE6,(byte)0x9E,(byte)0x2D,(byte)0x7B,(byte)0x59,(byte)0x41,(byte)0x50,(byte)0xD1,(byte)0xD5,(byte)0x07};
		    System.out.println("keyCode====");
		    
			for (int i = 0; i < keyCode.length; i++) {
				System.out.print(toHex(keyCode[i]));
			};
			System.out.println("----------keyCode.length="+keyCode.length);
			
			newSecetKey = new SecretKeySpec(keyCode,alg);  //(keyCode,"SM4")
			System.out.println("----SecretKeySpec(keyCode,"+alg+")------");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//plain = InUtil.getBytes("1234567812345678");
		
		test_Stream(key, transformation, "TestInputFile.txt", "TestOutputFile.dat");
	}


	private static void test_Stream(Key key, String transformation, String inputFileName, String outputFileName) {
		Cipher cipher = null;
		Cipher cipherDec = null;
		try {
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			System.out.println("transformation="+transformation);
			//CBC 模式传入向量参数
			if (transformation.contains("CBC")) {
				System.out.println("(transformation.contains(CBC)) ......is true");
				IvParameterSpec iIvParameterSpec=null;
				if(transformation.contains("DES")){
					iIvParameterSpec = new IvParameterSpec("00000000".getBytes());
				}else{
					iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				}
				cipher.init(Cipher.ENCRYPT_MODE, key,iIvParameterSpec);
			}else if (transformation.contains("CTR")) {
				System.out.println("(transformation.contains(CTR)) ......is true");
				IvParameterSpec iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				cipher.init(Cipher.ENCRYPT_MODE, key,iIvParameterSpec);
			} else{
				cipher.init(Cipher.ENCRYPT_MODE, key);				
			}
			
			int buffsize = 8;
			FileOutputStream out = new FileOutputStream(outputFileName);
			FileInputStream   in = new FileInputStream(inputFileName);
			byte[] buffer = new byte[buffsize];
			BufferedOutputStream bufferOS = null;
			BufferedInputStream bufferIS = null;
			try {
				bufferOS = new BufferedOutputStream(new CipherOutputStream(out,	cipher), buffsize);
				bufferIS = new BufferedInputStream(in, buffsize);
				int len = 0;
				while ((len = bufferIS.read(buffer)) > 0) {
					System.out.println("\r\nbufferIS.read length="+len);
					bufferOS.write(buffer, 0, len);
				}
			} catch (IOException e) {
				//throw new PKIException("Encrypt failure", e);
			} catch (Exception e) {
				//throw new PKIException("Encrypt failure", e);
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
			
			System.out.println("-----------------------------------------------------------------------------------");
			// 定义解密Cipher类对象
			cipherDec = Cipher.getInstance(transformation, "SwxaJCE");
			// 初始化Cipher类对象
			if (transformation.contains("CBC")) {
				System.out.println("(transformation.contains(CBC)) ......is true");
				IvParameterSpec iIvParameterSpec=null;
				if(transformation.contains("DES")){
					iIvParameterSpec = new IvParameterSpec("00000000".getBytes());
				}else{
					iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				}
				cipherDec.init(Cipher.DECRYPT_MODE, key,iIvParameterSpec);
			}else if (transformation.contains("CTR")) {
				System.out.println("(transformation.contains(CTR)) ......is true");
				IvParameterSpec iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
				cipherDec.init(Cipher.DECRYPT_MODE, key,iIvParameterSpec);
			} else{
				cipherDec.init(Cipher.DECRYPT_MODE, key);
			}
			
			
			int buffsize1 = 8;
			FileOutputStream out1 = new FileOutputStream(inputFileName+"1");
			FileInputStream   in1 = new FileInputStream(outputFileName);
			byte[] buffer1 = new byte[buffsize1];
			BufferedOutputStream bufferOS1 = null;
			BufferedInputStream bufferIS1 = null;
			try {
				bufferOS1 = new BufferedOutputStream(new CipherOutputStream(out1, cipherDec), buffsize1);
				bufferIS1 = new BufferedInputStream(in1, buffsize1);
				int len = 0;
				while ((len = bufferIS1.read(buffer1)) > 0) {
					System.out.println("\r\n  Decrypt bufferIS.read length="+len);
					bufferOS1.write(buffer1, 0, len);
				}
			} catch (IOException e) {
				//throw new PKIException("Encrypt failure", e);
			} catch (Exception e) {
				//throw new PKIException("Encrypt failure", e);
			} finally {
				if (bufferOS1 != null) {
					try {
						bufferOS1.close();
					} catch (IOException e) {
					}
				}
				if (bufferIS1 != null) {
					try {
						bufferIS1.close();
					} catch (IOException e) {
					}
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}