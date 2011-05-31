package org.com.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.snaplogic.common.Field;
import org.snaplogic.common.Record;
import org.snaplogic.snapi.ResDef;

public class FtpPut extends ComponentAPI {
	
	public static boolean ftpPut(String hostname, String username, String password, String SourceDir, String filename, String DestDir)
	{
		/**
		 * Testing FTP Put
		 */
		System.out.println("The ftpPut method has been called");
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
	
	
	
	@Override
	public void execute(Map<String, InputView> arg0,
			Map<String, OutputView> arg1) {
		
		//get connection cred's and hostname
		String serverUri = getStringPropertyValue("Connection");
		ResDef resdef = this.getLocalResourceObject(serverUri);
		String sourcedir = getStringPropertyValue("DirSource");		
		String targetdir = getStringPropertyValue("DirTarget");
		String filenamePropVal = getStringPropertyValue("File");
		
		
		String hostname = resdef.getPropertyValue("Host").toString();
		String username = resdef.getPropertyValue("Username").toString();
		String password = resdef.getPropertyValue("Password").toString();
		

		OutputView outputView = arg1.values().iterator().next(); 
		//Record outRec = outputView.createRecord();
		
		if (filenamePropVal != null && filenamePropVal != "")
		{
			boolean result = ftpPut(hostname, username, password, sourcedir, targetdir, filenamePropVal);
			info("Ftp Put of " + filenamePropVal + " = " + result);
			
			//write files to local FS
			Record outRec = outputView.createRecord();
			outRec.set("Name", targetdir+filenamePropVal);
			if(result)
				outRec.set("Success", "True");
			else
				outRec.set("Success", "False");
			//outRec.transferPassThroughFields(inputRec);

			outputView.writeRecord(outRec);
			
			// Complete output views
			for(OutputView ov: arg1.values()) {
				ov.completed();
			}
		}
		
		
		//Process input record
		InputView inView = null;
		if(arg0.size() > 0) {
			inView = arg0.values().iterator().next();
		}
		
		if(inView != null) {			
			while(true) {
				Record inputRec = inView.readRecord();
				if(inputRec == null) {
					break;
				}
				String file = inputRec.getString("FileName").toString();
				info(file);
				
				
				boolean result = ftpPut(hostname, username,	password, sourcedir, file, targetdir);

				//boolean result = ftpGet(hostname, username, password, sourcedir, targetdir, file);
				info("ftpget of " + file + " = " + result);
				
				//write files to local FS
				Record outRec = outputView.createRecord();
				outRec.set("Name", targetdir+file);
				if(result)
					outRec.set("Success", "True");
				else
					outRec.set("Success", "False");
				outRec.transferPassThroughFields(inputRec);

				outputView.writeRecord(outRec);
				

			}
	}
		
		
		// Complete output views
		for(OutputView ov: arg1.values()) {
			ov.completed();
		}
		
//		
//		//Process input record
//		InputView inView = arg0.values().iterator().next(); 
//		String file = inView.readRecord().getString("FileName").toString();
//		System.out.println((file));
//		//Read files from local FS
//		InputStream input = null;
//		boolean putResult = ftpPut(hostname, username,	password, sourcedir, file, targetdir);
//		
//		
//		outRec.set("FileName", file);
//		if (putResult)
//		{
//			outRec.set("Success", "True");
//		}
//		else
//		{
//			outRec.set("Success", "False");
//		}
//		
//		outputView.writeRecord(outRec);
//		outputView.completed();		
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
	    return "FTP Put";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return "This component Puts files from a directory onto the FTP Server";
	}

	@Override
	public Capabilities getCapabilities() {
	       return new Capabilities() {/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
	           put(Capability.INPUT_VIEW_LOWER_LIMIT, 1);
	           put(Capability.INPUT_VIEW_UPPER_LIMIT, 1);
	           put(Capability.OUTPUT_VIEW_LOWER_LIMIT, 1);
	           put(Capability.OUTPUT_VIEW_UPPER_LIMIT, 1);
	           put(Capability.ALLOW_PASS_THROUGH, false);
	       }};
	   }
	@Override
    public void createResourceTemplate() {
        setPropertyDef("Connection", new SimpleProp("Connection", SimplePropType.SnapString, "Connection Resource to FTP Server", true));
        setPropertyDef("DirSource", new SimpleProp("DirSource", SimplePropType.SnapString, "Directory to retrieve files from or blank for root", false));
        setPropertyDef("DirTarget", new SimpleProp("DirTarget", SimplePropType.SnapString, "Directory to saves files to or blank for root", false));
        setPropertyDef("File", new SimpleProp("File", SimplePropType.SnapString, "Put a single file to the FTP server on preview. All filenames from input view will be ignored", false));
   
        ArrayList<Field> fields = new ArrayList<Field>();
    	fields.add(new Field("FileName",Field.SnapFieldType.SnapString,"File name or * for all files in source directory"));
    	addRecordInputViewDef("Input",fields,"FTPGet Input",false);
        
        fields = new ArrayList<Field>();
      fields.add(new Field("Name",Field.SnapFieldType.SnapString,"File Name"));
      fields.add(new Field("Success",Field.SnapFieldType.SnapString,"FTP Put Result"));
    	addRecordOutputViewDef("Output",fields,"FTP Get Output",false);
	}

}
