package com.rich.rouletterobot;

import com.rich.rouletterobot.application.RunState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.rich.rouletterobot.application.RunState.*;

@Slf4j
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
@ComponentScan("com.rich.rouletterobot")
public class RouletteRobotApplication {

	@Value("${roulette_lobby_page}")
	private String rouletteLobbyPage;

	private Robot agent;

	private RunState state;

	public static void main(String[] args) {
		log.info("Robot running");
		SpringApplication.run(RouletteRobotApplication.class, args);
	}

	@PostConstruct
	public void init() throws AWTException {
		System.setProperty("java.awt.headless", "false");
		agent = new Robot();
		state = INIT;
	}

	@Scheduled(fixedRate = 1000)
	public void run() {
		try {
			switch (state){
				case INIT: {
					openPage();
					state = BROWSER_OPENING;
					break;
				}
				case BROWSER_OPENING: {
					Thread.sleep(10000);
					openInspectElement();
					state = RUNNING;
					break;
				}
				case RUNNING: {
					log.info("login");
					break;
				}
			}
		}catch (Exception e){

		}
	}

	private void rigthClick(){
		agent.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		agent.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
	}

	private void openInspectElement() throws InterruptedException {
		agent.keyPress(KeyEvent.VK_SHIFT);
		Thread.sleep(200);
		agent.keyPress(KeyEvent.VK_CONTROL);
		Thread.sleep(200);
		agent.keyPress(KeyEvent.VK_C);
		Thread.sleep(500);
		agent.keyRelease(KeyEvent.VK_SHIFT);
		Thread.sleep(200);
		agent.keyRelease(KeyEvent.VK_CONTROL);
		Thread.sleep(200);
		agent.keyRelease(KeyEvent.VK_C);
	}

	private void openPage() throws URISyntaxException, IOException {
		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(rouletteLobbyPage));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}else{
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + rouletteLobbyPage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

