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
import org.springframework.stereotype.Component;

import cs.sii.bot.active.CryptoAuth;

@Component
public class FileUtil {
	
	@Autowired
	CryptoAuth auth;
	
	private String filename="nfo.dll";
	private String codec="UTF-8";
	
public void encodeToFile(ArrayList<Object> data) throws FileNotFoundException, UnsupportedEncodingException{
	PrintWriter writer = new PrintWriter(filename, codec);
	for(Object obj:data){
		writer.println(auth.encrypt(obj.toString()));
	}
    writer.close();
	
}
	

public ArrayList<String> decodeFromFile(){
	BufferedReader br;
	ArrayList<String> data=new ArrayList<String>();
	try {
		 br = new BufferedReader(new FileReader(filename));
		 String rd;
		 while((rd=br.readLine())!=null){
			 data.add(auth.decrypt(rd));
		 }
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return data;
	}
	
	
}
