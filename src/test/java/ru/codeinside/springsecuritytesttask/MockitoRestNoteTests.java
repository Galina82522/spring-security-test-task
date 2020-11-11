package ru.codeinside.springsecuritytesttask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import ru.codeinside.springsecuritytesttask.controllers.dto.AckDTO;
import ru.codeinside.springsecuritytesttask.models.Note;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.repository.NoteRepository;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;
import ru.codeinside.springsecuritytesttask.services.NoteService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MockitoRestNoteTests {
    private final String adminUsername = "galya";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestStubs testStubs;
    @Autowired
    private NoteRepository noteRepository;
    @MockBean
    private NoteService noteService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext wac;
    private User user;
    private Note userNote;

    @BeforeTransaction
    public void setUpBeforeTransaction() {
        noteRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        user = testStubs.stubUserWithRoleUser();
        testStubs.stubUserWithRoleAdmin();

        userNote = testStubs.stubNote(user);
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

    @WithUserDetails(value = adminUsername, userDetailsServiceBeanName = "userDetailsService")
    @Test
    public void deleteNoteTest_WithAdmin() throws Exception {
        when(noteService.deleteNote(userNote.getId())).thenReturn(new AckDTO());

        mockMvc.perform(delete("/notes/" + userNote.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("deleted", is(true)));

        verify(noteService, times(1)).deleteNote(userNote.getId());
    }

    @WithAnonymousUser
    @Test
    public void deleteNoteTest_WithAnonymousUser() throws Exception {
        mockMvc.perform(delete("/notes/" + userNote.getId()))
                .andExpect(status().isForbidden());

        verify(noteService, times(0)).deleteNote(any());
    }
}
