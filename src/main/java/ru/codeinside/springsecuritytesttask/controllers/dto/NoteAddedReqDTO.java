package ru.codeinside.springsecuritytesttask.controllers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
@ApiModel(value = "Заметка")
@NoArgsConstructor
@AllArgsConstructor
public class NoteAddedReqDTO {

    @ApiModelProperty(value = "Заголовок", required = true, example = "\"Заголовок заметки\"")
    @NotNull
    @NotBlank
    private String title;

    @ApiModelProperty(value = "Текст заметки", required = true, example = "\"Текст заметки\"")
    @NotNull
    @NotBlank
    private String text;

}
