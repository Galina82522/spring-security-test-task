package ru.codeinside.springsecuritytesttask.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteAddedReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteResDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.models.Note;

import javax.annotation.Nonnull;


public interface NoteService {

    /**
     * Добавление заметки
     *
     * @param noteDTO ДТО, содержащий информацию о добавляемой заметке
     * @return заметка
     */
    @Nonnull
    Note addNote(@Nonnull NoteAddedReqDTO noteDTO);

    /**
     * Получение информации о заметке
     *
     * @param noteId ID заметки
     * @return заметка
     */
    @Nonnull
    Note getNote(@Nonnull Long noteId);

    /**
     * Получение всех заметок текущего пользователя
     *
     * @return список заметок
     */
    @Nonnull
    Page<Note> getCurrentUserNotes(Pageable pageable);

    /**
     * Получение версий заметки
     * @param noteId ID заметки
     * @return список заметок
     */
    @Nonnull
    Page<Note> getNoteVersions(@Nonnull Pageable pageable, Long noteId);

    /**
     * Обновление заметки
     *
     * @param noteId  ID заметки
     * @param noteDTO ДТО, содержащий информацию об обвновляемых полях в заметке
     * @return заметка
     */
    @Nonnull
    Note updateNote(@Nonnull Long noteId, @Nonnull NoteUpdatedReqDTO noteDTO);

    /**
     * Удаление заметки
     *
     * @param noteId ID заметки
     * @return ДТО, содержащий информацию об успешности выполнения удаления
     */
    @Nonnull
    AckDTO deleteNote(@Nonnull Long noteId);

    @Nonnull
    NoteResDTO toDTO(@Nonnull Note note);

    @Nonnull
    NoteResDTO toVersionDTO(@Nonnull Note note);
}
