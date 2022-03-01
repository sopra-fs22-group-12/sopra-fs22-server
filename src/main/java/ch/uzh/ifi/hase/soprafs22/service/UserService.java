package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date today = Calendar.getInstance().getTime();
    //String reportDate = df.format(today);
    newUser.setDate(today);
      System.out.println(today);

    checkNullPassword(newUser);
    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }
  public User authanticateUser(User newUser) {
      User userByUsername = userRepository.findByUsername(newUser.getUsername());
      if(userByUsername == null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Username not found"));
      }
      if(!userByUsername.getPassword().equals(newUser.getPassword()) ){
          System.out.println("From Database: " + userByUsername.getPassword());
          System.out.println("From Frontend: " + newUser.getPassword());
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Password is incorrect"));
      }
      System.out.println(userByUsername.getToken());
      userByUsername.setStatus(UserStatus.ONLINE);
      updateRepository(userByUsername);
      /**userRepository.save(newUser);
      userRepository.flush();*/
      return userByUsername;
  }

  public void updateRepository(User newUser){
      userRepository.save(newUser);
      userRepository.flush();
  }


  public void logout(User userToLogout){
      userToLogout.setStatus(UserStatus.OFFLINE);
      updateRepository(userToLogout);
  }

  public User getUserByIDNum(Long userId){
      Optional<User> userRepo = userRepository.findById(userId);
      User user = userRepo.orElse(null);
      if(user == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ID not found"));
      }
      else {
          return user;
      }
  }

  public void updateUsernameAndBirthday(User userToUpdate){
      User UserToUpdateInDB = getUserByIDNum(userToUpdate.getId());
      UserToUpdateInDB.setUsername(userToUpdate.getUsername());
      System.out.println(userToUpdate.getBirthday());
      //UserToUpdateInDB.getBirthday();
      UserToUpdateInDB.setBirthday(userToUpdate.getBirthday());
      updateRepository(UserToUpdateInDB);
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
    }
  }
  private void checkNullPassword(User user){
      if(user.getPassword() == null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Password is Null"));
      }
  }
}
