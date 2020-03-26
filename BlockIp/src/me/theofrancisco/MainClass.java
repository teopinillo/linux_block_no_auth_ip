package me.theofrancisco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {

	private final static String defaultLog = "//var//log//auth.log";
	private static String logFile;
	private static String outFile;
	private static Path logPath;
	private static Path outPath;

	public static void main(String[] args) {

		final String COMPROMISED_IPS_URL_LIST = "http://rules.emergingthreats.net/blockrules/compromised-ips.txt";
		HashSet<String> ipset = new HashSet<>();
		String ip;
		int col = 0;

		// check if the OS is Linux
		String OS = System.getProperty("os.name");
		if (OS != null) {
			if (!OS.startsWith("Linux")) {
				System.out.println("This program is intended for Linux");
				System.exit(1);
			}
		} else {
			System.out.println("Error getting the OS");
			System.exit(2);
		}

		// first argument is the log file
		if (args.length == 1) {
			System.out.println("Using log file by default: " + defaultLog);
			System.out.println("blockip logfile outputfile");
			logFile = defaultLog;
			outFile = args[0];
		}
		if (args.length == 2) {
			logFile = args[0];
			outFile = args[1];
		}

		try {
			logPath = Paths.get(logFile);
			outPath = Paths.get(outFile);

		if (!Files.exists(logPath)) {
			System.out.println("File not found: " + logFile);
			System.exit(5);
		}
		}catch (Exception e) {
			System.out.println("I/O Error while assigning Path.");
			System.out.println ("log file: " + logFile);
			System.out.println("output file: "+ outFile);
			System.out.println (e.getMessage());
			System.exit(6);
		}
		// ==============================================================================
		try {
			URL comromised_ip_ulr = new URL(COMPROMISED_IPS_URL_LIST);
			InputStreamReader is = new InputStreamReader(comromised_ip_ulr.openStream());
			Scanner ips = new Scanner(new BufferedReader(is));
			System.out.println("Reading ips from site: " + COMPROMISED_IPS_URL_LIST + "\n");

			while (ips.hasNext()) {
				ip = ips.nextLine();
				ipset.add(ip);

				System.out.print(ip + "\t");
				if (col++ == 5) {
					col = 0;
					System.out.println();
				}
			}
			is.close();
			ips.close();
		} catch (MalformedURLException e1) {
			System.out.println(e1.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		// Check if console is available
		if (System.console() == null) {
			System.out.println("No system console found!");
			System.exit(3);
		}

		System.out.println("Reading log...");
		try {

			List<String> lines = Files.readAllLines(logPath);
			Pattern pattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");

			for (String line : lines) {
				Matcher match = pattern.matcher(line);
				if (match.find()) {
					ip = match.group(0);
					ipset.add(ip);
				}
			}

			System.out.print("ips found: ");
			System.out.println(ipset.size());
			if (Files.exists(outPath))
				Files.delete(outPath);
			Files.write(outPath, ipset, StandardOpenOption.CREATE_NEW);

			for (String ips : ipset) {
				System.out.print(ips + "\t");
				if (col++ == 5) {
					col = 0;
					System.out.println();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
