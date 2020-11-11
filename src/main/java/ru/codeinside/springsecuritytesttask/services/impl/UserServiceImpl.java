package ru.codeinside.springsecuritytesttask.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserRegistrationReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserResDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.exceptions.NotAllowedException;
import ru.codeinside.springsecuritytesttask.exceptions.UserAlreadyExistException;
import ru.codeinside.springsecuritytesttask.exceptions.UserNotFoundException;
import ru.codeinside.springsecuritytesttask.models.AccessState;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.models.UserDetailsImpl;
import ru.codeinside.springsecuritytesttask.oauth2.Scopes;
import ru.codeinside.springsecuritytesttask.oauth2.SecurityUtils;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;
import ru.codeinside.springsecuritytesttask.services.RoleService;
import ru.codeinside.springsecuritytesttask.services.UserService;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static ru.codeinside.springsecuritytesttask.models.AuthoritiesConstants.ADMIN;
import static ru.codeinside.springsecuritytesttask.models.AuthoritiesConstants.USER;


@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthorizationServerTokenServices tokenService;
    private final ObjectMapper objectMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleService roleService,
            AuthorizationServerTokenServices tokenService,
            ObjectMapper objectMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Nonnull
    @Transactional
    public OAuth2AccessToken registerNewUserAccount(@Nonnull final UserRegistrationReqDTO accountDTO) {
        if (loginExists(accountDTO.getLogin())) {
            throw new UserAlreadyExistException("There is an account with that login: " + accountDTO.getLogin());
        }
        final User user = new User();

        user.setFirstName(accountDTO.getFirstName());
        user.setLastName(accountDTO.getLastName());
        user.setLogin(accountDTO.getLogin());
        user.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        user.setRoles(List.of(roleService.findRoleByName(USER)));
        user.setAccessState(AccessState.ACTIVE);
        User savedUser = userRepository.save(user);

        log.info("Добавлен новый пользователь с id {}", savedUser.getId());

        Map<String, String> requestParameters = ImmutableMap.<String, String>builder()
                .put("grant_type", "password")
                .put("client_id", "client")
                .put("client_secret", "secret")
                .put("username", savedUser.getLogin())
                .put("password", savedUser.getPassword())
                .build();

        final HashSet<String> scopes = new HashSet<>(Arrays.asList(Scopes.DEFAULT_CLIENT));

        final Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        final OAuth2Request oAuth2Request = new OAuth2Request(requestParameters,
                "client",
                authorities,
                true,
                scopes,
                null,
                "",
                Collections.singleton("password"),
                null);

        final UserDetailsImpl userDetails = new UserDetailsImpl(savedUser);
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities
        );
        final OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, token);

        authentication.setAuthenticated(true);

        return tokenService.createAccessToken(authentication);
    }

    @Nonnull
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllByOrderById(pageable);
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public User getUserAccount(@Nonnull Long userId) {
        if (hasPermissions(userId)) {
            User user = findUserById(userId);

            log.info("Просмотр информации о пользователе с id {}", user.getId());

            return user;
        } else {
            throw new NotAllowedException();
        }
    }

    @Nonnull
    @Override
    @Transactional
    public User updateUserAccount(@Nonnull Long userId, @Nonnull UserUpdatedReqDTO accountDTO) {
        if (hasPermissions(userId)) {
            User user = findUserById(userId);

            user.setFirstName(accountDTO.getFirstName());
            user.setLastName(accountDTO.getLastName());
            user.setPassword(passwordEncoder.encode(accountDTO.getPassword()));

            log.info("Обновлен пользователь с id {}", user.getId());

            return user;
        } else {
            throw new NotAllowedException();
        }
    }

    @Nonnull
    @Override
    @Transactional
    public AckDTO deleteUserAccount(@Nonnull Long userId) {
        User user = findUserById(userId);
        user.setAccessState(AccessState.DELETED);

        log.info("Отключен пользователь с id {}", user.getId());

        return new AckDTO();
    }

    @Override
    public boolean hasPermissions(@Nonnull Long userId) {
        return SecurityUtils.isCurrentUserInRole(ADMIN) || isCurrentUser(userId);
    }

    @Override
    public boolean isCurrentUser(@Nonnull Long userId) {
        String login = SecurityUtils.getCurrentUserLogin();

        return userRepository.findByLogin(login)
                .map(user -> user.getId().equals(userId))
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    public User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin();

        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Override
    @Nonnull
    @Transactional(readOnly = true)
    public User findUserByLogin(@Nonnull String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден")
        );
    }

    @Override
    @Nonnull
    public UserResDTO toDTO(@Nonnull User user) {
        return UserResDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .login(user.getLogin())
                .password(user.getPassword())
                .accessState(user.getAccessState())
                .roles(user.getRoles().stream().map(roleService::toDTO).collect(Collectors.toList()))
                .build();
    }

    @Nonnull
    private User findUserById(@Nonnull Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден")
        );
    }

    private boolean loginExists(@Nonnull final String login) {
        return userRepository.findByLogin(login).isPresent();
    }
}
