package ru.codeinside.springsecuritytesttask.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Обновляемый пользователь")
public class UserUpdatedReqDTO {

    @ApiModelProperty(value = "Имя пользователя", example = "\"Василий\"")
    @JsonProperty("first_name")
    private String firstName;

    @ApiModelProperty(value = "Фамилия пользователя", example = "\"Иванов\"")
    @JsonProperty("last_name")
    private String lastName;

    @ApiModelProperty(value = "Пароль пользователя", example = "\"qwerty123\"")
    private String password;
}
