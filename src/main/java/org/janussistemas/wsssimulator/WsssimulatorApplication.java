package org.janussistemas.wsssimulator;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WsssimulatorApplication {

	
	static ApplicationContext ctx;
	
	public static void main(String[] args) {
		ApplicationContext ctx=SpringApplication.run(WsssimulatorApplication.class, args);
		String[] beans = ctx.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
      	if(bean.contains("Repository") || bean.contains("Service")) {
      		System.out.println(bean);
     		}
      	}
        System.out.println("paso y salio");
		
		System.exit(0);
	}

}
