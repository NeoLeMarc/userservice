package net.xcore.usermanagement.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xcore.usermanagement.userservice.dao.UserRepository;
import net.xcore.usermanagement.userservice.domain.User;
import net.xcore.usermanagement.userservice.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserService service;

  private UserRepository userRepositoryMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    userRepositoryMock = Mockito.mock(UserRepository.class);
    service.setRepository(userRepositoryMock);
  }

  @Test
  public void contextLoads(){
  }

  @Test
  @WithMockUser
  public void normalUserCanAccessNormalUserRequests() throws Exception {
    performNormalUserRequests(status().isOk());
  }
  @Test
  @WithMockUser
  public void normalUserCanNotAccessHelloAdminRequests() throws Exception {
    performAdminRequests(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = {"USER", "ADMIN"})
  public void adminUserCanAccessNormalUserAndAdminRequests() throws Exception {
    performNormalUserRequests(status().isOk());
    performAdminRequests(status().isOk());
  }


  private void performNormalUserRequests(ResultMatcher expectedResultMatcher) throws Exception {
    mockMvc.perform(get("/hellouser").contentType(MediaType.APPLICATION_JSON))
        .andExpect(expectedResultMatcher);
    mockMvc.perform(get("/user/testuser").contentType(MediaType.APPLICATION_JSON))
        .andExpect(expectedResultMatcher);
    mockMvc.perform(post("/user/verify").contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "foo")
        .param("password", "bar"))
        .andExpect(expectedResultMatcher);
    mockMvc.perform((get("/user/testuser").contentType(MediaType.APPLICATION_JSON)))
        .andExpect(expectedResultMatcher);
  }

  private void performAdminRequests(ResultMatcher expectedResultMatcher) throws Exception {
    mockMvc.perform(get("/helloadmin").contentType(MediaType.APPLICATION_JSON)).andExpect(
        expectedResultMatcher);
    performPostUserRequest(expectedResultMatcher);
  }

  @SuppressWarnings("OverlyBroadThrowsClause")
  private void performPostUserRequest(ResultMatcher matcher) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    User testUser = new User();
    String jsonUser = mapper.writeValueAsString(testUser);
    mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(jsonUser)
    ).andExpect(matcher);
  }
}
