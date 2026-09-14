package com.itvedant.StudentManagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.model.Topic;
import com.itvedant.StudentManagement.reposatory.TopicRepository;
import com.itvedant.StudentManagement.services.TopicService;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public List<Topic> getTopicsByChapterId(Long chapterId) {
        return topicRepository.findByChapterIdOrderByIdAsc(chapterId);
    }

    @Override
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }

    @Override
    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public Topic updateTopic(Long id, Topic topic) {

        Topic existingTopic = getTopicById(id);

        existingTopic.setName(topic.getName());
        existingTopic.setDescription(topic.getDescription());

        return topicRepository.save(existingTopic);
    }

    @Override
    public void deleteTopic(Long id) {

        Topic topic = getTopicById(id);

        topicRepository.delete(topic);
    }
}