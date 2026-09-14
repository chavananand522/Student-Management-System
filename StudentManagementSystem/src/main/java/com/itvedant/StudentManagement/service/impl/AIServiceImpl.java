package com.itvedant.StudentManagement.service.impl;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.services.AIService;
import com.itvedant.StudentManagement.services.WebSearchService;

import reactor.core.publisher.Flux;

@Service
public class AIServiceImpl implements AIService {

	private final OllamaChatModel chatModel;

	private final WebSearchService webSearchService;

	public AIServiceImpl(OllamaChatModel chatModel, WebSearchService webSearchService) {
		this.chatModel = chatModel;
		this.webSearchService = webSearchService;
	}

	@Override
	public Flux<String> askAI(String message) {

		if (!needsWebSearch(message)) {

			String prompt = """
					You are a helpful AI assistant inside a Student Management System.

					Answer the user's question naturally like ChatGPT.

					Follow these rules:
					- Understand the question before answering.
					- Give enough information to make the answer useful.
					- Keep simple questions concise.
					- Give more explanation when the question needs it.
					- Use clear and natural language.
					- Correct obvious spelling mistakes silently.
					- Use the correct spelling in your answer.
					- Do not mention that you corrected a spelling mistake.
					- Do not invent facts.
					- Do not repeat the user's question.
					- Do not use unnecessary emojis or decorative symbols.
					- Use headings or numbered points only when they improve readability.
					- Do not add unnecessary introductions or conclusions.

					USER QUESTION:

					%s

					""".formatted(message);

			return chatModel.stream(prompt);
		}

		String webResults = webSearchService.search(message);

		String prompt = """
				You are a helpful AI assistant inside a Student Management System.

				Answer the user's question naturally like ChatGPT using the web search results when useful.

				Follow these rules:
				- Understand the question before answering.
				- Give enough information to make the answer useful.
				- Keep simple questions concise.
				- Give more explanation when the question needs it.
				- Use clear and natural language.
				- Correct obvious spelling mistakes silently.
				- Use the correct spelling in your answer.
				- Do not mention that you corrected a spelling mistake.
				- Do not invent facts.
				- Do not repeat the user's question.
				- Do not use unnecessary emojis or decorative symbols.
				- Use headings or numbered points only when they improve readability.
				- Do not add unnecessary introductions or conclusions.
				- Use the web results to verify current information.
				- If the web results do not provide enough information, clearly say that the information could not be verified.

				WEB SEARCH RESULTS:

				%s

				USER QUESTION:

				%s

				"""
				.formatted(webResults, message);

		return chatModel.stream(prompt);
	}

	private boolean needsWebSearch(String message) {

		String query = message.toLowerCase();

		return query.contains("today") || query.contains("tomorrow") || query.contains("latest")
				|| query.contains("current") || query.contains("now") || query.contains("live")
				|| query.contains("weather") || query.contains("news") || query.contains("price")
				|| query.contains("stock") || query.contains("forecast") || query.contains("recent");
	}

}