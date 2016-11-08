package cs.sii.bot.passive;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.reflect.TypeToken;

import cs.sii.bot.active.CryptoAuth;
import cs.sii.config.bot.Engine;
import cs.sii.connection.AsyncRequest;
import cs.sii.connection.NetworkService;
import cs.sii.model.Conversions;
import cs.sii.model.Pairs;
import javassist.bytecode.ByteArray;
import javassist.compiler.SymbolTable;

@Service
public class Bot {

@Autowired
private Engine engineBot;
	
@Autowired
private NetworkService networkService;

@Autowired
private AsyncRequest request;

@Autowired
private CryptoAuth auth;
	
public Bot(){}


public void initializeBot(){

//Non necessario 
//networkService.selectIp();
//networkService.firstConnectToMockServerDns();
	Long q=(Long.MAX_VALUE/4)*3;
	byte l=q.to
	
System.out.println(l);
System.out.println(Long.toBinaryString(l));
System.out.println(Long.toHexString(l));
System.out.println(l.toString());
System.out.println(Long.valueOf(l));
System.out.println(String.valueOf(l));
System.out.println(String.valueOf(Long.toBinaryString(l)));
System.out.println(String.valueOf(Long.toHexString(l)));
System.out.println(String.valueOf(l.toString()));
//System.out.println(Long.parseLong(s));


//byte [] c=((Long.MAX_VALUE/4)*3);
//System.out.println(c);
//String s=new String(Conversions.longToBytes((Long.MAX_VALUE/4)*3));
//Byte c=new Byte(s);
//
//System.out.println(s);
//
//if(s.equals(c))
//System.out.println(s+" Uguali "+c);
//if(challengeToCommandConquer())
//	System.out.println("Bot is Ready");
//else
//	System.out.println("Bot not Ready, authentication failed");
//

}


private boolean challengeToCommandConquer(){
	
	System.out.println(networkService.getCommandConquerIps().getIPList().get(0));
		
	Pairs<Long,Long> challenge=request.getChallengeFromCommandAndConquer(networkService.getCommandConquerIps().getIPList().get(0));
	
	System.out.println(challenge.getValue1());
	System.out.println(challenge.getValue2());
	if(challenge!=null){
		//TODO richiesta di challenge
		String key=auth.generateStringKey(challenge.getValue2());
		String hashMac=auth.generateHmac(challenge.getValue1(), auth.generateSecretKey(key));
		
		String response=request.getResponseFromCommandAndConquer(networkService.getCommandConquerIps().toString(),challenge.getValue1(), challenge.getValue2(), hashMac);
		
		System.out.println("La risposta del CeC: "+response);
		
		//if()//TODO gestisco la risposta del C&C e genero hmac
	}
	

	
	
	return true;
}



}
