package org.example.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@TestPropertySource("/application-test.properties")
@Sql(value = {"/db/create_url_list_before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/db/create_url_list_after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void initPage() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//*[@id='urlList']/tr")
                        .nodeCount(2));
    }

    @Test
    public void generateUrlSuccess() throws Exception {
        String url = "https://github.com/AndrejsKrilovs/UrlConverter";

        mockMvc.perform(post("/")
                .param("originalUrl", url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//*[@id='urlList']/tr").nodeCount(3))
                .andExpect(xpath("//*[@id='urlList']/tr[1]/td[1]").exists())
                .andExpect(xpath("//*[@id='urlList']/tr[1]/td[2]").string(url));
    }

    @Test
    public void generateUrlFail() throws Exception {
        String url = "https://www.github.com/AndrejsKrilovs/UrlConverter";

        mockMvc.perform(post("/")
                .param("originalUrl", url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//*[@id='urlList']/tr").nodeCount(2))
                .andExpect(xpath("//*[@id='urlList']/tr[1]/td[2]").string(not(url)));
    }

    @Test
    public void filterUrlValues() throws Exception {
        mockMvc.perform(get("/filter").param("param", "goo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//*[@id='urlList']/tr").nodeCount(1))
                .andExpect(xpath("//*[@id='urlList']/tr/td[2]").string("https://google.com"));
    }

    @Test
    public void emptyFilter() throws Exception {
        mockMvc.perform(get("/filter").param("param", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//*[@id='urlList']/tr").nodeCount(2))
                .andExpect(xpath("//*[@id='urlList']/tr[1]/td[2]").string("https://spring.io/"));
    }

    @Test
    public void redirectToOriginalUrl() throws Exception {
        mockMvc.perform(get("/{key}", "htrd7jr"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://google.com"));
    }
}