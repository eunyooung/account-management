package com.eunyooung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eunyooung.service.OpensearchService;

@Controller
public class OpensearchController {

	
	@Autowired
	private OpensearchService opensearchService;
	
	@GetMapping("/opensearch/user/list")
	public String getOpensearchList(Model model) {
		
		model.addAttribute("userList", opensearchService.getUserList());
		model.addAttribute("service", "opensearch");
		
		return "opensearchUser";
	}
	
	@GetMapping("/opensearch/user/delete")
	public String deleteOpensearchUser(Model model, String id, @RequestParam(required=false) String search) {

		if (search == null) {
			
			model.addAttribute("url", "/opensearch/user/list");
		} else {
			
			model.addAttribute("url", "/search?id=" + id);			
		}
		
		if (opensearchService.deleteUser(id) == 200) {
			
			opensearchService.deleteRoleMapping(id);
			model.addAttribute("message", id + ": Opensearch account has been successfully deleted");
		} else {
			
			model.addAttribute("message", id + ": Opensearch account deletion failed");
		}
		
		return "common/message";
	}
}
