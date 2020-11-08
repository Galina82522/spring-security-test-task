package ru.codeinside.springsecuritytesttask;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.codeinside.springsecuritytesttask.repository.RoleRepository;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;

import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestStubs testStubs;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        testStubs.stubUserWithRoleUser();
    }

    @After
    public void tearDown() {
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
    }

    @Test
    public void testAuthorizationServer() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic("client", "secret"))
                .queryParam("grant_type", "password")
                .queryParam("username", "john")
                .queryParam("password", "12345"))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthorizationServer_WrongClient() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic("client1", "secret"))
                .queryParam("grant_type", "password")
                .queryParam("username", "john")
                .queryParam("password", "12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizationServer_WrongClientPassword() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic("client", "secret1"))
                .queryParam("grant_type", "password")
                .queryParam("username", "john")
                .queryParam("password", "12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorizationServer_WrongUsername() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic("client", "secret"))
                .queryParam("grant_type", "password")
                .queryParam("username", "john1")
                .queryParam("password", "12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthorizationServer_WrongUsernamePassword() throws Exception {
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic("client", "secret"))
                .queryParam("grant_type", "password")
                .queryParam("username", "john")
                .queryParam("password", "123456"))
                .andExpect(status().isBadRequest());
    }

    public String getTokenForUser() throws Exception {
        String content = mockMvc.perform(post("/oauth/token")
                .with(httpBasic("client", "secret"))
                .queryParam("grant_type", "password")
                .queryParam("username", "john")
                .queryParam("password", "12345"))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String> contentMap = objectMapper.readValue(content, Map.class);

        return contentMap.get("access_token");
    }

    @Test
    public void testResourceServer() throws Exception {
        mockMvc.perform(get("/oauth/check_token")
                .param("token", getTokenForUser())
                .with(httpBasic("client", "secret")))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.user_name").value("john"))
                .andExpect(jsonPath("$.client_id").value("client"))
                .andExpect(jsonPath("$.authorities").value(containsInAnyOrder("ROLE_USER")))
                .andExpect(status().isOk());
    }

    @Test
    public void testResourceServer_WrongToken() throws Exception {
        mockMvc.perform(get("/oauth/check_token")
                .param("token", "invalidToken")
                .with(httpBasic("client", "secret")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testResourceServer_WrongClientPassword() throws Exception {
        mockMvc.perform(get("/oauth/check_token")
                .param("token", getTokenForUser())
                .with(httpBasic("client", "secret1")))
                .andExpect(status().isUnauthorized());
    }
}
