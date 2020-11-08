package ru.codeinside.springsecuritytesttask.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@ApiModel
@Data
@NoArgsConstructor
public class ErrorDTO {

    @ApiModelProperty(value = "Код ошибки", example = "invalid_grant")
    private String error;

    @ApiModelProperty(value = "Описание ошибки", example = "Invalid password")
    @JsonProperty("error_description")
    private String errorDescription;

    public ErrorDTO(String error, String errorDescription) {
        this.error = error.toUpperCase();
        this.errorDescription = errorDescription;
    }

    public ErrorDTO(HttpStatus httpStatus, String errorDescription) {
        this(httpStatus.name(), errorDescription);
    }
}
