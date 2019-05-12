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
		

	public static void main(String[] args) {
		
		final String COMPROMISED_IPS_URL_LIST = "http://rules.emergingthreats.net/blockrules/compromised-ips.txt";		
		HashSet<String> ipset = new HashSet<>();
		String ip;
		int col=0;
		//==============================================================================
		try {
			URL comromised_ip_ulr = new URL (COMPROMISED_IPS_URL_LIST);
			InputStreamReader is = new InputStreamReader(comromised_ip_ulr.openStream());
		    Scanner ips = new Scanner (	new BufferedReader( is ) );
		    System.out.println("Reading ips from site: " + COMPROMISED_IPS_URL_LIST+"\n");
		    
		    while (ips.hasNext()) {
		    	ip = ips.nextLine();
		    	ipset.add(ip);
		    	
		    	System.out.print(ip +"\t");
		    	if (col++ == 5) {
		    		col=0;
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
		
		//Check if console is available
		if (System.console()==null) {
			System.exit(3);
		}
		//check if the OS is Linux
		String OS = System.getProperty("os.name");
		if (OS!=null) {
			if (!OS.startsWith("Linux")) {
				System.out.println("This program is intended for Linux");
				System.exit(1);
			}
		}else {
			System.out.println("Error getting the OS");
			System.exit(2);
		}

		//first argument is the log file
		if (args.length!=2) {
			System.out.println("Error Argument Missing");
			System.out.println("bloclip logfile outputfile");
		    System.exit(4);
		}
		String logFile = args[0];
		String outFile = args[1];
	    Path logPath = Paths.get(logFile);
	    Path outPath = Paths.get(outFile);
	    
		if (!Files.exists(logPath)) {
			System.out.println("File not found: "+logFile);
			System.exit(5);
		}
		
		System.out.println("Reading log...");
		try {
			
			List<String> lines= Files.readAllLines(logPath);
			Pattern pattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
			
			for (String line:lines) {
				Matcher match = pattern.matcher(line);
				if (match.find()) {
					ip = match.group(0);
					ipset.add(ip);
				}					
			}
			
			System.out.print("ips found: ");
			System.out.println(ipset.size());
			if (Files.exists(outPath)) Files.delete(outPath);
			Files.write(outPath, ipset, StandardOpenOption.CREATE_NEW);
			
			
			for (String ips:ipset) {
				System.out.print(ips +"\t");
		    	if (col++ == 5) {
		    		col=0;
		    		System.out.println();
		    	}				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}