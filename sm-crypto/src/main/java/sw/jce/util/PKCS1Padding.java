package sw.jce.util;

public class PKCS1Padding {

	public static byte[] Padding(int type, byte[] inData, int outLength)
	{
		if(outLength - inData.length < 4)
			return null;
		byte[] outData = new byte[outLength];
		int padingLength = outLength - inData.length -3;
		outData[0] = 0x0;

		if(type == 1){
			outData[1] = 0x1;
			for(int i=0;i<padingLength;i++)
				outData[i+2] = (byte) 0xFF;
		}
		else if(type == 2){
			outData[1] = 0x2;
			for(int i=0;i<padingLength;i++) {
				outData[i+2] = (byte) 0xF;				
			}
		}
		else {
			return null;
		}
		outData[padingLength + 2] = 0x0;
		System.arraycopy(inData, 0, outData, padingLength + 3, inData.length); 
		return outData;
	}
	
	public static byte[] Unpadding(int type, byte[] inData)
	{
		
		if((inData[0] != 0)){
			return null;
		}
		int paddingLength = 0;
		if(type == 1) {
			if(inData[1] != 1) {
				return null;
			} else {
				while(inData[2+paddingLength++] != 0);
			}
		} else if(type == 2) {
			if(inData[1] != 2) {
				return null;
			} else {
				while(inData[2+paddingLength++] != 0);
			}
		}
		paddingLength--;
		
		byte[] outData = new byte[inData.length-paddingLength-3];
		System.arraycopy(inData, paddingLength+3, outData, 0, outData.length);
		
		return outData;
	}
	
	public static byte[] SignPadding(byte[] inData, int outLength)
	{
		return Padding(1, inData, outLength);
	}
	
	public static byte[] SignUnpadding(byte[] inData)
	{
		return Unpadding(1, inData);
	}
	
	public static byte[] EncPadding(byte[] inData, int outLength)
	{
		return Padding(2, inData, outLength);
	}
	
	public static byte[] EncUnpadding(byte[] inData)
	{
		return Unpadding(2, inData);
	}
	
	public static byte[] symmetryPadding(byte[] inData)  {
		
		int inLen = inData.length;
		int outLen = (inLen/16+1)*16;
		int paddingLen = outLen - inLen;
		
		byte[] outData = new byte[outLen];
		System.arraycopy(inData, 0, outData, 0, inLen);
		for(int i=inLen; i<outLen; i++) {
			outData[i] = (byte)paddingLen;
		}
		
		return outData;
	}
	
	public static byte[] symmetryUnpadding(byte[] inData) {

		int inLen = inData.length;
		int unpaddingLen = inData[inLen-1];
		int outLen = inLen - unpaddingLen;
		byte[] outData = new byte[outLen];
		for(int i=0; i<outLen; i++) {
			outData[i] = inData[i];
		}
		return outData;
	}
	
}
