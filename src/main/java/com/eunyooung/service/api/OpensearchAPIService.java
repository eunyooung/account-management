package com.eunyooung.service.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.eunyooung.response.OpensearchRoleResponse;
import com.eunyooung.response.OpensearchUserResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OpensearchAPIService {
	
	@Autowired
	private RestTemplate restTemplate;
		
	public Map<String, OpensearchUserResponse> getUserList(String url, HttpHeaders headers) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);

		Map<String, OpensearchUserResponse> userList = new HashMap<>();
		
		try {
			
			userList = restTemplate.exchange(url + "/_plugins/_security/api/internalusers", HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, OpensearchUserResponse>>() {}).getBody();
			
		} catch (HttpStatusCodeException e) {
			
		}
		
		return userList;
	}

	public Map<String, OpensearchUserResponse> getUser(String url, HttpHeaders headers, String userId) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		Map<String, OpensearchUserResponse> user = new HashMap<>();
		
		try {
			
			user = restTemplate.exchange(url + "/_plugins/_security/api/internalusers/" + userId, HttpMethod.GET, request,  new ParameterizedTypeReference<Map<String, OpensearchUserResponse>>() {}).getBody();
		
		} catch (HttpStatusCodeException e) {
			
		}
		
		return user;
	}
	
	public Map<String, OpensearchRoleResponse> getRoleList(String url, HttpHeaders headers) {
		
		Map<String, OpensearchRoleResponse> userList = new HashMap<>();
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		try {
			
			userList= restTemplate.exchange(url + "/_plugins/_security/api/rolesmapping/", HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, OpensearchRoleResponse>>() {}).getBody();
		
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
		}
		
		return userList;
	}
	
	public Map<String, OpensearchRoleResponse> getRole(String url, HttpHeaders headers, String roleName) {
		
		Map<String, OpensearchRoleResponse> userList = new HashMap<>();
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		try {
			
			userList= restTemplate.exchange(url + "/_plugins/_security/api/rolesmapping/" + roleName, HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, OpensearchRoleResponse>>() {}).getBody();
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
		}
		
		return userList;
	}
	
	public int addRoleMapping(String url, HttpHeaders headers, String userId, String role) {
		
		List<String> userList = getRole(url, headers, role).get(role).getUsers();
		
		userList.add(userId);
		userList = userList.stream().distinct().collect(Collectors.toList());
		
		HashMap<String, List<String>> body = new HashMap<>();
		body.put("users", userList);
		
		HttpEntity<HashMap<String, List<String>>> request = new HttpEntity<>(body, headers);
		
		try {
			
			int statusCode = restTemplate.exchange(url + "/_plugins/_security/api/rolesmapping/" + role, HttpMethod.PUT, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			
			return e.getStatusCode().value();
		}
	}
	
	public int deleteRoleMapping(String url, HttpHeaders headers, String userId, String role) {
		
		List<String> userList = getRole(url, headers, role).get(role).getUsers();
		while (userList.remove(userId)) {};
		
		HashMap<String, List<String>> body = new HashMap<>();
		body.put("users", userList);
		
		HttpEntity<HashMap<String, List<String>>> request = new HttpEntity<>(body, headers);
		
		try {
			
			int statusCode = restTemplate.exchange(url + "/_plugins/_security/api/rolesmapping/" + role, HttpMethod.PUT, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			
			return e.getStatusCode().value();
		}
	}

	public int createUser(String url, HttpHeaders headers, String userId, String userPassword, Map<String, String> attributes) {
		
		HashMap<String, Object> body = new HashMap<>();
		body.put("password", userPassword);
		body.put("attributes", attributes);
		
		HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(body, headers);
		
		try {
			
			int statusCode = restTemplate.exchange(url + "/_plugins/_security/api/internalusers/" + userId, HttpMethod.PUT, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			
			return e.getStatusCode().value();
		}
	}
	
	public int deleteUser(String url, HttpHeaders headers, String userId) {
		
		HttpEntity<String> request = new HttpEntity<>(headers);
		
		// Request
		try {
			
			int statusCode = restTemplate.exchange(url + "/_plugins/_security/api/internalusers/" + userId, HttpMethod.DELETE, request, String.class).getStatusCode().value();
			log.info("host : " + url + ",  status code : " + statusCode);
			
			return statusCode;
			
		} catch (HttpStatusCodeException e) {
			
			log.error("HttpStatusCodeException : " + e.getMessage());
			
			return e.getStatusCode().value();
		}
	}
}
