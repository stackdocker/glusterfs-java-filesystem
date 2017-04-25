package cryptostack;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SwxaCryptoTests {
	
	private SwxaCrypto mock;

	@Before
	public void setUp() {
		mock = SwxaCrypto.createInstance();
		
		assertNotNull("SWXA crypto must be ready!", mock);
	}
	
    @Test
    public void test1st_KeyPair() {
        mock.algorithm("SM2").keyGen().outputPublicKey_Base64(System.out);

        // assert statements
        assertEquals("Algorithm must be SM2", "SM2", mock.algorithm());
    }
    
    @After
    public void tearDown() {
    	mock = null;
    }
}