package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.bind.DatatypeConverter;


public class CertifyService {

	private static CertifyService data = new CertifyService();

	public static CertifyService sharedInstance() {
		return data;
	}

	public X509Certificate getCertificate(String pathString) throws Exception {
		Path path = Paths.get(pathString);
		byte[] file = Files.readAllBytes(path);

		return this.createCertificate(file);
	}

	private X509Certificate createCertificate(byte[] certificateBytes) throws Exception {
		InputStream certificateInputStream = new ByteArrayInputStream(certificateBytes);
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

		return (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
	}

	public String certToString(X509Certificate cert) {
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

}
