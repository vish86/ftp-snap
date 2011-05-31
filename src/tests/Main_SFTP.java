package tests;

import tests.SFTP.MyUserInfo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class Main_SFTP {

	public static void main(String args[])
	{
//		boolean result = sftp("root@209.114.33.182", "vish-dbT5RQ1exh0", "/opt/vish/sftp/", ".", "blah.txt");
//		System.out.println("Got from secure ftp? " + result);
		
		boolean ftpgot = sftpGet("209.114.33.182", "root", "vish-dbT5RQ1exh0", "22", "/opt/vish/sftp/", "/Applications/snaplogic", "blah.txt");
		System.out.println("FTP GOT? " + ftpgot);
	}
	
	
	
	
	
	 public static boolean sftpGet(String host, String username, String password, String port, String SourceDir, String DestDir, String filename){


		 try{
		      JSch jsch=new JSch();

		      int default_port=22;
		      
		      if(port != null && !port.equalsIgnoreCase(""))
		      {
		    	  	default_port = Integer.parseInt(port);
		      }

		      Session session=jsch.getSession(username, host, default_port);

		      // username and password will be given via UserInfo interface.
		      UserInfo ui=new MyUserInfo();
		      session.setUserInfo(ui);

		      session.setPassword(password);
		      session.connect();

		      Channel channel=session.openChannel("sftp");
		      channel.connect();
		      ChannelSftp c=(ChannelSftp)channel;
		   
		 		try
		 		{
					c.get(SourceDir + filename, DestDir, null, ChannelSftp.OVERWRITE);
		 		}
		 		catch(SftpException e)
		 		{
		 			System.out.println(e.toString());
		 			return false;
		 		}
		      System.out.println(("Connected Successfully!!!"));
		    }
		    catch(Exception e)
		    {
		    	System.out.println("Something screwed up!!!");
		    	e.printStackTrace();
		    	return false;
		    }
	
	return true;
	  }
	
	
	
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
					c.get(source + filenamez, destination, null, ChannelSftp.OVERWRITE);
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
		    	e.printStackTrace();
		    	return false;
		    }
	
	return true;
	  }
	
} 
