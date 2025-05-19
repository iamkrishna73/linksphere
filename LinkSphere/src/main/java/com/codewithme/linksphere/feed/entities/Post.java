package com.codewithme.linksphere.feed.entities;

import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String content;
    private String picture;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AuthenticationUser user;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime updatedDate;

   @ManyToMany
   @JoinTable(
           name = "post_likes",
           joinColumns = @JoinColumn(name = "post_id"),
           inverseJoinColumns = @JoinColumn(name = "user_id")
   )
    private Set<AuthenticationUser> likes;

   @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Comment> comments;

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public Post(String content, AuthenticationUser user) {
        this.user = user;
        this.content = content;
    }
    public Post() {
    }
    public AuthenticationUser getUser() {
        return user;
    }

    public void setUser(AuthenticationUser user) {
        this.user = user;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getContent() {
        return content;
    }

    public void setContent(@NotBlank String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Set<AuthenticationUser> getLikes() {
        return likes;
    }

    public void setLikes(Set<AuthenticationUser> likes) {
        this.likes = likes;
    }
}
