package com.eunyooung.response;

import java.util.Date;

import lombok.Data;

@Data
public class SonarqubeProjectResponse {
	
	private String key;
	
	private String name;
	
	private String qualifier;
	
	private String visibility;

	private Date lastAnalysisDate;

	private String revision;
}
