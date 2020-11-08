package ru.codeinside.springsecuritytesttask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codeinside.springsecuritytesttask.models.*;
import ru.codeinside.springsecuritytesttask.repository.NoteRepository;
import ru.codeinside.springsecuritytesttask.repository.UserRepository;
import ru.codeinside.springsecuritytesttask.services.RoleService;

import java.util.Collections;


@Service
public class TestStubs {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private RoleService roleService;

    public User stubUserWithRoleUser() {
        Role role = roleService.findRoleByName(AuthoritiesConstants.USER);
        User user = new User();
        user.setLogin("john");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setRoles(Collections.singletonList(role));
        user.setAccessState(AccessState.ACTIVE);
        user.setFirstName("Джон");

        return userRepository.save(user);
    }

    public User stubUserWithRoleAdmin() {
        Role role = roleService.findRoleByName(AuthoritiesConstants.ADMIN);
        User user = new User();
        user.setLogin("galya");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setRoles(Collections.singletonList(role));
        user.setAccessState(AccessState.ACTIVE);
        user.setFirstName("Галина");

        return userRepository.save(user);
    }

    public Note stubNote(User user) {
        Note note = new Note();
        note.setUser(user);
        note.setTitle("Test Title");
        note.setText("Test text");
        note.setCreatedBy(user.getLogin());
        note.setLastModifiedBy(user.getLogin());

        return noteRepository.save(note);
    }

}
