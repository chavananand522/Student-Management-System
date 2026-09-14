package com.itvedant.StudentManagement.services;

import reactor.core.publisher.Flux;

public interface AIService {

    Flux<String> askAI(String message);

}