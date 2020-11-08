package ru.codeinside.springsecuritytesttask.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@ApiModel(value = "Пользователь")
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationReqDTO {
    @ApiModelProperty(value = "Логин пользователя", required = true, example = "\"vasya\"")
    @NotNull
    @NotBlank
    private String login;

    @ApiModelProperty(value = "Имя пользователя", required = true, example = "\"Василий\"")
    @NotNull
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    @ApiModelProperty(value = "Фамилия пользователя", example = "\"Иванов\"")
    @JsonProperty("last_name")
    private String lastName;

    @ApiModelProperty(value = "Пароль пользователя", required = true, example = "\"qwerty123\"")
    @NotNull
    @NotBlank
    private String password;
}
