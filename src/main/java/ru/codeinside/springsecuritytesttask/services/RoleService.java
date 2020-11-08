package ru.codeinside.springsecuritytesttask.services;

import ru.codeinside.springsecuritytesttask.controllers.dto.RoleResDTO;
import ru.codeinside.springsecuritytesttask.models.Role;

import javax.annotation.Nonnull;

public interface RoleService {
    Role findRoleByName(@Nonnull String name);

    @Nonnull
    RoleResDTO toDTO(@Nonnull Role role);
}
