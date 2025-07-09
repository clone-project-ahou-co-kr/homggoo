package com.hgc.homggoo.entities.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    private int id;
    private int articleId;
    private String userEmail;
    private Integer commentId;
    private String content;
    private LocalDateTime createdAt;
    private boolean isDeleted;
}
