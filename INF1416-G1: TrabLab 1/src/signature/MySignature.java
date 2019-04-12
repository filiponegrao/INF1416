package signature;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import java.security.SignatureException;

public class MySignature {

    // Singleton variable
    private static MySignature data = null; 

    // Signature variables
    private String algo;
    private PublicKey pub;
    private PrivateKey priv;
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

    // DOC: Store private key
    public void initSign(PrivateKey privateKey) throws InvalidKeyException {
    	try {
            priv = (PrivateKey) privateKey;
        } catch (ClassCastException cce) {
            throw new InvalidKeyException("Wrong private key type");
        }
    }

    // DOC: Compute message digest from plain text
    public void update(byte[] bytes) throws SignatureException {
    	try {
            md.update(bytes);
        } catch (NullPointerException npe) {
            throw new SignatureException("No SHA digest found");
        }
    }

    // DOC: Sign the message digest
    public byte[] sign() throws SignatureException {
    	byte b[] = null;

        // get message digest
        try {
            b = md.digest();
        } catch (NullPointerException npe) {
            throw new SignatureException("No SHA digest found");
        }

        //sign
        return crypt(b, priv);
    }
    
    // MARK: Verify methods

    // DOC: store public key
    public void initVerify(PublicKey publicKey) throws InvalidKeyException {
    	try {
            pub = (PublicKey) publicKey;
        } catch (ClassCastException cce) {
            throw new InvalidKeyException("Wrong public key type");
        }
    }

    // DOC: 
    public boolean verify(byte[] sigBytes) throws SignatureException {
        byte b[] = null;

        try {
            b = md.digest();
        } catch (NullPointerException npe) {
            throw new SignatureException("No SHA digest found");
        }

        // ONGOING: this seems wrong, check slides to verify
        byte sig[] = crypt(sigBytes, pub);
        System.out.println(sig);
        System.out.println(sigBytes);
        return MessageDigest.isEqual(sig, b);
    }
    
    
    // MARK: Auxiliary Methods
    
    private byte[] crypt(byte s[], Key key) {
        int rotValue = key.hashCode();
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