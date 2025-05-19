package com.codewithme.linksphere.feed.controller;

import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import com.codewithme.linksphere.feed.dto.CommentDto;
import com.codewithme.linksphere.feed.dto.PostDto;
import com.codewithme.linksphere.feed.entities.Comment;
import com.codewithme.linksphere.feed.entities.Post;
import com.codewithme.linksphere.feed.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    private final FeedService feedService;

    public FeedController( FeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping("/post")
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Post post = feedService.createPost(user.getId(),  postDto);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }


    @PutMapping("/post/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable Long postId, @RequestBody PostDto postDto, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Post post = feedService.editPost(postId, user.getId(), postDto);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Post>>  getFeedPosts(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        List<Post> posts = feedService.getFeedPosts(user.getId());
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPostsByUser() {
        List<Post> posts = feedService.getAllPost();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("posts/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        List<Post> posts = feedService.getPostByUserId(user.getId());
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable("postId") Long postId) {
        Post post = feedService.getPostByPostId(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        feedService.deletePost(postId, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/post/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable("postId") Long postId, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Post post = feedService.likePost(postId, user.getId());
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<Comment> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDto commentDto, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Comment comment = feedService.addComment(postId, user.getId(), commentDto.getContent());
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{commentId}/comments")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        feedService.deleteComment(commentId, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable("commentId") Long commentId, @RequestBody CommentDto commentDto, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Comment comment = feedService.editComment(commentId, user.getId(), commentDto);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}
