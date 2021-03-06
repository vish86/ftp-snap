package snaplogic.components;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.snaplogic.cc.Capabilities;
import org.snaplogic.cc.Capability;
import org.snaplogic.cc.ComponentAPI;
import org.snaplogic.cc.InputView;
import org.snaplogic.cc.OutputView;
import org.snaplogic.cc.prop.SimpleProp;
import org.snaplogic.cc.prop.SimpleProp.SimplePropType;
import org.snaplogic.snapi.PropertyConstraint;
import org.snaplogic.snapi.PropertyConstraint.Type;


public class FtpConnect extends ComponentAPI {
	
	private FTPClient cli;

	/**
	 * @param args
	 */
	public void setCli(FTPClient cli) {
		this.cli = cli;
	}

	public FTPClient getCli() {
		return cli;
	}

	@Override
	public void execute(Map<String, InputView> arg0,
			Map<String, OutputView> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAPIVersion() {
		// TODO Auto-generated method stub
		return "1.1";
	}

	@Override
	public String getComponentVersion() {
		// TODO Auto-generated method stub
		return "1.0";
	}
	
	@Override
	public String getLabel() {
	    return "FTP Connector";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return "This component connects to an FTP Server";
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
	           put(Capability.OUTPUT_VIEW_LOWER_LIMIT, 0);
	           put(Capability.OUTPUT_VIEW_UPPER_LIMIT, 0);
	           put(Capability.ALLOW_PASS_THROUGH, false);
	       }};
	   }
	@Override
    public void createResourceTemplate() {
		PropertyConstraint passwdConstraint = new PropertyConstraint(Type.OBFUSCATE, 0);
        setPropertyDef("Host", new SimpleProp("Host", SimplePropType.SnapString, "Hostname of FTP Server", true));
        setPropertyDef("Username", new SimpleProp("Username", SimplePropType.SnapString, "Username", true));
        setPropertyDef("Password", new SimpleProp("Password", SimplePropType.SnapString, "Password", passwdConstraint, true));
    }


}
