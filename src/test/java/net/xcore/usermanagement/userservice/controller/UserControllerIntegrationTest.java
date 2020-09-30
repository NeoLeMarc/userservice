package net.xcore.usermanagement.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xcore.usermanagement.userservice.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  public void contextLoads(){
  }

  @Test
  @WithMockUser
  public void normalUserCanAccessNormalUserRequests() throws Exception {
    performNormalUserRequests();
  }

  private void performNormalUserRequests() throws Exception {
    mockMvc.perform(get("/hellouser").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    mockMvc.perform(get("/user/testuser").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    mockMvc.perform(post("/user/verify").contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "foo")
        .param("password", "bar"))
        .andExpect(status().isOk());
    mockMvc.perform((get("/user/testuser").contentType(MediaType.APPLICATION_JSON)))
        .andExpect(status().isOk());
  }

  @SuppressWarnings("OverlyBroadThrowsClause")
  @Test
  @WithMockUser
  public void normalUserCanNotAccessHelloAdminRequests() throws Exception {
    performAdminRequests();
  }

  private void performAdminRequests() throws Exception {
    mockMvc.perform(get("/helloadmin").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    performPostUserRequest();
  }

  private void performPostUserRequest() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    User testUser = new User();
    String jsonUser = mapper.writeValueAsString(testUser);
    mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(jsonUser)
    ).andExpect(status().isForbidden());
  }


}
