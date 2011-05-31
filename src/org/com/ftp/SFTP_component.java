package org.com.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;

import com.jcraft.jsch.*;

import java.awt.*;
import javax.swing.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.snaplogic.cc.Capabilities;
import org.snaplogic.cc.Capability;
import org.snaplogic.cc.ComponentAPI;
import org.snaplogic.cc.InputView;
import org.snaplogic.cc.OutputView;
import org.snaplogic.cc.prop.SimpleProp;
import org.snaplogic.cc.prop.SimpleProp.SimplePropType;
import org.snaplogic.common.ComponentResourceErr;
import org.snaplogic.common.Field;
import org.snaplogic.common.Record;
import org.snaplogic.snapi.ResDef;

//import tests.SFTP.MyProgressMonitor;
import tests.SFTP.MyUserInfo;

public class SFTP_component extends ComponentAPI {
	
	
	
	  public static boolean sftp(String host, String password, String source, String destination, String filenamez){


		    try{
		      JSch jsch=new JSch();
		      
		      //host = "root@209.114.33.182";
		      System.out.println("Enter username@hostname: " + host);
		     
		      String user=host.substring(0, host.indexOf('@'));
		      host=host.substring(host.indexOf('@')+1);
		      int port=22;

		      Session session=jsch.getSession(user, host, port);

		      // username and password will be given via UserInfo interface.
		      UserInfo ui=new MyUserInfo();
		      session.setUserInfo(ui);
		      
		      System.out.println("User info before connecting" + session.getUserInfo().getPassphrase());
		      System.out.println(session.getUserInfo().getPassword());
		      System.out.println("yes or no! " + session.getUserInfo().promptYesNo("YesOrNo"));
		      session.setPassword(password);
		      session.connect();

		      Channel channel=session.openChannel("sftp");
		      channel.connect();
		      ChannelSftp c=(ChannelSftp)channel;
		   
		 		try
		 		{
					c.get(source, destination, null, ChannelSftp.OVERWRITE);
		 		}
		 		catch(SftpException e)
		 		{
		 			System.out.println(e.toString());
		 			return false;
		 		}

		    }
		    catch(Exception e)
		    {
		    	System.out.println("Something screwed up!!!");
		    	return false;
		    }
	
	return true;
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
	
	
	@Override
	public void execute(Map<String, InputView> inputVws,
			Map<String, OutputView> outputVws) {
		
		//get connection cred's and hostname
		//String serverUri = getStringPropertyValue("Connection");
		String login = getStringPropertyValue("Login");
		String password = getStringPropertyValue("Password");
		//ResDef resdef = this.getLocalResourceObject(serverUri);
		String sourcedir = getStringPropertyValue("DirSource");		
		String targetdir = getStringPropertyValue("DirTarget");
		String filenamePropVal = getStringPropertyValue("FileName");

		
		//Process input record
		InputView inView = null;
		if(inputVws.size() > 0) {
			inView = inputVws.values().iterator().next();
		}

		OutputView outputView = outputVws.values().iterator().next();
		OutputStream output = null;

		if(inView != null) {			
			while(true) {
				Record inputRec = inView.readRecord();
				if(inputRec == null) {
					break;
				}
				String file = inputRec.getString("FileName").toString();
				info(file);

				boolean result = false;
				if(filenamePropVal != null || filenamePropVal.equalsIgnoreCase(""))
				{
					result = sftp(login, password, sourcedir, targetdir, file);

				}
				else
				{
					result = sftp(login, password, sourcedir, targetdir, file);
				}
				
				info("ftpget of " + file + " = " + result);
				
				//write files to local FS
				Record outRec = outputView.createRecord();
				outRec.set("Name", file);
				if(result)
					outRec.set("GotFile", "True");
				else
					outRec.set("GotFile", "False");
				outRec.transferPassThroughFields(inputRec);

				outputView.writeRecord(outRec);
				
		
		// Complete output views
		for(OutputView ov: outputVws.values()) {
			ov.completed();
		}
			}
	}}
		

	@Override
	public String getAPIVersion() {
		// TODO Auto-generated method stub
		return "1.0";
	}

	@Override
	public String getComponentVersion() {
		// TODO Auto-generated method stub
		return "1.0";
	}
	
	@Override
	public String getLabel() {
	    return "SFTP Get";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return "This component gets files from a directory";
	}

	@Override
	public Capabilities getCapabilities() {
	       return new Capabilities() {/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
	           put(Capability.INPUT_VIEW_LOWER_LIMIT, 0);
	           put(Capability.INPUT_VIEW_UPPER_LIMIT, 1);
	           put(Capability.OUTPUT_VIEW_LOWER_LIMIT, 1);
	           put(Capability.OUTPUT_VIEW_UPPER_LIMIT, 1);
	           put(Capability.ALLOW_PASS_THROUGH, true);
	       }};
	   }
	@Override
    public void createResourceTemplate() {
        setPropertyDef("Login", new SimpleProp("Login", SimplePropType.SnapString, "username@host. Example: root@127.0.0.1", true));
        setPropertyDef("Password", new SimpleProp("Password", SimplePropType.SnapString, "Enter Password for ssh", true));
        setPropertyDef("DirSource", new SimpleProp("DirSource", SimplePropType.SnapString, "Directory to retrieve files from or blank for root", false));
        setPropertyDef("DirTarget", new SimpleProp("DirTarget", SimplePropType.SnapString, "Directory to saves files to or blank for root", false));
        setPropertyDef("FileName", new SimpleProp("FileName", SimplePropType.SnapString, "", false));
       
        ArrayList<Field> fields = new ArrayList<Field>();
    	fields.add(new Field("FileName",Field.SnapFieldType.SnapString,"File name or * for all files"));
    	addRecordInputViewDef("Input",fields,"FTPGet Input",true);
        
        fields = new ArrayList<Field>();
        fields.add(new Field("Name",Field.SnapFieldType.SnapString,"Object Name"));
        fields.add(new Field("Type",Field.SnapFieldType.SnapString,"Object Type"));
    	fields.add(new Field("User",Field.SnapFieldType.SnapString,"Username"));
    	fields.add(new Field("Group",Field.SnapFieldType.SnapString,"Group"));
    	fields.add(new Field("GotFile",Field.SnapFieldType.SnapString,"GotFile"));

    	fields.add(new Field("Size",Field.SnapFieldType.SnapString,"Size"));
    	fields.add(new Field("TimeStamp",Field.SnapFieldType.SnapString,"Time Stamp"));
    	addRecordOutputViewDef("Output",fields,"FTP Get Output",false);
	}
	
	@Override
	public void validate(ComponentResourceErr resdefError) {
		/**
		 * Check if no input and filename prop blank then error.
		 */
		
		
	}
}





