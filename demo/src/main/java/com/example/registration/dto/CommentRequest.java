package com.example.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    @NotNull
    private Long articleId;

    @NotBlank
    @Size(min = 1, max = 1000, message = "评论长度1-1000字符")
    private String content;

}
