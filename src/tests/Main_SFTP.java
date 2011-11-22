package tests;

import java.io.File;
import java.util.Vector;

import tests.SFTP.MyUserInfo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class Main_SFTP {

	public static void main(String args[])
	{
//		boolean result = sftp("root@209.114.33.182", "vish-dbT5RQ1exh0", "/opt/vish/sftp/", ".", "blah.txt");
//		System.out.println("Got from secure ftp? " + result);
		
		boolean ftpgot = sftpGet("209.114.33.182", "root", "vish-dbT5RQ1exh0", "22", "/opt/vish/sftp/", "/Applications/snaplogic/", "blah.txt");
		//boolean ftpgot = sftpBrowse("209.114.33.182", "root", "vish-dbT5RQ1exh0", "22", "/opt/vish/sftp/");

		System.out.println("FTP got = " + ftpgot);
		
		//boolean success = createDir("/Applications/snaplogic/newfolder");
		
	//	System.out.println(".....crating directories" + success);
		
	}
	
	
	
	
	public static boolean createDir(String path)
	{
		
		  boolean success = false;
		  try{
	
		  File temp = new File(path);
		  if (temp.exists())
		  {
			  System.out.println("Directory exists");
		  }
		  else
		  {
			  System.out.println("Directory does not exist");
		  }
			  
		  success = (new File(path)).mkdirs();
		  if (success) {
		  System.out.println("Directories: " 
		   + path + " created");
		  }

		  }catch (Exception e){//Catch exception if any
		  System.err.println("Error: " + e.getMessage());
		  }
		  
		  return success;
	}
	
	
	public static boolean sftpBrowse(String host, String username, String password, String port, String SourceDir){

	       java.util.Vector v=new java.util.Vector();

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
					//c.get(SourceDir + filename, DestDir, null, ChannelSftp.OVERWRITE);
		 			//v = c.ls(SourceDir);
		 			    java.util.Vector vv=c.ls(SourceDir);
		 			    if(vv!=null){
		 			      for(int ii=0; ii<vv.size(); ii++){
//		 				out.println(vv.elementAt(ii).toString());

		 		                Object obj=vv.elementAt(ii);
		 		                if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
		 		                   //System.out.println("Long name" + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getLongname().);
		 		                   System.out.println("Filename:" +  ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getFilename());
		 		                   System.out.println("Is Directory" + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getAttrs().isDir());
		 		                   System.out.println("Modified Time" + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getAttrs().getMtimeString());
		 		                   System.out.println("Permissions" + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getAttrs().getPermissionsString());
		 		                   System.out.println("Size:" + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getAttrs().getSize());
		 		                   System.out.println("Time" + ((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getAttrs().getAtimeString());
		 		                   

		 		                }

		 			      }
		 			    
		 			    }
		 		}
		 		catch(SftpException e)
		 		{
		 			System.out.println(e.toString());
		 			return false;
		 		}
		      //System.out.println(("Connected Successfully!!!"));
		    }
		    catch(Exception e)
		    {
		    	System.out.println("Something screwed up!!!");
		    	e.printStackTrace();
		    	return false;
		    }
	
		  
		    
	return true;
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
		   
		      System.out.println("PWD" + c.pwd());
		      c.cd("/opt/");
		      System.out.println("PWD" + c.pwd());
		      c.cd("vish/");
		      System.out.println("PWD" + c.pwd());


		     	      
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
