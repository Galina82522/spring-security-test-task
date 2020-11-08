package ru.codeinside.springsecuritytesttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.codeinside.springsecuritytesttask.models.Role;

import javax.annotation.Nonnull;
import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    @Nonnull
    Optional<Role> findByName(@Nonnull String name);
}
