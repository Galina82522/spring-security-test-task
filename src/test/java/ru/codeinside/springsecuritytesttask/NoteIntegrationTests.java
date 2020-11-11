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
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteAddedReqDTO;
import ru.codeinside.springsecuritytesttask.controllers.dto.NoteUpdatedReqDTO;
import ru.codeinside.springsecuritytesttask.exceptions.NoteNotFoundException;
import ru.codeinside.springsecuritytesttask.models.Note;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.repository.NoteRepository;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NoteIntegrationTests {
    private final String userUsername = "john";
    private final String adminUsername = "galya";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestStubs testStubs;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext wac;
    private User user;
    private User admin;
    private Note userNote;

    private Note adminNote;

    @BeforeTransaction
    public void setUpBeforeTransaction() {
        noteRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        user = testStubs.stubUserWithRoleUser();
        admin = testStubs.stubUserWithRoleAdmin();

        userNote = testStubs.stubNote(user);
        adminNote = testStubs.stubNote(admin);
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @AfterTransaction
    public void tearDown() {
        noteRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void addNoteTest_WithUser() throws Exception {
        NoteAddedReqDTO dto = new NoteAddedReqDTO();
        dto.setTitle("Test Title");
        dto.setText("Test Text");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", notNullValue()))
                .andExpect(jsonPath("title", equalTo("Test Title")))
                .andExpect(jsonPath("text", equalTo("Test Text")))
                .andExpect(jsonPath("created", notNullValue()))
                .andExpect(jsonPath("updated", notNullValue()))
                .andExpect(jsonPath("created_by", equalTo("john")))
                .andExpect(jsonPath("last_modified_by", equalTo("john")));
    }

    @WithAnonymousUser
    @Test
    public void addNoteTest_WithAnonymous() throws Exception {
        NoteAddedReqDTO dto = new NoteAddedReqDTO();
        dto.setTitle("Test Title");
        dto.setText("Test Text");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getNoteTest_WithUser() throws Exception {
        mockMvc.perform(get("/notes/" + userNote.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", equalTo(userNote.getId().intValue())))
                .andExpect(jsonPath("title", equalTo("Test Title")))
                .andExpect(jsonPath("text", equalTo("Test text")));
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getNoteTest_NotCurrentUserNote() throws Exception {
        mockMvc.perform(get("/notes/" + adminNote.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getNoteTest_NotExistNote() throws Exception {
        long id = userNote.getId();
        noteRepository.delete(userNote);
        mockMvc.perform(get("/notes/" + id))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getNoteTest_WithAdmin() throws Exception {
        mockMvc.perform(get("/notes/" + userNote.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", equalTo(userNote.getId().intValue())))
                .andExpect(jsonPath("title", equalTo("Test Title")))
                .andExpect(jsonPath("text", equalTo("Test text")));
    }

    @WithAnonymousUser
    @Test
    public void getNoteTest_WithAnonymous() throws Exception {
        mockMvc.perform(get("/notes/" + userNote.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getCurrentUserNotes_WithUser() throws Exception {
        noteRepository.deleteAllInBatch();
        Note note1 = testStubs.stubNote(user);
        Note note2 = testStubs.stubNote(user);

        mockMvc.perform(get("/notes/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].created_by", is(user.getLogin())))
                .andExpect(jsonPath("$.content[0].id", is(note1.getId().intValue())))
                .andExpect(jsonPath("$.content[1].created_by", is(user.getLogin())))
                .andExpect(jsonPath("$.content[1].id", is(note2.getId().intValue())));
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void getCurrentUserNotes_WithAdmin() throws Exception {
        mockMvc.perform(get("/notes/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].created_by", is(admin.getLogin())))
                .andExpect(jsonPath("$.content[0].id", is(adminNote.getId().intValue())));
    }

    @WithAnonymousUser
    @Test
    public void getCurrentUserNotes_WithAnonymous() throws Exception {
        mockMvc.perform(get("/notes/user"))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateNoteTest_WithUser() throws Exception {
        NoteUpdatedReqDTO dto = new NoteUpdatedReqDTO();
        dto.setTitle("Test");
        dto.setText("Test");

        String created_by = userNote.getCreatedBy();
        String last_modified_by = userNote.getLastModifiedBy();
        LocalDateTime created = userNote.getCreated();
        LocalDateTime updated = userNote.getUpdated();

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/notes/" + userNote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", notNullValue()))
                .andExpect(jsonPath("title", equalTo("Test")))
                .andExpect(jsonPath("text", equalTo("Test")));

        noteRepository.flush();
        Note updatedNote = noteRepository.findById(userNote.getId()).orElseThrow(
                () -> new NoteNotFoundException("Заметка не найдена")
        );

        assertEquals(created_by, updatedNote.getCreatedBy());
        assertEquals(last_modified_by, updatedNote.getLastModifiedBy());
        assertEquals(created, updatedNote.getCreated());
        assertNotEquals(updated, updatedNote.getUpdated());
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateNoteTest_WithAdmin() throws Exception {
        NoteUpdatedReqDTO dto = new NoteUpdatedReqDTO();
        dto.setTitle("Test");
        dto.setText("Test");

        String created_by = userNote.getCreatedBy();
        String last_modified_by = userNote.getLastModifiedBy();
        LocalDateTime created = userNote.getCreated();
        LocalDateTime updated = userNote.getUpdated();

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/notes/" + userNote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", notNullValue()))
                .andExpect(jsonPath("title", equalTo("Test")))
                .andExpect(jsonPath("text", equalTo("Test")));

        noteRepository.flush();
        Note updatedNote = noteRepository.findById(userNote.getId()).orElseThrow(
                () -> new NoteNotFoundException("Заметка не найдена")
        );

        assertEquals(created_by, updatedNote.getCreatedBy());
        assertNotEquals(last_modified_by, updatedNote.getLastModifiedBy());
        assertEquals(created, updatedNote.getCreated());
        assertNotEquals(updated, updatedNote.getUpdated());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateNoteTest_NotCurrentUserNote() throws Exception {
        NoteUpdatedReqDTO dto = new NoteUpdatedReqDTO();
        dto.setTitle("Test");
        dto.setText("Test");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/notes/" + adminNote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isMethodNotAllowed());
    }

    @WithAnonymousUser
    @Test
    public void updateNoteTest_WithAnonymousUser() throws Exception {
        NoteUpdatedReqDTO dto = new NoteUpdatedReqDTO();
        dto.setTitle("Test");
        dto.setText("Test");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/notes/" + adminNote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void updateNoteTest_NotExistNote() throws Exception {
        Long id = userNote.getId();
        noteRepository.delete(userNote);

        NoteUpdatedReqDTO dto = new NoteUpdatedReqDTO();
        dto.setTitle("Test");
        dto.setText("Test");

        String req = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/notes/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteNoteTest_WithUser() throws Exception {
        mockMvc.perform(delete("/notes/" + userNote.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("deleted", is(true)));
    }

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteNoteTest_WithAdmin() throws Exception {
        mockMvc.perform(delete("/notes/" + userNote.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("deleted", is(true)));
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteNoteTest_NotCurrentUserNote() throws Exception {
        mockMvc.perform(delete("/notes/" + adminNote.getId()))
                .andExpect(status().isMethodNotAllowed());
    }

    @WithUserDetails(value = userUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteNoteTest_NotExistNote() throws Exception {
        Long id = userNote.getId();
        noteRepository.delete(userNote);

        mockMvc.perform(delete("/notes/" + id))
                .andExpect(status().isBadRequest());
    }
}
