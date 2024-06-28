package com.raddan.OldVK.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CommentDTO {
    private Long commentID;
    private String content;
    private LocalDate timestamp;
    private String username;
    private Long postID;

}

