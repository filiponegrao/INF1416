package controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class FileController {
	
	private static FileController data = new FileController();
	
	public static FileController sharedInstance() {
		return data;
	}
	
    public String[][] getFilesIndex(String folderPath) throws Exception {
        String indexPath = folderPath + "/index";
        String indexString = new String(this.readFile(indexPath));
        String[] indexLines = indexString.split("\n");
        String[][] indexTable = new String[indexLines.length][];

        for(int i = 0; i < indexLines.length; i++)
            indexTable[i] = indexLines[i].split(" ");

        return indexTable;
    }
	
    public byte[] readFile(String filePath) throws Exception {
        Key key = getSecretKey(filePath);
        
        byte[] fileContent = getFileContent(filePath, key);

        DBManager.insereRegistro(8013);

        if(!isSignatureFine(filePath, fileContent)) {
            DBManager.insereRegistro(8016);
            return null; // do something
        }
        DBManager.insereRegistro(8014);

        return fileContent;
    }
    
    public Key getSecretKey(String filePath) throws Exception {
        Path path = Paths.get(filePath + ".env");
        byte[] fileBytes = Files.readAllBytes(path);
        
        Cipher cipher = Cipher.getInstance("RSA");
        PrivateKey privateKey = AuthenticationService.sharedInstance().privateKey;
        
        
//        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//        keyGen.initialize(1024, random);
//        KeyPair keyPair = keyGen.generateKeyPair();
        
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] seed = cipher.doFinal(fileBytes);

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);

        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56, secureRandom);

        return keyGenerator.generateKey();
    }

    public byte[] getFileContent(String filePath, Key secretKey) throws Exception {
        try {
            Path path = Paths.get(filePath + ".enc");
            byte[] fileBytes = Files.readAllBytes(path);

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return cipher.doFinal(fileBytes);
        } catch (Exception e) {
//            Database.log(8015, filePath, Validation1.user.getString("email"));
            e.printStackTrace();

            return null;
        }
    }
    
    public boolean isSignatureFine(String filePath, byte[] fileContent) throws Exception {
        Path path = Paths.get(filePath + ".asd");
        byte[] fileSignature = Files.readAllBytes(path);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(AuthenticationService.sharedInstance().publicKey);
        signature.update(fileContent);

        return signature.verify(fileSignature);
    }
}
