package ru.codeinside.springsecuritytesttask.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.codeinside.springsecuritytesttask.models.User;

import javax.annotation.Nonnull;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Nonnull
    Optional<User> findById(@Nonnull Long id);

    @Nonnull
    Optional<User> findByLogin(@Nonnull String login);

    @Nonnull
    Page<User> findAllByOrderById(@Nonnull Pageable pageable);
}
