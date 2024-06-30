package com.raddan.OldVK.controller;

import com.raddan.OldVK.dto.AuthDTO;
import com.raddan.OldVK.repository.ProfileRepository;
import com.raddan.OldVK.repository.RoleRepository;
import com.raddan.OldVK.repository.UserRepository;
import com.raddan.OldVK.service.AuthService;
import com.raddan.OldVK.service.UserService;
import com.redis.testcontainers.RedisContainer;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource("/application-dev.properties")
class ProfileControllerTest {

    @Value(value = "${custom.max.session}")
    private int MAX_SESSION;

    @Value(value = "${admin.username}")
    private String ADMIN_USERNAME;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MockMvc MOCK_MVC;

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("prototype_vk")
            .withUsername("postgres")
            .withPassword("7654");

    @Container
    private static final RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() {
        this.authService.register(new AuthDTO(ADMIN_USERNAME, "password"));
        this.authService.setAdminUsername(ADMIN_USERNAME);
        this.authService.setMaxSession(MAX_SESSION);
    }

    @AfterEach
    void after() {
        this.profileRepository.deleteAll();
        this.roleRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @WithMockUser(username = "raddan", roles = {"ADMIN"})
    void getProfile() throws Exception {
        this.MOCK_MVC
                .perform(get("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(jsonPath("$.username").value("raddan"));
    }

    @Test
    @Order(2)
    @WithMockUser(username = "raddan", roles = {"ADMIN"})
    void getProfileWithUsername() throws Exception {
        this.authService.register(new AuthDTO("userB", "userB"));
        this.MOCK_MVC
                .perform(get("/userB")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(jsonPath("$.username").value("userB"));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "raddan", roles = {"ADMIN"})
    void updateUser() throws Exception {
        AuthDTO authDTO = new AuthDTO(ADMIN_USERNAME, "newPassword"); // Assuming you want to update the password

        this.MOCK_MVC
                .perform(put("/profile/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authDTO.convertToJSON().toString()))
                .andExpect(status().isOk());

        this.MOCK_MVC
                .perform(get("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "newPassword").convertToJSON().toString())
                )
                .andExpect(jsonPath("$.username").value("raddan"));
    }

    @Test
    @Order(4)
    @WithMockUser(username = "raddan", roles = {"ADMIN"})
    void deleteUser() throws Exception {
        this.MOCK_MVC
                .perform(delete("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User 'raddan' deleted."));
    }
}
