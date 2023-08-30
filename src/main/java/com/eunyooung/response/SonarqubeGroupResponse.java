package com.eunyooung.response;

import java.util.List;

import lombok.Data;

@Data
public class SonarqubeGroupResponse {
	
	private String id;
	
	private String name;
	
	private String description;
	
	private String memberCount;
	
	private List<String> permissions;
}
