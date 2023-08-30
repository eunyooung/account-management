package com.eunyooung.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.eunyooung.dto.UserDto;
import com.eunyooung.response.SonarqubeGroupResponse;
import com.eunyooung.response.SonarqubeProjectResponse;
import com.eunyooung.response.SonarqubeUserResponse;
import com.eunyooung.service.api.SonarqubeAPIService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SonarqubeService {
	
	@Value("${sonarqube.url}")
	private String url;
	
	@Value("${sonarqube.id}")
	private String authId;
	
	@Value("${sonarqube.password}")
	private String authPassword;
	
	@Value("${sonarqube.group.admin}")
	private List<String> adminGroup;

	@Value("${sonarqube.group.exclude}")
	private List<String> excludeGroup;

	@Autowired
	SonarqubeAPIService sonarqubeAPIService;
	
	public List<SonarqubeGroupResponse> getGroupList() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		List<SonarqubeGroupResponse> groupList = sonarqubeAPIService.getGroupList(url, headers);
		
		try {
			
			for (String role : excludeGroup) {				
				groupList.removeIf(group -> group.getName().equals(role));
			}
		}
		catch (NullPointerException e) {
			
		}
		
		return groupList;
	}
	
	public int createUser(UserDto user) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		List<SonarqubeUserResponse> userResponseList = sonarqubeAPIService.searchUser(url, headers, user.getId());
		try {
			for (SonarqubeUserResponse userResponse : userResponseList) {
				
				if (userResponse.getId().equals(user.getId())) {
					
					log.info(user.getId() + " : account already exists");
					return 0;
				}
			}
		} catch (NullPointerException e) {
			
		}
		
		return sonarqubeAPIService.createUser(url, headers, user);
	}
	
	public int deleteUser(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return sonarqubeAPIService.deleteUser(url, headers, userId);
	}
	
	public void addUserToGroup(UserDto user) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		List<String> groupList;
		
		if (Boolean.TRUE.equals(user.getIsAdmin())) {
			
			groupList = adminGroup;
		} else {
			
			groupList = user.getSonarqubeGroups();
		}
		
		
		try {
			
			for (String group : groupList) {
				
				sonarqubeAPIService.addUserToGroup(url, headers, user.getId(), group);
			}
			
		} catch (NullPointerException e) {
			
		}
	}
	
	public List<SonarqubeUserResponse> getUserList() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return sonarqubeAPIService.getUserList(url, headers);
	}
	
	public List<SonarqubeProjectResponse> getProjectList() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return sonarqubeAPIService.getProjectList(url, headers);
	}
	
	public List<SonarqubeGroupResponse> getProjectPermission(String projectKey) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		List<SonarqubeGroupResponse> groupList = sonarqubeAPIService.getProjectPermission(url, headers, projectKey);
		
		groupList.removeIf(group -> group.getPermissions().isEmpty());
		return groupList;
	}
	
	public List<SonarqubeUserResponse> searchUser(String userId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(authId, authPassword);
		
		return sonarqubeAPIService.searchUser(url, headers, userId);
	}
}
