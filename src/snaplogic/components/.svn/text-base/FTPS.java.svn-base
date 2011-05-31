package snaplogic.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
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

public class FTPS extends ComponentAPI {
	
	@Override
	public void execute(Map<String, InputView> arg0,
			Map<String, OutputView> arg1) {
		
		//get connection cred's and hostname
		String serverUri = getStringPropertyValue("Connection");
		ResDef resdef = this.getLocalResourceObject(serverUri);
		String sourcedir = getStringPropertyValue("DirSource");		
		String targetdir = getStringPropertyValue("DirTarget");
		FTPClient client = new FTPClient();
		//ftps
		FTPSClient ftpsclient = null;
		try {
			ftpsclient = new FTPSClient();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		FTPFile[] files = null;
		try {
			ftpsclient.connect(resdef.getPropertyValue("Host").toString());
			
			ftpsclient.login(resdef.getPropertyValue("Username").toString(), resdef.getPropertyValue("Password").toString());
			ftpsclient.enterLocalPassiveMode();
			System.out.println("Able to connect?");
			info(Boolean.toString(ftpsclient.isConnected()));
			if(sourcedir != null)
				files = ftpsclient.listFiles(sourcedir);
		
			else
				files = ftpsclient.listFiles();
			for(FTPFile f : files)
			{
				info(f.getName());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			info(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			info(e.getMessage());
		}
		//Process input record
		InputView inView = arg0.values().iterator().next();
		if(inView == null)
		{
			info("No input view");
		}
		info("Input View: "+inView.getName());
		String file = inView.readRecord().getString("FileName").toString();
		info(file);
	
		//write files to local FS
		OutputStream output = null;
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].getName().compareTo(file) == 0 || files[i].getName().compareTo("*") ==0)
			if(!files[i].getName().startsWith(".") && files[i].getType() != 1)
            {
				try {
					if(targetdir != null)
					output = new FileOutputStream(new File(targetdir + files[i].getName()));
					else
						output = new FileOutputStream(new File(files[i].getName()));
					ftpsclient.retrieveFile(files[i].getName(), output);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
            }  
		}
		
		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//Create output view
		
		OutputView outputView = arg1.values().iterator().next(); 
		Record outRec = outputView.createRecord();
		for(int i =0; i < files.length ;i++)
		{
			outRec.set("Name", files[i].getName());
			if(files[i].getType() == 0)
				outRec.set("Type", "Directory");
			else
				outRec.set("Type", "File");	
			outRec.set("User", files[i].getUser());
			outRec.set("Group", files[i].getGroup());
			outRec.set("Size", Long.toString(files[i].getSize()));
			outRec.set("TimeStamp", files[i].getTimestamp().getTime().toString());
			if(files[i].getName().compareTo(file) == 0)
			outputView.writeRecord(outRec);
		}
		outputView.completed();

		info(file);
		
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
	    return "FTPS Get";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return "Get file using FTPS client";
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
    
        ArrayList<Field> fields = new ArrayList<Field>();
    	fields.add(new Field("FileName",Field.SnapFieldType.SnapString,"File name or * for all files"));
    	addRecordInputViewDef("Input",fields,"FTPGet Input",false);
        
        fields = new ArrayList<Field>();
        fields.add(new Field("Name",Field.SnapFieldType.SnapString,"Object Name"));
        fields.add(new Field("Type",Field.SnapFieldType.SnapString,"Object Type"));
    	fields.add(new Field("User",Field.SnapFieldType.SnapString,"Username"));
    	fields.add(new Field("Group",Field.SnapFieldType.SnapString,"Group"));
    	fields.add(new Field("Size",Field.SnapFieldType.SnapString,"Size"));
    	fields.add(new Field("TimeStamp",Field.SnapFieldType.SnapString,"Time Stamp"));
    	addRecordOutputViewDef("Output",fields,"FTP Get Output",false);
	}

}
