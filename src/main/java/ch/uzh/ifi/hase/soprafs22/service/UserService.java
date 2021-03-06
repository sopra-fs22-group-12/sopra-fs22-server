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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    newUser.setLoggedIn(true);

    if (newUser.getName() == null){
        newUser.setName(newUser.getUsername());
    }

    //creates the creation date form today
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date today = Calendar.getInstance().getTime();
    newUser.setCreationDate(today);

    //password and Username/Name check
    checkNullPassword(newUser);
    String hashedPassword = hashPassword(newUser.getPassword());
    newUser.setPassword(hashedPassword);
    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }


  public User authanticateUser(User newUser) {
      //find user
      User userByUsername = userRepository.findByUsername(newUser.getUsername());
      if(userByUsername == null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Username not found"));
      }
      //check for the correct password
      String hashedNewUserPassword = hashPassword(newUser.getPassword());
      if(!userByUsername.getPassword().equals(hashedNewUserPassword) ){
          System.out.println("From Database: " + userByUsername.getPassword());
          System.out.println("From Frontend: " + newUser.getPassword());
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Password is incorrect"));
      }
      //updates the changes
      userByUsername.setToken(UUID.randomUUID().toString());
      userByUsername.setStatus(UserStatus.ONLINE);
      userByUsername.setLoggedIn(true);
      //does the flushing and saving
      updateRepository(userByUsername);
      return userByUsername;
  }

  private void updateRepository(User newUser){
      userRepository.save(newUser);
      userRepository.flush();
  }


  public void logout(User userToLogout){
      userToLogout.setStatus(UserStatus.OFFLINE);
      userToLogout.setLoggedIn(false);
      updateRepository(userToLogout);
  }

  public User getUserByIDNum(Long userId){
      Optional<User> userRepo = userRepository.findById(userId);
      User user;
      try{
          user = userRepo.orElse(null);
          if (user == null){
              throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ID not found"));
          }
      }catch (NullPointerException e){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ID not found"));
      }
      return user;

  }

  public void checkTokenExists(String token) {
      if (token == null){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Unauthorized for this entry"));
      }
  }

  public void compareUserByToken(String profileUserToken, String accountUserToken){
      if (!("Bearer "+ profileUserToken).equals(accountUserToken)){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Unauthorized for the update"));
      }
  }

  public void updateUsernameAndBirthday(User userProfile, User userRequestChange){
      //update the Profile fields.
      userProfile.setUsername(userRequestChange.getUsername());
      if(userRequestChange.getBirthday() != null){
        userProfile.setBirthday(userRequestChange.getBirthday());
      }
      updateRepository(userProfile);
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
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "name", "is"));
    }
  }
  private void checkNullPassword(User user){
      if(user.getPassword() == null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Password is Null"));
      }
  }
  private String hashPassword(String passwordToHash) {
      String hashedPassword = null;
      try {
          MessageDigest md = MessageDigest.getInstance("MD5");

          md.update(passwordToHash.getBytes());

          byte[] bytes = md.digest();

          StringBuilder sb = new StringBuilder();
          for (int i=0; i <bytes.length; i++){
              sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
          }
          hashedPassword = sb.toString();
      }
      catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
      }
      return hashedPassword;
  }
}
