package org.com.ftp;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.snaplogic.cc.Capabilities;
import org.snaplogic.cc.Capability;
import org.snaplogic.cc.ComponentAPI;
import org.snaplogic.cc.InputView;
import org.snaplogic.cc.OutputView;
import org.snaplogic.cc.prop.SimpleProp;
import org.snaplogic.cc.prop.SimpleProp.SimplePropType;
import org.snaplogic.common.Field;
import org.snaplogic.common.Record;
import org.snaplogic.snapi.PropertyConstraint;
import org.snaplogic.snapi.ResDef;

public class FtpDir extends ComponentAPI {

	public FTPFile[] FtpGetDir(String hostname, String username, String password, String DestDir) 
	{
		FTPClient client = new FTPClient();			
		ArrayList<Date> ModTimes = new ArrayList();
		ArrayList<String> FileNames = new ArrayList();

		FTPFile[] files = null;
		try {
			// Connect to the FTP server
			client.connect(hostname);			
			client.enterLocalPassiveMode();
			client.login(username, password);

			info(("Connected to the server: " + Boolean.toString(client.isConnected())));
			if(DestDir != null && DestDir != "")
			{
				/**
				 * This is the expected way to list the directory. Noticed that this does not always work,
				 * eg. on Edens and Avant's FTP server. If the list files returns an empty array, change working directory 
				 * and then do a list files with no arguments.
				 */
//				debug("Destination dir" + DestDir);
//				files = client.listFiles(DestDir);

				/**
				 * Special case
				 */
				debug("Special case: change working directory and then listfiles() instead of listfiles(dir)");
				debug("Changing working dir to..... " + DestDir);
				client.changeWorkingDirectory(DestDir);
				files = client.listFiles();
			}
			else
			{
				debug("Root dir");
				files = client.listFiles();		
			}

			for(FTPFile f : files)
			{
//				try
//				{
//					FileNames.add(f.getName());				
//					ModTimes.add(f.getTimestamp().getTime());
//					debug("Displaying all filenames");
//					debug("Name: " + f.getName() + ", Size: " + f.getSize() + ", Modified Time" + f.getTimestamp().getTime());
//
//				} catch(Exception e)
//				{}
			}
//
//			Collections.sort(ModTimes);
//			info("The latest file is: "+ FileNames.get(ModTimes.size()-1)+ " and was modified on:" + ModTimes.get(ModTimes.size()-1) );

			//info ("Logging out.... Success + " + client.logout());
			//client.disconnect();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			info("IO Exception - Could not display the specified directory");

			//info((e.getMessage()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			info("IO Exception - Could not display the specified directory");
			e.printStackTrace();
			//info((e.getMessage()));			
		} catch(Exception oe) {
			info("IO Exception - Could not display the specified directory");

			oe.printStackTrace();
		}
		
	
		return files;
	}
	
	
	@Override
	public void execute(Map<String, InputView> arg0,
			Map<String, OutputView> arg1) {
		String serverUri = getStringPropertyValue("Connection");
		ResDef resdef = this.getLocalResourceObject(serverUri);
		String pathname = getStringPropertyValue("Dir");
		String hostname = resdef.getPropertyValue("Host").toString();
		String username = resdef.getPropertyValue("Username").toString();
		String password = resdef.getPropertyValue("Password").toString();

		FTPFile[]  files = FtpGetDir(hostname, username, password, pathname);
		try
		{
			OutputView outputView = arg1.values().iterator().next(); 
			Record outRec = outputView.createRecord();
			for(int i =0; i < files.length ;i++)
			{
				outRec.set("Name", files[i].getName());
				if(files[i].isDirectory())
				{
					outRec.set("Type", "Directory");
				}
				else if (files[i].isFile())
				{
					outRec.set("Type", "File");
				}
				else
				{
					outRec.set("Type", "Unknown");
				}
				outRec.set("User", files[i].getUser());
				outRec.set("Group", files[i].getGroup());
				outRec.set("Size", Long.toString(files[i].getSize()));
				outRec.set("TimeStamp", files[i].getTimestamp().getTime().toString());
				outputView.writeRecord(outRec);
			}
			// Complete output view
			outputView.completed();
		}
		catch(Exception e){	
			info(e.getMessage());
		}
	}

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
	    return "FTP Directory Browser";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return "This component lists a directories content";
	}

	@Override
	public Capabilities getCapabilities() {
	       return new Capabilities() {/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
	           put(Capability.INPUT_VIEW_LOWER_LIMIT, 0);
	           put(Capability.INPUT_VIEW_UPPER_LIMIT, 0);
	           put(Capability.OUTPUT_VIEW_LOWER_LIMIT, 1);
	           put(Capability.OUTPUT_VIEW_UPPER_LIMIT, 1);
	           put(Capability.ALLOW_PASS_THROUGH, false);
	       }};
	   }
	@Override
    public void createResourceTemplate() {
		String[] options = {"YES", "NO"};
        setPropertyDef("Connection", new SimpleProp("Connection", SimplePropType.SnapString, "Connection Resource to FTP Server", true));
        setPropertyDef("Dir", new SimpleProp("Dir", SimplePropType.SnapString, "Directory to browse or blank for root", false));
        //TODO: Add limit on the number of files
        //setPropertyDef("NumberOfFiles", new SimpleProp("NumberOfFiles", SimplePropType.SnapString, "Number of filenames to retrieve or blank for all files", false));
        setPropertyDef("SortFiles", new SimpleProp("SortFiles", SimplePropType.SnapString, "Sort files by last modified date", new PropertyConstraint(PropertyConstraint.Type.LOV, options), true));
                
        ArrayList<Field> fields = new ArrayList<Field>();
        fields.add(new Field("Name",Field.SnapFieldType.SnapString,"Object Name"));
        fields.add(new Field("Type",Field.SnapFieldType.SnapString,"Object Type"));
    	fields.add(new Field("User",Field.SnapFieldType.SnapString,"Username"));
    	fields.add(new Field("Group",Field.SnapFieldType.SnapString,"Group"));
    	fields.add(new Field("Size",Field.SnapFieldType.SnapString,"Size"));
    	fields.add(new Field("TimeStamp",Field.SnapFieldType.SnapString,"Time Stamp"));
    	addRecordOutputViewDef("Output",fields,"FTP Dir Output",false);
	}

}
