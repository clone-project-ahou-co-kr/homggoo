package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.product.ProductOrderEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.Locale;

@Getter
@Setter
public class ProductBuyVo extends ProductOrderEntity {
    private String nickname;
    private int id;
    private String title;
    private String category;
    private int price;

    public String getFormattedPrice() {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }
}

