package com.eunyooung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eunyooung.service.NexusService;

@Controller
public class NexusController {
	
	@Autowired
	private NexusService nexus;
	
	@GetMapping("/nexus/user/list")
	public String getNexusList(Model model) {
		
		model.addAttribute("userList", nexus.getUserList());
		model.addAttribute("service", "nexus");
		
		return "nexusUser";
	}
	
	@GetMapping("/nexus/user/delete")
	public String deleteNexusUser(Model model, String id, @RequestParam(required=false) String search) {
		
		if (search == null) {
			
			model.addAttribute("url", "/nexus/user/list");
		} else {
			
			model.addAttribute("url", "/search?id=" + id);			
		}
		
		if (nexus.deleteUser(id) == 204) {
			
			model.addAttribute("message", id + ": Nexus account has been successfully deleted");
		} else {
				
			model.addAttribute("message", id + ": Nexus account deletion failed");
		}
		
		return "common/message";
	}
	
}
