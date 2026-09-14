package com.itvedant.StudentManagement.services;

import java.util.List;

import com.itvedant.StudentManagement.model.Topic;

public interface TopicService {

    List<Topic> getTopicsByChapterId(Long chapterId);

    Topic getTopicById(Long id);

    Topic createTopic(Topic topic);

    Topic updateTopic(Long id, Topic topic);

    void deleteTopic(Long id);
}