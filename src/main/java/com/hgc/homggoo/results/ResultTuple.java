package com.hgc.homggoo.results;

import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResultTuple<T> {
    private Results result;
    private T payload;
}
