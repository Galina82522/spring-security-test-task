package ru.codeinside.springsecuritytesttask.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserRegistrationReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserResDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.exceptions.UserAlreadyExistException;
import ru.codeinside.springsecuritytesttask.models.User;

import javax.annotation.Nonnull;


public interface UserService {
    /**
     * Регистрация нового пользователя
     *
     * @param accountDTO ДТО, содержащий информацию об новом пользователе
     * @return Токен для нового пользователя
     * @throws UserAlreadyExistException выбрасывает исключение, если логин уже зарегистрирован в системе
     */
    @Nonnull
    OAuth2AccessToken registerNewUserAccount(@Nonnull UserRegistrationReqDTO accountDTO) throws UserAlreadyExistException;

    /**
     * Получение всех пользователей
     *
     * @return список всех пользователей
     */
    @Nonnull
    Page<User> getAllUsers(Pageable pageable);

    /**
     * Получение информации о пользователе
     *
     * @param userId ID пользователя
     * @return пользователь
     */
    @Nonnull
    User getUserAccount(@Nonnull Long userId);

    /**
     * Обновление информации о пользователе
     *
     * @param userId     ID пользователя
     * @param accountDTO ДТО, содержащий информацию об обновляемом пользователе
     * @return пользоваетль
     */
    @Nonnull
    User updateUserAccount(@Nonnull Long userId, @Nonnull UserUpdatedReqDTO accountDTO);

    /**
     * Выключение аккаунта пользователя
     *
     * @param userId ID пользователя
     * @return ДТО, содержащий информацию об успешности выполнения выключения пользователя
     */
    @Nonnull
    AckDTO deleteUserAccount(@Nonnull Long userId);

    @Nonnull
    User findUserByLogin(@Nonnull String login);

    /**
     * Проверка прав.
     *
     * @param userId ID пользователя
     * @return Если пользователь имеет права админа или userId равен Id текущего пользователя,
     * то будет возвращено true, иначе false.
     */
    boolean hasPermissions(@Nonnull Long userId);

    /**
     * Проверка пользователя.
     *
     * @param userId ID пользователя
     * @return Если userId равен Id текущего пользователя, то будет возвращено true, иначе false.
     */
    boolean isCurrentUser(@Nonnull Long userId);

    /**
     * Метод возвращает текущего пользователя
     *
     * @return текущий пользователь
     */
    User getCurrentUser();

    @Nonnull
    UserResDTO toDTO(@Nonnull User user);
}
