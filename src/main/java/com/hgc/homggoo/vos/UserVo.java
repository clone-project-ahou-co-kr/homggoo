package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVo extends UserEntity {
    private String userEmail;
}
