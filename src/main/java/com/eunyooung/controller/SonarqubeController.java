package com.eunyooung.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eunyooung.response.SonarqubeGroupResponse;
import com.eunyooung.response.SonarqubeProjectResponse;
import com.eunyooung.service.SonarqubeService;

@Controller
public class SonarqubeController {

	@Autowired
	private SonarqubeService sonarqube;
	
	@GetMapping("/sonarqube/user/list")
	public String getSonarqubeList(Model model) {
		
		model.addAttribute("userList", sonarqube.getUserList());
		model.addAttribute("service", "sonarqube");
		
		return "sonarqubeUser";
	}
	
	@GetMapping("/sonarqube/project/list")
	public String getProjectList(Model model) {
		
		List<SonarqubeProjectResponse> projectList = sonarqube.getProjectList();
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("service", "sonarqube");
		
		return "sonarqubeProject";
	}
	
	@ResponseBody
	@GetMapping("/sonarqube/permission/list")
	public List<SonarqubeGroupResponse> getProjectList(String projectKey) {
		return sonarqube.getProjectPermission(projectKey);
	}

	@GetMapping("/sonarqube/user/delete")
	public String deleteSonarqubeUser(Model model, String id, @RequestParam(required=false) String search) {
		
		if (search == null) {
			
			model.addAttribute("url", "/sonarqube/user/list");
		} else {
			
			model.addAttribute("url", "/search?id=" + id);			
		}
		
		if (sonarqube.deleteUser(id) == 200) {

			model.addAttribute("message", id + ": Sonarqube account has been successfully deleted");
		} else {
			
			model.addAttribute("message", id + ": Sonarqube account deletion failed");
		}
		
		return "common/message";
	}
}
