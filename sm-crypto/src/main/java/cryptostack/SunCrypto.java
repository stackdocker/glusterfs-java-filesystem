package cryptostack;

import java.io.BufferedOutputStream; 
import java.io.File;
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.OutputStream; 
import java.math.BigInteger; 
import java.nio.ByteBuffer;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher; 
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.*; 

import com.sun.crypto.provider.SunJCE; 
 
public class SunCrypto { 
	
	private SunCrypto(Provider provider) throws NullPointerException, SecurityException {
		this();
		if ( null == provider ) return;
		
		Security.addProvider(provider);
		p = provider;
	}
	
	private SunCrypto() throws NullPointerException, SecurityException {
		p = Security.getProvider("SunJCE"); 
		System.out.println("SunJCE provider:" + ( null != p));
	}
	
	private static Provider p = null; 
	private static SunCrypto instance = null;
	
	public static SunCrypto createInstance() { 
		try {
		    // instance = new SunCrypto(new SunJCE());
			instance = new SunCrypto();
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	private String alg = "RSA";
	
	public String algorithm() {
		return this.alg;
	}
	
	public SunCrypto algorithm(String algorithm) {
		this.alg = algorithm;
		return this;
	}
	
	public SunCrypto algorithm_RSA() { 
	    this.alg = "RSA";
	    return this;
	}
	
	 
    private final static BigInteger N = new BigInteger 
    ("116231208661367609700141079576488663663527180869991078124978203037949869" 
    +"312762870627991319537001781149083155962615105864954367253799351549459177" 
    +"839995715202060014346744789001273681801687605044315560723525700773069112" 
    +"214443196787519930666193675297582113726306864236010438506452172563580739" 
    +"994193451997175316921"); 
 
    private final static BigInteger E = BigInteger.valueOf(65537); 
 
    private final static BigInteger D = new BigInteger 
    ("528278531576995741358027120152717979850387435582102361125581844437708890" 
    +"736418759997555187916546691958396015481089485084669078137376029510618510" 
    +"203389286674134146181629472813419906337170366867244770096128371742241254" 
    +"843638089774095747779777512895029847721754360216404183209801002443859648" 
    +"26168432372077852785"); 
 
    private final static SecureRandom RANDOM = new SecureRandom(); 
	
	private KeyFactory kf = null;
	
	public SunCrypto keyFactory(String algorithm) {		 
        try { 
            kf = KeyFactory.getInstance(algorithm, p); 
            this.alg = algorithm;
        } catch (NoSuchAlgorithmException e) { 
        	System.out.println(e.getMessage());
        	try {
                kf = KeyFactory.getInstance(algorithm); 
                this.alg = algorithm;
        	} catch (NoSuchAlgorithmException e1) { 
            	e1.printStackTrace();
            	kf = null;
        	}
        }
        return this;
	}
	
	public SunCrypto keyFactory_RSA() {
        try { 
            kf = KeyFactory.getInstance("RSA", p); 
            this.alg = "RSA";
        } catch (NoSuchAlgorithmException e) { 
        	e.printStackTrace();
        	try {
                kf = KeyFactory.getInstance("RSA"); 
                this.alg = "RSA";
        	} catch (NoSuchAlgorithmException e1) { 
            	e1.printStackTrace();
            	kf = null;
        	}
        } 
        this.alg = "RSA";
        return this;
	}	
	
	private PublicKey publickey = null;
	private PrivateKey privatekey = null;
	
	public SunCrypto keyPair_RSA(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent) {
		if ( modulus.compareTo(new BigInteger("65536")) < 0 &&
				publicExponent.compareTo(new BigInteger("65536")) < 0 &&
				privateExponent.compareTo(new BigInteger("65536")) < 0 ) {
			System.out.println("Using default modulus and exponent");
            try {
				PublicKey publicKey = kf.generatePublic(new RSAPublicKeySpec(N, E));
				PrivateKey privateKey = kf.generatePrivate(new RSAPrivateKeySpec(N, D));
				this.publickey = publicKey;
				this.privatekey = privateKey;
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			return this;
		}
		
        try {
	        RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modulus, publicExponent); 
	        PublicKey publicKey = kf.generatePublic(pubSpec); 
	 
	        RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modulus, privateExponent); 
	        PrivateKey privateKey = kf.generatePrivate(privSpec); 
	
	        this.publickey = publicKey;
			this.privatekey = privateKey;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
        return this;
	}
	
	private KeyPair kp = null;
	private int keysize = 2048;

	public SunCrypto keyPair_RSA(int keySize) {
		if ( 1024 > keySize) keysize = 2048;
		keysize = keySize;
		System.out.print("Create External " + alg + " Key " + ':' + " KeyLength " + keysize + ' ' + " ... ");
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(alg, p);
			kpg.initialize(keysize);
			KeyPair keypair = kpg.genKeyPair();
			if (kp == null) {
				System.out.println("fail to generate key pair");
			} else {
				System.out.println("ok!");
				this.kp = keypair;
			    this.publickey = keypair.getPublic();
			    this.privatekey = keypair.getPrivate();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return this;
	}
	
	public SunCrypto keyPair_Base64Printer(OutputStream out) {
		String key;
		key = Base64.getEncoder().encodeToString(publickey.getEncoded());
		if (null == out) {
		    System.out.println("Public Key: " + key);
		    return this;
		}
		try {
		    out.write(key.getBytes(), 0, key.getBytes().length);
			out.write('\n');
		} catch (Exception e) {
			e.printStackTrace();
		}
		key = Base64.getEncoder().encodeToString(privatekey.getEncoded());
		if (null == out) {
		    System.out.println("Private Key: " + key);
		    return this;
		}
		try {
		    out.write(key.getBytes(), 0, key.getBytes().length);
			out.write('\n');
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;				
	}
	
	private String tmpfilename;
	private int tmplen = 20 * 1000;

	public SunCrypto originalFile(int size) {
		return originalFile(size, false);
	}

	public SunCrypto originalFile(int size, boolean binary) {
		if ( 1024 > size ) size = 20 * 1000;

		File temp = null;
    	try{
    	    if ( null == tmpfilename || 0 == tmpfilename.length()) {
     	        //create a temp file
     	        temp = File.createTempFile("original-clear-", ".tmp"); 
     		    tmpfilename = temp.getAbsolutePath();
     	        System.out.println("Temp file : " + tmpfilename);
    	    } else {
    	    	temp = new File(tmpfilename);
    	    }
     	} catch(IOException e){
     	    e.printStackTrace();
     	    temp = null;
     	    return this;
     	}
    	
    	BufferedOutputStream bs = null;
    	try {
    	    FileOutputStream fs = new FileOutputStream(temp);
    	    bs = new BufferedOutputStream(fs);
        	
    		byte[] b = new byte[4096];
    		int offset = 0;
        	do {
    		    RANDOM.nextBytes(b);
    		    if ( offset + 4096 < size ) { 
    		    	if (!binary) {
    		    		for (int i = 0; i < 4096; i++ ) {
    		    			b[i] &= 0x7f;
    		    			if ( b[i] < 0x20 && b[i] != 0x0c ) { b[i] += 0x20; }
    		    			else if ( b[i] == 0x7f ) { b[i] = 0x20; }
    		    		}
    		    	} else bs.write(b);
    		    } else {
    		    	if (!binary) {
    		    		for (int i = 0; i < 4096; i++ ) {
    		    			b[i] &= 0x7f;
    		    			if ( b[i] < 0x20 && b[i] != 0x0c ) { b[i] += 0x20; }
    		    			else if ( b[i] == 0x7f ) { b[i] = 0x20; }
    		    		}
    		    	} else bs.write(b, 0, size - offset); 
    		    }
    		    offset += 4096;
    		} while ( offset >= size );

    	    bs.write(b);
    	    bs.close();
    	    bs = null;

    	} catch (Exception e) {
    	    e.printStackTrace();
    	}

    	if (bs != null) { try { bs.close(); } catch (Exception e) { e.printStackTrace(); } }
    	return this;
	}
	
	public void cleanTempData() {
		Path path = Paths.get(tmpfilename);
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
		System.out.println("cleaned!");
	}
	
	private String transform = "RSA/ECB/PKCS1Padding";
	private ByteBuffer encbuf, decbuf;
	
    public SunCrypto encryptWithPublic(String plainText) { 
        System.out.println("Start encryption using " + transform + "..."); 
        try {
            Cipher c = Cipher.getInstance(transform, p); 
//            byte[] b = new byte[len]; 
//            RANDOM.nextBytes(b); 
//            b[0] &= 0x3f; 
//            b[0] |= 1; 

            byte[] b = plainText.getBytes();
            
            c.init(Cipher.ENCRYPT_MODE, publickey); 
            byte[] enc = c.doFinal(b); 
		    System.out.println("Encrypted!");
        
		    encbuf = ByteBuffer.allocate(enc.length).put(enc);
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
        } catch (BadPaddingException e) {
        	e.printStackTrace();
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        }
		return this;
    }
    
    public SunCrypto encryptWithPublic() { 
        System.out.println("Start encryption using " + transform + "..."); 
        try {
            Cipher c = Cipher.getInstance(transform, p); 
//            byte[] b = new byte[len]; 
//            RANDOM.nextBytes(b); 
//            b[0] &= 0x3f; 
//            b[0] |= 1; 

            Path path = Paths.get(tmpfilename);
            byte[] b = Files.readAllBytes(path);

            c.init(Cipher.ENCRYPT_MODE, publickey); 
            byte[] enc = c.doFinal(b); 
		    System.out.println("Encrypted!");
        
		    encbuf = ByteBuffer.allocate(enc.length).put(enc);
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
        } catch (BadPaddingException e) {
        	e.printStackTrace();
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
		return this;
    }
    
    public SunCrypto encryption_Printer(FileOutputStream out) {
    	return this;
    }
    
    public SunCrypto decryptWithPrivate() {
        System.out.println("Start decryption using " + transform + "..."); 
        
        try {
	        Cipher c = Cipher.getInstance(transform, p); 
	 
	        c.init(Cipher.DECRYPT_MODE, privatekey); 
	        byte[] dec = c.doFinal(encbuf.array()); 
	 
	        decbuf = ByteBuffer.allocate(dec.length).put(dec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();
        } catch (BadPaddingException e) {
        	e.printStackTrace();
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
        }
        
        return this;
    }
    
    public SunCrypto decryption_Printer() {
    	return this;
    }
    
    public void comparison() {
        if (Arrays.equals(encbuf.array(), decbuf.array()) == false) { 
            System.out.println("result:  " + encbuf.compareTo(decbuf)); 
            // throw new Exception("Failure"); 
            return;
        } 
    }
}