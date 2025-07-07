package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.notice.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoticeVo extends NoticeEntity {
    private String nickname;
}
