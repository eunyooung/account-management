package com.eunyooung.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.eunyooung.dto.UserDto;
import com.eunyooung.dto.UserListDto;
import com.eunyooung.service.NexusService;
import com.eunyooung.service.OpensearchService;
import com.eunyooung.service.SonarqubeService;

@Controller
public class MainController {
		
	@Autowired
	private SonarqubeService sonarqube;
	
	@Autowired
	private NexusService nexus;
	
	@Autowired
	private OpensearchService opensearchService;
		
	@GetMapping("/")
	public String getMain(Model model) {
		
		return "redirect:/form";
	}
	
	@GetMapping("/form")
	public String getForm(Model model) {
		
		model.addAttribute("sonarqubeGroups", sonarqube.getGroupList());
		model.addAttribute("service", "form");
		return "form";
	}
	
	@GetMapping("/search")
	public String searchUser(Model model, String id) {
		
		model.addAttribute("sonarUserList", sonarqube.searchUser(id));
		model.addAttribute("nexusUserList", nexus.searchUser(id));
		model.addAttribute("opensearchUserList", opensearchService.searchUser(id));
		return "search";
	}
	
	@PostMapping("/user/create")
	public String createUser(Model model, UserListDto users) {
		
		String message = "";
		for (UserDto user : users.getUsers()) {
			
			user.setId(user.getId().toUpperCase());
			List<String> services = user.getServices();
			
			if (services.contains("sonarqube")) {
				
				if (sonarqube.createUser(user) == 200) {
					
					sonarqube.addUserToGroup(user);
					message += user.getId() + ": Sonarqube account has been successfully created\n";
				} else {
					message += user.getId() + ": Sonarqube account creation failed\n";
				}
			}
			
			if (services.contains("nexus")) {
				
				if (nexus.createUser(user) == 200) {
					
					message += user.getId() + ": Nexus account has been successfully created\n";
					
				} else {
					message += user.getId() + ": Nexus account creation failed\n";
				}
			}
			
			if (services.contains("opensearch")) {
				
				if (opensearchService.createUser(user) == 201) {
					
					opensearchService.addRoleMapping(user);
					message += user.getId() + ": Opensearch account has been successfully created\n";
					
				} else {
					
					message += user.getId() + ": Opensearch account creation failed\n";
				}
			}
		}
		
		model.addAttribute("message", message);
		model.addAttribute("url", "/form");
		
		return "common/message";
	}
}
