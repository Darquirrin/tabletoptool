/*
 * This software copyright by various authors including the RPTools.net development team, and licensed under the LGPL
 * Version 3 or, at your option, any later version.
 * 
 * Portions of this software were originally covered under the Apache Software License, Version 1.1 or Version 2.0.
 * 
 * See the file LICENSE elsewhere in this distribution for license details.
 */

package com.t3.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sbbi.upnp.devices.UPNPRootDevice;
import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

import org.apache.log4j.Logger;

import com.t3.client.AppUtil;
import com.t3.client.TabletopTool;
import com.t3.client.swing.MemoryStatusBar;
import com.t3.image.ImageUtil;

/**
 * Retrieves certain characteristics of the execution environment for the purposes of problem determination and
 * diagnostics. This class is invoked via the Help menu, Gather Debug Info... menu option.
 * 
 * @author frank
 */
public class SysInfo {
	private static final Logger log = Logger.getLogger(SysInfo.class);
	private static final DecimalFormat format = new DecimalFormat("#,##0.#");

	private static JDialog frame;
	private JTextArea infoTextArea;
	private JScrollPane scrollPane;
	private static String os = new String();
	private static String hostIP = new String();
	private static String routerIP = new String();

	public HashMap<String, Object> getSysInfo() {
		HashMap<String, Object> info = new HashMap<String, Object>();

		HashMap<String, Object> mt = new HashMap<String, Object>();

		HashMap<String, Object> java = new HashMap<String, Object>();
		HashMap<String, Object> locale = new HashMap<String, Object>();
		HashMap<String, Object> os = new HashMap<String, Object>();

		Properties p = System.getProperties();
		Map<String, String> env = System.getenv();

		// tabletoptool info
		mt.put("version", TabletopTool.getVersion());
//		mt.put("home", AppUtil.getAppHome()); // this  line crashes, I didnt figured out why
		mt.put("max mem avail", format.format(Runtime.getRuntime().maxMemory() / (1024 * 1024)));
		mt.put("max mem used", format.format(MemoryStatusBar.getInstance().getLargestMemoryUsed()));
		info.put("tabletoptool", mt);

		//java
		java.put("vendor", p.getProperty("java.vendor"));
		java.put("home", p.getProperty("java.home"));
		java.put("version", p.getProperty("java.version"));
		info.put("java", java);

		//locale
		Locale loc = Locale.getDefault();
		locale.put("country", loc.getDisplayCountry());
		locale.put("language", loc.getDisplayLanguage());
		locale.put("locale", loc.getDisplayName());
		locale.put("variant", loc.getDisplayVariant());
		info.put("locale", locale);

		//os
		os.put("name", p.getProperty("os.name"));
		os.put("version", p.getProperty("os.version"));
		os.put("arch", p.getProperty("os.arch"));
//		os.put("path",  (env.get("PATH") != null ? env.get("PATH") : p.getProperty("java.library.path")));
		info.put("os", os);

		return info;

	}

	private void appendInfo(String s) {
		infoTextArea.append(s + "\n");
	}

	private void getTabletopToolInfo(Properties p) {
		appendInfo("==== TabletopTool Information ====");
		appendInfo("TabletopTool Version: " + TabletopTool.getVersion());
		appendInfo("TabletopTool Home...: " + AppUtil.getAppHome());
		appendInfo("Max mem avail..: " + format.format(Runtime.getRuntime().maxMemory() / (1024 * 1024)) + "M");
		appendInfo("Max mem used...: " + format.format(MemoryStatusBar.getInstance().getLargestMemoryUsed()) + "M");
		for (String prop : p.stringPropertyNames()) {
			if (prop.startsWith("T3_")) {
				appendInfo("Custom Property: -D" + prop + "=" + p.getProperty(prop));
			}
		}
		appendInfo("");
	}

	private void getJavaInfo(Properties p) {
		appendInfo("==== Java Information ====");
		appendInfo("Java Vendor.: " + p.getProperty("java.vendor"));
		appendInfo("Java Home...: " + p.getProperty("java.home"));
		appendInfo("Java Version: " + p.getProperty("java.version"));
		appendInfo(getJavaVersionInfo());
	}

	private void getLocaleInfo() {
		appendInfo("\n==== Locale Information ====");
		Locale loc = Locale.getDefault();
		appendInfo("Country.: " + loc.getDisplayCountry());
		appendInfo("Language: " + loc.getDisplayLanguage());
		appendInfo("Locale..: " + loc.getDisplayName());
		appendInfo("Variant.: " + loc.getDisplayVariant());
		appendInfo("");
	}

	private void getOsInfo(Properties p) {
		Map<String, String> env = System.getenv();

		appendInfo("\n==== OS Information ====");
		appendInfo("OS Name........: " + p.getProperty("os.name"));
		appendInfo("OS Version.....: " + p.getProperty("os.version"));
		appendInfo("OS Architecture: " + p.getProperty("os.arch"));
		if (os.contains("Windows")) {
			appendInfo("Processor......: " + env.get("PROCESSOR_IDENTIFIER"));
		}
		appendInfo("PATH...........: " + (env.get("PATH") != null ? env.get("PATH") : p.getProperty("java.library.path")));
		appendInfo("Number of Procs: " + Runtime.getRuntime().availableProcessors());

		appendInfo("\n==== User Information ====");
		appendInfo("User Name: " + p.getProperty("user.name"));
		appendInfo("User Home: " + p.getProperty("user.home"));
		appendInfo("User Dir.: " + p.getProperty("user.dir"));
	}

	private void getInfo() {
		clearInfo();
		Properties p = System.getProperties();

		getTabletopToolInfo(p);
		getJavaInfo(p);
		getOsInfo(p);
		getNetworkInterfaces();
		getLocaleInfo();
		getDisplayInfo();
		getIGDs();
	}

	private void clearInfo() {
		this.infoTextArea.setText("");
	}

	private void getNetworkInterfaces() {
		appendInfo("\n==== Network Interfaces ====");
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netIf : Collections.list(nets)) {
				if ((os.contains("Mac")) || (netIf.isUp())) {
					appendInfo("Display Name..: " + netIf.getDisplayName());
					appendInfo("Interface Name: " + netIf.getName());
					Enumeration<InetAddress> inetAddresses = netIf.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {
						appendInfo("Address...: " + inetAddress.getHostAddress());
					}
					appendInfo("");
				}
			}

			try {
				hostIP = InetAddress.getLocalHost().getHostAddress();
				appendInfo("Host Address...: " + hostIP);
			} catch (UnknownHostException ex) {
				appendInfo("Host Address...: failed");
			}

			routerIP = getRouterIP();
			appendInfo("Default Gateway: " + routerIP);
		} catch (SocketException se) {
			appendInfo("*** Could net get list of network interfaces ***");
		}
	}

	private String getJavaVersionInfo() {
		String info = "Result of executing 'java -version':\n";
		try {
			Process result = Runtime.getRuntime().exec("java -version");

			BufferedReader output = new BufferedReader(new InputStreamReader(result.getErrorStream()));

			String line = output.readLine();
			while ((line = output.readLine()) != null)
				info = info.concat("............: " + line + "\n");
		} catch (Exception e) {
			TabletopTool.showError("Exception running 'java -version' to get version number information", e);
		}
		return info;
	}

	private void getDisplayInfo() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		appendInfo("==== Display Information ====");
		appendInfo("Number of Displays: " + gs.length);
// XXX Is there any way to report on the actual hardware?  nVidia and
// ATI/AMD cards sometimes have bugs in their proprietary drivers that
// cause headache for Java.  It would be nice to have that information.
// For Windows it would be good to see DirectX module names and version
// numbers, but can we obtain that from the JRE...?
		int i = 0;
		for (GraphicsDevice gd : gs) {
			i++;
			DisplayMode dm = gd.getDisplayMode();
			int bits = dm.getBitDepth();
			String depth = "(" + bits + ")";
			appendInfo("Display " + i + ": " + dm.getWidth() + "x" + dm.getHeight() + depth);
		}
	}

	private static String getRouterIP() {
		String oneOctet = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		String exIP = "(?:" + oneOctet + "\\.){3}" + oneOctet;

		Pattern pat = Pattern.compile("^\\s*(?:0\\.0\\.0\\.0\\s*){1,2}(" + exIP + ").*");
		try {
			Process proc = Runtime.getRuntime().exec("netstat -rn");
			InputStream inputstream = proc.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);

			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			String line;
			while ((line = bufferedreader.readLine()) != null) {
				Matcher m = pat.matcher(line);

				if (m.matches()) {
					return m.group(1);
				}
				if (line.startsWith("default")) {
					StringTokenizer st = new StringTokenizer(line);
					st.nextToken();
					return st.nextToken();
				}
			}
		} catch (IOException ex) {
			return "Failed";
		}
		return "Unknown";
	}

	private void getIGDs() {
		int discoveryTimeout = 5000;
		InternetGatewayDevice[] IGDs = null;

		appendInfo("\n==== Internet Gateway Devices ====");
		try {
			IGDs = InternetGatewayDevice.getDevices(discoveryTimeout);
		} catch (IOException ex) {
			appendInfo("\tError scanning for IGDs.");
		}

		if (IGDs != null)
			for (InternetGatewayDevice igd : IGDs) {
				UPNPRootDevice rootDev = igd.getIGDRootDevice();
				appendInfo("Device Name.: " + rootDev.getFriendlyName());
				appendInfo("Model Name..: " + rootDev.getModelName());
				appendInfo("Manufacturer: " + rootDev.getManufacturer());
				appendInfo("Model Number: " + rootDev.getModelNumber());
				appendInfo("Model Desc..: " + rootDev.getModelDescription());
				appendInfo("Firmware....: " + rootDev.getVendorFirmware());
				try {
					appendInfo("External IP.: " + igd.getExternalIPAddress());
				} catch (UPNPResponseException ex) {
					TabletopTool.showError("UPNPResponseException", ex);
				} catch (IOException ex) {
					TabletopTool.showError("IOException", ex);
				}
				appendInfo("");
			}
		else
			appendInfo("\tNo IGDs Found!");
	}

	private Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		this.infoTextArea = new JTextArea(5, 30);
		this.infoTextArea.setEditable(false);
		this.infoTextArea.setLineWrap(true);
		this.infoTextArea.setWrapStyleWord(true);
		this.infoTextArea.setFont(new Font("Monospaced", 0, 13));

		this.scrollPane = new JScrollPane(this.infoTextArea);
		this.scrollPane.setHorizontalScrollBarPolicy(31);
		this.scrollPane.setVerticalScrollBarPolicy(22);
		this.scrollPane.setViewportView(this.infoTextArea);

		contentPane.add(this.scrollPane, "Center");

		getInfo();
		return contentPane;
	}

	public static void createAndShowGUI(String title) {
		if (frame != null) {
			frame.dispose(); // This is so that the memory characteristics are queried each time this frame is displayed.
			frame = null;
		}
		frame = new JDialog(TabletopTool.getFrame(), title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		SysInfo demo = new SysInfo();
		// frame.setJMenuBar(demo.createMenuBar());
		frame.setContentPane(demo.createContentPane());
		try {
			Image img = ImageUtil.getImage("com/t3/client/image/tabletoptool_icon.png");
			frame.setIconImage(img);
//			URL url = TabletopTool.class.getClassLoader().getResource("com/t3/client/image/tabletoptool_icon.png");
//			Toolkit tk = Toolkit.getDefaultToolkit();
//			if (url != null) {
//				Image img = tk.createImage(url);
//				frame.setIconImage(img);
//			}
		} catch (Exception ex) {
			TabletopTool.showError("While retrieving TabletopTool logo image?!", ex);
		}
		frame.setSize(550, 640);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
}