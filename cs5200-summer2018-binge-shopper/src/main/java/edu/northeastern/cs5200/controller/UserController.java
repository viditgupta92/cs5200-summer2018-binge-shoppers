package edu.northeastern.cs5200.controller;

import edu.northeastern.cs5200.entity.UserEntity;
import edu.northeastern.cs5200.exception.AccessDeniedException;
import edu.northeastern.cs5200.exception.AuthenticationException;
import edu.northeastern.cs5200.exception.NotFoundException;
import edu.northeastern.cs5200.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public UserEntity login(String username, String password, HttpSession session) throws AccessDeniedException,
            AuthenticationException {

        UserEntity user = userService.findUserByUsername(username);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {

            if ((user.getUserType().equals("Buyer") || user.getUserType().equals("Seller")) && !user.getApproved()) {
                throw new AccessDeniedException(new Throwable("User requires admin approval to login!"));
            }
            session.setAttribute("user_session", user);
            session.setMaxInactiveInterval(300);
            return user;
        } else
            throw new AuthenticationException("Invalid username and password| Try again !");

    }

    @PostMapping("register")
    public UserEntity register(@RequestBody UserEntity u, HttpSession session) throws NotFoundException {

        if (userService.findUserByUsername(u.getUsername()) != null) {
            throw new AuthenticationException("User already registered| Login directly!");
        }

        u.setPassword(BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
        UserEntity user = userService.addUser(u);

        session.setAttribute("user_session", user);
        session.setMaxInactiveInterval(300);
        return user;
    }

    @GetMapping("getAll")
    public List<UserEntity> getAllUsersExceptAdmin(HttpSession session){
        return userService.findAllUsersExceptAdmin();
    }

    @PutMapping("{userId}/approve/{status}")
    public UserEntity updateApproveUser(HttpSession session, @PathVariable int userId, @PathVariable boolean status){
        return userService.updateApproveUser(userId, status);
    }

    @PutMapping("update")
    public UserEntity update(@RequestBody UserEntity user, HttpSession session){
        return userService.updateUser(user);
    }

    @DeleteMapping("{userId}/delete")
    public void deleteUser(HttpSession session, @PathVariable int userId){
        userService.deleteUser(userId);
    }

    @DeleteMapping("logout")
    public ResponseEntity logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity("{}", HttpStatus.OK);
    }
}
