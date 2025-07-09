package com.hgc.homggoo.entities.images;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "index")
public class ImageEntity {
    private int index;
    private String name;
    private String contentType;
    private Integer noticeIndex;
    private Integer articleIndex;
    private LocalDateTime createdAt;
    private byte[] data;
}
