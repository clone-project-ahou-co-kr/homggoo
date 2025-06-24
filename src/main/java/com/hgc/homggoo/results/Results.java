package com.hgc.homggoo.results;

public interface Results {
    String name();

    default String nameToLower(){
        return this.toString().toLowerCase();
    }
}
