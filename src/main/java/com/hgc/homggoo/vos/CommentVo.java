package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.comment.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentVo extends CommentEntity {
    private String userNickname;
    private boolean isMine;
}
