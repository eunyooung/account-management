package com.eunyooung.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDto {
	
	private String id;
	private String password = "Test12345!@#$%";
	
	private String name;
	private String email;
	private String company;
	
	private Boolean isAdmin;
	
	private List<String> services;
	
	private List<String> sonarqubeGroups;
}