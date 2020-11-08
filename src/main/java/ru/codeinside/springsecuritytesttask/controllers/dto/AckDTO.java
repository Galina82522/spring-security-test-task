package ru.codeinside.springsecuritytesttask.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AckDTO {
    @Builder.Default
    private Boolean deleted = true;
}
