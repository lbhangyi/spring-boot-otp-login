package com.codingwithtashi.springsecurityjwt;

import com.codingwithtashi.springsecurityjwt.config.TwilioConfig;
import com.codingwithtashi.springsecurityjwt.model.User;
import com.codingwithtashi.springsecurityjwt.repository.UserRepository;
import com.twilio.Twilio;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition( info = @Info(title = "Spring boot otp authentication",version = "1.0.0"))
@Log4j2
public class SpringSecurityJwtApplication {
	@Autowired
	private UserRepository repository;
	@Autowired
	private TwilioConfig twilioConfig;

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}


	@PostConstruct
	public void initTwilio(){
		Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
	}


	@PostConstruct
	public void initUsers() {
		User user1 = new User();
		user1.setUserName( "7022752477");
		User user2 = new User();
		user2.setUserName( "777229511");
		List<User> users=List.of(user1,user2);
		repository.saveAll(users);
		log.debug("We have initialized {} users", users.size());
	}
}
