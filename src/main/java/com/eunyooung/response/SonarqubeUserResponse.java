package com.eunyooung.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SonarqubeUserResponse {
	
	@JsonProperty(value = "login")
	private String id;
	
	private String name;
	
	private String email;
	
	private List<String> groups;
	
	private Date lastConnectionDate;
}
