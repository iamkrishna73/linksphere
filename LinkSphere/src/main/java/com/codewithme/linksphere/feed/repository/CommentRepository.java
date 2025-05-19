package com.codewithme.linksphere.feed.repository;

import com.codewithme.linksphere.feed.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
