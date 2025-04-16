package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private Integer id;
    private UserDetailsDto author;
    private OffsetDateTime createTime;
    private Integer likesCount;
    private boolean liked;
    private String comment;
}
