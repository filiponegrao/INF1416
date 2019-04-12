package signature;

import java.util.Arrays;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import java.security.SignatureException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MySignature {

    // Singleton variable
    private static MySignature data = null; 

    // Signature variables

	private MessageDigest md;	
	private Cipher cipher; 
	
    // Singleton access method
    public static MySignature getInstance(String algo) throws NoSuchAlgorithmException, NoSuchPaddingException  { 
        if (data == null) {
            data = new MySignature(); 
        }
        
        String[] parts = algo.split("With");
        String alg = parts[0];
        String crypto = parts[1];
        
		data.md = MessageDigest.getInstance(alg);
		data.cipher = Cipher.getInstance(crypto);
		
        return data; 
    }
    
    // MARK: Sign methods

    // DOC: Store private key
    public void initSign(PrivateKey privateKey) throws InvalidKeyException {
		this.cipher.init(Cipher.ENCRYPT_MODE, privateKey);
    }

    // DOC: Compute message digest from plain text
    public void update(byte[] bytes) throws SignatureException {
	    this.md.update(bytes);
    }

    // DOC: sign the message digest, creating digital envelope    
    public byte[] sign() throws IllegalBlockSizeException, BadPaddingException {
		byte[] digest = this.md.digest();
		return this.cipher.doFinal(digest);
    }
    
    // MARK: Verify methods

    // DOC: store public key
    public void initVerify(PublicKey publicKey) throws InvalidKeyException {
		this.cipher.init(Cipher.DECRYPT_MODE, publicKey);
    }
    
    // DOC: decrypt digital envelope and verify message digest
    public boolean verify(byte[] sigBytes) throws IllegalBlockSizeException, BadPaddingException {
		byte[] decryptedMessageDigest = this.cipher.doFinal(sigBytes);
		return Arrays.equals(this.md.digest(), decryptedMessageDigest);
    }
}
