package com.hgc.homggoo.entities.product;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductOrderEntity {
    private int id;
    private int productId;
    private String buyerEmail;
    private boolean cancel;
    private LocalDateTime createdAt;
}
