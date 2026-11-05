package io.hexlet.cv.security.oauth;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = {
    "app.oauth.github.client-id=test-client-id",
    "app.oauth.github.client-secret=test-secret"
})
class GithubLoginEndpointTest {

    @Autowired
    private WebApplicationContext wac;

    @Test
    void startsOauthFlowOnGithub() throws Exception {
        var mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();

        mockMvc.perform(get("/oauth2/authorization/github"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith(
                        "https://github.com/login/oauth/authorize?response_type=code&client_id=test-client-id")));
    }
}
