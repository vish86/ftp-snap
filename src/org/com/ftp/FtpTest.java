package org.com.ftp;

//File imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

//FTP imports
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import org.apache.commons.net.ftp.FTPSClient;
import org.snaplogic.cc.InputView;
import org.snaplogic.cc.OutputView;
import org.snaplogic.common.Record;
//Scheduler imports
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FtpTest extends TimerTask {

	public static void Timertest()
	{
		Timer timer = new Timer();
	    Calendar date = Calendar.getInstance();
	    date.set(
	      Calendar.DAY_OF_WEEK,
	      Calendar.FRIDAY
	    );
	    date.set(Calendar.HOUR, 11);
	    date.set(Calendar.MINUTE, 21);
	    date.set(Calendar.SECOND, 0);
	    date.set(Calendar.MILLISECOND, 0);
	    // Schedule to run every Sunday in midnight
	    timer.schedule(
	      new FtpTest(),
	      date.getTime(),
	      1000 * 60 * 60 * 24 * 7
	    );
	  }
	
	
	public static void FtpGetDirTest(String hostname, String username, String password, String DestDir) 
	{
		FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);

		FTPClient client = new FTPClient();	
		client.configure(conf);
		ArrayList<Date> ModTimes = new ArrayList();
		ArrayList<String> FileNames = new ArrayList();
		
		FTPFile[] files = null;
		try {
			
			client.connect(hostname);
			client.enterLocalPassiveMode();
			//client.enterRemotePassiveMode();
			int pasv = client.pasv();
			System.out.println("Pasv: " + pasv);
			

			client.login(username, password);
			System.out.println((Boolean.toString(client.isConnected())));
			System.out.println("PWD:" + client.printWorkingDirectory());
			files = client.listFiles();
			
			client.changeWorkingDirectory("/EDEN2NEXUS/Vendors/");
			files = client.listFiles();
					
			
			for(FTPFile f : files)
			{
				try
				{
//				FileNames.add(f.getName());
				System.out.println("Name: " + f.getName());
//				System.out.println("Size: " + f.getSize());	
//			
//					System.out.println("Modified Time: " + f.getTimestamp().getTime());
//					ModTimes.add(f.getTimestamp().getTime());
				} catch(Exception e)
				{}
				//System.out.println("\n");
			}
			
			//Collections.sort(ModTimes);
			
			//System.out.println("The latest file is: "+ FileNames.get(ModTimes.size()-1)+ " and was modifed on:" + ModTimes.get(ModTimes.size()-1) );
					
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println((e.getMessage()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println((e.getMessage()));
		}
	}
	
	
	public static boolean ftpPut(String hostname, String username, String password, String SourceDir, String filename, String DestDir)
	{
		/**
		 * Testing FTP Put
		 */
		FTPClient client = new FTPClient();
		try {
			client.connect(hostname);
			
			client.login(username, password);
			client.enterLocalPassiveMode();
			System.out.println((Boolean.toString(client.isConnected())));
		}
		catch(Exception e){}
		InputStream input = null;
		String file = SourceDir+ filename;
		System.out.println(file);
		//String filename = "testboxcsv.csv";
		try {
			input = new FileInputStream(new File(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println((file.toString()));
		boolean dir_ex = false;
		boolean result = false;
		try {
			if(DestDir != null && DestDir != "")
				dir_ex = client.changeWorkingDirectory(DestDir);
			else
				dir_ex = client.changeWorkingDirectory("/");
			result = client.storeFile(filename, input);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public static boolean ftpGet(String hostname, String username, String password, String SourceDir, String DestDir, String filename)
	{
		boolean got = false;		
		FTPClient client = new FTPClient();		
		
		FTPFile[] files = null;
		try {
			client.connect(hostname);
			
			client.login(username, password);
			client.enterLocalPassiveMode();
			System.out.println(Boolean.toString(client.isConnected()));
			//info(Boolean.toString(client.isConnected()));
			if(SourceDir != null)
				files = client.listFiles(SourceDir);		
			else
				files = client.listFiles();
			
			for(FTPFile f : files)
			{
				//info(f.getName());
				System.out.println(f.getName());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//info(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//info(e.getMessage());
		}
				//write files to local FS
				OutputStream output = null;
				
							try {
								if (DestDir != null)
								{
									// check if the targetdir has a / appended or not
									if (!DestDir.endsWith("/"))
										DestDir += "/";

									// check if the folder exists, if not, create it
									File targetFolder = new File(DestDir);
									if (!targetFolder.exists())
										targetFolder.mkdir();

									output = new FileOutputStream(new File(DestDir + filename));
								}
								else
									output = new FileOutputStream(new File(filename));
								
								if(SourceDir != null)
								{
									if (!SourceDir.endsWith("/"))
										SourceDir += "/";
									got = client.retrieveFile(SourceDir + filename, output);
								}
								else
								{
									got = client.retrieveFile(filename, output);
								}
									
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
							return got;
	}  
				
	public static boolean ftpDelete(String hostname, String username, String password, String SourceDir, String DestDir, String filename)
	{
		boolean deleted = false;		
		FTPClient client = new FTPClient();		
		
		try {
			client.connect(hostname);
			
			client.login(username, password);
			client.enterLocalPassiveMode();
			System.out.println(Boolean.toString(client.isConnected()));
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//info(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//info(e.getMessage());
		}
				try
				{
								
								if(SourceDir != null)
								{
									if (!SourceDir.endsWith("/"))
										SourceDir += "/";
									deleted = client.deleteFile(SourceDir + filename);
								}
								else
								{
									deleted = client.deleteFile(filename);
								}
									
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
							return deleted;
	}  
					
	
	
	
	public static void main(String args[])
	{
		//FtpGetDirTest("ftp.operative.com", "pandora", "3FpNEny4", "/Finance/");	
		//FtpGetDirTest("ftpnexus.edensandavant.com", "edensftp", "N3xusF7pEd3n", "/EDEN2NEXUS/Vendors/");
		FtpGetDirTest("ftpnexus.edensandavant.com", "edensftp", "N3xusF7pEd3n", "/EDEN2NEXUS/Vendors/");

		//System.out.println("Deleted? " + ftpDelete("ftp.operative.com", "pandora", "3FpNEny4", "/snaplogic/", "/Applications/snaplogic/3.1.0.14275PE/", "testboxcsv.csv"));
		//Timertest();
		//boolean putOrNot = ftpPut("ftp.operative.com", "pandora", "3FpNEny4", "/users/vishwanath/Desktop/", "testboxcsv.csv", null);
		//System.out.println("Result = "+ putOrNot);

	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}
