package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
        userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    String token = createdUser.getToken();
    response.addHeader("token", token);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);//.setHeader("token", createdUser.getToken());
  }
  @PostMapping("/user/login")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
      // convert API user to internal representation
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      //System.out.println(userInput.getPassword());

      // authenticate user
      User authenticatedUser = userService.authanticateUser(userInput);
      String token = authenticatedUser.getToken();

      response.addHeader("token", token);
      // convert internal representation of user back to API
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(authenticatedUser);
    }

  @PutMapping("/logout/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void logout(@PathVariable("userId") Long userId){
      // get the User from its ID
      User user = userService.getUserByIDNum(userId);
      //Does the logout
      userService.logout(user);
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserFromUserID(@PathVariable("userId") Long userId, @RequestHeader("Authorization") String Token){
      //check if token exists
      userService.checkTokenExists(Token);

      // get the User from its ID
      User user = userService.getUserByIDNum(userId);

      // convert internal representation of user back to API
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUserFromUserID(@PathVariable("userId") Long userId, @RequestBody UserPostDTO userPostDTO, @RequestHeader("Authorization") String Token){
      User usertoUpdate = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      // get the User from its ID
      User profileUserId = userService.getUserByIDNum(userId);

      //Validates if it is the correct user
      userService.compareUserByToken(profileUserId.getToken(),Token);

      //updates the specified requirements
      userService.updateUsernameAndBirthday(profileUserId,usertoUpdate);
    }
}
