package ru.codeinside.springsecuritytesttask.controllers;

import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserRegistrationReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserResDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.services.UserService;

import javax.validation.Valid;

import static ru.codeinside.springsecuritytesttask.models.AuthoritiesConstants.*;


@RestController
@RequestMapping("/user")
@Api(tags = "Пользователи", description = "Контроллер, определяющий все операции с учетными записями пользователей")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured({ANONYMOUS, ADMIN})
    @PostMapping("/registration")
    @ApiOperation(value = "Регистрация нового пользователя",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "write", description = "")})}
    )
    public ResponseEntity<OAuth2AccessToken> registerUserAccount(
            @ApiParam(required = true, name = "Registration Data", value = "Данные для регистрации пользователя")
            @RequestBody @Valid UserRegistrationReqDTO userRegistrationDTO) {
        OAuth2AccessToken accessToken = userService.registerNewUserAccount(userRegistrationDTO);

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @Secured({ADMIN, USER})
    @GetMapping("/{id:[\\d]+}")
    @ApiOperation(value = "Просмотр информации о пользователе",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "read", description = "")})}
    )
    public UserResDTO getUserAccount(
            @ApiParam(required = true, value = "id пользователя, данные которого нужно получить") @PathVariable("id") String userId
    ) {
        User user = userService.getUserAccount(Long.parseLong(userId));

        return userService.toDTO(user);
    }

    @Secured({ADMIN, USER})
    @PutMapping("/{id:[\\d]+}")
    @ApiOperation(value = "Изменение пользователя",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "write", description = "")})}
    )
    public UserResDTO updateUserAccount(
            @ApiParam(required = true, name = "User Data", value = "Данные для изменения пользователя")
            @RequestBody @Valid UserUpdatedReqDTO userDto,
            @ApiParam(required = true, value = "id пользователя, данные которого нужно обновить") @PathVariable("id") String userId
    ) {
        User user = userService.updateUserAccount(Long.parseLong(userId), userDto);

        return userService.toDTO(user);
    }

    @Secured(ADMIN)
    @DeleteMapping("/{id:[\\d]+}")
    @ApiOperation(value = "Удаление пользователя",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization(value = "OAuth", scopes = {@AuthorizationScope(scope = "write", description = "")})}
    )
    public AckDTO deleteUserAccount(
            @ApiParam(required = true, value = "id пользователя, аккаунт которого делаем неактивным") @PathVariable("id") String userId
    ) {
        return userService.deleteUserAccount(Long.parseLong(userId));
    }
}
