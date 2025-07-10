package com.hgc.homggoo.vos;

import com.hgc.homggoo.entities.product.ProductEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter
public class ProductVo extends ProductEntity {
    private String nickname;

    public String getElapsedTime() {
        if (getCreatedAt() == null) return "알 수 없음";

        Duration duration = Duration.between(getCreatedAt(), LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (hours < 24) return hours + "시간 전";
        return days + "일 전";
    }

    public String getFormattedPrice() {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(this.getPrice());
    }
}
