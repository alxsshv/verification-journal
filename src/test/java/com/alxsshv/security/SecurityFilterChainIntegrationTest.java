package com.alxsshv.security;

import com.alxsshv.journal.dto.JournalDto;
import com.alxsshv.journal.repository.JournalRepository;
import com.alxsshv.journal.repository.ProtocolRepository;
import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.dto.UserNoPassDto;
import com.alxsshv.security.initializer.RoleInitializer;
import com.alxsshv.security.initializer.UserInitializer;
import com.alxsshv.security.repository.RoleRepository;
import com.alxsshv.security.repository.UserRepository;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Set;

@SpringBootTest
public class SecurityFilterChainIntegrationTest {
    @Autowired
    private UserInitializer userInitializer;
    @Autowired
    private RoleInitializer roleInitializer;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private ProtocolRepository protocolRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DefaultRoleService defaultRoleService;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:14");

    @DynamicPropertySource
    public static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        POSTGRES.start();
    }

    @AfterAll
    public static void afterAll() {
        POSTGRES.stop();
    }

    @BeforeEach
    public void setMockMvc() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        roleInitializer.initialize();
        userInitializer.initialize();
    }

    @AfterEach
    public void clearDatabase() {
        protocolRepository.deleteAll();
        journalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test register user when user no authorized then get 201")
    public void testRegisterUser_whenUserNoAuthorized_thenGet201() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("password");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setPhoneNumber("user1number");
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        System.out.println(response.getContentAsString());
        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    @DisplayName("Test get registration page when user no authorized then 200")
    public void testGetRegistrationPage_whenUserIsNoAuthorized_then200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/registration"))
                .andReturn().getResponse();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.getContentAsString().isEmpty());
    }

    @Test
    @DisplayName("Test get login page when user no authorized then 200")
    public void testGetLoginPage_whenUserIsNoAuthorized_then200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/login"))
                .andReturn().getResponse();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.getContentAsString().isEmpty());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test journals when user has role VERIFICATION_EMPLOYEE then get 201")
    public void testJournals_whenUserIsVerificationEmployee_thenGet201() throws Exception {
        final JournalDto journalDto = new JournalDto();
        journalDto.setNumber("777");
        final String journalString = objectMapper.writeValueAsString(journalDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/journals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(journalString))
                .andReturn().getResponse();
        Assertions.assertEquals(201, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test journals when user has role SYSTEM_ADMIN then get 201")
    public void testJournals_whenUserIsSystemAdmin_thenGet201() throws Exception {
        final JournalDto journalDto = new JournalDto();
        journalDto.setNumber("777");
        final String journalString = objectMapper.writeValueAsString(journalDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/journals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(journalString))
                .andReturn().getResponse();
        Assertions.assertEquals(201, response.getStatus());
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test journals when user has not roles then get 403")
    public void testJournals_whenUserHasNotRoles_thenGet403() throws Exception {
        final JournalDto journalDto = new JournalDto();
        journalDto.setNumber("777");
        final String journalString = objectMapper.writeValueAsString(journalDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/journals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(journalString))
                .andReturn().getResponse();
        System.out.println(response.getErrorMessage());
        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Test journals when user no authorized then get 302")
    public void testJournals_whenUserNoAuthorized_thenGet302() throws Exception {
        final JournalDto journalDto = new JournalDto();
        journalDto.setNumber("777");
        final String journalString = objectMapper.writeValueAsString(journalDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/journals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(journalString))
                .andReturn().getResponse();
        Assertions.assertEquals(302, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test users/username when user has role VERIFICATION_EMPLOYEE then get 200")
    public void testUsername_whenUserIsVerificationEmployee_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/username"))
                .andReturn().getResponse();
        final UserNoPassDto currentUserDto = objectMapper.readValue(response.getContentAsString(), UserNoPassDto.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Root", currentUserDto.getUsername());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test users/username when user has role SYSTEM_ADMIN then get status 200")
    public void testUsername_whenUserIsSystemAdmin_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/username"))
                .andReturn().getResponse();
        final UserNoPassDto currentUserDto = objectMapper.readValue(response.getContentAsString(), UserNoPassDto.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Root", currentUserDto.getUsername());
    }

    @WithMockUser(value = "Root")
    @Test
    @DisplayName("Test users/username when user has not roles then get status 403")
    public void testUsername_whenUserIsSystemAdmin_thenGet403() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/username"))
                .andReturn().getResponse();
        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Test users/username when user no authorized then get status 302")
    public void testUsername_whenUserIsNoAuthorized_thenGet302() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/username"))
                .andReturn().getResponse();
        Assertions.assertEquals(302, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test /users when user has role SYSTEM_ADMIN then get status 201")
    public void testAddUser_whenUserIsSystemAdmin_thenGet201() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(modelMapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(201, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("test /users when user has role VERIFICATION_EMPLOYEE then get status 403")
    public void testAddUser_whenUserIsVerificationEmployee_thenGet403() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(modelMapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(403, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test get system admin page when user has role SYSTEM_ADMIN then get status 200")
    public void testGetSystemAdminPage_whenUserIsSystemAdmin_then200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/settings"))
                .andReturn().getResponse();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.getContentAsString().isEmpty());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test get system admin page when user has role VERIFICATION_EMPLOYEE then get status 403")
    public void testGetSystemAdminPage_whenUserIsVerificationEmployee_then403() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/settings"))
                .andReturn().getResponse();
        Assertions.assertEquals(403, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "")
    @Test
    @DisplayName("Test get system admin page when user has not roles then get status 403")
    public void testGetSystemAdminPage_whenUserHasNotRoles_then403() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/settings"))
                .andReturn().getResponse();
        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    @DisplayName("Test get system admin page when user no authorized then get status 302")
    public void testGetSystemAdminPage_whenUserNoAuthorized_then302() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/settings"))
                .andReturn().getResponse();
        Assertions.assertEquals(302, response.getStatus());
    }
}
