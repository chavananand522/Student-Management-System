package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.model.Chapter;
import com.itvedant.StudentManagement.model.Topic;
import com.itvedant.StudentManagement.services.ChapterService;
import com.itvedant.StudentManagement.services.TopicService;

@Controller
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;
    private final ChapterService chapterService;

    public TopicController(
            TopicService topicService,
            ChapterService chapterService) {

        this.topicService = topicService;
        this.chapterService = chapterService;
    }

    @GetMapping("/new")
    public String addTopic(
            @RequestParam Long chapterId,
            Model model) {

        Chapter chapter = chapterService.getChapterById(chapterId);

        Topic topic = new Topic();
        topic.setChapterId(chapterId);

        model.addAttribute("topic", topic);
        model.addAttribute("chapter", chapter);

        return "add-topic";
    }

    @PostMapping("/save")
    public String saveTopic(
            Topic topic,
            RedirectAttributes redirectAttributes) {

        Chapter chapter =
                chapterService.getChapterById(topic.getChapterId());

        topicService.createTopic(topic);

        redirectAttributes.addFlashAttribute(
                "message",
                "Topic added successfully."
        );

        return "redirect:/chapter/" + chapter.getId();
    }

    @GetMapping("/{id}")
    public String viewTopic(
            @PathVariable Long id,
            Model model) {

        Topic topic = topicService.getTopicById(id);

        Chapter chapter =
                chapterService.getChapterById(topic.getChapterId());

        model.addAttribute("topic", topic);
        model.addAttribute("chapter", chapter);

        return "topic-view";
    }

    @GetMapping("/{id}/edit")
    public String editTopic(
            @PathVariable Long id,
            Model model) {

        Topic topic = topicService.getTopicById(id);

        Chapter chapter =
                chapterService.getChapterById(topic.getChapterId());

        model.addAttribute("topic", topic);
        model.addAttribute("chapter", chapter);

        return "topic-edit";
    }

    @PostMapping("/{id}/update")
    public String updateTopic(
            @PathVariable Long id,
            Topic topic,
            RedirectAttributes redirectAttributes) {

        Topic existingTopic = topicService.getTopicById(id);

        topicService.updateTopic(id, topic);

        redirectAttributes.addFlashAttribute(
                "message",
                "Topic updated successfully."
        );

        return "redirect:/chapter/" +
                existingTopic.getChapterId();
    }

    @PostMapping("/{id}/delete")
    public String deleteTopic(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Topic topic = topicService.getTopicById(id);

        Long chapterId = topic.getChapterId();

        topicService.deleteTopic(id);

        redirectAttributes.addFlashAttribute(
                "message",
                "Topic deleted successfully."
        );

        return "redirect:/chapter/" + chapterId;
    }
}