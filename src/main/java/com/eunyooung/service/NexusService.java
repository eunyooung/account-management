package com.eunyooung.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.eunyooung.dto.UserDto;
import com.eunyooung.response.NexusUserResponse;
import com.eunyooung.service.api.NexusAPIService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NexusService {

	@Value("${nexus.url}")
	private String url;
	
	@Value("${nexus.id}")
	private String authId;
	
	@Value("${nexus.password}")
	private String authPassword;
	
	@Value("${nexus.role.admin}")
	private List<String> adminRole;
	
	@Value("${nexus.role.user}")
	private List<String> userRole;
	
	@Autowired
	private NexusAPIService nexusAPIService;
	
	public int createUser(UserDto user) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		List<NexusUserResponse> userResponseList = nexusAPIService.searchUser(url, headers, user.getId());
		
		try {
			
			for (NexusUserResponse userResponse : userResponseList) {
				
				if (userResponse.getId().equals(user.getId())) {
					
					log.info(user.getId() + " : account already exists");
					return 0;
				}
			}
		} catch (NullPointerException e) {
			
		}
		
		List<String> roles = new ArrayList<>();
		
		if (Boolean.TRUE.equals(user.getIsAdmin())) {
		
			for (String role : adminRole) {
				roles.add(role);
			}
		} else {
			
			for (String role : userRole) {
				roles.add(role);
			}
		}
		
		return nexusAPIService.createUser(url, headers, user, roles);
	}
	
	public int deleteUser(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return nexusAPIService.deleteUser(url, headers, userId);
	}
	
	public List<NexusUserResponse> getUserList() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return nexusAPIService.getUserList(url, headers);
	}
	
	public List<NexusUserResponse> searchUser(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return nexusAPIService.searchUser(url, headers, userId);
	}
}
