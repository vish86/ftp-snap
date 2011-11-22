package org.com.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
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
import org.snaplogic.common.ComponentResourceErr;
import org.snaplogic.common.Field;
import org.snaplogic.common.Record;
import org.snaplogic.snapi.ResDef;

public class FtpDelete extends ComponentAPI {
	
	public boolean ftpDelete(String hostname, String username, String password, String SourceDir, String filename)
	{
		boolean deleted = false;		
		FTPClient client = new FTPClient();		
		
		//Connect to the FTP server in local passive mode
		try {
			client.connect(hostname);
			client.login(username, password);
			client.enterLocalPassiveMode();
			info(Boolean.toString(client.isConnected()));

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
		
	
	
	@Override
	public void execute(Map<String, InputView> inputVws,
			Map<String, OutputView> outputVws) {
		
		boolean result = false;
		
		//get connection cred's and hostname
		String serverUri = getStringPropertyValue("Connection");
		ResDef resdef = this.getLocalResourceObject(serverUri);
		String sourcedir = getStringPropertyValue("DirSource");	
		// check if the sourcedir has a / appended or not
		if (!sourcedir.endsWith("/"))
			sourcedir += "/";
		
		String filenamePropVal = getStringPropertyValue("File");

		String hostname = resdef.getPropertyValue("Host").toString();
		String username = resdef.getPropertyValue("Username").toString();
		String password = resdef.getPropertyValue("Password").toString();

		OutputView outputView = outputVws.values().iterator().next();
		OutputStream output = null;
		

		
		//Process input record
		InputView inView = null;
		if(inputVws.size() > 0) {
			inView = inputVws.values().iterator().next();
		}

		if(inView != null) {			
			while(true) {
				Record inputRec = inView.readRecord();
				if(inputRec == null) {
					break;
				}
				String file = inputRec.getString("FileName").toString();
								
				result = ftpDelete(hostname, username, password, sourcedir, file);
				info("Ftp Delete of " + file + " = " + result);
				
				//write files to local FS
				Record outRec = outputView.createRecord();
				outRec.set("Name", sourcedir+file);
				if(result)
					outRec.set("Success", "True");
				else
					outRec.set("Success", "False");
				outRec.transferPassThroughFields(inputRec);

				outputView.writeRecord(outRec);
	
			}
	}

		
		
		// If filename property has a value, delete that file
		if (filenamePropVal != null && filenamePropVal != "")
		{
			result = ftpDelete(hostname, username, password, sourcedir, filenamePropVal);
			info("Ftp Delete of " + filenamePropVal + " = " + result);
			
			//write files to local FS
			Record outRec = outputView.createRecord();
			outRec.set("Name", sourcedir+filenamePropVal);
			if(result)
				outRec.set("Success", "True");
			else
				outRec.set("Success", "False");
			outputView.writeRecord(outRec);
			
//			// Complete output views
//			for(OutputView ov: outputVws.values()) {
//				ov.completed();
//			}
		}
		
		// Complete output views
		for(OutputView ov: outputVws.values()) {
			ov.completed();
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
	    return "FTP Delete";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return "This component deletes files from a directory";
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
	           put(Capability.ALLOW_PASS_THROUGH, false);
	       }};
	   }
	@Override
    public void createResourceTemplate() {
        setPropertyDef("Connection", new SimpleProp("Connection", SimplePropType.SnapString, "Connection Resource to FTP Server", true));
        setPropertyDef("DirSource", new SimpleProp("DirSource", SimplePropType.SnapString, "Directory to delete files from or blank for root", false));
        setPropertyDef("File", new SimpleProp("File", SimplePropType.SnapString, "Deletes a single file from the FTP server on Preview. All filenames from input view will be ignored ", false));
       
        ArrayList<Field> fields = new ArrayList<Field>();
    	fields.add(new Field("FileName",Field.SnapFieldType.SnapString,"File name or * for all files"));
    	addRecordInputViewDef("Input",fields,"FTP Delete Input",true);
        
        fields = new ArrayList<Field>();
        fields.add(new Field("Name",Field.SnapFieldType.SnapString,"Object Name"));
        fields.add(new Field("Success",Field.SnapFieldType.SnapString,"True if file has been deleted successfully, False if not "));
    	
    	addRecordOutputViewDef("Output",fields,"FTP Delete Output",false);
	}
	
	@Override
	public void validate(ComponentResourceErr resdefError) {
		/**
		 * TODO: Check if no input and filename prop blank then error.
		 */
				
	}
}





