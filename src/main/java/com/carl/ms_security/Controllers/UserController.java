package com.carl.ms_security.Controllers;

import com.carl.ms_security.Models.User;
import com.carl.ms_security.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService theUserService;

    @GetMapping("")
    public List<User> find() {
        return this.theUserService.find();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable("id") String id) {
        return this.theUserService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        return this.theUserService.create(newUser);
    }

    @PutMapping("{id}")
    public User update(@PathVariable("id") String id, @RequestBody User newUser) {
        return this.theUserService.update(id, newUser);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        this.theUserService.delete(id);
    }
    @PostMapping("{userId}/profile/{profileId}")
    public ResponseEntity<Map<String, String>> addUserProfile(
            @PathVariable("userId") String userId,
            @PathVariable("profileId") String profileId) {

        boolean response = this.theUserService.addProfile(userId, profileId);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User or Profile not found"));
        }
    }
    @DeleteMapping("{userId}/profile/{profileId}")
    public ResponseEntity<Map<String, String>> deleteUserProfile(
            @PathVariable("userId") String userId,
            @PathVariable("profileId") String profileId) {

        boolean response = this.theUserService.removeProfile(userId, profileId);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User or Profile not found"));
        }
    }

}

