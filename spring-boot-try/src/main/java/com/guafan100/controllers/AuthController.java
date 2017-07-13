package com.guafan100.controllers;

import java.io.FileNotFoundException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.guafan100.model.SiteUser;
import com.guafan100.service.EmailService;
import com.guafan100.service.UserService;

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@RequestMapping("/login")
	String admin() {
		return "app.login";
	}

	@RequestMapping("/verifyemail")
	String verifyEmail() {
		return "app.verifyemail";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	ModelAndView register(ModelAndView mav) throws FileNotFoundException {

		SiteUser user = new SiteUser();

		mav.getModel().put("user", user);
		mav.setViewName("app.register");

		return mav;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	ModelAndView register(ModelAndView mav, @ModelAttribute(value = "user") @Valid SiteUser user,
			BindingResult result) {

		mav.setViewName("app.register");

		if (!result.hasErrors()) {
			userService.register(user);

			emailService.sendVerificationEmail(user.getEmail());

			mav.setViewName("redirect:/verifyemail");
		}

		return mav;
	}

}
