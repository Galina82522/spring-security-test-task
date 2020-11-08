package ru.codeinside.springsecuritytesttask.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.codeinside.springsecuritytesttask.models.AccessState;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String login;

    @NotNull
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @NotBlank
    private String password;

    @NotNull
    private List<RoleResDTO> roles;

    @Enumerated(EnumType.STRING)
    @NotNull
    @NotBlank
    private AccessState accessState;
}
