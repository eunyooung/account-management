package com.eunyooung.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class NexusUserResponse {
	
	@JsonProperty(value = "userId")
	private String id;
	
	private String firstName;
	
	private String lastName;
	
	@JsonProperty(value = "emailAddress")
	private String email;
	
	private List<String> roles;
}
