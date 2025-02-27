package com.alxsshv.journal.controller;

import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.dto.ProtocolFileInfo;
import com.alxsshv.journal.model.Journal;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.journal.repository.JournalRepository;
import com.alxsshv.journal.repository.ProtocolRepository;
import com.alxsshv.journal.service.interfaces.ProtocolServiceFacade;
import com.alxsshv.security.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
public class ProtocolControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProtocolRepository protocolRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProtocolServiceFacade protocolServiceFacade;
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
    public void setMockMvc() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    public void fillDatabase() throws IOException {
        final Journal journal = new Journal();
        journal.setNumber("number");
        journal.setDescription("journalDescription");
        journal.setTitle("journal");
        journalRepository.save(journal);
        final MockMultipartFile testFile = new MockMultipartFile(
                "files", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test pdf".getBytes());
        final ProtocolFileInfo protocolInfo = new ProtocolFileInfo();
        protocolInfo.setNumber("55555");
        protocolInfo.setDescription("testDescription");
        protocolInfo.setMiModification("testModification");
        protocolInfo.setMiSerialNum("testSerialNum");
        protocolInfo.setVerificationDate(LocalDate.now().toString());
        protocolInfo.setJournalId(journalRepository.findByNumber("number").get().getId());
        protocolInfo.setVerificationEmployeeId(userRepository.findByUsername("Root").get().getId());
        protocolServiceFacade.upload(testFile, protocolInfo);
    }

    @AfterEach
    public void clearDatabase() {
        protocolRepository.deleteAll();
        journalRepository.deleteAll();
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test addProtocol when file uploaded then gen 201")
    public void testAddProtocol_whenFileUploaded_thenGet201() throws Exception {
        final long journalId = journalRepository.findByNumber("number").get().getId();
        final long userId = userRepository.findByUsername("Root").get().getId();
        final MockMultipartFile file = new MockMultipartFile("files",
                "file.txt", MediaType.TEXT_PLAIN_VALUE, "this text".getBytes());
        final ProtocolFileInfo protocolFileInfo = new ProtocolFileInfo();
        protocolFileInfo.setNumber("777");
        protocolFileInfo.setDescription("description");
        protocolFileInfo.setJournalId(journalId);
        protocolFileInfo.setMiModification("multimeter");
        protocolFileInfo.setMiSerialNum("serialNum");
        protocolFileInfo.setVerificationDate(LocalDate.now().toString());
        protocolFileInfo.setVerificationEmployeeId(userId);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .multipart(HttpMethod.POST, "/journals/protocols/form")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("protocolInfo", objectMapper.writeValueAsString(protocolFileInfo)))
                .andReturn().getResponse();
        Assertions.assertEquals(201, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test addProtocol when file is null then gen 400")
    public void testAddProtocol_whenFileIsNull_thenGet400() throws Exception {
        final long journalId = journalRepository.findByNumber("number").get().getId();
        final long userId = userRepository.findByUsername("Root").get().getId();
        final ProtocolFileInfo protocolFileInfo = new ProtocolFileInfo();
        protocolFileInfo.setNumber("777");
        protocolFileInfo.setDescription("description");
        protocolFileInfo.setJournalId(journalId);
        protocolFileInfo.setMiModification("multimeter");
        protocolFileInfo.setMiSerialNum("serialNum");
        protocolFileInfo.setVerificationDate(LocalDate.now().toString());
        protocolFileInfo.setVerificationEmployeeId(userId);
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .multipart(HttpMethod.POST, "/journals/protocols/form")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("protocolInfo", objectMapper.writeValueAsString(protocolFileInfo)))
                .andReturn().getResponse();
        System.out.println(response.getContentAsString());
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test addProtocol when protocolInfo is null then get status 400")
    public void testAddProtocol_whenProtocolInfoIsNull_thenGet400() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("files",
                "file.txt", MediaType.TEXT_PLAIN_VALUE, "this text".getBytes());
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .multipart(HttpMethod.POST, "/journals/protocols/form")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();
        System.out.println(response.getContentAsString());
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getAllProtocolsForJournal when get pages with empty searchString then get 200 and not empty page")
    public void testGetAllProtocolsForJournal_whenSearchStringEmpty_thenGet200() throws Exception {
        final String page = "0";
        final String size = "10";
        final String dir = "ASC";
        final String searchString = "";
        final String journalId = String.valueOf(journalRepository.findByNumber("number").get().getId());
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/pages")
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir)
                        .param("search", searchString)
                        .param("journal", journalId))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(jsonObject.getInt("numberOfElements") > 0);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getAllProtocolsForJournal when searchString found  get 200 and not empty page")
    public void testGetAllProtocolsForJournal_whenSearchStringFound_thenGet200() throws Exception {
        final String page = "0";
        final String size = "10";
        final String dir = "ASC";
        final String searchString = "test";
        final String journalId = String.valueOf(journalRepository.findByNumber("number").get().getId());
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/pages")
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir)
                        .param("search", searchString)
                        .param("journal", journalId))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(jsonObject.getInt("numberOfElements") > 0);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getAllProtocolsForJournal when searchString found  get 200 and empty page")
    public void testGetAllProtocolsForJournal_whenSearchStringFound_thenGet200andEmptyPage() throws Exception {
        final String page = "0";
        final String size = "10";
        final String dir = "ASC";
        final String searchString = "xxxxx";
        final String journalId = String.valueOf(journalRepository.findByNumber("number").get().getId());
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/pages")
                        .param("page", page)
                        .param("size", size)
                        .param("dir", dir)
                        .param("search", searchString)
                        .param("journal", journalId))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(jsonObject.getInt("numberOfElements") == 0);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getAllProtocolsForJournal when pages parameters is null get 200 and empty page")
    public void testGetAllProtocolsForJournal_whenPagesParametersIsNull_thenGet200andEmptyPage() throws Exception {
        final String journalId = String.valueOf(journalRepository.findByNumber("number").get().getId());
        final String searchString = "";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/pages")
                        .param("journal", journalId)
                        .param("search", searchString))
                .andReturn().getResponse();
        final JSONObject jsonObject = new JSONObject(response.getContentAsString());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(jsonObject.getInt("numberOfElements") > 0);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getAllProtocolsForJournal when journal id set null get 400")
    public void testGetAllProtocolsForJournal_whenJournalIdIsNull_thenGet400() throws Exception {
        final String searchString = "";
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/pages")
                        .param("search", searchString))
                .andReturn().getResponse();
        Assertions.assertEquals(400, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getProtocolFile when file found then get status 200")
    public void testGetProtocolFile_whenFileFound_thenGetStatus200() throws Exception {
        final Journal journal = journalRepository.findByNumber("number").get();
        final long protocolId = protocolRepository.findByJournal(journal).getFirst().getId();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/" + protocolId))
                .andReturn().getResponse();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.getContentAsString().isEmpty());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getProtocolFile when file not found then get status 200")
    public void testGetProtocolFile_whenFileNotFound_thenGetStatus200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/9999"))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getProtocolFile when pdf file not correct (not pdf) then get status 500")
    public void testSigningProtocolFile_whenFileNotCorrect_thenGetStatus500() throws Exception {
        final Journal journal = journalRepository.findByNumber("number").get();
        final long protocolId = protocolRepository.findByJournal(journal).getFirst().getId();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/signing/" + protocolId))
                .andReturn().getResponse();
        System.out.println(response.getContentAsString());
        Assertions.assertEquals(500, response.getStatus());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getWaitingSignProtocolsCount when send request then get status 200")
    public void testGetWaitingSignProtocolsCount_whenSendRequest_thenGetStatus200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/signing/wait/count"))
                .andReturn().getResponse();
        final int count = objectMapper.readValue(response.getContentAsString(), Integer.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(count > 0);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test getWaitingSignProtocols when send request then get status 200 and list not empty")
    public void testGetWaitingSignProtocols_whenSendRequest_thenGetStatus200AndListNotEmpty() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/journals/protocols/signing/wait"))
                .andReturn().getResponse();
        final ProtocolDto[] protocols = objectMapper.readValue(response.getContentAsString(), ProtocolDto[].class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(protocols.length > 0);
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test deleteProtocolById when protocol found then get status 200")
    public void testDeleteProtocolById_whenProtocolFound_thenGetStatus200() throws Exception {
        final Journal journal = journalRepository.findByNumber("number").get();
        final long protocolId = protocolRepository.findByJournal(journal).getFirst().getId();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.DELETE, "/journals/protocols/" + protocolId))
                .andReturn().getResponse();
        final Optional<Protocol> protocol = protocolRepository.findById(1L);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(protocol.isEmpty());
    }

    @WithMockUser(value = "Root", roles = "VERIFICATION_EMPLOYEE")
    @Test
    @DisplayName("Test deleteProtocolById when protocol not found then get status 200")
    public void testDeleteProtocolById_whenProtocolNotFound_thenGetStatus200() throws Exception {
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.DELETE, "/journals/protocols/9999"))
                .andReturn().getResponse();
        Assertions.assertEquals(404, response.getStatus());
    }
}
