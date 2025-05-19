package com.codewithme.linksphere.feed.service;

import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import com.codewithme.linksphere.authentication.repositories.UserRepository;
import com.codewithme.linksphere.feed.dto.CommentDto;
import com.codewithme.linksphere.feed.dto.PostDto;
import com.codewithme.linksphere.feed.entities.Comment;
import com.codewithme.linksphere.feed.entities.Post;
import com.codewithme.linksphere.feed.repository.CommentRepository;
import com.codewithme.linksphere.feed.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public FeedService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Post createPost(Long userId, PostDto postDto) {
        AuthenticationUser authenticationUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = new Post(postDto.getContent(), authenticationUser);
        post.setPicture(postDto.getPicture());
        return postRepository.save(post);
    }

    public Post editPost(Long postId, Long userId, PostDto postDto) {
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to edit this post");
        }
        post.setContent(postDto.getContent());
        post.setPicture(postDto.getPicture());
        return postRepository.save(post);

    }

    public List<Post> getFeedPosts(Long userId) {
        return postRepository.findByUserIdNotOrderByCreationDateDesc(userId);
    }

    public List<Post> getAllPost() {
        return postRepository.findAllByOrderByCreationDate();
    }

    public List<Post> getPostByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    public Post getPostByPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete this post");
        }
        postRepository.delete(post);
    }

    public Post likePost(Long postId, Long userId) {
      Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
      AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

      if(post.getLikes().contains(user)) {
          post.getLikes().remove(user);
      } else {
          post.getLikes().add(user);
      }
      return postRepository.save(post);
    }

    public Comment addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = new Comment(post, user, content);
        return commentRepository.save(comment);

    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    public Comment editComment(Long commentId, Long userId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete this comment");
        }
        comment.setContent(commentDto.getContent());
        return commentRepository.save(comment);
    }
}
