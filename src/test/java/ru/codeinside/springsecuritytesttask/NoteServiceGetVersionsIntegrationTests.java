package ru.codeinside.springsecuritytesttask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;
import ru.codeinside.springsecuritytesttask.models.Note;
import ru.codeinside.springsecuritytesttask.models.User;
import ru.codeinside.springsecuritytesttask.repository.NoteRepository;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;
import ru.codeinside.springsecuritytesttask.services.NoteService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(propagation = NOT_SUPPORTED)
public class NoteServiceGetVersionsIntegrationTests {
    @Autowired
    private TestStubs testStubs;

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Note userNote;

    @Before
    public void setUp() {
        noteRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        user = testStubs.stubUserWithRoleUser();
        testStubs.stubUserWithRoleAdmin();

        userNote = testStubs.stubNote(user);

    }

    @AfterTransaction
    public void tearDown() {
        noteRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    public void getNoteVersions() {
        userNote.setText("New text");
        noteRepository.save(userNote);
        Pageable pageable = PageRequest.of(0, 10);

        List<Note> noteVersions = noteService.getNoteVersions(pageable, userNote.getId()).getContent();

        assertThat(noteVersions, hasSize(2));
        Note note1 = noteVersions.get(0);
        assertEquals("Test text", note1.getText());

        assertThat(noteVersions, hasSize(2));
        Note note2 = noteVersions.get(1);
        assertEquals("New text", note2.getText());
    }

    @Test
    public void getNoteVersions_NotExistId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Note> noteVersions = noteService.getNoteVersions(pageable, userNote.getId() + 1).getContent();
        assertTrue(noteVersions.isEmpty());
    }

}
