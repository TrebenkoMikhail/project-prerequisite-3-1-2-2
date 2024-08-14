package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.model.Role;
import habsida.spring.boot_security.demo.model.User;
import habsida.spring.boot_security.demo.repository.RoleRepository;
import habsida.spring.boot_security.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;
    private final ResourceLoader resourceLoader;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    @GetMapping("/allRoles")
    public ResponseEntity<List<Role>> getAllRoles(){
        List<Role> allRoles = roleRepository.findAll();
        return ResponseEntity.ok(allRoles);
    }
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    public RegisterController(UserService userService, RoleRepository roleRepository, ResourceLoader resourceLoader, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.resourceLoader = resourceLoader;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public ResponseEntity<String> getLoginUser() {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/login.html");
            byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String loginFormHtml = new String(fileData, StandardCharsets.UTF_8);
            return new ResponseEntity<>(loginFormHtml, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error loading login form: ", e);
            return new ResponseEntity<>("Error loading login form", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User user) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String role = authentication.getAuthorities().iterator().next().getAuthority();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Invalid username or password: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/register")
    public ResponseEntity<String> showRegisterForm(){
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/register.html");
            byte[] fileData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String registerFormHtml = new String(fileData, StandardCharsets.UTF_8);
            return new ResponseEntity<>(registerFormHtml, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error loading register form: ", e);
            return new ResponseEntity<>("Error loading register form", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.debug("Received user registration data: {}", user);
            userService.addUser(user);

            response.put("success", true);
            response.put("message", "User registered successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error registering user: ", e);
            response.put("success", false);
            response.put("message", "Error registering user.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
