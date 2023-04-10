package com.pet.blogproject.controllers;

import com.pet.blogproject.models.Post;
import com.pet.blogproject.repo.PostRepository;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/blog")
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public String blogGet(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("title", "Блог");
        return "blog";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("title", "Добавление поста");
        return "blog-add";
    }
    @PostMapping("/add")
    public String addPost(Post post, Model model) {
        model.addAttribute("title", "Блог");
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/{id}")
    public String getBlogDetails(@PathVariable(value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        List<Post> result = new ArrayList<>();
        post.ifPresent(result::add);
        model.addAttribute("title", "Статья");
        model.addAttribute("post", result);
        return "blog-details";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        List<Post> result = new ArrayList<>();
        post.ifPresent(result::add);
        model.addAttribute("title", "Редактирование статьи");
        model.addAttribute("post", result);
        return "blog-edit";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable(value = "id") long id, Post post, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Post oldPost = postRepository.findById(id).orElseThrow();
        oldPost.setTitle(post.getTitle());
        oldPost.setAnnounce(post.getAnnounce());
        oldPost.setFull_text(post.getFull_text());
        postRepository.save(oldPost);
        return "redirect:/blog";
    }

    @PostMapping("/{id}/remove")
    public String removePost(@PathVariable(value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Post oldPost = postRepository.findById(id).orElseThrow();
        postRepository.delete(oldPost);
        return "redirect:/blog";
    }
}
