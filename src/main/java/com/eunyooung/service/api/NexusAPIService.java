package com.eunyooung.service.api;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.eunyooung.response.NexusUserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NexusAPIService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public List<NexusUserResponse> getUserList(String url, HttpHeaders headers) {
		
		return searchUser(url, headers, "");
	}
	
	public List<NexusUserResponse> searchUser(String url, HttpHeaders headers, String userId) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		List<NexusUserResponse> userList = new ArrayList<>();
		
		UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url + "/service/rest/v1/security/users");
        
		if (!userId.isEmpty()) {
			
			uri.queryParam("userId", userId);
		}
		
		try {
			
			String res = restTemplate.exchange(uri.build().toUri(), HttpMethod.GET, request, String.class).getBody();
			userList = objectMapper.readValue(res,  new TypeReference<List<NexusUserResponse>>() {});
			
		} catch (HttpStatusCodeException | JsonProcessingException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
		}
		
		return userList;
	}
	
	public int createUser(String url, HttpHeaders headers, UserDto user, List<String> roles) {
		
		HashMap<String, Object> body = new HashMap<>();
				
		body.put("userId", user.getId());
		body.put("firstName", user.getName());
		body.put("lastName", "(" + user.getCompany() + ")");
		body.put("emailAddress", user.getEmail());
		body.put("password", user.getPassword());
		body.put("status", "active");
		body.put("roles", roles);
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(body, headers);
		
		try {
			
			int statusCode = restTemplate.exchange(url + "/service/rest/v1/security/users", HttpMethod.POST, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			return e.getStatusCode().value();
		}
	}
	
	public int deleteUser(String url, HttpHeaders headers, String userId) {
		
		HashMap<String, Object> body = new HashMap<>();
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(body, headers);
		
		try {
			
			int statusCode = restTemplate.exchange(url + "/service/rest/v1/security/users/" + userId, HttpMethod.DELETE, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			return e.getStatusCode().value();
		}
	}
}
