package com.eunyooung.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OpensearchUserResponse {
	
	@Data
	public static class Attributes {
	
		private String name;
		
		private String company;
		
		private String email;
	}
	
	@JsonProperty(value = "backend_roles")
	private List<String> backendRoles;
	
	private Attributes attributes;
	
	@JsonIgnore
	private List<String> roles;
}
