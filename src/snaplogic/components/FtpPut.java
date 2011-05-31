package snaplogic.components;

import java.io.File;
import java.io.FileInputStream;
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
	
	@Override
	public void execute(Map<String, InputView> arg0,
			Map<String, OutputView> arg1) {
		
		//get connection cred's and hostname
		String serverUri = getStringPropertyValue("Connection");
		ResDef resdef = this.getLocalResourceObject(serverUri);
		String sourcedir = getStringPropertyValue("DirSource");		
		String targetdir = getStringPropertyValue("DirTarget");
		FTPClient client = new FTPClient();
		try {
			client.connect(resdef.getPropertyValue("Host").toString());
			client.login(resdef.getPropertyValue("Username").toString(), resdef.getPropertyValue("Password").toString());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Process input record
		InputView inView = arg0.values().iterator().next(); 
		String file = inView.readRecord().getString("FileName").toString();
		info(file);
		//Read files from local FS
		InputStream input = null;
		try{
			if(file.compareTo("*") ==0)
			{
				File dir = null;
				if(sourcedir ==null)
					{
					dir = new File(System.getProperty("user.dir"));
					sourcedir = System.getProperty("user.dir") + "//";
					}
				else
					dir = new File(sourcedir);
				//File dir = new File(sourcedir);
				File[] files = dir.listFiles();
				info(dir.toString());
				for (int i = 0; i < files.length; i++)  {
					if (files[i].isFile())
					{
						info(files[i].toString());
						input = new FileInputStream(new File(sourcedir + files[i].getName()));
						
						if(targetdir != null)
						{
							boolean dir_ex = client.changeWorkingDirectory(targetdir);
							if(dir_ex)
							client.storeFile(files[i].getName(), input);
							else
							{
								client.mkd(targetdir);
								client.changeWorkingDirectory(targetdir);
								client.storeFile(files[i].getName(), input);
							}
						}
						else
						{
							client.storeFile(files[i].getName(), input);
						}
					}
				}
			}
			else
			{
				input = new FileInputStream(new File(sourcedir+file));
				info(file.toString());
				if(targetdir != null)
				{
					boolean dir_ex = client.changeWorkingDirectory(targetdir);
					if(dir_ex)
					client.storeFile(file, input);
					else
					{
						client.mkd(targetdir);
						client.changeWorkingDirectory(targetdir);
						client.storeFile(file, input);
					}
				}
				else
				{
					client.storeFile(file, input);
				}
			}

		}
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		/*
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
//Create output view
		
		OutputView outputView = arg1.values().iterator().next(); 
		Record outRec = outputView.createRecord();
		FTPFile[] fil = null;
		try {
			fil = client.listFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i =0; i < fil.length ;i++)
		{
			outRec.set("Name", fil[i].getName());
			if(fil[i].getType() == 0)
				outRec.set("Type", "Directory");
			else
				outRec.set("Type", "File");	
			outRec.set("User", fil[i].getUser());
			outRec.set("Group", fil[i].getGroup());
			outRec.set("Size", Long.toString(fil[i].getSize()));
			outRec.set("TimeStamp", fil[i].getTimestamp().getTime().toString());
			if(fil[i].getName().compareTo(file) == 0)
			outputView.writeRecord(outRec);
		}
		outputView.completed();
		
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
    
        ArrayList<Field> fields = new ArrayList<Field>();
    	fields.add(new Field("FileName",Field.SnapFieldType.SnapString,"File name or * for all files in source directory"));
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
