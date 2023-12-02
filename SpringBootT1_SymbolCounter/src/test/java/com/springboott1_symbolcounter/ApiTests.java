package com.springboott1_symbolcounter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void checkApiWithOkString() throws Exception {
        var input = "aabbvbcdcdc";

        var mvcResult = performGetRequest(input);

        Assertions.assertEquals(200, mvcResult.getResponse().getStatus());
        Assertions.assertEquals("\"b\": 3, \"c\": 3, \"a\": 2, \"d\": 2, \"v\": 1", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void checkApiWithEmptyString() throws Exception {
        var input = "";

        var mvcResult = performGetRequest(input);

        Assertions.assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void checkApiWithTooLongString() throws Exception {
        var input = "*".repeat(100001);

        var mvcResult = performGetRequest(input);

        Assertions.assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void checkApiWithWrongContentType() throws Exception {
        var mvcResult = mockMvc.perform(
                        get("/api/counter").contentType("text/plain")
                )
                .andReturn();

        Assertions.assertEquals(415, mvcResult.getResponse().getStatus());
    }

    private MvcResult performGetRequest(String input) throws Exception {
        return mockMvc.perform(
                        get("/api/counter")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(input)
                )
                .andReturn();
    }
}
