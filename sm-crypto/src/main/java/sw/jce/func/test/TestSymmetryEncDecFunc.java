package objectstack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
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

public class TestSymmetryEncDecFunc {
	
	public static void main(String[] args) {
		Security.addProvider(new SwxaProvider());
		while (true) {
			int choice = -1;
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("++++++++++++++++++++++ SwxaJCE API RSA Encryption Func Test +++++++++++++++++++++");
			System.out.println("                                                                                 ");
			System.out.println(" 1 DES                                                                           ");
			System.out.println(" 2 3DES                                                                          ");
			System.out.println(" 3 AES    include(--Stream Mode(CipherOutputStream))                             ");
			System.out.println(" 4 SM1                                                                           ");
			System.out.println(" 5 SSF33                                                                         ");
			System.out.println(" 6 SM4                                                                           ");
			System.out.println("                                                                                 ");
			System.out.println(" 0 Return to Prev Menu                                                           ");
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			choice = InUtil.getSelect();
			if (choice == 0) {
				return;
			}
			if ((choice < 1) || (choice > 6)) {
				continue;
			}

			switch (choice) {
			case 1:
				DESTest.run();
				break;
			case 2:
				DES3Test.run();
				break;
			case 3:
				AESTest.run();
				break;
			case 4:
				SM1Test.run();
				break;
			case 5:
				SSF33Test.run();
				break;
			case 6:
				SM4Test.run();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 外部密钥带加解密<br>
	 */
	public static void testExternal(String alg, String mode, String padding) {
		
		String transformation = alg+"/"+mode+"/"+padding;
		if (padding.equalsIgnoreCase("NOPADDING")){
			if (mode.equalsIgnoreCase("CTR")) {
				transformation = alg+mode;
			}
		}
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
			byte[]  keyCode = key.getEncoded();
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
		
		plain = InUtil.getBytes("1234567812345678");
		
		test(key, transformation, plain);
	}
	
	
	/**
	 * 内部密钥加解密<br>
	 */
	public static void testInternal(String alg, String mode, String padding) {
		String transformation = alg+"/"+mode+"/"+padding;
		if (padding.equalsIgnoreCase("NOPADDING")){
			if (mode.equalsIgnoreCase("CTR")) {
				transformation = alg+mode;
			}
		}
		byte[] plain = "hello world!".getBytes();
		Key key = null;
		int keylength = -1;
		keylength = InUtil.createSymmetryKeyIndex();
		
		System.out.print("Create Internal "+alg+" Key " + ':' + " KeyIndex[" + keylength + "] ... ");
		try {
			KeyGenerator kg = KeyGenerator.getInstance(alg, "SwxaJCE");
			kg.init(keylength<<16);
			key = kg.generateKey();
			if (key == null) {
				System.err.println("fail！");
			} else {
				// 获取密钥成功。
				System.out.println("ok！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		plain = InUtil.getBytes("1234567812345678");
		
		test(key, transformation, plain);
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

	
	private static void test(Key key, String transformation, byte[] plain) {
		Cipher cipher = null;
		try {
			System.out.println("Plain Text: " + new String(plain));
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
			byte[] tTemp = cipher.doFinal(plain);
			if (tTemp == null) {
				System.err.println(transformation+ " Mode Encrypt ERROR! Return value is NULL!");
			} else {
				for (int i = 0; i < tTemp.length; i++) {
					System.out.print(toHex(tTemp[i]));
				};
				System.out.println("----------Length="+tTemp.length);
				
				System.out.println("Cipher Text: " + new BASE64Encoder().encode(tTemp));
				// 定义解密Cipher类对象
				cipher = Cipher.getInstance(transformation, "SwxaJCE");
				// 初始化Cipher类对象
				if (transformation.contains("CBC")) {
					System.out.println("(transformation.contains(CBC)) ......is true");
					IvParameterSpec iIvParameterSpec=null;
					if(transformation.contains("DES")){
						iIvParameterSpec = new IvParameterSpec("00000000".getBytes());
					}else{
						iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
					}
					cipher.init(Cipher.DECRYPT_MODE, key,iIvParameterSpec);
				}else if (transformation.contains("CTR")) {
					System.out.println("(transformation.contains(CTR)) ......is true");
					IvParameterSpec iIvParameterSpec = new IvParameterSpec("0000000000000000".getBytes());
					cipher.init(Cipher.DECRYPT_MODE, key,iIvParameterSpec);
				} else{
					cipher.init(Cipher.DECRYPT_MODE, key);
				}
				// 调用解密函数
				byte[] tResult = cipher.doFinal(tTemp);
				System.out.println("First doFinal Result.length="+tResult.length);
				byte[] tResult1 = cipher.doFinal(tTemp);
				System.out.println("First doFinal Result1.length="+tResult1.length);

				if (transformation.contains("CBC")) {
					byte[] tempIV = cipher.getIV();
					System.out.println("cipher.getIV()==================================");
					for (int i = 0; i < tempIV.length; i++) {
						System.out.print(toHex(tempIV[i]));
					};
					System.out.println("");
					System.out.println("cipher.getIV()==================================");

				}
				if (tResult == null) {
					System.err.println(transformation+ " Mode Decrypt ERROR! Return value is NULL!");
				}
				// 比较结果
				if (new String(plain).equals(new String(tResult))) {
					System.out.println(transformation+ " Mode Encrypt and Decrypt Success!");
					
				} else {
					System.err.println(transformation+ " Mode Encrypt and Decrypt ERROR!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	static class DESTest {
		public static void run() {
			while (true) {
				int choice = -1;
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++ SwxaJCE API DES Encryption Func Test +++++++++++++++++++++");
				System.out.println("                                                                                 ");
				System.out.println(" 1 DES-ECB-NOPADDING ");
				System.out.println(" 2 DES-ECB-PKCS5PADDING ");
				System.out.println(" 3 DES-CBC-NOPADDING ");
				System.out.println(" 4 DES-CBC-PKCS5PADDING ");
				System.out.println("                                                                                 ");
				System.out.println(" 0 Return to Prev Menu                                                           ");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				choice = InUtil.getSelect();
				if (choice == 0) {
					return;
				}
				if ((choice < 1) || (choice > 8)) {
					continue;
				}

				String alg = "DES";
				switch (choice) {
				case 1:
					testExternal(alg, "ECB", "NOPADDING");
					break;
				case 2:
					testExternal(alg, "ECB", "PKCS5PADDING");
					break;
				case 3:
					testExternal(alg, "CBC", "NOPADDING");
					break;
				case 4:
					testExternal(alg, "CBC", "PKCS5PADDING");
					break;
				default:
					break;
				}
			}
		}
	}
	
	static class DES3Test {
		public static void run() {
			while (true) {
				int choice = -1;
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++ SwxaJCE API DES3 Encryption Func Test +++++++++++++++++++++");
				System.out.println("                                                                                 ");
				System.out.println(" 1 DES3-ECB-NOPADDING ");
				System.out.println(" 2 DES3-ECB-PKCS5PADDING ");
				System.out.println(" 3 DES3-CBC-NOPADDING ");
				System.out.println(" 4 DES3-CBC-PKCS5PADDING ");
				System.out.println("                                                                                 ");
				System.out.println(" 0 Return to Prev Menu                                                           ");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				choice = InUtil.getSelect();
				if (choice == 0) {
					return;
				}
				if ((choice < 1) || (choice > 4)) {
					continue;
				}

				String alg = "3DES";
				switch (choice) {
				case 1:
					testExternal(alg, "ECB", "NOPADDING");
					break;
				case 2:
					testExternal(alg, "ECB", "PKCS5PADDING");
					break;
				case 3:
					testExternal(alg, "CBC", "NOPADDING");
					break;
				case 4:
					testExternal(alg, "CBC", "PKCS5PADDING");
					break;
				default:
					break;
				}
			}
		}
	}
	
	static class AESTest {
		public static void run() {
			while (true) {
				int choice = -1;
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++ SwxaJCE API AES Encryption Func Test +++++++++++++++++++++");
				System.out.println("                                                                                 ");
				System.out.println(" 1 AES-ECB-NOPADDING ");
				System.out.println(" 2 AES-ECB-PKCS5PADDING ");
				System.out.println(" 3 AES-CBC-NOPADDING ");
				System.out.println(" 4 AES-CBC-PKCS5PADDING ");
				System.out.println(" 5 AES-CBC-PKCS7PADDING --Stream Mode(CipherOutputStream)            ");
				System.out.println(" 0 Return to Prev Menu                                                           ");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				choice = InUtil.getSelect();
				if (choice == 0) {
					return;
				}
				if ((choice < 1) || (choice > 5)) {
					continue;
				}

				String alg = "AES";
				switch (choice) {
				case 1:
					testExternal(alg, "ECB", "NOPADDING");
					break;
				case 2:
					testExternal(alg, "ECB", "PKCS5PADDING");
					break;
				case 3:
					testExternal(alg, "CBC", "NOPADDING");
					break;
				case 4:
					testExternal(alg, "CBC", "PKCS5PADDING");
					break;
				case 5:
					testExternal_Stream(alg,"CBC","PKCS7PADDING");
					break;
				default:
					break;
				}
			}
		}
	}
	
	static class SM1Test {
		public static void run() {
			while (true) {
				int choice = -1;
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++ SwxaJCE API SM1 Encryption Func Test +++++++++++++++++++++");
				System.out.println("                                                                                 ");
				System.out.println(" 1 SM1-ECB-NOPADDING ");
				System.out.println(" 2 SM1-ECB-PKCS5PADDING ");
				System.out.println(" 3 SM1-CBC-NOPADDING ");
				System.out.println(" 4 SM1-CBC-PKCS5PADDING ");
				System.out.println(" 5 SM1-CTR-NOPADDING 外部密钥 ");
				System.out.println(" 6 SM1-CTR-内部密钥 ");
				System.out.println("                                                                                 ");
				System.out.println(" 0 Return to Prev Menu                                                           ");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				choice = InUtil.getSelect();
				if (choice == 0) {
					return;
				}
				if ((choice < 1) || (choice > 6)) {
					continue;
				}

				String alg = "SM1";
				switch (choice) {
				case 1:
					testExternal(alg, "ECB", "NOPADDING");
					break;
				case 2:
					testExternal(alg, "ECB", "PKCS5PADDING");
					break;
				case 3:
					testExternal(alg, "CBC", "NOPADDING");
					break;
				case 4:
					testExternal(alg, "CBC", "PKCS5PADDING");
					break;
				case 5:
					testExternal(alg, "CTR", "NOPADDING");
					break;
				case 6:
					testInternal(alg, "CTR", "PKCS5PADDING");
					break;
				default:
					break;
				}
			}
		}
	}
	
	
	static class SSF33Test {
		public static void run() {
			while (true) {
				int choice = -1;
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++ SwxaJCE API SSF33 Encryption Func Test +++++++++++++++++++++");
				System.out.println("                                                                                 ");
				System.out.println(" 1 SSF33-ECB-NOPADDING ");
				System.out.println(" 2 SSF33-ECB-PKCS5PADDING ");
				System.out.println(" 3 SSF33-CBC-NOPADDING ");
				System.out.println(" 4 SSF33-CBC-PKCS5PADDING ");
				System.out.println("                                                                                 ");
				System.out.println(" 0 Return to Prev Menu                                                           ");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				choice = InUtil.getSelect();
				if (choice == 0) {
					return;
				}
				if ((choice < 1) || (choice > 4)) {
					continue;
				}

				String alg = "SSF33";
				switch (choice) {
				case 1:
					testExternal(alg, "ECB", "NOPADDING");
					break;
				case 2:
					testExternal(alg, "ECB", "PKCS5PADDING");
					break;
				case 3:
					testExternal(alg, "CBC", "NOPADDING");
					break;
				case 4:
					testExternal(alg, "CBC", "PKCS5PADDING");
					break;
				default:
					break;
				}
			}
		}
	}
	
	static class SM4Test {
		public static void run() {
			while (true) {
				int choice = -1;
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("++++++++++++++++++++++ SwxaJCE API SM4 Encryption Func Test +++++++++++++++++++++");
				System.out.println("                                                                                 ");
				System.out.println(" 1 SM4-ECB-NOPADDING ");
				System.out.println(" 2 SM4-ECB-PKCS5PADDING ");
				System.out.println(" 3 SM4-CBC-NOPADDING ");
				System.out.println(" 4 SM4-CBC-PKCS5PADDING ");
				System.out.println(" 5 Internal SM4-ECB-NOPADDING ");
				System.out.println(" 6 SM4-CTR-NOPADDING ");
				System.out.println(" 7 SM4-CTR-PKCS5PADDING ");
				System.out.println(" 8 SM4-ECB-PKCS7PADDING and Internal ");
				System.out.println(" 9 SM4-CBC-PKCS7PADDING and Internal ");
				System.out.println("                                                                                 ");
				System.out.println(" 0 Return to Prev Menu                                                           ");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				choice = InUtil.getSelect();
				if (choice == 0) {
					return;
				}
				if ((choice < 1) || (choice > 9)) {
					continue;
				}

				String alg = "SM4";
				switch (choice) {
				case 1:
					testExternal(alg, "ECB", "NOPADDING");
					break;
				case 2:
					testExternal(alg, "ECB", "PKCS5PADDING");
					break;
				case 3:
					testExternal(alg, "CBC", "NOPADDING");
					break;
				case 4:
					testExternal(alg, "CBC", "PKCS5PADDING");
					break;
				case 5:
					testInternal(alg, "ECB", "NOPADDING");
					break;
				case 6:
					testExternal("SM1", "CTR", "NOPADDING");
					break;
				case 7:
					testExternal("SM1", "CTR", "PKCS5PADDING");
					break;
				case 8:
					testExternal(alg, "ECB", "PKCS7PADDING");
					testInternal(alg, "ECB", "PKCS7PADDING");
					break;
				case 9:
					testExternal(alg, "CBC", "PKCS7PADDING");
					testInternal(alg, "CBC", "PKCS7PADDING");
					break;
				default:
					break;
				}
			}
		}
	}
	
	public static final String toHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
	
}
