package com.alxsshv.security.controller;

import com.alxsshv.security.dto.RoleDto;
import com.alxsshv.security.model.Role;
import com.alxsshv.security.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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

@SpringBootTest
public class RoleControllerIntegrationTest {
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private WebApplicationContext context;
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14");

    @DynamicPropertySource
    public static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("test getRoleList when get roles list then get status 200")
    public void testGetRoleList_whenGetRoleList_thenGetStatus200() throws Exception {
        final int expectedRoleListSize = roleRepository.findAll().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        final RoleDto[] roles = objectMapper.readValue(response.getContentAsString(), RoleDto[].class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedRoleListSize, roles.length);
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("test getRoleById when get role by id then get status code 200")
    public void testGetRoleById_whenGetRoleById_thenStatus200() throws Exception {
        final long roleId = 1;
        final Role expectedRole = roleRepository.findById(roleId).get();
        final MockHttpServletResponse response = mvc
                .perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/roles/" + roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        final RoleDto actualRoleDto = objectMapper.readValue(response.getContentAsString(), RoleDto.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedRole.getPseudonym(), actualRoleDto.getPseudonym());
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("test getRoleById when get role by wrong id then get status code 404")
    public void testGetRoleById_whenGetRoleById_thenStatus404() throws Exception {
        final long roleId = -1;
        final MockHttpServletResponse response = mvc
                .perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/roles/" + roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }
}
