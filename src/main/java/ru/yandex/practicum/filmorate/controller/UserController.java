package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserResult;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        User lUser;
        try {
            lUser = userService.create(user);
        } catch (ValidationException ex) {
            log.debug("Ошибка при валидации: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return lUser;
    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
        UserResult lUser;
        try {
            Boolean isFound = false;
            lUser = userService.put(user);
            if (! lUser.isResult()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id < 0");
            }
        } catch (ValidationException ex) {
            log.debug("Ошибка при валидации: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cause description here");
        }
        return lUser.getUser();
    }



    /**
     * Получение user по id
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    public User userId(@PathVariable int id) {
        User user = userService.userId(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found id");
        }
        return user;
    }


    /**
     * добавление в друзья
     */
    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    /**
     * удаление из друзей
     * @param id
     * @param friendId
     */
    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    /**
     * возвращаем список пользователей, являющихся его друзьями
     *
     * @return
     */
    @GetMapping("/users/{id}/friends")
    public List<User> listFriends(@PathVariable int id) {
        return userService.listFriends(id);
    }

    /**
     * список друзей, общих с другим пользователем
     * @param id
     * @param otherId
     * @return
     */
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> listOtherFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.listOtherFriends(id, otherId);
    }
}
