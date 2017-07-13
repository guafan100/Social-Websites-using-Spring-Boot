package com.guafan100.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.guafan100.model.StatusUpdate;
import com.guafan100.service.StatusUpdateService;

@Controller
public class StatusUpdateController {

	@Autowired
	private StatusUpdateService statusUpdateService;

	@RequestMapping(value = "/editstatus", method = RequestMethod.GET)
	ModelAndView editStatus(ModelAndView mav, @RequestParam(name = "id") Long id) {

		StatusUpdate statusUpdate = statusUpdateService.get(id);
		
		mav.getModel().put("statusUpdate", statusUpdate);

		mav.setViewName("app.editStatus");

		return mav;
	}
	
	@RequestMapping(value = "/editstatus", method = RequestMethod.POST)
	ModelAndView editStatus(ModelAndView mav, @Valid StatusUpdate statusUpdate, BindingResult result) {
		
		mav.setViewName("app.editStatus");
		
		if(!result.hasErrors()) {
			statusUpdateService.save(statusUpdate);
			mav.setViewName("redirect:/viewstatus");
		}
		
		return mav;
	}

	@RequestMapping(value = "/deletestatus", method = RequestMethod.GET)
	ModelAndView deleteStatus(ModelAndView mav, @RequestParam(name = "id") Long id) {

		statusUpdateService.delete(id);

		mav.setViewName("redirect:/viewstatus");

		return mav;
	}

	@RequestMapping(value = "/viewstatus", method = RequestMethod.GET)
	ModelAndView viewStatus(ModelAndView mav, @RequestParam(name = "p", defaultValue = "1") int pageNumber) {

		Page<StatusUpdate> page = statusUpdateService.getPage(pageNumber);

		mav.getModel().put("page", page);

		mav.setViewName("app.viewStatus");

		return mav;
	}

	@RequestMapping(value = "/addstatus", method = RequestMethod.GET)
	ModelAndView addStatus(ModelAndView mav, @ModelAttribute("statusUpdate") StatusUpdate statusUpdate) {

		mav.setViewName("app.addStatus");

		StatusUpdate latestStatusUpdate = statusUpdateService.getLatest();

		mav.getModel().put("latestStatusUpdate", latestStatusUpdate);

		return mav;
	}

	@RequestMapping(value = "/addstatus", method = RequestMethod.POST)
	ModelAndView addStatus(ModelAndView mav, @Valid StatusUpdate statusUpdate, BindingResult result) {

		mav.setViewName("app.addStatus");

		if (!result.hasErrors()) {
			statusUpdateService.save(statusUpdate);
			mav.getModel().put("statusUpdate", new StatusUpdate());
			mav.setViewName("redirect:/viewstatus");
		}

		StatusUpdate latestStatusUpdate = statusUpdateService.getLatest();
		mav.getModel().put("latestStatusUpdate", latestStatusUpdate);

		return mav;
	}
}
