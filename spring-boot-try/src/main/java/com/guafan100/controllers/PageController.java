package com.guafan100.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.guafan100.model.StatusUpdate;
import com.guafan100.service.StatusUpdateService;

@Controller
public class PageController {

	@Autowired
	private StatusUpdateService statusUpdateService;
	
	@Value("${message.error.forbidden}")
	private String accessDeniedMessage;
	
	@RequestMapping("/")
	ModelAndView home(ModelAndView mav) {
		
		StatusUpdate statusUpdate = statusUpdateService.getLatest();
		
		mav.getModel().put("statusUpdate", statusUpdate);
		
		mav.setViewName("app.homepage");
		
		return mav;
	}
	
	@RequestMapping("/403")
	ModelAndView accessDenied(ModelAndView mav) {
		
		mav.getModel().put("message", accessDeniedMessage);
		
		mav.setViewName("app.message");
		
		return mav;
	}
	
	@RequestMapping("/about")
	String about() {
		return "app.about";
	}
	
	
}
