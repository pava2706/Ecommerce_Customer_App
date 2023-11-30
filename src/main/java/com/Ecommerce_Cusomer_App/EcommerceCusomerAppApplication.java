package com.Ecommerce_Cusomer_App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.Ecommerce_Cusomer_App.configuration.TwilioConfig;
import com.Ecommerce_Cusomer_App.entity.Admin;
import com.Ecommerce_Cusomer_App.service.AdminService;
import com.Ecommerce_Cusomer_App.utils.Constants.AdminStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.UserRole;
import com.twilio.Twilio;

@SpringBootApplication 
//@EnableSwagger2
public class EcommerceCusomerAppApplication implements CommandLineRunner {

	@Autowired
	private TwilioConfig twilioConfig;

	@Autowired
	private AdminService adminService;

	@jakarta.annotation.PostConstruct
	public void setUp() {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	}

	public static void main(String[] args) {
		SpringApplication.run(EcommerceCusomerAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Admin admin = this.adminService.getUserByEmailIdAndStatus("demo.admin@demo.com", AdminStatus.ACTIVE.value());

		if (admin == null) {

			// "Admin not found in system, so adding default admin"

			Admin user = new Admin();
			user.setEmail("demo.admin@demo.com");
			user.setPassword("123456");
			user.setStatus(AdminStatus.ACTIVE.value());
			user.setRole(UserRole.ROLE_ADMIN.value());
			this.adminService.addAdmin(user);
		}
	}

}
