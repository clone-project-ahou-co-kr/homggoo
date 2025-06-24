package com.hgc.homggoo.entities.user;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "index")
public class UserEntity {
    private int index;
    private String email;
    private byte[] profile;
    private String password;
    private boolean isAdmin;
    private String nickname;
    private String providerType;
    private String providerKey;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isDeleted;
    private boolean isDormancy;
}
