package com.eunyooung.service.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.eunyooung.dto.UserDto;
import com.eunyooung.response.SonarqubeGroupResponse;
import com.eunyooung.response.SonarqubeProjectResponse;
import com.eunyooung.response.SonarqubeUserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SonarqubeAPIService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	public List<SonarqubeUserResponse> getUserList(String url, HttpHeaders headers) {
		
		return searchUser(url, headers, "");
	}
	
	public List<SonarqubeUserResponse> searchUser(String url, HttpHeaders headers, String userId) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		List<SonarqubeUserResponse> userList = new ArrayList<>();
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/api/users/search");
		
		if (!userId.isEmpty()) {
			
			uri.queryParam("q", userId);
		}
		
		try {
			
			String res = restTemplate.exchange(uri.build().toUri(), HttpMethod.GET, request, String.class).getBody();
			String users = objectMapper.readTree(res).get("users").toString();
			userList = objectMapper.readValue(users,  new TypeReference<List<SonarqubeUserResponse>>() {});
		
		} catch (HttpStatusCodeException | JsonProcessingException e) {
			
			log.error(e.getMessage());
		}
		
		return userList;
	}
	
	public List<SonarqubeGroupResponse> getGroupList(String url, HttpHeaders headers) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		List<SonarqubeGroupResponse> groupList = new ArrayList<>();
		
		try {
			
			String res = restTemplate.exchange(url + "/api/user_groups/search", HttpMethod.GET, request, String.class).getBody();
			String groups = objectMapper.readTree(res).get("groups").toString();
			groupList = objectMapper.readValue(groups,  new TypeReference<List<SonarqubeGroupResponse>>() {});
			
		} catch (HttpStatusCodeException | JsonProcessingException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
		}
		
		return groupList;
	}
	
	public List<SonarqubeProjectResponse> getProjectList(String url, HttpHeaders headers) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		List<SonarqubeProjectResponse> projectList = new ArrayList<>();
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/api/projects/search");
		uri.queryParam("ps", 500);
		
		try {
			
			String res = restTemplate.exchange(uri.build().toUri(), HttpMethod.GET, request, String.class).getBody();
			String projects = objectMapper.readTree(res).get("components").toString();
			projectList = objectMapper.readValue(projects,  new TypeReference<List<SonarqubeProjectResponse>>() {});
			
		} catch (HttpStatusCodeException | JsonProcessingException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
		}
		
		return projectList;
	}
	
	public List<SonarqubeGroupResponse> getProjectPermission(String url, HttpHeaders headers, String projectKey) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		List<SonarqubeGroupResponse> groupList = new ArrayList<>();
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/api/permissions/groups");
		uri.queryParam("projectKey", projectKey);
		
		try {
			
			String res = restTemplate.exchange(uri.build().toUri(), HttpMethod.GET, request, String.class).getBody();
			String groups = objectMapper.readTree(res).get("groups").toString();
			groupList = objectMapper.readValue(groups,  new TypeReference<List<SonarqubeGroupResponse>>() {});
			
		} catch (HttpStatusCodeException | JsonProcessingException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
		}
		
		return groupList;
	}

	public int createUser(String url, HttpHeaders headers, UserDto user) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/api/users/create");
        uri.queryParam("login", user.getId());
        uri.queryParam("name", user.getName() + "(" + user.getCompany() + ")");
        uri.queryParam("email", user.getEmail());
        uri.queryParam("password", user.getPassword());
        
		try {
			
			int statusCode = restTemplate.exchange(uri.build().toUri(), HttpMethod.POST, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			return e.getStatusCode().value();
		}
	}
	
	public int deleteUser(String url, HttpHeaders headers, String userId) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/api/users/deactivate");
        uri.queryParam("login", userId);
        
		try {
			
			int statusCode = restTemplate.exchange(uri.build().toUri(), HttpMethod.POST, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			return e.getStatusCode().value();
		}
	}
	
	public int addUserToGroup(String url, HttpHeaders headers, String userId, String group) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/api/user_groups/add_user");
        uri.queryParam("login", userId);
        uri.queryParam("name", group);
        
		try {
			
			int statusCode = restTemplate.exchange(uri.build().toUri(), HttpMethod.POST, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			
			return e.getStatusCode().value();
		}
	}
}
