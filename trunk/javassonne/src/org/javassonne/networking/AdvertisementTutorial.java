package org.javassonne.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Logger;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Attributable;
import net.jxta.document.Document;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;

/**
 * Simple Advertisement Tutorial creates a advertisment describing a system
 * <p/>
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot;?&gt;
 * &lt;!DOCTYPE jxta:System&gt;
 * &lt;jxta:System xmlns:jxta=&quot;http://jxta.org&quot;&gt;
 * &lt;id&gt;id&lt;/id&gt;
 * &lt;name&gt;Device Name&lt;/name&gt;
 * &lt;ip&gt;ip address&lt;/ip&gt;
 * &lt;hwarch&gt;x86&lt;/hwarch&gt;
 * &lt;hwvendor&gt;Sun MicroSystems&lt;/hwvendor&gt;
 * &lt;OSName&gt;&lt;/OSName&gt;
 * &lt;OSVer&gt;&lt;/OSVer&gt;
 * &lt;osarch&gt;&lt;/osarch&gt;
 * &lt;sw&gt;&lt;/sw&gt;
 * &lt;/jxta:System&gt;
 * </pre>
 */
public class AdvertisementTutorial extends Advertisement implements Comparable,
		Cloneable, Serializable {
	private String hwarch;
	private String hwvendor;
	private ID id = ID.nullID;
	private String ip;
	private String name;
	private String osname;
	private String osversion;
	private String osarch;
	private String inventory;
	private final static Logger LOG = Logger
			.getLogger(AdvertisementTutorial.class.getName());
	private final static String OSNameTag = "OSName";
	private final static String OSVersionTag = "OSVer";
	private final static String OSarchTag = "osarch";
	private final static String hwarchTag = "hwarch";
	private final static String hwvendorTag = "hwvendor";
	private final static String idTag = "ID";

	private final static String ipTag = "ip";
	private final static String nameTag = "name";
	private final static String swTag = "sw";
	/**
	 * Indexable fields. Advertisements must define the indexables, in order to
	 * properly index and retrieve these advertisements locally and on the
	 * network
	 */
	private final static String[] fields = { idTag, nameTag, hwarchTag };

	/**
	 * Default Constructor
	 */
	public AdvertisementTutorial() {
	}

	/**
	 * Construct from a StructuredDocument
	 * 
	 * @param root
	 *            Root element
	 */
	public AdvertisementTutorial(Element root) {
		TextElement doc = (TextElement) root;
		if (!getAdvertisementType().equals(doc.getName())) {
			throw new IllegalArgumentException("Could not construct : "
					+ getClass().getName() + "from doc containing a "
					+ doc.getName());
		}
		initialize(doc);
	}

	/**
	 * Construct a doc from InputStream
	 * 
	 * @param stream
	 *            the underlying input stream.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public AdvertisementTutorial(InputStream stream) throws IOException {
		StructuredTextDocument doc = (StructuredTextDocument) StructuredDocumentFactory
				.newStructuredDocument(MimeMediaType.XMLUTF8, stream);
		initialize(doc);
	}

	/**
	 * Sets the hWArch attribute of the AdvertisementTutorial object
	 * 
	 * @param hwarch
	 *            The new hWArch value
	 */
	public void setHWArch(String hwarch) {
		this.hwarch = hwarch;
	}

	/**
	 * Sets the OSArch attribute of the AdvertisementTutorial object JXSE 2.5
	 * Programmers Guide : The Basics 40
	 * 
	 * @param osarch
	 *            The new hWArch value
	 */
	public void setOSArch(String osarch) {
		this.osarch = osarch;
	}

	/**
	 * Sets the hWVendor attribute of the AdvertisementTutorial object
	 * 
	 * @param hwvendor
	 *            The new hWVendor value
	 */
	public void setHWVendor(String hwvendor) {
		this.hwvendor = hwvendor;
	}

	/**
	 * sets the unique id
	 * 
	 * @param id
	 *            The id
	 */
	public void setID(ID id) {
		this.id = (id == null ? null : id);
	}

	/**
	 * Sets the iP attribute of the AdvertisementTutorial object
	 * 
	 * @param ip
	 *            The new iP value
	 */
	public void setIP(String ip) {
		this.ip = ip;
	}

	/**
	 * Sets the name attribute of the AdvertisementTutorial object
	 * 
	 * @param name
	 *            The new name value
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the oSName attribute of the AdvertisementTutorial object
	 * 
	 * @param osname
	 *            The new oSName value
	 */
	public void setOSName(String osname) {
		this.osname = osname;
	}

	/**
	 * Sets the oSVersion attribute of the AdvertisementTutorial object
	 * 
	 * @param osversion
	 *            The new oSVersion value
	 */

	public void setOSVersion(String osversion) {
		this.osversion = osversion;
	}

	/**
	 * Sets the SWInventory attribute of the AdvertisementTutorial object
	 * 
	 * @param inventory
	 *            the software inventory of the system
	 */
	public void setSWInventory(String inventory) {
		this.inventory = inventory;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param asMimeType
	 *            Document encoding
	 * @return The document value
	 */
	@Override
	public Document getDocument(MimeMediaType asMimeType) {
		StructuredDocument adv = StructuredDocumentFactory
				.newStructuredDocument(asMimeType, getAdvertisementType());
		if (adv instanceof Attributable) {
			((Attributable) adv).addAttribute("xmlns:jxta", "http://jxta.org");
		}
		Element e;
		e = adv.createElement(idTag, getID().toString());
		adv.appendChild(e);
		e = adv.createElement(nameTag, getName().trim());
		adv.appendChild(e);
		e = adv.createElement(OSNameTag, getOSName().trim());
		adv.appendChild(e);
		e = adv.createElement(OSVersionTag, getOSVersion().trim());
		adv.appendChild(e);
		e = adv.createElement(OSarchTag, getOSArch().trim());
		adv.appendChild(e);
		e = adv.createElement(ipTag, getIP().trim());
		adv.appendChild(e);
		e = adv.createElement(hwarchTag, getHWArch().trim());
		adv.appendChild(e);
		e = adv.createElement(hwvendorTag, getHWVendor().trim());
		adv.appendChild(e);
		e = adv.createElement(swTag, getSWInventory().trim());
		adv.appendChild(e);
		return adv;
	}

	/**
	 * Gets the hWArch attribute of the AdvertisementTutorial object
	 * 
	 * @return The hWArch value
	 */
	public String getHWArch() {
		return hwarch;
	}

	/**
	 * Gets the OSArch attribute of the AdvertisementTutorial object
	 * 
	 * @return The OSArch value
	 */
	public String getOSArch() {
		return osarch;
	}

	/**
	 * Gets the hWVendor attribute of the AdvertisementTutorial object
	 * 
	 * @return The hWVendor value
	 */
	public String getHWVendor() {
		return hwvendor;
	}

	/**
	 * returns the id of the device
	 * 
	 * @return ID the device id
	 */
	@Override
	public ID getID() {
		return (id == null ? null : id);
	}

	/**
	 * Gets the IP attribute of the AdvertisementTutorial object
	 * 
	 * @return The IP value
	 */
	public String getIP() {
		return ip;
	}

	/**
	 * Gets the name attribute of the AdvertisementTutorial object
	 * 
	 * @return The name value
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the OSName attribute of the AdvertisementTutorial object
	 * 
	 * @return The OSName value
	 */
	public String getOSName() {
		return osname;
	}

	/**
	 * JXSE 2.5 Programmers Guide : The Basics 43 Gets the Software Inventory
	 * text element
	 * 
	 * @return The Inventory value
	 */
	public String getSWInventory() {
		if (inventory == null) {
			inventory = "";
		}
		return inventory;
	}

	/**
	 * Gets the OSVersion attribute of the AdvertisementTutorial object
	 * 
	 * @return The OSVersion value
	 */
	public String getOSVersion() {
		return osversion;
	}

	/**
	 * Process an individual element from the document.
	 * 
	 * @param elem
	 *            the element to be processed.
	 * @return true if the element was recognized, otherwise false.
	 */
	protected boolean handleElement(TextElement elem) {
		if (elem.getName().equals(idTag)) {
			try {
				URI id = new URI(elem.getTextValue());
				setID(IDFactory.fromURI(id));
			} catch (URISyntaxException badID) {
				throw new IllegalArgumentException(
						"unknown ID format in advertisement: "
								+ elem.getTextValue());
			} catch (ClassCastException badID) {
				throw new IllegalArgumentException(
						"Id is not a known id type: " + elem.getTextValue());
			}
			return true;
		}
		if (elem.getName().equals(nameTag)) {
			setName(elem.getTextValue());
			return true;
		}
		if (elem.getName().equals(OSNameTag)) {
			setOSName(elem.getTextValue());
			return true;
		}
		if (elem.getName().equals(OSVersionTag)) {
			setOSVersion(elem.getTextValue());
			return true;
		}
		if (elem.getName().equals(OSarchTag)) {
			setOSArch(elem.getTextValue());
			return true;

		}
		if (elem.getName().equals(ipTag)) {
			setIP(elem.getTextValue());
			return true;
		}
		if (elem.getName().equals(hwarchTag)) {
			setHWArch(elem.getTextValue());
			return true;
		}
		if (elem.getName().equals(hwvendorTag)) {
			setHWVendor(elem.getTextValue());
			return true;
		}
		if (elem.getName().equals(swTag)) {
			setSWInventory(elem.getTextValue());
			return true;
		}
		// element was not handled
		return false;
	}

	/**
	 * Intialize a System advertisement from a portion of a structured document.
	 * 
	 * @param root
	 *            document root
	 */
	protected void initialize(Element root) {
		if (!TextElement.class.isInstance(root)) {
			throw new IllegalArgumentException(getClass().getName()
					+ " only supports TextElement");
		}
		TextElement doc = (TextElement) root;
		if (!doc.getName().equals(getAdvertisementType())) {
			throw new IllegalArgumentException("Could not construct : "
					+ getClass().getName() + "from doc containing a "
					+ doc.getName());
		}
		Enumeration elements = doc.getChildren();
		while (elements.hasMoreElements()) {
			TextElement elem = (TextElement) elements.nextElement();
			if (!handleElement(elem)) {
				LOG.warning("Unhandleded element \'" + elem.getName()
						+ "\' in " + doc.getName());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getIndexFields() {
		return fields;
	}

	/**
	 * {@inheritDoc} JXSE 2.5 Programmers Guide : The Basics 45
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AdvertisementTutorial) {
			AdvertisementTutorial adv = (AdvertisementTutorial) obj;
			return getID().equals(adv.getID());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object other) {
		return getID().toString().compareTo(other.toString());
	}

	/**
	 * All messages have a type (in xml this is &#0033;doctype) which identifies
	 * the message
	 * 
	 * @return String "jxta:AdvertisementTutorial"
	 */
	public static String getAdvertisementType() {
		return "jxta:AdvertisementTutorial";
	}

	/**
	 * Instantiator
	 */
	public static class Instantiator implements
			AdvertisementFactory.Instantiator {
		/**
		 * Returns the identifying type of this Advertisement.
		 * 
		 * @return String the type of advertisement
		 */
		public String getAdvertisementType() {
			return AdvertisementTutorial.getAdvertisementType();
		}

		/**
		 * Constructs an instance of <CODE>Advertisement</CODE> matching the
		 * type specified by the <CODE>advertisementType</CODE> parameter.
		 * 
		 * @return The instance of <CODE>Advertisement</CODE> or null if it
		 *         could not be created.
		 */
		public Advertisement newInstance() {
			return new AdvertisementTutorial();
		}

		/**
		 * Constructs an instance of <CODE>Advertisement</CODE> matching the
		 * JXSE 2.5 Programmers Guide : The Basics 46 type specified by the
		 * <CODE>advertisementType</CODE> parameter.
		 * 
		 * @param root
		 *            Specifies a portion of a StructuredDocument which will be
		 *            converted into an Advertisement.
		 * @return The instance of <CODE>Advertisement</CODE> or null if it
		 *         could not be created.
		 */
		public Advertisement newInstance(net.jxta.document.Element root) {
			return new AdvertisementTutorial(root);
		}
	}

	/**
	 * Main method
	 * 
	 * @param args
	 *            command line arguments. None defined
	 */
	public static void main(String args[]) {
		// The following step is required and only need to be done once,
		// without this step the AdvertisementFactory has no means of
		// associating an advertisement name space with the proper obect
		// in this cast the AdvertisementTutorial
		AdvertisementFactory.registerAdvertisementInstance(
				AdvertisementTutorial.getAdvertisementType(),
				new AdvertisementTutorial.Instantiator());
		AdvertisementTutorial advTutorial = new AdvertisementTutorial();
		advTutorial.setID(ID.nullID);
		advTutorial.setName("AdvertisementTutorial");
		try {
			advTutorial.setIP(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException ignored) {
			// ignored
		}
		advTutorial.setOSName(System.getProperty("os.name"));
		advTutorial.setOSVersion(System.getProperty("os.version"));
		advTutorial.setOSArch(System.getProperty("os.arch"));
		advTutorial.setHWArch(System.getProperty("HOSTTYPE", System
				.getProperty("os.arch")));
		advTutorial.setHWVendor(System.getProperty("java.vm.vendor"));
		System.out.println(advTutorial.toString());
	}
}
