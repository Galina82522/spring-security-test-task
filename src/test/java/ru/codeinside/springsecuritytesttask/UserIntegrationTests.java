package ru.codeinside.springsecuritytesttask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.codeinside.springsecuritytesttask.controllers.dto.AccessTokenDto;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserRegistrationReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.UserUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.exceptions.UserNotFoundException;
import ru.codeinside.springsecuritytesttask.models.AccessState;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTests {
    private final String userUsername = "john";
    private final String adminUsername = "galya";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestStubs testStubs;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext wac;
    private User user;
    private User admin;

    @BeforeTransaction
    public void setUpBeforeTransaction() {
        userRepository.deleteAllInBatch();
        user = testStubs.stubUserWithRoleUser();
        admin = testStubs.stubUserWithRoleAdmin();
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @AfterTransaction
    public void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @WithAnonymousUser
    @Test
    public void registerUserTest_WithAnonymous() throws Exception {
        UserRegistrationReqDTO dto = new UserRegistrationReqDTO();
        dto.setLogin("vasya");
        dto.setFirstName("Василий");
        dto.setPassword("12345");

        String req = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AccessTokenDto token = objectMapper.readValue(response, AccessTokenDto.class);
        assertNotNull(token);
        assertNotNull(token.getAccessToken());
        assertEquals("bearer", token.getTokenType());
        assertNotNull(token.getRefreshToken());
        assertThat(token.getExpiresIn(), not(equalTo(0)));
        assertEquals("read write", token.getScope());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void registerUserTest_WithAdmin() throws Exception {
        UserRegistrationReqDTO dto = new UserRegistrationReqDTO();
        dto.setLogin("vasya");
        dto.setFirstName("Василий");
        dto.setPassword("12345");

        String req = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AccessTokenDto token = objectMapper.readValue(response, AccessTokenDto.class);
        assertNotNull(token);
        assertNotNull(token.getAccessToken());
        assertEquals("bearer", token.getTokenType());
        assertNotNull(token.getRefreshToken());
        assertThat(token.getExpiresIn(), not(equalTo(0)));
        assertEquals("read write", token.getScope());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void registerUserTest_WithUser() throws Exception {
        UserRegistrationReqDTO dto = new UserRegistrationReqDTO();
        dto.setLogin("vasya");
        dto.setFirstName("Василий");
        dto.setPassword("12345");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void registerUserTest_LoginExist() throws Exception {
        UserRegistrationReqDTO dto = new UserRegistrationReqDTO();
        dto.setLogin("john");
        dto.setFirstName("Василий");
        dto.setPassword("12345");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isConflict());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getUserAccountTest_WithAdmin() throws Exception {
        mockMvc.perform(get("/users/" + user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", equalTo(user.getId().intValue())))
                .andExpect(jsonPath("login", equalTo("john")));
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getUserAccountTest_WithCurrentUser() throws Exception {
        mockMvc.perform(get("/users/" + user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", equalTo(user.getId().intValue())))
                .andExpect(jsonPath("login", equalTo("john")));
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getUserAccountTest_WithNotCurrentUser() throws Exception {
        mockMvc.perform(get("/users/" + admin.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getUserAccountTest_NotExistId_WithAdmin() throws Exception {
        long id = user.getId();
        userRepository.delete(user);

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getUserAccountTest_NotExistId_WithUser() throws Exception {
        userRepository.deleteAllInBatch();

        mockMvc.perform(get("/users/" + 1))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getAllUsers_WithUser() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void getAllUsers_WithAnonymous() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getAllUsers_WithAdmin() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", notNullValue()))
                .andExpect(jsonPath("$.content[0].login", is("john")))
                .andExpect(jsonPath("$.content[1].id", notNullValue()))
                .andExpect(jsonPath("$.content[1].login", is("galya")));
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateUserAccountTest_WithAdmin() throws Exception {
        UserUpdatedReqDTO dto = new UserUpdatedReqDTO();
        dto.setFirstName("Василий");
        dto.setPassword("new_password");
        dto.setLastName("Володин");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", equalTo(user.getId().intValue())))
                .andExpect(jsonPath("last_name", equalTo("Володин")));
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateUserAccountTest_WithUser() throws Exception {
        UserUpdatedReqDTO dto = new UserUpdatedReqDTO();
        dto.setFirstName("Василий");
        dto.setPassword("new_password");
        dto.setLastName("Володин");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", equalTo(user.getId().intValue())))
                .andExpect(jsonPath("last_name", equalTo("Володин")));
    }

    @WithAnonymousUser
    @Test
    public void updateUserAccountTest_WithAnonymous() throws Exception {
        UserUpdatedReqDTO dto = new UserUpdatedReqDTO();
        dto.setFirstName("Василий");
        dto.setPassword("new_password");
        dto.setLastName("Володин");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateUserAccountTest_WithNotCurrentUser() throws Exception {
        UserUpdatedReqDTO dto = new UserUpdatedReqDTO();
        dto.setFirstName("Василий");
        dto.setPassword("new_password");
        dto.setLastName("Володин");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/users/" + admin.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isMethodNotAllowed());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateUserAccountTest_NotExistId_WithAdmin() throws Exception {
        long id = user.getId();
        userRepository.delete(user);

        UserUpdatedReqDTO dto = new UserUpdatedReqDTO();
        dto.setFirstName("Василий");
        dto.setPassword("new_password");
        dto.setLastName("Володин");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteUserAccountTest_WithAdmin() throws Exception {
        mockMvc.perform(put("/users/" + user.getId() + "/access_state"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("deleted", is(true)));

        User deletedUser = userRepository.findByLogin(user.getLogin())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        assertEquals(AccessState.DELETED, deletedUser.getAccessState());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteUserAccountTest_WithUser() throws Exception {
        mockMvc.perform(put("/users/" + admin.getId() + "/access_state"))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void deleteUserAccountTest_WithAnonymousUser() throws Exception {
        mockMvc.perform(put("/users/" + admin.getId() + "/access_state"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteUserAccountTest_NotExistId_WithAdmin() throws Exception {
        long id = user.getId();
        userRepository.delete(user);
        mockMvc.perform(put("/users/" + id + "/access_state"))
                .andExpect(status().isBadRequest());
    }
}
