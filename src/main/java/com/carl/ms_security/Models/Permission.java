package com.carl.ms_security.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Permission {

    @Id
    private String id;

    private String url;
    private String method;
    private String description;

    public Permission(){}

    public Permission(String url, String method, String description){
        this.url = url;
        this.method = method;
        this.description = description;
    }
}