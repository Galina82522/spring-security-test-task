package ru.codeinside.springsecuritytesttask.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResDTO {
    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String text;

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @NotNull
    @JsonProperty("created_by")
    private String createdBy;

    @NotNull
    @JsonProperty("last_modified_by")
    private String lastModifiedBy;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private LocalDateTime updated;
}
