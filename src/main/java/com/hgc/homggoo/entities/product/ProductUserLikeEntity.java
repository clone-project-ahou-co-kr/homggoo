package com.hgc.homggoo.entities.product;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductUserLikeEntity {
    private int productId;
    private String UserEmail;
    private LocalDateTime createdAt;
}
