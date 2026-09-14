package com.itvedant.StudentManagement.model;

public class WebSearchResult {

	private String title;
	private String url;
	private String description;

	public WebSearchResult() {
	}

	public WebSearchResult(String title, String url, String description) {
		this.title = title;
		this.url = url;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}