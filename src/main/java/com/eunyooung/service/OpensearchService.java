package com.eunyooung.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.eunyooung.dto.UserDto;
import com.eunyooung.response.OpensearchRoleResponse;
import com.eunyooung.response.OpensearchUserResponse;
import com.eunyooung.service.api.OpensearchAPIService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpensearchService {
	
	@Autowired
	OpensearchAPIService opensearchAPIService;
	
	@Value("${opensearch.url}")
	private String url;
	
	@Value("${opensearch.id}")
	private String authId;
	
	@Value("${opensearch.password}")
	private String authPassword;
	
	@Value("${opensearch.role.admin}")
	private List<String> adminRole;
	
	@Value("${opensearch.role.user}")
	private List<String> userRole;
	
	public int createUser(UserDto user) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		Map<String,OpensearchUserResponse> userResponse = opensearchAPIService.getUser(url, headers, user.getId());
		
		if (userResponse.get(user.getId()) != null) {
			
			log.info(user.getId() + " : account already exists");
			return 0;
		}
		
		HashMap<String, String> attributes = new HashMap<>();
		attributes.put("name", user.getName());
		attributes.put("company", user.getCompany());
		attributes.put("email", user.getEmail());
		
		return opensearchAPIService.createUser(url, headers, user.getId(), user.getPassword(), attributes);
	}
	
	public int deleteUser(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return opensearchAPIService.deleteUser(url, headers, userId);
	}
	
	public void addRoleMapping(UserDto user) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		List<String> roleList;
		
		if (Boolean.TRUE.equals(user.getIsAdmin())) {
			
			roleList = adminRole;
		} else {
			
			roleList = userRole;
		}
		
		try {
			
			for (String role : roleList) {
				
				opensearchAPIService.addRoleMapping(url, headers, user.getId(), role);
			}
			
		} catch (NullPointerException e) {
			
		}
	}
	
	public void deleteRoleMapping(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		Map<String, OpensearchRoleResponse> roleList = opensearchAPIService.getRoleList(url, headers);
		
		for (Map.Entry<String, OpensearchRoleResponse> role : roleList.entrySet()) {
			
			if (role.getValue().getUsers().contains(userId)) {
				
				opensearchAPIService.deleteRoleMapping(url, headers, userId, role.getKey());
			}
		}
	}
	
	public Map<String, OpensearchRoleResponse> getRoleMappingList() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return opensearchAPIService.getRoleList(url, headers);
	}
	
	public Map<String, OpensearchUserResponse> setUserRoleMapping(Map<String, OpensearchUserResponse> userList) {
		
		Map<String, OpensearchRoleResponse> roleList = getRoleMappingList();
		
		for (Map.Entry<String, OpensearchUserResponse> user : userList.entrySet()) {
			
			String id = user.getKey();
			List<String> roles = new ArrayList<>();
			
			for (Map.Entry<String, OpensearchRoleResponse>  role : roleList.entrySet()) {
				
				List<String> roleMappingUserList = role.getValue().getUsers();
				String roleName = role.getKey();
				
				if (roleMappingUserList.isEmpty()) {
					
					continue;
				}
				
				if (roleMappingUserList.contains(id)) {
					
					roles.add(roleName);
				}
			}
			
			user.getValue().setRoles(roles);
		}
		
		return userList;
	}
	
	public Map<String, OpensearchUserResponse> getUserList() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		Map<String, OpensearchUserResponse> userList = opensearchAPIService.getUserList(url, headers);
		
		userList = setUserRoleMapping(userList);
		
		return userList;
	}
	
	public Map<String, OpensearchUserResponse> searchUser(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		Map<String, OpensearchUserResponse> userList = opensearchAPIService.getUserList(url, headers);
		Map<String, OpensearchUserResponse> newUserList = new HashMap<>();
		
		for (Map.Entry<String, OpensearchUserResponse> user : userList.entrySet()) {
			
			String id = user.getKey();
			if (id.toLowerCase().contains(userId.toLowerCase())) {
				
				newUserList.put(id, user.getValue());
			}
		}

		newUserList = setUserRoleMapping(newUserList);
		return newUserList;
	}
}
