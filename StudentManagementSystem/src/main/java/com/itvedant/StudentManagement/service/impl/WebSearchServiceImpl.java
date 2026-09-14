package com.itvedant.StudentManagement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itvedant.StudentManagement.services.WebSearchService;

@Service
public class WebSearchServiceImpl implements WebSearchService {

	private final RestClient restClient;

	@Value("${web.search.base-url}")
	private String baseUrl;

	public WebSearchServiceImpl() {
		this.restClient = RestClient.create();
	}

	@Override
	public String search(String query) {

		try {

			SearchResponse response = restClient.get()
					.uri(baseUrl + "/search?q=" + query.replace(" ", "+") + "&format=json").retrieve()
					.body(SearchResponse.class);

			if (response == null || response.results == null || response.results.isEmpty()) {
				return "No web search results found.";
			}

			StringBuilder results = new StringBuilder();

			int count = Math.min(response.results.size(), 2);

			for (int i = 0; i < count; i++) {

				SearchResult result = response.results.get(i);

				results.append("Title: ").append(result.title).append("\n");

				results.append("URL: ").append(result.url).append("\n");

				results.append("Description: ").append(result.content == null ? ""
						: result.content.substring(0, Math.min(result.content.length(), 300))).append("\n\n");
			}

			return results.toString();

		} catch (Exception e) {

			return "Web search is currently unavailable.";
		}
	}

	public static class SearchResponse {

		@JsonProperty("results")
		public List<SearchResult> results;
	}

	public static class SearchResult {

		@JsonProperty("title")
		public String title;

		@JsonProperty("url")
		public String url;

		@JsonProperty("content")
		public String content;
	}
}