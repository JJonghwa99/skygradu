package com.SkyGradU.SkyGradU.QnA;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaRequestDto {
    private String title;
    private String content;
    private Boolean anonymous;
}
