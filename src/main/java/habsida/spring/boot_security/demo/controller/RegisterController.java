package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api")
public class RegisterController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        List<Role> allRoles = roleRepository.findAll();
        return ResponseEntity.ok(allRoles);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

}
