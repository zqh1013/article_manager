package com.example.registration.dto;


import lombok.Getter;
import lombok.Setter;

@Getter // 自动生成所有getter
@Setter // 自动生成所有setter
public class CommentDTO {
    private Long id;
    private String content;
    private String author;
//    private String avatar;
    private String date;
    private String email;
}
