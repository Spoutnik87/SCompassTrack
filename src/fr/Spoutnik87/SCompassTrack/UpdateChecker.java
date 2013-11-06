package fr.Spoutnik87.SCompassTrack;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker {
	private Main p;
	private URL url;
	private String version = "";
	private String link = "";
	
	public UpdateChecker(Main p, String url) {
		this.p = p;
		
		try {
			this.url = new URL(url);
		} catch (Exception ex) {}
	}
	
	public boolean checkUpdate() {
		try {
			InputStream input = this.url.openConnection().getInputStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			Node lf = doc.getElementsByTagName("item").item(0);
			NodeList c = lf.getChildNodes();
			version = c.item(1).getTextContent().replaceAll("[a-zA-Z]", "");
			link = c.item(3).getTextContent();
			if (!p.getDescription().getVersion().equals(version)) {
				return true;
			}
		} catch (Exception ex) {}
		return false;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getLink() {
		return this.link;
	}
}
