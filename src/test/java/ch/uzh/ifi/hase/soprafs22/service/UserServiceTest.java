package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void getUsersList_validInputs_success(){
      User createdUser = userService.createUser(testUser);

      List<User> listOfUsers = new ArrayList<User>();
      listOfUsers.add(createdUser);

      Mockito.when(userRepository.findAll()).thenReturn(listOfUsers);

      List<User> testListOfUsers = userService.getUsers();

      assertEquals(1, testListOfUsers.size());
      assertEquals(testUser.getId(), testListOfUsers.get(0).getId());
      assertEquals(testUser.getName(), testListOfUsers.get(0).getName());
      assertEquals(testUser.getUsername(), testListOfUsers.get(0).getUsername());
      assertNotNull(testListOfUsers.get(0).getToken());
      assertEquals(UserStatus.ONLINE, testListOfUsers.get(0).getStatus());
      assertEquals(true, testListOfUsers.get(0).getLoggedIn());
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    assertEquals(true, createdUser.getLoggedIn());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }
  @Test
  public void authenticateUser_validInput_success(){
    User createdUser = userService.createUser(testUser);

    Mockito.verify(userRepository).save(Mockito.any());

    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    User testUser2 = new User();
    testUser2.setId(1L);
    testUser2.setName("testName");
    testUser2.setUsername("testUsername");
    testUser2.setPassword("password");
    User authenticatedUser = userService.authanticateUser(testUser2);

    assertEquals(testUser.getId(), authenticatedUser.getId());
    assertEquals(testUser.getName(), authenticatedUser.getName());
    assertEquals(testUser.getUsername(), authenticatedUser.getUsername());
    assertNotNull(authenticatedUser.getToken());
    assertEquals(UserStatus.ONLINE, authenticatedUser.getStatus());
    assertEquals(true, authenticatedUser.getLoggedIn());
  }
  @Test
  public void authenticateUser_wrongPassword_fail(){
    User createdUser = userService.createUser(testUser);

    Mockito.verify(userRepository).save(Mockito.any());

    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(null);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    User testUser2 = new User();
    testUser2.setId(1L);
    testUser2.setName("testName");
    testUser2.setUsername("testUsername");
    testUser2.setPassword("password9");

    assertThrows(ResponseStatusException.class, () -> userService.authanticateUser(testUser2));
  }
  @Test
  public void getUserByID_validInput_success(){
    User createdUser = userService.createUser(testUser);

    Mockito.verify(userRepository).save(Mockito.any());

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(testUser));

    User userId = userService.getUserByIDNum(testUser.getId());

    assertEquals(testUser.getId(), userId.getId());
    assertEquals(testUser.getName(), userId.getName());
    assertEquals(testUser.getUsername(), userId.getUsername());
    assertNotNull(userId.getToken());
    assertEquals(UserStatus.ONLINE, userId.getStatus());
    assertEquals(true, userId.getLoggedIn());
  }
  @Test
  public void getUserByID_invalidID_fail(){
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(null);

    assertThrows(ResponseStatusException.class, () -> userService.getUserByIDNum(testUser.getId()));
  }
    @Test
    public void checkTokenExists_stringToken_success(){
      userService.checkTokenExists("2");
    }
    @Test
    public void checkTokenExistence_nullToken_fail(){
        assertThrows(ResponseStatusException.class, () -> userService.checkTokenExists(null));
    }

    @Test
    public void compareUserByToken_validToken(){
        userService.compareUserByToken("1","Bearer 1");
    }
  @Test
  public void compareUserByID_mismatchingToken_fail(){
    assertThrows(ResponseStatusException.class, () -> userService.compareUserByToken("1L","2L"));
  }
}
