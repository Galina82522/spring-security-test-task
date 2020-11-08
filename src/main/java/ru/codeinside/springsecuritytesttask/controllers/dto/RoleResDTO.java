package ru.codeinside.springsecuritytesttask.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String name;
}
