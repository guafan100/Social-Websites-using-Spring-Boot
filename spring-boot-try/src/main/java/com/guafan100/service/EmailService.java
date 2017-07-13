package com.guafan100.service;

import java.util.Date;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${mail.enable}")
	private boolean enable;
	
	private void send(MimeMessagePreparator preparator) {
		if(enable) {
			mailSender.send(preparator);
		}
	}
	
	public void sendVerificationEmail(String emailAddress) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<HTML>");
		sb.append("<p>Hello there, this is a<strong> verification email</strong></p>");
		sb.append("</HTML>");
		
//		HashMap<String, Object> model = new HashMap<>();
//		model.put("test", "This is some dynamic data");
//		
//		String contents = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/com/guafan100/velocity/verifyemail.vm", "UTF-8", model);
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				
				message.setTo(emailAddress);
				message.setFrom(new InternetAddress("no-reply@gmail.com"));
				message.setSubject("Please Verify Your Email Address");
				message.setSentDate(new Date());
				
				message.setText(sb.toString(), true);
				
			}
			
		};
		
		send(preparator);
	}
}
