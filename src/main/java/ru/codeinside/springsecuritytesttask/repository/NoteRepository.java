package ru.codeinside.springsecuritytesttask.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.codeinside.springsecuritytesttask.models.Note;
import ru.codeinside.springsecuritytesttask.models.User;

import javax.annotation.Nonnull;
import java.util.Optional;


@Repository
public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {
    @Nonnull
    Optional<Note> findById(@Nonnull Long id);

    @Nonnull
    Page<Note> findAllByUserOrderByCreated(@Nonnull Pageable pageable, @Nonnull User user);
}
