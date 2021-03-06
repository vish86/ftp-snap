package snaplogic.components;

import java.io.IOException;
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

public class FtpDir extends ComponentAPI {
	
	private FTPClient client;
	
	@Override
	public void execute(Map<String, InputView> arg0,
			Map<String, OutputView> arg1) {
		String serverUri = getStringPropertyValue("Connection");
		ResDef resdef = this.getLocalResourceObject(serverUri);
		String pathname = getStringPropertyValue("Dir");
		client = new FTPClient();
		FTPFile[] files = null;

			try {
				client.connect(resdef.getPropertyValue("Host").toString());
				client.login(resdef.getPropertyValue("Username").toString(), resdef.getPropertyValue("Password").toString());
				if(pathname != null)
					files = client.listFiles(pathname);
				else
					files = client.listFiles();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
try
{
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
			outputView.writeRecord(outRec);
		}
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
        setPropertyDef("Connection", new SimpleProp("Connection", SimplePropType.SnapString, "Connection Resource to FTP Server", true));
        setPropertyDef("Dir", new SimpleProp("Dir", SimplePropType.SnapString, "Directory to browse or blank for root", false));
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
