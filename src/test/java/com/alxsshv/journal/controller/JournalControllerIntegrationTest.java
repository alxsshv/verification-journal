package com.alxsshv.journal.controller;

import com.alxsshv.journal.dto.JournalDto;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.repository.JournalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class JournalControllerIntegrationTest {
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:14");
    private MockMvc mvc;

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
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        final Journal journal = new Journal();
        journal.setNumber("1");
        journal.setTitle("journal");
        journal.setDescription("description");
        journalRepository.save(journal);
    }

    @AfterEach
    public void clearAll() {
        journalRepository.deleteAll();
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test createJournal when create new journal with All fields then get 201")
    public void testCreateJournal_whenCreateNewJournalWithAllFields_thenGet201() throws Exception {
        final JournalDto journalDto = new JournalDto();
        journalDto.setNumber("777");
        journalDto.setTitle("NewJournal");
        journalDto.setDescription("Description");
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
    @DisplayName("Test createJournal when create new journal with only number field then get 201")
    public void testCreateJournal_whenCreateNewJournalWithOnlyNumberField_thenGet201() throws Exception {
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
    @DisplayName("Test createJournal when create new journal with empty number field then throw exception")
    public void testCreateJournal_whenCreateNewJournalWithEmptyNumberField_thenThrowException() throws Exception {
        final JournalDto journalDto = new JournalDto();
        journalDto.setNumber("");
        journalDto.setTitle("NewJournal");
        journalDto.setDescription("Description");
        final String journalString = objectMapper.writeValueAsString(journalDto);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .request(HttpMethod.POST, "/journals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(journalString)).andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test findAllJournals when get all journals then get 200 and not empty content list")
    public void testFindAllJournals_whenGetAllJournals_ThenGet200() throws Exception {
        final int expectedJournalCount = journalRepository.findAll().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/journals")).andReturn().getResponse();
        final int actualJournalCount = objectMapper.readValue(response.getContentAsString(), JournalDto[].class).length;
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedJournalCount, actualJournalCount);
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test findAllJournalsPageable when get all journals" +
            " with empty search then get 200 and not empty content list")
    public void testFindAllJournalsPageable_whenGetAllJournalsWithEmptySearch_ThenGet200() throws Exception {
        final String searchString = "";
        final String page = "0";
        final String size = "10";
        final String dir = "ASC";
        final Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.by(Sort.Direction.ASC, "number"));
        final int expectedJournalCount = journalRepository.findAll(pageable).getContent().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/pages")
                        .param("search", searchString)
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int actualJournalCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedJournalCount, actualJournalCount);
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test findAllJournalsPageable when get all journals " +
            "without pageable parameters then get 200 and not empty content list")
    public void testFindAllJournalsPageable_whenGetAllJournalsWithoutPageableParameters_ThenGet200() throws Exception {
        final String searchString = "";
        final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "number"));
        final int expectedJournalCount = journalRepository.findAll(pageable).getContent().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/pages")
                        .param("search", searchString))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int actualJournalCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedJournalCount, actualJournalCount);
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test findAllJournalsPageable when get all journals" +
            " with empty all parameters then get 200 and not empty content list")
    public void testFindAllJournalsPageable_whenGetAllJournalsWithEmptyAllArgs_ThenGet200() throws Exception {
        final String searchString = "";
        final String page = "";
        final String size = "";
        final String dir = "";
        final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "number"));
        final int expectedJournalCount = journalRepository.findAll(pageable).getContent().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/pages")
                        .param("search", searchString)
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int actualJournalCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedJournalCount, actualJournalCount);
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test findAllJournalsPageable when get all journals by searchString" +
            " with empty pageable parameters then get 200 and not empty content list")
    public void testFindAllJournalsPageable_whenGetAllJournalsBySearchStringWithEmptyPageableArgs_ThenGet200AndNotEmptyList() throws Exception {
        final String searchString = "ourn";
        final String page = "";
        final String size = "";
        final String dir = "";
        final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "number"));
        final int expectedJournalCount = journalRepository.findAll(pageable).getContent().size();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/pages")
                        .param("search", searchString)
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int actualJournalCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedJournalCount, actualJournalCount);
    }

    @WithMockUser("Root")
    @Test
    @DisplayName("Test findAllJournalsPageable when get all journals by searchString " +
            "with empty pageable parameters then get 200 and empty content list")
    public void testFindAllJournalsPageable_whenGetAllJournalsBySearchString_ThenGet200AndEmptyList() throws Exception {
        final String searchString = "xxxx";
        final String page = "";
        final String size = "";
        final String dir = "";
        final int expectedJournalCount = 0;
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/pages")
                        .param("search", searchString)
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        final int actualJournalCount = jsonObject.getInt("numberOfElements");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expectedJournalCount, actualJournalCount);
    }

    @WithMockUser
    @Test
    @DisplayName("Test update when journal found then get 200")
    public void testUpdateJournal_whenJournalFound_thenGet200() throws Exception {
        final Journal journalFromDb = journalRepository.findByNumber("1").get();
        final JournalDto updateJournalData = new JournalDto();
        updateJournalData.setId(journalFromDb.getId());
        updateJournalData.setNumber(journalFromDb.getNumber());
        updateJournalData.setTitle("newTitle");
        updateJournalData.setDescription("newDescription");
        final String updateJournalString = objectMapper.writeValueAsString(updateJournalData);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/journals/" + journalFromDb.getId())
                        .content(updateJournalString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        final String actualTitle = journalRepository.findById(journalFromDb.getId()).get().getTitle();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(updateJournalData.getTitle(), actualTitle);
    }

    @WithMockUser
    @Test
    @DisplayName("Test update when journal not found then get 404")
    public void testUpdateJournal_whenJournalNotFound_then404() throws Exception {
        final long journalid = 10000;
        final JournalDto updateJournalData = new JournalDto();
        updateJournalData.setId(journalid);
        updateJournalData.setNumber("number");
        updateJournalData.setTitle("newTitle");
        updateJournalData.setDescription("newDescription");
        final String updateJournalString = objectMapper.writeValueAsString(updateJournalData);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/journals/" + journalid)
                        .content(updateJournalString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }

    @WithMockUser
    @Test
    @DisplayName("Test update when update data journal number is empty then get 400")
    public void testUpdateJournal_whenUpdateDataJournalNumberIsEmpty_then404() throws Exception {
        final Journal journalFromDb = journalRepository.findByNumber("1").get();
        final JournalDto updateJournalData = new JournalDto();
        updateJournalData.setId(journalFromDb.getId());
        updateJournalData.setNumber("");
        updateJournalData.setTitle("newTitle");
        updateJournalData.setDescription("newDescription");
        final String updateJournalString = objectMapper.writeValueAsString(updateJournalData);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/journals/" + journalFromDb.getId())
                        .content(updateJournalString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser
    @Test
    @DisplayName("Test update when update data journal id and request parameter id is different then get 400")
    public void testUpdateJournal_whenUpdateIdAndRequestParamIdIsDifferent_then404() throws Exception {
        final Journal journalFromDb = journalRepository.findByNumber("1").get();
        final JournalDto updateJournalData = new JournalDto();
        updateJournalData.setId(journalFromDb.getId());
        updateJournalData.setNumber(journalFromDb.getNumber());
        updateJournalData.setTitle("newTitle");
        updateJournalData.setDescription("newDescription");
        final String updateJournalString = objectMapper.writeValueAsString(updateJournalData);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PUT, "/journals/" + 10000)
                        .content(updateJournalString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }
}
