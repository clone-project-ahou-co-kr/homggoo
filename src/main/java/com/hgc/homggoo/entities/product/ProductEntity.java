package com.hgc.homggoo.entities.product;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductEntity {
    private int id;
    private String userEmail;
    private byte[] image;
    private String title;
    private String description;
    private int price;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
