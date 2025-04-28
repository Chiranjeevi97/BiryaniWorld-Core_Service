package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetail {

    public Integer status;

    public String title;

    public String message;

    public String detail;

    public String instance;

    public LocalDate timeStamp;

    public Map<String, Object> errors;

}
