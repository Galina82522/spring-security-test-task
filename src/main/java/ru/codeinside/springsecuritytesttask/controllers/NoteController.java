package ru.codeinside.springsecuritytesttask.controllers;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.codeinside.springsecuritytesttask.configs.SwaggerConfig.ApiPageable;
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteAddedReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteResDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.models.Note;
import ru.codeinside.springsecuritytesttask.services.NoteService;

import javax.validation.Valid;

import static ru.codeinside.springsecuritytesttask.models.AuthoritiesConstants.ADMIN;
import static ru.codeinside.springsecuritytesttask.models.AuthoritiesConstants.USER;


@RestController
@RequestMapping("/notes")
@Api(tags = "Заметки", description = "Контроллер, определяющий все операции с заметками")
public class NoteController {

    NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Secured({ADMIN, USER})
    @PostMapping
    @ApiOperation(value = "Добавление новой заметки",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "write", description = "")})}
    )
    public NoteResDTO addNote(
            @ApiParam(required = true, name = "Note Data", value = "Данные для добавления новой заметки")
            @RequestBody @Valid NoteAddedReqDTO noteDTO) {
        Note note = noteService.addNote(noteDTO);

        return noteService.toDTO(note);
    }

    @Secured({ADMIN, USER})
    @GetMapping("/{id:[\\d]+}")
    @ApiOperation(value = "Просмотр заметки",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "read", description = "")})}
    )
    public NoteResDTO getNote(
            @ApiParam(required = true, value = "id заметки, данные которой нужно отобразить") @PathVariable("id") String noteId
    ) {
        Note note = noteService.getNote(Long.parseLong(noteId));

        return noteService.toDTO(note);
    }

    @Secured({ADMIN, USER})
    @ApiPageable
    @GetMapping("/user")
    @ApiOperation(value = "Просмотр своих заметок",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "read", description = "")})}
    )
    public Page<NoteResDTO> getCurrentUserNotes(Pageable pageable) {
        Page<Note> notes = noteService.getCurrentUserNotes(pageable);

        return notes.map(noteService::toDTO);
    }

    @Secured({ADMIN})
    @ApiPageable
    @GetMapping("/{id:[\\d]+}/versions")
    @ApiOperation(value = "Просмотр версий заметки",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "read", description = "")})}
    )
    public Page<NoteResDTO> getNoteVersions(
            @ApiParam(required = true, value = "id заметки, версии которой нужно отобразить") @PathVariable("id") String noteId,
            Pageable pageable
    ) {
        Page<Note> notes = noteService.getNoteVersions(pageable, Long.parseLong(noteId));

        return notes.map(noteService::toVersionDTO);
    }

    @Secured({ADMIN, USER})
    @PutMapping("/{id:[\\d]+}")
    @ApiOperation(value = "Обновление заметки",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "write", description = "")})}
    )
    public NoteResDTO updateNote(
            @ApiParam(required = true, name = "Note Data", value = "Данные для изменения заметки")
            @RequestBody @Valid NoteUpdatedReqDTO noteDto,
            @ApiParam(required = true, value = "id заметки, данные которой нужно обновить") @PathVariable("id") String noteId
    ) {
        Note note = noteService.updateNote(Long.parseLong(noteId), noteDto);

        return noteService.toDTO(note);
    }

    @Secured({ADMIN, USER})
    @DeleteMapping("/{id:[\\d]+}")
    @ApiOperation(value = "Удаление заметки",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "write", description = "")})}
    )
    public AckDTO deleteNote(
            @ApiParam(required = true, value = "id заметки, которую нужно удалить") @PathVariable("id") String noteId
    ) {
        return noteService.deleteNote(Long.parseLong(noteId));
    }
}
