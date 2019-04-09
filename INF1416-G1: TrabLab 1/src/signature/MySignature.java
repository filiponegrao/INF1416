package signature;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

public class MySignature {

	// Singleton variable
    private static MySignature data = null; 
    
    // Signature variables
    private String algo;
	private DSAPublicKey pub;
	private DSAPrivateKey priv;
	private MessageDigest md;
	
    // Singleton access method
    public static MySignature getInstance(String algo) throws NoSuchAlgorithmException  { 
        if (data == null) {
        	data = new MySignature(); 
        }
        data.algo = algo;
		data.md = MessageDigest.getInstance(algo);
        return data; 
    }
    
    // MARK: Sign methods
    
    public void initSign(PrivateKey privateKey) throws InvalidKeyException {
    	try {
			priv = (DSAPrivateKey) privateKey;
		} catch (ClassCastException cce) {
			throw new InvalidKeyException("Wrong private key type");
		}
    }
    
    public void update(byte[] bytes) throws SignatureException {
    	try {
			md.update(bytes);
		} catch (NullPointerException npe) {
			throw new SignatureException("No SHA digest found");
		}
    }
    
    public byte[] sign() throws SignatureException {
    	byte b[] = null;
		try {
			b = md.digest();
		} catch (NullPointerException npe) {
			throw new SignatureException("No SHA digest found");
		}
		return crypt(b, priv);
    }
    
    // MARK: Verify methods
    
    public void initVerify(PublicKey publicKey) throws InvalidKeyException {
    	try {
			pub = (DSAPublicKey) publicKey;
		} catch (ClassCastException cce) {
			throw new InvalidKeyException("Wrong public key type");
		}
    }
    
    public boolean verify(byte[] sigBytes) throws SignatureException {
		byte b[] = null;
		try {
			b = md.digest();
		} catch (NullPointerException npe) {
			throw new SignatureException("No SHA digest found");
		}
		byte sig[] = crypt(sigBytes, pub);
		return MessageDigest.isEqual(sig, b);
    }
    
    
    // MARK: Auxiliary Methods
    
    private byte[] crypt(byte s[], DSAKey key) {
		DSAParams p = key.getParams();
		int rotValue = p.getP().intValue();
		byte d[] = rot(s, (byte) rotValue);
		return d;
	}

	private byte[] rot(byte in[], byte rotValue) {
		byte out[] = new byte[in.length];
		for (int i = 0; i < in.length; i++) {
			out[i] = (byte) (in[i] ^ rotValue);
		}
		return out;
	}

}
