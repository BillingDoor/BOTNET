package cs.sii.domain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs.sii.bot.active.BotAuth;
import cs.sii.service.crypto.CryptoUtils;

@Service
public class FileUtil {
	
	@Autowired
	CryptoUtils cryptoUtils;
	
	private String filename="nfo.dll";
	private String codec="UTF-8";
	
	
	public void writeObjToFile(String filename, ArrayList<Object> data) {
		if (filename == "") filename= this.filename;

		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, codec);
			for(Object obj:data){
				writer.println(obj.toString());
			}
		    writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}	
	



public ArrayList<String> readObjFromFile(String filename){
	if (filename == "") filename= this.filename;
	BufferedReader br;
	ArrayList<String> data=new ArrayList<String>();
	try {
		 br = new BufferedReader(new FileReader(filename));
		 String rd;
		 while((rd=br.readLine())!=null){
			 data.add(rd);
		 }
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	return data;
	}
	
	
	
//public void encodeObjToFile(String data) throws FileNotFoundException, UnsupportedEncodingException{
//	PrintWriter writer = new PrintWriter(filename, codec);
//		writer.println(cryptoUtils.encryptAES(data));
//    writer.close();
//	
//}



	
	
}
