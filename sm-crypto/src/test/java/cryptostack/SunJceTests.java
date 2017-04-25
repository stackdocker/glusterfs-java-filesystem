package cryptostack;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger; 

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SunJceTests {
	
	private static SunCrypto mock;

	@BeforeClass
	public static void preStart() {
		mock = SunCrypto.createInstance();
		
		assertNotNull("SunJCE crypto must be ready!", mock);
	}
	
	@Before
	public void setUp() {
		
	}
	
    @Test
    public void test0_NewOriginalFile() {
        mock.originalFile(15000000);

        // assert statements
        assertEquals("Algorithm must be RSA", "RSA", mock.algorithm());
    }
	
    @Test
    public void test1st_KeyPair() {
        mock.algorithm("RSA").keyFactory("RSA").keyPair_RSA(new BigInteger("0"), new BigInteger("0"), new BigInteger("0")).keyPair_Base64Printer(System.out);

        // assert statements
        assertEquals("Algorithm must be RSA", "RSA", mock.algorithm());
    }
	
    @Test
    public void test2nd_EncDec() {
        mock.encryptWithPublic("this is a test").decryptWithPrivate().comparison();

        // assert statements
        // assertEquals("Algorithm must be RSA", "RSA", mock.algorithm());
    }
    
    @After
    public void tearDown() {
    	
    }
    
    @AfterClass
    public static void postStop() {
    	mock.cleanTempData();
    }
}