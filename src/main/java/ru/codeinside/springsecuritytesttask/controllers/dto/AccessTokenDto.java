package ru.codeinside.springsecuritytesttask.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccessTokenDto {

    @ApiModelProperty(value = "Access токен",
            example = "\"5b8494ad-087b-40b3-a321-55bdd32f6d7b\"")
    @JsonProperty("access_token")
    private String accessToken;

    @ApiModelProperty(value = "Тип токена (всегда bearer)",
            example = "\"bearer\"")
    @JsonProperty("token_type")
    private String tokenType;

    @ApiModelProperty(value = "Refresh токен",
            example = "\"4f958c54-f596-42ae-ae2c-00768db1b736\"")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @ApiModelProperty(value = "Срок жизни токена (в секундах)",
            example = "\"299\"")
    @JsonProperty("expires_in")
    private int expiresIn;

    @ApiModelProperty(value = "Сфера действия (не используется)",
            example = "\"read write\"")
    @JsonProperty("scope")
    private String scope;
}
