package ftv.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import ftv.types.InvoiceFile;

public class PostCXML {

	public static String send(InvoiceFile file, String strUrl, String strProxy) throws Exception {
		
		String ret = "";

		System.out.println("init proxy 1.3a sec lv5");

		System.out.println("Here 1");
		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		// Neue URL, g�ltig ab 30.09.2017
		//String s = "https://service-2.ariba.com/service/transaction/cxml.asp";
		System.out.println("Using URL " + strUrl);
		System.out.println("Using proxy " + strProxy);
		

		System.setProperty("http.proxySet", "true");
		System.setProperty("https.proxyPort", "80");
		System.setProperty("https.proxyHost", strProxy);
		System.setProperty("https.proxySet", "true");
		System.setProperty("http.proxyPort", "80");
		System.setProperty("http.proxyHost", strProxy);

		URL url = new URL(strUrl);
		URLConnection urlconnection = url.openConnection();
		urlconnection.setDoOutput(true);
		//PrintWriter printwriter = new PrintWriter(urlconnection.getOutputStream());

		//FileReader filereader = new FileReader(file);
		String s2 = "";
		FileInputStream fileinputstream = new FileInputStream(file.getFile());
		boolean flag = false;
		System.out.println("Here 2");
		for (int i = 0; !flag; i++) {
			int j = fileinputstream.read();
			char c = (char) j;
			if (j == -1)
				flag = true;
			else
				s2 = s2 + c;
		}

		fileinputstream.close();
		System.out.println("Here 3");
		PrintWriter printwriter = new PrintWriter(urlconnection.getOutputStream()); 
		// System.out.print(s2);
	        printwriter.print(s2);
	        printwriter.flush();
	        printwriter.close();
		// prepare DOM
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document doc;

		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));

		String s3;
		// Raja : Added to write the output to a file
		File fileOut = new File("ResponsePostCXML " + file.getFile().getName() + ".txt");
		file.setResponseFile(fileOut);
		FileOutputStream streamOut = new FileOutputStream(fileOut);
		boolean sucFlag = false;
		try {
			PrintStream p = new PrintStream(streamOut);
			while ((s3 = bufferedreader.readLine()) != null) {
				p.print(s3);
				// System.out.println(s3);
				sucFlag = true;
			}
			if (sucFlag)
				System.out.println(" Successfully posted. See ResponsePostCXML.txt for the response ");
			p.close();
			streamOut.close();
		} catch (Exception ed) {
		}
		try {
        	factory = DocumentBuilderFactory.newInstance();
        	factory.setNamespaceAware(false);
        	factory.setFeature("http://xml.org/sax/features/namespaces",false);
        	factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",false);
        	factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
        	factory.setFeature("http://xml.org/sax/features/validation",false);
        	builder = factory.newDocumentBuilder();
        	doc = builder.parse(fileOut);
        	String status = doc.getElementsByTagName("Status").item(0).getTextContent();
        	file.setStatus(status);
        	String response = doc.getElementsByTagName("Status").item(0).getAttributes().getNamedItem("text").getTextContent();
        	file.setResponse(response);
        	if (response.equals("Accepted")) {
        		file.setSent(true);
        		file.setSelected(true);
        		
        	}
        	System.out.println(ret);
        	/*Desktop desktop = Desktop.getDesktop();
        	desktop.open(fileOut);*/
        	
        	 
        } catch (ParserConfigurationException pe) {
        	pe.printStackTrace();
        }
		// End of Adding < by raja >

		bufferedreader.close();
		return ret;
	}
	

}
