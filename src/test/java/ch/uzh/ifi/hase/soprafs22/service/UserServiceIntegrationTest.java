package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */

@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void getUsers_vaildInput_success(){
      // given
      assertNull(userRepository.findByUsername("testUsername"));

      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      User createdUser = userService.createUser(testUser);

      // when
      List<User> userList = userService.getUsers();

      // then
      assertEquals(testUser.getId(), userList.get(0).getId());
      assertEquals(testUser.getName(), userList.get(0).getName());
      assertEquals(testUser.getUsername(), userList.get(0).getUsername());
      assertEquals(testUser.getPassword(),userList.get(0).getPassword());
      assertNotNull(userList.get(0).getToken());
      assertEquals(UserStatus.ONLINE, userList.get(0).getStatus());
  }

  @Test
  public void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getPassword(),createdUser.getPassword());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setName("testName2");
    testUser2.setUsername("testUsername");
    testUser2.setPassword("password");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
  @Test
    public void authenticateUser_validInput_success(){
      assertNull(userRepository.findByUsername("testUsername"));

      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      User createdUser = userService.createUser(testUser);

      // when
      User testUser2 = new User();
      testUser2.setName("testName");
      testUser2.setUsername("testUsername");
      testUser2.setPassword("password");
      User authenticatedUser = userService.authanticateUser(testUser2);

      // then
      assertEquals(testUser.getId(), authenticatedUser.getId());
      assertEquals(testUser.getName(), authenticatedUser.getName());
      assertEquals(testUser.getUsername(), authenticatedUser.getUsername());
      assertEquals(testUser.getPassword(),authenticatedUser.getPassword());
      assertNotNull(createdUser.getToken());
      assertEquals(UserStatus.ONLINE, authenticatedUser.getStatus());
  }
  @Test
  public void logout_validUser_success(){
      assertNull(userRepository.findByUsername("testUsername"));

      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      User createdUser = userService.createUser(testUser);

      //when
      userService.logout(createdUser);

      //then
      assertEquals(testUser.getId(), createdUser.getId());
      assertEquals(testUser.getName(), createdUser.getName());
      assertEquals(testUser.getUsername(), createdUser.getUsername());
      assertEquals(testUser.getPassword(),createdUser.getPassword());
      assertNotNull(createdUser.getToken());
      assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }
  @Test
  public void getUserByIDNum_validLond_success(){
      assertNull(userRepository.findByUsername("testUsername"));

      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      User createdUser = userService.createUser(testUser);

      //when
      User userByIDNum = userService.getUserByIDNum(createdUser.getId());

      //then
      assertEquals(testUser.getId(), userByIDNum.getId());
      assertEquals(testUser.getName(), userByIDNum.getName());
      assertEquals(testUser.getUsername(), userByIDNum.getUsername());
      assertEquals(testUser.getPassword(), userByIDNum.getPassword());
      assertNotNull(createdUser.getToken());
      assertEquals(UserStatus.ONLINE, userByIDNum.getStatus());
  }
  @Test
  public void getUserByIDNum_invalidLong_fail(){
      assertNull(userRepository.findByUsername("testUsername"));

      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      User createdUser = userService.createUser(testUser);
      //User user = userService.getUserByIDNum(testUser.getId()+1L);
      //when
      assertThrows(ResponseStatusException.class, () -> userService.getUserByIDNum(testUser.getId() +2L));
      // This gives me an InvalidDataAccessApiUsageException (not correct type
      // User userByIDNum = userService.getUserByIDNum(createdUser.getId());
  }
  @Test
  public void compareUserByID_differentID(){
      assertThrows(ResponseStatusException.class, ()-> userService.compareUserByID(1L,2L));
  }
  /**
  @Test
  public void updateUsernameAndBirthday_success(){
      assertNull(userRepository.findByUsername("testUsername"));

      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      User createdUserProfile = userService.createUser(testUser);

      assertNull(userRepository.findByUsername("testUsername2"));

      User testUser2 = new User();
      testUser2.setName("testName2");
      testUser2.setUsername("testUsername2");
      testUser2.setPassword("password");
      testUser2.setBirthday(Calendar.getInstance().getTime());
      User UserRequestChange = userService.createUser(testUser2);

      userService.updateUsernameAndBirthday(createdUserProfile, UserRequestChange);

      assertEquals(createdUserProfile.getUsername(), UserRequestChange.getUsername());
  }**/
}
