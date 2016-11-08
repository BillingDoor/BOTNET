package cs.sii.bot;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import cs.sii.bot.passive.Bot;

@SpringBootApplication
@ComponentScan("cs.sii")
@EnableAsync
public class Application {

	@Autowired
	private Bot bot;
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
    }


   
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

        	
        	bot.initializeBot();
//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }


        };
    }   

 
     
}