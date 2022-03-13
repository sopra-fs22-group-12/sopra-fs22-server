package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByName_success() {
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setPassword("password");
    user.setStatus(UserStatus.OFFLINE);
    user.setLoggedIn(false);
    user.setToken("1");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date today = Calendar.getInstance().getTime();
    //String reportDate = df.format(today);
    user.setCreationDate(today);

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByName(user.getName());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getName(), user.getName());
    assertEquals(found.getUsername(), user.getUsername());
    assertEquals(found.getPassword(), user.getPassword());
    assertEquals(found.getCreationDate(),user.getCreationDate());
    assertEquals(found.getToken(), user.getToken());
    assertEquals(found.getStatus(), user.getStatus());
    assertEquals(found.getLoggedIn(), user.getLoggedIn());
    assertEquals(found.getBirthday(), user.getBirthday());
  }
  @Test
  public void findByUsername_success() {
      // given
      User user = new User();
      user.setName("Firstname Lastname");
      user.setUsername("firstname@lastname");
      user.setPassword("password");
      user.setStatus(UserStatus.OFFLINE);
      user.setLoggedIn(false);
      user.setToken("1");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      Date today = Calendar.getInstance().getTime();
      //String reportDate = df.format(today);
      user.setCreationDate(today);

      entityManager.persist(user);
      entityManager.flush();

      // when
      User found = userRepository.findByUsername(user.getUsername());

      // then
      assertNotNull(found.getId());
      assertEquals(found.getName(), user.getName());
      assertEquals(found.getUsername(), user.getUsername());
      assertEquals(found.getPassword(), user.getPassword());
      assertEquals(found.getCreationDate(),user.getCreationDate());
      assertEquals(found.getToken(), user.getToken());
      assertEquals(found.getStatus(), user.getStatus());
      assertEquals(found.getLoggedIn(), user.getLoggedIn());
      assertEquals(found.getBirthday(), user.getBirthday());
    }
}
