package ru.codeinside.springsecuritytesttask.controllers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Обновляемая заметка")
public class NoteUpdatedReqDTO {

    @ApiModelProperty(value = "Заголовок", example = "\"Заголовок заметки\"")
    private String title;

    @ApiModelProperty(value = "Текст заметки", example = "\"Текст заметки\"")
    private String text;

}
