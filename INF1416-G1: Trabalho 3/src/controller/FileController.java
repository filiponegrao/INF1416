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

import model.User;

public class FileController {
	
	private static FileController data = new FileController();
	
	public static FileController sharedInstance() {
		return data;
	}
	
    public String[][] getFilesIndex(String folderPath) throws Exception  {
        String indexPath = folderPath + "/index";
        String indexString;
		try {
			indexString = new String(this.readFile(indexPath));
		} catch (Exception e) {
            DBManager.insereRegistro(8008);
			throw new Exception("Falha na decriptação do arquivo de índice.");
		}
        String[] indexLines = indexString.split("\n");
        String[][] indexTable = new String[indexLines.length][];

        for(int i = 0; i < indexLines.length; i++) {
            indexTable[i] = indexLines[i].split(" ");
        }

        return indexTable;
    }
	
    public byte[] readFile(String filePath) throws Exception {
        Key key = getSecretKey(filePath);
        
        byte[] fileContent = getFileContent(filePath, key);

        DBManager.insereRegistro(8013, "", filePath);

        if(!isSignatureFine(filePath, fileContent)) {
            DBManager.insereRegistro(8016, "", filePath);
            
            throw new Exception("O arquivo não parece estar íntegro.");
        }
        DBManager.insereRegistro(8014, "", filePath);

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
            DBManager.insereRegistro(8015);
            e.printStackTrace();

            throw new Exception("Chave privada incorreta");
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
    
    // MARK: File checker
    
    public boolean checkAccess(String fileOwner, String fileGroup) {
        try {
            if(isFromGroup(fileGroup) || isFromOwner(fileOwner))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isFromGroup(String fileGroup) throws Exception {
    	User user = AuthenticationService.sharedInstance().getUser();
    	if (user == null) return false;
    	
        if(user.getGroupName().equalsIgnoreCase(fileGroup))
            return true;

        return false;
    }

    public boolean isFromOwner(String fileOwner) throws Exception {
    	User user = AuthenticationService.sharedInstance().getUser();
    	if (user == null) return false;
    	
        if(user.getEmail().equalsIgnoreCase(fileOwner))
            return true;

        return false;
    }
    
    public void createSecretFile(String filePath, String secretPath) throws Exception {
        byte[] fileContent = readFile(filePath);
        DBManager.insereRegistro(8013, "", filePath);
        createAndWriteFile(secretPath, fileContent);
    }
    
    public void createAndWriteFile(String filePath, byte[] content) throws Exception {
        Path path = Paths.get(filePath);
        Files.write(path, content);
    }
        
    
    
    
}
