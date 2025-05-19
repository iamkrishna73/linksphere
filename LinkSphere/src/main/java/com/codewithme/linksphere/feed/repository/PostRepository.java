package com.codewithme.linksphere.feed.repository;

import com.codewithme.linksphere.feed.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdNotOrderByCreationDateDesc(Long userId);

    List<Post> findAllByOrderByCreationDate();

    List<Post> findByUserId(Long userId);
}
