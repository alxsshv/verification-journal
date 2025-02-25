package com.alxsshv.security.controller;

import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.dto.UserDto;
import com.alxsshv.security.dto.UserNoPassDto;
import com.alxsshv.security.model.User;
import com.alxsshv.security.repository.UserRepository;
import com.alxsshv.security.service.interfaces.DefaultRoleService;
import com.alxsshv.security.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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
public class UserControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultRoleService defaultRoleService;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;
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
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    public void fillDatabase() {
        final UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setName("testusername");
        userDto.setSurname("testusersurname");
        userDto.setPatronymic("testuserpatronymic");
        userDto.setChecked(false);
        userDto.setEnabled(false);
        userDto.setPhoneNumber("number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        userService.create(userDto);
    }

    @AfterEach
    public void clearDatabase() {
        userRepository.deleteAll();
    }

    @WithMockUser(value = "testuser", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getCurrentUsername when get request then get 200 and root user")
    public void testGetCurrentUserName_whenGetRequest_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/username"))
                .andReturn().getResponse();
        final UserNoPassDto currentUserDto = objectMapper.readValue(response.getContentAsString(), UserNoPassDto.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("testuser", currentUserDto.getUsername());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUsersPageableList when get with all parameters then get 200 and match not found")
    public void testGetUsersPageableList_whenGetWithAllParameters_thenGet200AndNotMatch() throws Exception {
        final String page = "0";
        final String size = "10";
        final String direction = "ASC";
        final String searchString = "xxxxxxx";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/pages")
                        .param("page", page)
                        .param("size", size)
                        .param("dir", direction)
                        .param("search", searchString))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int count = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(0, count);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUsersPageableList when get request with only searchString then get 200 and match found")
    public void testGetUsersPageableList_whenGetRequestWithSearchStringOnly_thenGet200() throws Exception {
        final String searchString = "surname";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/pages")
                        .param("search", searchString))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int count = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(1, count);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUsersPageableList when get request with no parameters then get 200 and matches found")
    public void testGetUsersPageableList_whenGetRequestWithNoParameters_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/pages"))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int count = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(count > 0);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUsersPageableList when get request with all parameters then get 200 and match found")
    public void testGetUsersPageableList_whenGetRequestWithAllParameters_thenGet200() throws Exception {
        final String page = "0";
        final String size = "10";
        final String direction = "ASC";
        final String searchString = "surname";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/pages")
                        .param("page", page)
                        .param("size", size)
                        .param("dir", direction)
                        .param("search", searchString))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int count = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(1, count);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test findWaitingCheckUsers when get users then get status 200")
    public void testFindWaitingCheckUsers_whenGetWaitingCheckUsers_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/pages/wait"))
                .andReturn().getResponse();
        final JSONObject jsonRequest = new JSONObject(response.getContentAsString());
        final int usersCount = jsonRequest.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(1, usersCount);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test searchUser when searchString is null no matchers get status 200")
    public void testSearchUser_whenSearchStringIsNull_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/search"))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test searchUser when searchString has no matchers get status 200")
    public void testSearchUser_whenSearchStringHasNoMatches_thenGet200() throws Exception {
        final String searchString = "xxxxx";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/search")
                        .param("search", searchString))
                .andReturn().getResponse();
        final UserNoPassDto[] users = objectMapper.readValue(response.getContentAsString(), UserNoPassDto[].class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(0, users.length);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test searchUser when get empty searchString then get status 400")
    public void testSearchUser_whenGetEmptySearchString_thenGet200() throws Exception {
        final String searchString = "";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/search")
                        .param("search", searchString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test searchUser when get not empty searchString then get status 200")
    public void testSearchUser_whenGetNotEmptySearchString_thenGet200() throws Exception {
        final String searchString = "surname";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/search")
                        .param("search", searchString))
                .andReturn().getResponse();
        final UserNoPassDto[] users = objectMapper.readValue(response.getContentAsString(), UserNoPassDto[].class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(users.length > 0);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test findWaitingCheckUsersCount when get users count then get status 200")
    public void testFindWaitingCheckUsersCount_whenGet_thenGet200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/users/wait/count"))
                .andReturn().getResponse();
        final int count = Integer.parseInt(response.getContentAsString());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(count > 0);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUserListWithoutPageable when get Users then get status 200")
    public void testGetUserListWithoutPageable_whenGetUsers_thenGet200() throws Exception {
        final int expectedUsersCount = userRepository.findAll().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/users"))
                .andReturn().getResponse();
        final UserDto[] users = objectMapper.readValue(response.getContentAsString(), UserDto[].class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(expectedUsersCount > 0);
        Assertions.assertEquals(expectedUsersCount, users.length);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUser when user is found by id then get status 200")
    public void testGetUser_whenUserIsFound_thenGet200() throws Exception {
        final User userFromDb = userRepository.findByUsername("testuser").get();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/" + userFromDb.getId()))
                .andReturn().getResponse();
        final UserNoPassDto actualUserDto = objectMapper.readValue(response.getContentAsString(), UserNoPassDto.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(userFromDb.getId(), actualUserDto.getId());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUser when user is not found by id then get status 404")
    public void testGetUser_whenUserIsNotFound_thenGet404() throws Exception {
        final long userid = 10000L;
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .request(HttpMethod.GET, "/users/" + userid))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test getUser when user is not correct by id then get status 400")
    public void testGetUser_whenUserIdIsNotCorrect_thenGet400() throws Exception {
        final long userid = -1L;
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/users/" + userid))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());

    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test register user when register userDto with all fields then get 201")
    public void testRegisterUser_whenRegisterUser_thenGet201() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        final User actualUser = userRepository.findByUsername("user1").get();
        Assertions.assertEquals(201, response.getStatus());
        Assertions.assertEquals(userDto.getSurname(), actualUser.getSurname());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test register user when user already exist then get 400")
    public void testRegisterUser_whenUserAlreadyExist_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("Test register user when password is empty then get 400")
    public void testRegisterUser_whenPasswordIsEmpty_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when userDto has all fields then get status 201")
    public void testAddUser_whenUserDtoHasAllFields_thenGet201() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(201, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when userDto roles is null then get status 400")
    public void testAddUser_whenUserRolesIsNull_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when user already exist then get status 400")
    public void testAddUser_whenUserAlreadyExist_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when password is null then get status 400")
    public void testAddUser_whenPasswordIsNull_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when password length is less than 5 then get status 400")
    public void testAddUser_whenPasswordLengthIsLessThan5_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("123");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when name is null then get status 400")
    public void testAddUser_whenNameIsNull_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when surname is empty then get status 400")
    public void testAddUser_whenSurnameIsEmpty_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test addUser when patronymic is empty then get status 400")
    public void testAddUser_whenPatronymicIsEmpty_thenGet400() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        userDto.setPassword("user1");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.POST, "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test editUser when id is null then get status 404")
    public void testEditUser_whenIdIsNull_thenGet404() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test editUser when id is not found then get status 404")
    public void testEditUser_whenUserNotFound_thenGet404() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setId(19999L);
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setName("name");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test editUser when name is empty then get status 400")
    public void testEditUser_whenNameIsEmpty_thenGet400() throws Exception {
        final User userFromDb = userRepository.findByUsername("testuser").get();
        final UserDto userDto = new UserDto();
        userDto.setId(userFromDb.getId());
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setName("");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test editUser when name is null then get status 400")
    public void testEditUser_whenNameIsNull_thenGet400() throws Exception {
        final User userFromDb = userRepository.findByUsername("testuser").get();
        final UserDto userDto = new UserDto();
        userDto.setId(userFromDb.getId());
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setSurname("surname");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test editUser when put full data then get status 200")
    public void testEditUser_whenPutFullData_thenGet200() throws Exception {
        final User userFromDb = userRepository.findByUsername("testuser").get();
        final UserDto userDto = new UserDto();
        userDto.setId(userFromDb.getId());
        userDto.setUsername("testuser");
        userDto.setPassword("testuser");
        userDto.setSurname("updated" + userFromDb.getSurname());
        userDto.setName("name");
        userDto.setPatronymic("patronymic");
        userDto.setChecked(true);
        userDto.setEnabled(true);
        userDto.setPhoneNumber("user1number");
        userDto.setRoles(Set.of(mapper.map(defaultRoleService.getDefaultRole(), RoleDto.class)));
        final String userDtoString = objectMapper.writeValueAsString(userDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/users/" + userFromDb.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoString))
                .andReturn().getResponse();
        final User editedUser = userRepository.findById(userFromDb.getId()).get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotEquals(userFromDb.getSurname(), editedUser.getSurname());
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test deleteUser when send correct id then get status 200")
    public void testDeleteUser_whenSendCorrectId_thenGet200() throws Exception {
        final User userFromDb = userRepository.findByUsername("testuser").get();
        final int expectedCount = userRepository.findAll().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.DELETE, "/users/" + userFromDb.getId()))
                                        .andReturn().getResponse();
        final int actualCount = userRepository.findAll().size();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotEquals(expectedCount, actualCount);
    }

    @WithMockUser(value = "Root", roles = "SYSTEM_ADMIN")
    @Test
    @DisplayName("test deleteUser when sendid=0 then get status 400")
    public void testDeleteUser_whenSend0_thenGet400() throws Exception {
        final int expectedCount = userRepository.findAll().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.DELETE, "/users/0"))
                .andReturn().getResponse();
        final int actualCount = userRepository.findAll().size();
        Assertions.assertEquals(400, response.getStatus());
        Assertions.assertEquals(expectedCount, actualCount);
    }
}
