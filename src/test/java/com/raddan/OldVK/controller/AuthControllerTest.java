package com.raddan.OldVK.controller;

import com.raddan.OldVK.dto.AuthDTO;
import com.raddan.OldVK.repository.RoleRepository;
import com.raddan.OldVK.repository.UserRepository;
import com.raddan.OldVK.service.AuthService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource("/application-dev.properties")
class AuthControllerTest {

    @Value(value = "${custom.max.session}")
    private int MAX_SESSION;

    @Value(value = "${admin.username}")
    private String ADMIN_USERNAME;

    @Autowired
    private MockMvc MOCK_MVC;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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
        this.roleRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    @Order(1)
    void register() throws Exception {
        this.MOCK_MVC
                .perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO("yes@yes.com", "password").convertToJSON().toString())
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("Register!"));
    }

    @Test
    @Order(2)
    void login() throws Exception {
        MvcResult login = this.MOCK_MVC
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Logged In!"))
                .andReturn();

        this.MOCK_MVC
                .perform(get("/authenticated")
                        .cookie(login.getResponse().getCookie("JSESSIONID"))
                )
                .andExpect(status().isOk());
    }

    // Simulate max session set to 1
    @Test
    @Order(3)
    void test_max_session_for_multiple_login_request() throws Exception {
        if (MAX_SESSION != 1) {
            return;
        }

        // Simulate login from browser 1
        MvcResult login1 = this.MOCK_MVC
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie1 = login1.getResponse().getCookie("JSESSIONID");
        assert cookie1 != null;

        this.MOCK_MVC
                .perform(get("/authenticated")
                        .cookie(cookie1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Admin name is " + ADMIN_USERNAME));

        // Simulate login from browser 2
        MvcResult login2 = this.MOCK_MVC
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie2 = login2.getResponse().getCookie("JSESSIONID");
        assert cookie2 != null;

        // Expired cookie from browser 1
        this.MOCK_MVC
                .perform(get("/authenticated")
                        .cookie(cookie1)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Full authentication is required to access this resource")
                )
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));

        // Valid cookie from browser 2
        this.MOCK_MVC
                .perform(get("/authenticated").cookie(cookie2))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void validate_logout_route() throws Exception {
        // When
        MvcResult login = this.MOCK_MVC
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new AuthDTO(ADMIN_USERNAME, "password").convertToJSON().toString())
                )
                .andExpect(status().isOk())
                .andReturn();

        // Cookie
        Cookie cookie = login.getResponse().getCookie("JSESSIONID");
        assert cookie != null;

        this.MOCK_MVC
                .perform(get("/logout").cookie(cookie))
                .andExpect(status().isOk());

        // Access protected route with invalid cookie
        this.MOCK_MVC
                .perform(get("/authenticated").cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Full authentication is required to access this resource")
                )
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));
    }

    @Test
    @Order(5)
    @WithMockUser(username = "test@test.com", password = "password", authorities = {"USER"})
    void role_employee_access_role_admin_route() throws Exception {
        this.MOCK_MVC
                .perform(get("/authenticated"))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
                .andExpect(result ->
                        assertEquals(
                                "Access Denied",
                                Objects.requireNonNull(result.getResolvedException()).getMessage()
                        )
                );
    }

    @Test
    @Order(6)
    void unauthenticated_user_hits_protected_routes() throws Exception {
        this.MOCK_MVC
                .perform(get("/authenticated"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Full authentication is required to access this resource")
                )
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));
    }
}
