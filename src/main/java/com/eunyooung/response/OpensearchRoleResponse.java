package com.eunyooung.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OpensearchRoleResponse {
	
	private List<String> hosts;
	
	private List<String> users;
	
	@JsonProperty(value = "backend_roles")
	private List<String> backendRoles;
	
	@JsonProperty(value = "and_backend_roles")
	private List<String> andBackendRoles;
}
