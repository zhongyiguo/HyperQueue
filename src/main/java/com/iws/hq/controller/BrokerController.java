package com.iws.hq.controller;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iws.hq.service.BrokerService;
import com.iws.hq.util.APIResponse;
import com.iws.hq.util.JsonUtil;
import com.iws.hq.util.ResponseStatus;

@Controller
public class BrokerController {
	@Autowired
	private BrokerService brokerService;

	// Ajax call: Retrieve to get the message by topic
	@RequestMapping(value = "/{topic}")
	public @ResponseBody
	SimpleEntry<String, Set<String>> getByTopicName(
			@PathVariable(value = "topic") String topic,
			Model model, RedirectAttributes redirectAttrs) {
		String message="";
		try {
			message = brokerService.getMessageByTopic(topic, "sessionID");
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		APIResponse<String> response = new APIResponse<String>();
		response.setResponseObj(message);
		response.setStatus(ResponseStatus.SUCCESS);
		// second phase
		SimpleEntry<String, Set<String>> res;

		if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
			Set<String> set = new HashSet<String>();
			set.add(JsonUtil.getSimpleObjAsJSON(response.getResponseObj()));
			res = new SimpleEntry<String, Set<String>>(response.getStatus()
					.getValue(), set);
		} else {
			res = new SimpleEntry<String, Set<String>>(response.getStatus()
					.getValue(), response.getErrors());
		}
		return res;
	}


	//Aggregator page
	@RequestMapping(value = "/aggregator/{topic}", method = RequestMethod.GET)
	public String getNewsByTopic(@PathVariable(value = "topic") String topic,
			Locale locale, Model model) {
		String message="";
		try {
			message = brokerService.getMessageByTopic(topic);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("t", topic);
		model.addAttribute("m", message);
		System.out.println("initail messaage:"+message);
		return "aggregator";
	}

	//Post action
	@RequestMapping(value = "/{topic}", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	APIResponse<String> postMessage(
			@PathVariable(value = "topic") String topic,
			@ModelAttribute("message") String message, final Locale locale) {

		APIResponse<String> res = new APIResponse<>();
		try {
			brokerService.postMessageByTopic(topic, message);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("post message: " + message + " here with:" + topic);
		// TODO: add condition to tell if it is successful or not
		res.setResponseObj("success");
		res.setStatus(ResponseStatus.SUCCESS);

		return res;
	}
}
