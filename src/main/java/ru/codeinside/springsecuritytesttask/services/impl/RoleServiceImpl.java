package ru.codeinside.springsecuritytesttask.services.impl;

import org.springframework.stereotype.Service;
import ru.codeinside.springsecuritytesttask.controllers.dto.RoleResDTO;
import ru.codeinside.springsecuritytesttask.exceptions.RoleNotFoundException;
import ru.codeinside.springsecuritytesttask.models.Role;
import ru.codeinside.springsecuritytesttask.repository.RoleRepository;
import ru.codeinside.springsecuritytesttask.services.RoleService;

import javax.annotation.Nonnull;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Nonnull
    @Override
    public Role findRoleByName(@Nonnull String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new RoleNotFoundException(String.format("Роль %s не найдена", name))
        );
    }

    @Override
    @Nonnull
    public RoleResDTO toDTO(@Nonnull Role role) {
        return RoleResDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
