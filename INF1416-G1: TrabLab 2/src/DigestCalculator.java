import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import file.FileInfo;

public class DigestCalculator {

	private List<FileInfo> digestListInfo = new ArrayList<FileInfo>();
	private MessageDigest messageDigest;
	private String digestType;

	public static void main(String[] args) throws NoSuchAlgorithmException {

		DigestCalculator digestCalculator = new DigestCalculator();
		List<String> paths = new ArrayList<String>();
		
		if (args.length < 3) {
			System.err.println("Usage: java DigestCalulator Tipo_Digest Caminho_ArqListaDigest CaminhoArq1 ... CaminhoArqN");
			System.exit(1);
		}

		digestCalculator.setDigestType(args[0]);
		digestCalculator.setDigestListInfo(args[1]);

		for (int i = 2; i < args.length; i++) {
			paths.add(args[i]);
		}

		for (String arq: paths) {
			String digest = digestCalculator.calculateDigest(arq);
			if (digest != null) {
				String status = digestCalculator.searchDigest(arq, digest);
				System.out.println(String.format("%s %s %s %s", arq, digestCalculator.digestType, digest, status));
			}
		}
		digestCalculator.save();
		
		return;
	}
	
	String calculateDigest(String filePath)  {
		List<String> content;
		try{
			content = Files.readAllLines(Paths.get(filePath));	
		}
		catch (IOException e) {
			System.out.println(String.format("Failed to read file %s", filePath));
			return null;
		}

		for (String str: content) {
			this.messageDigest.update(str.getBytes());	
		}

		return toHex(this.messageDigest.digest());

	}

	String searchDigest(String filePath, String digest) {

		FileInfo info = this.searchInfoByName(filePath);

		if (info != null) {
			// encontrou arquivo
			if ((this.digestType.equals("MD5") && digest.equals(info.getHashMD5())) || 
					(this.digestType.equals("SHA1") && digest.equals(info.getHashSHA1()))) {

				return "OK";
			} else if ((this.digestType.equals("MD5") && info.getHashMD5() == null) || 
					(this.digestType.equals("SHA1") && info.getHashSHA1() == null)) {

				if (this.digestType.equals("MD5")) {
					info.setHashMD5(digest);
				} else if (this.digestType.equals("SHA1")) {
					info.setHashSHA1(digest);
				}

				return "NOT FOUND";

			} else {
				FileInfo colisionInfo = this.searchInfoByHash(digest);

				if (colisionInfo != null) {
					return "COLISION";
				} else {
					return "NOT OK";
				}
			}
		}
		else {
			FileInfo colisaoInfo = this.searchInfoByHash(digest);
			if (colisaoInfo != null) {
				return "COLISION";

			} else {
				FileInfo novoInfo = new FileInfo();
				novoInfo.setName(filePath);

				if (this.digestType.equals("MD5")) {
					novoInfo.setHashMD5(digest);
				}
				else if (this.digestType.equals("SHA1")) {
					novoInfo.setHashSHA1(digest) ;
				}

				this.digestListInfo.add(novoInfo);
				return "NOT FOUND";
			}

		}
	}

	void save() {
		String content = "";

		for (FileInfo fileInfo: this.digestListInfo) {

			if (fileInfo.getHashMD5() != null && fileInfo.getHashSHA1() != null) {
				content += String.format("%s %s %s %s %s\n", fileInfo.getName(), "MD5", fileInfo.getHashMD5(), "SHA1", fileInfo.getHashSHA1());
			}
			else if (fileInfo.getHashMD5() != null) {
				content += String.format("%s %s %s\n", fileInfo.getName(), "MD5", fileInfo.getHashMD5());
			}
			else if (fileInfo.getHashSHA1() != null) {
				content += String.format("%s %s %s\n", fileInfo.getName(), "SHA1", fileInfo.getHashSHA1());
			}
			else {
				System.out.println("Arquivo não possui hashes");
				System.exit(1);
			}

		}

		try {
			Files.write(Paths.get("list.txt"), content.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	public void setDigestType(String tipo) throws NoSuchAlgorithmException {
		this.digestType = tipo;
		this.messageDigest = MessageDigest.getInstance(tipo);	
	}

	public void setDigestListInfo(String caminhoArqListaDigest) {

		List<String> content = null;
		try{
			content = Files.readAllLines(Paths.get(caminhoArqListaDigest));
		}
		catch (IOException e) {
			return;
		}

		for (String line: content) {

			String[] lineInfo = line.split(" ");
			FileInfo info = new FileInfo();
			info.setName(lineInfo[0]);

			if (lineInfo[1].equals("MD5")) {
				info.setHashMD5(lineInfo[2]);
			} else if (lineInfo[1].equals("SHA1")) {
				info.setHashSHA1(lineInfo[2]);
			}

			if (lineInfo.length > 3) {

				if (lineInfo[3].equals("MD5")) {
					info.setHashMD5(lineInfo[4]);
				}
				else if (lineInfo[3].equals("SHA1")) {
					info.setHashSHA1(lineInfo[4]);
				}
			}

			this.digestListInfo.add(info);
		}
	}

	public FileInfo searchInfoByName(String value) {

		for (FileInfo info: this.digestListInfo) {
			if (info.getName().equals(value)) {
				return info;
			}
		}
		return null;
	}

	public FileInfo searchInfoByHash(String value) {

		for (FileInfo i: this.digestListInfo) {
			if ((this.digestType.equals("MD5") && value.equals(i.getHashMD5())) || 
					(this.digestType.equals("SHA1") && value.equals(i.getHashSHA1()))) {
				return i;
			}
		}
		return null;
	}

	public static String toHex(byte[] data) {

		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(0x0100 + (data[i] & 0x00FF)).substring(1);
			buffer.append((hex.length() < 2 ? "0" : "") + hex);
		}

		return buffer.toString();
	}

}
