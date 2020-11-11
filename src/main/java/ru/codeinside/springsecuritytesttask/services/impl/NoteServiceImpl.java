package ru.codeinside.springsecuritytesttask.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteAddedReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteResDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.exceptions.NotAllowedException;
import ru.codeinside.springsecuritytesttask.exceptions.NoteNotFoundException;
import ru.codeinside.springsecuritytesttask.models.Note;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.oauth2.SecurityUtils;
import ru.codeinside.springsecuritytesttask.repository.NoteRepository;
import ru.codeinside.springsecuritytesttask.services.NoteService;
import ru.codeinside.springsecuritytesttask.services.UserService;

import javax.annotation.Nonnull;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;
    private final EntityManagerFactory entityManagerfactory;

    public NoteServiceImpl(
            NoteRepository noteRepository, UserService userService, EntityManagerFactory entityManagerfactory
    ) {
        this.noteRepository = noteRepository;
        this.userService = userService;
        this.entityManagerfactory = entityManagerfactory;
    }

    @Override
    @Nonnull
    @Transactional
    public Note addNote(@Nonnull NoteAddedReqDTO noteDTO) {
        Note note = new Note();

        note.setTitle(noteDTO.getTitle());
        note.setText(noteDTO.getText());

        String login = SecurityUtils.getCurrentUserLogin();
        note.setUser(userService.findUserByLogin(login));

        Note savedNote = noteRepository.save(note);

        log.info("Добавлена новая заметка с id {}", savedNote.getId());

        return savedNote;
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public Note getNote(@Nonnull Long noteId) {
        Note note = findNoteById(noteId);

        if (userService.hasPermissions(note.getUser().getId())) {
            log.info("Просмотр заметки с id {}", note.getId());

            return note;
        } else {
            throw new NotAllowedException();
        }
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public Page<Note> getCurrentUserNotes(Pageable pageable) {
        User user = userService.getCurrentUser();

        return noteRepository.findAllByUserOrderByCreated(pageable, user);
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public Page<Note> getNoteVersions(@Nonnull Pageable pageable, Long noteId) {
        List<Note> notes = AuditReaderFactory.get(entityManagerfactory.createEntityManager())
                .createQuery()
                .forRevisionsOfEntity(Note.class,true, true)
                .add(AuditEntity.id().eq(noteId))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();

        int toSkip = pageable.getPageSize() * pageable.getPageNumber();
        notes = notes.stream().skip(toSkip).limit(pageable.getPageSize()).collect(Collectors.toList());

        return new PageImpl<>(notes, pageable, pageable.getPageNumber());
    }

    @Nonnull
    @Override
    @Transactional
    public Note updateNote(@Nonnull Long noteId, @Nonnull NoteUpdatedReqDTO noteDTO) {
        Note note = findNoteById(noteId);

        if (userService.hasPermissions(note.getUser().getId())) {

            note.setTitle(noteDTO.getTitle());
            note.setText(noteDTO.getText());

            log.info("Обновлена заметка с id {}", note.getId());

            return note;
        } else {
            throw new NotAllowedException();
        }
    }

    @Nonnull
    @Override
    @Transactional
    public AckDTO deleteNote(@Nonnull Long noteId) {
        Note note = findNoteById(noteId);
        if (userService.hasPermissions(note.getUser().getId())) {
            noteRepository.delete(note);

            log.info("Удалена заметка с id {}", note.getId());

            return new AckDTO();
        } else {
            throw new NotAllowedException();
        }
    }

    @Override
    @Nonnull
    public NoteResDTO toDTO(@Nonnull Note note) {
        return NoteResDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .text(note.getText())
                .created(note.getCreated())
                .updated(note.getUpdated())
                .createdBy(note.getCreatedBy())
                .lastModifiedBy(note.getLastModifiedBy())
                .userId(note.getUser().getId())
                .build();
    }

    @Override
    @Nonnull
    public NoteResDTO toVersionDTO(@Nonnull Note note) {
        return NoteResDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .text(note.getText())
                .created(note.getCreated())
                .updated(note.getUpdated())
                .createdBy(note.getCreatedBy())
                .lastModifiedBy(note.getLastModifiedBy())
                .build();
    }

    @Nonnull
    private Note findNoteById(@Nonnull Long noteId) {
        return noteRepository.findById(noteId).orElseThrow(
                () -> new NoteNotFoundException("Заметка не найдена")
        );
    }
}
