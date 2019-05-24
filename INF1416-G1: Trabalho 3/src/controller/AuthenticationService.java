package controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import model.TreeNode;
import model.User;

public class AuthenticationService {

    private static AuthenticationService data = new AuthenticationService();

    private User user;

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public static AuthenticationService sharedInstance() {
        return data;
    }

    public User getUser() {
        return this.user;
    }
    
    public void loggout() {
    	this.user = null;
    	this.privateKey = null;
    	this.publicKey = null;
    }

    // MARK: ETAPA 1

    public User verifyEmail(String email) throws ParseException {

        try {
            this.user = DBManager.getUser(email);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        };
        if (this.user == null) {
        }
        return this.user;
    }


    // MARK: ETAPA 2

    public boolean checkPasswordTree(TreeNode root, User user, String senhaFormada) {

        if (root.right == null && root.left == null) {
            return this.passwordAuthentication(senhaFormada, user);
        }
        boolean ret1 = checkPasswordTree(root.left, user, senhaFormada + root.left.value);
        boolean ret2 = checkPasswordTree(root.right, user, senhaFormada + root.right.value);

        return ret1 || ret2;
    }

    public boolean passwordAuthentication(String senha, User user)  {

        String senhaDigest = AuthenticationService.generatePasswordDiggest(senha, user.getSalt());
        if (user.getPassword().equals(senhaDigest)) {
            return true;
        } else {
            return false;
        }
    }

    // MARK: ETAPA 3

    public PublicKey getPublicKey() throws Exception {

        if (this.user == null) {
            throw new Exception("Necessário realizar a verificao de usuario primeiro");
        }

        String certificate = this.user.getCertificate();
        byte[] certificateBytes = certificate.getBytes();

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        InputStream certificateInputStream = new ByteArrayInputStream(certificateBytes);
        X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);

        return x509Certificate.getPublicKey();
    }

    public PrivateKey getPrivateKey(String password, Path path) throws Exception {

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());

        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56, secureRandom);
        Key key = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] cipherPemBytes;
		try {
			cipherPemBytes = Files.readAllBytes(path);
		} catch (IOException e) {
            DBManager.insereRegistro(4004);

			// TODO Auto-generated catch block
			throw new Exception("Caminho para o arquivo da chave privada inválido.");
		}
        byte[] pemBytes = cipher.doFinal(cipherPemBytes);

        String pemString = new String(pemBytes);
        pemString = pemString.replace("-----BEGIN PRIVATE KEY-----\n","");
        pemString = pemString.replace("-----END PRIVATE KEY-----\n","");

        byte[] privateKeyBytes = Base64.getMimeDecoder().decode(pemString);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }



    public void isPrivateKeyValid(String password, String pathString) throws Exception {

        Path path = Paths.get(pathString);

        try {
            privateKey = getPrivateKey(password, path);
        } catch (Exception e) {
            DBManager.insereRegistro(4005);

            // Frase secreta incorreta
            throw new Exception("Frase secreta incorreta.");
        }

        publicKey = getPublicKey();

        byte[] message = new byte[2048];
        (new SecureRandom()).nextBytes(message);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(message);
        byte[] cipherMessage = signature.sign();

        signature.initVerify(publicKey);
        signature.update(message);

        if(signature.verify(cipherMessage)) {
            return ;
        } else {
            DBManager.insereRegistro(4006);
            throw new Exception("Chave privada inválida.");
        }
    }


    // MARK: Common Methods

    public static String generatePasswordDiggest(String senha, String salt) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Não encontrou algoritmo SHA1");
            return null;
        }
        sha1.update((senha + salt).getBytes());
        return Tools.toHex(sha1.digest());
    }

    public static boolean accessFile(HashMap user, String index, String nomeArquivo, PrivateKey chavePrivada, String pastaArquivos) {
        try {
            String[] linhasIndex = index.split("\n");
            for (String linha: linhasIndex) {
                String[] params = linha.split(" ");
                String nomeSecreto = params[1];

                if (nomeSecreto.equals(nomeArquivo)) {
                    String email = params[2];
                    String grupo = params[3];
                    if (user.get("email").equals(email) || user.get("groupName").equals(grupo)) {
                        String nomeCodigoArquivo = params[0];
                        byte[] conteudoArquivo = AuthenticationService.decryptFile(user, pastaArquivos, nomeCodigoArquivo, chavePrivada);
                        Files.write(Paths.get(pastaArquivos + "/" + nomeSecreto), conteudoArquivo);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] decryptFile(HashMap user, String caminho, String filename, PrivateKey chavePrivada) {
        try {

            byte[] arqEnv = Files.readAllBytes(Paths.get(caminho + "/" + filename + ".env"));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, chavePrivada);
            cipher.update(arqEnv);

            byte [] semente = cipher.doFinal();

            byte[] arqEnc = Files.readAllBytes(Paths.get(caminho + "/" + filename + ".enc"));
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            rand.setSeed(semente);

            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56, rand);
            Key chaveSecreta = keyGen.generateKey();

            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, chaveSecreta);
            byte[] index = cipher.doFinal(arqEnc);

            X509Certificate cert = AuthenticationService.leCertificadoDigital(((String) user.get("certificado")).getBytes());
            Signature assinatura = Signature.getInstance("MD5withRSA");
            assinatura.initVerify(cert.getPublicKey());
            assinatura.update(index);

            byte[] arqAsd = Files.readAllBytes(Paths.get(caminho + "/" + filename + ".asd"));
            if (assinatura.verify(arqAsd) == false) {
                System.out.println(filename + " pode ter sido adulterado");
                return null;
            }
            else {
                System.out.println("Decriptou index ok");
                return index;
            }
        }
        catch (Exception IOError) {
            return null;
        }
    }

    public static PrivateKey readPrivateKey(String fraseSecreta, String pathString, HashMap user) {
        try {
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            rand.setSeed(fraseSecreta.getBytes());

            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56, rand);
            Key chave = keyGen.generateKey();

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            try {
                cipher.init(Cipher.DECRYPT_MODE, chave);
            }
            catch (Exception e) {
                return null;
            }

            byte[] bytes = null;
            try {
                Path path = Paths.get(pathString);
                bytes = Files.readAllBytes(path);
            }
            catch (Exception e) {
                return null;
            }

            String chavePrivadaBase64 = new String(cipher.doFinal(bytes), "UTF8");
            chavePrivadaBase64 = chavePrivadaBase64.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").trim();
            byte[] chavePrivadaBytes = DatatypeConverter.parseBase64Binary(chavePrivadaBase64);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(new PKCS8EncodedKeySpec(chavePrivadaBytes));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean testaChavePrivada(PrivateKey chavePrivada, HashMap user) {
        try {
            byte[] teste = new byte[1024];
            SecureRandom.getInstanceStrong().nextBytes(teste);
            Signature assinatura = Signature.getInstance("MD5withRSA");
            assinatura.initSign(chavePrivada);
            assinatura.update(teste);
            byte[] resp = assinatura.sign();

            PublicKey chavePublica = AuthenticationService.leCertificadoDigital(((String) user.get("certificado")).getBytes()).getPublicKey();
            assinatura.initVerify(chavePublica);
            assinatura.update(teste);

            if (assinatura.verify(resp)) {
                System.out.println("Chave válida!");
                return true;
            }
            else {
                DBManager.incrementaNumChavePrivadaErrada((String) user.get("email"));
                System.out.println("Chave rejeitada!");
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("Erro ao testar chave privada");
            return false;
        }
    }

    public static X509Certificate leCertificadoDigital(byte[] bytes) {
        try {

            InputStream stream = new ByteArrayInputStream(bytes);
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) factory.generateCertificate(stream);
            stream.close();
            return cert;
        }
        catch (IOException | CertificateException e) {
            System.out.println("Certificado digital inválido");
            return null;
        }
    }

    public static String certToString(X509Certificate cert) {
        StringWriter sw = new StringWriter();
        try {
            sw.write("-----BEGIN CERTIFICATE-----\n");
            sw.write(DatatypeConverter.printBase64Binary(cert.getEncoded()).replaceAll("(.{64})", "$1\n"));
            sw.write("\n-----END CERTIFICATE-----\n");
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }


    public boolean createUser(String group, String password, X509Certificate cert) throws Exception {

        String subjectDN = cert.getSubjectDN().getName();
        int start = subjectDN.indexOf("=");
        int end = subjectDN.indexOf(",");
        String email = subjectDN.substring(start + 1, end);

        start = subjectDN.indexOf("=", end);
        end = subjectDN.indexOf(",", start);
        String nome = subjectDN.substring(start + 1, end);

        String salt = AuthenticationService.generateSalt();
        String senhaProcessada = AuthenticationService.generatePasswordDiggest(password, salt);

        boolean ret = DBManager.createUser(nome, email, group, salt, senhaProcessada, certToString(cert));

        return ret;
    }

    public void updateUser(String password, X509Certificate cert) {
        if (password != null && password.length() > 0) {

            String salt = user.getSalt();
            String enc = this.generatePasswordDiggest(password, salt);

            DBManager.changeUserPassword(enc, user.getEmail());
        }

        if (cert != null) {
            DBManager.changeUserCert(certToString(cert), user.getEmail());
        }
    }


    private static String generateSalt() {
        SecureRandom rand = new SecureRandom();
        StringBuffer salt = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            salt.append(new Integer(rand.nextInt(9)).toString());
        }
        return salt.toString();
    }


}
