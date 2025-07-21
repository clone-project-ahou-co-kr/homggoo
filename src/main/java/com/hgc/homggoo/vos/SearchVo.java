package com.hgc.homggoo.vos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchVo {
    private String by;
    private String keyword;
    private String email;
}
