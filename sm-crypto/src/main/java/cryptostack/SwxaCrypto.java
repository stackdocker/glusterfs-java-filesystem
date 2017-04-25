package cryptostack;

import com.sansec.jce.provider.SwxaProvider;

import sw.jce.util.InUtil;
import sw.jce.util.PKCS1Padding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 

@Component
public class SwxaCrypto {
	
	@Autowired
	AppConf appConf;
	
	int eccIndex;
	
	public SwxaCrypto() throws java.net.MalformedURLException, java.net.URISyntaxException {
		URL url;
		if (appConf.getSwxaInifile() == null || appConf.getSwxaInifile().length() == 0)
		    url = getResource("/swsds.ini");
		else if (appConf.getSwxaInifile().startsWith("classpath:"))
			url = getResource(appConf.getSwxaInifile().substring("classpath:".length()));
		else 
			url = Paths.get(appConf.getSwxaInifile()).toUri().toURL();
		File file = new File(url.toURI());
		//Security.addProvider( new SwxaProvider(file.getAbsolutePath()));
		Security.addProvider( new SwxaProvider());
		eccIndex = appConf.getSwxaEccindex();
	}
	
	private URL getResource(String resource){

	    URL url ;

	    //Try with the Thread Context Loader. 
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    if(classLoader != null){
	        url = classLoader.getResource(resource);
	        if(url != null){
	            return url;
	        }
	    }

	    //Let's now try with the classloader that loaded this class.
	    classLoader = ClassLoader.class.getClassLoader();
	    if(classLoader != null){
	        url = classLoader.getResource(resource);
	        if(url != null){
	            return url;
	        }
	    }

	    //Last ditch attempt. Get the resource from the classpath.
	    return ClassLoader.getSystemResource(resource);
	}

	private KeyPair keypair = null;
	private int keylength = 256;
	private String transformation = "SM2";
	private String algorithm = "SM2";
	private SecretKeySpec sks = null;
	private PublicKey pubkey = null;

	public SwxaCrypto retrieveSM2KeyPair() {
		int keynum = -1;
		keynum = eccIndex * 2;
		while ((keynum < 1) || (keynum > 100)) {
			//keynum = getInput("Please Input the KeyNumber (1-100) :", 3);
			keynum = 6;
		}
		System.out.print("Create Internal SM2 Key " + ':' + " KeyIndex " + keynum + ' ' + " ... ");
		
		KeyPair kp = null;
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
		} finally {
			keypair = kp;
		}
		return this;
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
            key = keypair.getPrivate();
			
			System.out.println("-----------------------------------------------------------------------------------");
			// 定义解密Cipher类对象
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			// 初始化Cipher类对象
			cipher.init(Cipher.DECRYPT_MODE, key);
			
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
	
	
	private SwxaCrypto(Provider provider) throws NullPointerException, SecurityException {
		Security.addProvider(provider);
	}

	private static SwxaCrypto instance = null;
	
	public static SwxaCrypto createInstance() {
		try {
		    instance = new SwxaCrypto(new SwxaProvider());
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SwxaCrypto algorithm(String alg) {
		this.algorithm = alg;
		return this;
	}
	
	public SwxaCrypto algorithm_SM2() {
		this.algorithm = "SM2";
		return this;
	}
	
	public String algorithm() {
		return this.algorithm;
	}
	
	public SwxaCrypto keyGen() {
		String transformation = "SM2";
		KeyPair kp = null;
		int keylength = 256;
		try {
			System.out.print("Create External " + algorithm + " Key " + ':' + " KeyLength " + keylength + ' ' + " ... ");
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm, "SwxaJCE");
			kpg.initialize(keylength);
			kp = kpg.genKeyPair();
			if (kp == null) {
				System.out.println("fail to generate ");
			} else {
				// ������Կ�ɹ���
				System.out.println("ok!");
				this.keypair = kp;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this;
	}
	
	public SwxaCrypto keyGen_SM2() {
		algorithm("SM2");
		return keyGen();
	}
	
	public byte[] publicKey() {
		return this.keypair.getPublic().getEncoded();
	}
	
	public SwxaCrypto outputPublicKey_Base64(OutputStream out) {
		if (null == keypair) {
			System.out.println("KeyPair not existed!");
			return this;
		}
		String key = Base64.getEncoder().encodeToString(publicKey());
		if (null == out) {
		    System.out.println("Cipher Text: " + key);
		    return this;
		}
		OutputStreamWriter w = new OutputStreamWriter(out);
		try {
		    w.write(key, 0, key.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;		
	}
	
	public SwxaCrypto original(int len, boolean binary, boolean saved) {
		
		return this;
	}
	
	public SwxaCrypto original_50M(boolean binary, boolean saved) {
		return original(50 * 1000 * 1000, binary, saved);
	}
	
	public SwxaCrypto encrypt() {
		
		return this;
	}
	
	public static void testExternal() {
		String transformation = "SM2";
		KeyPair kp = null;
		int keylength = 256;
//		while (keylength != 256) {
//			keylength = InUtil.getInput("Please Input the Key Length (256) :", 3);
//		}
		System.out.print("Create External SM2 Key " + ':' + " KeyLength " + keylength + ' ' + " ... ");
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("SM2", "SwxaJCE");
			kpg.initialize(keylength);
			kp = kpg.genKeyPair();
			if (kp == null) {
				System.out.println("fail to generate ");
			} else {
				// ������Կ�ɹ���
				System.out.println("ok��");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		byte[] plain = PKCS1Padding.EncPadding("hello".getBytes(), keylength>>3);
		//plain = InUtil.getBytes(32);
		
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(transformation, "SwxaJCE");
			cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			byte[] tTemp = cipher.doFinal(plain);
			if (tTemp == null) {
				System.out.println(transformation+ " Mode Encrypt ERROR! Return value is NULL!");
			} else {
				// �������Cipher�����
				cipher = Cipher.getInstance(transformation, "SwxaJCE");
				// ��ʼ��Cipher�����
				cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
				// ���ý��ܺ���
				byte[] tResult = cipher.doFinal(tTemp);

				if (tResult == null) {
					System.out.println(transformation+ " Mode Decrypt ERROR! Return value is NULL!");
				}
				// �ȽϽ��
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
	
	public SwxaCrypto importKey(byte[] key, String algorithm) throws IllegalArgumentException {
		this.algorithm = algorithm;
		sks = new SecretKeySpec(key, algorithm);
		return this;
	}
	
	public SwxaCrypto importSM2PublicKey(byte[] key) throws InvalidKeySpecException, java.security.NoSuchAlgorithmException {
		this.importKey(key, "SM2");
		pubkey = KeyFactory.getInstance("SM2").generatePublic(this.sks);
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
	
	public static final String toHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
}