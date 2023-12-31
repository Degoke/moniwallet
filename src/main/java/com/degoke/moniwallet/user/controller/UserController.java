package com.degoke.moniwallet.user.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.degoke.moniwallet.user.assembler.UserModelAssembler;
import com.degoke.moniwallet.user.entity.User;
import com.degoke.moniwallet.user.exception.UserNotFoundException;
import com.degoke.moniwallet.user.repository.UserRepository;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserModelAssembler userAssembler;

    UserController(
        UserRepository userRepository,
        UserModelAssembler userAssembler
    ) {
        this.userRepository = userRepository;
        this.userAssembler = userAssembler;
    }

    // Aggregate root
    // tag::get-aggregate-root
    @GetMapping("")
    // @PreAuthorize("hasRole('USER')")
    public CollectionModel<EntityModel<User>> allUsers() {
        List<EntityModel<User>> users = userRepository.findAll().stream()
            .map(userAssembler::toModel).collect(Collectors.toList());
        
        return CollectionModel.of(users, linkTo(methodOn(UserController.class).allUsers()).withSelfRel());
    }
    // end::get-aggregate-root[]
    
    @PostMapping("/")
    public ResponseEntity<?> newUser(@RequestBody User newUser) {
        EntityModel<User> user = userAssembler.toModel(userRepository.save(newUser));

        return ResponseEntity
            .created(user.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(user);
    }
    
    @GetMapping("/{id}")
    public EntityModel<User> oneUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return userAssembler.toModel(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceUser(@PathVariable Long id, @RequestBody User userEntity) {
        User updatedUser = userRepository.findById(id).map(user -> {
            user.setFirstName(userEntity.getFirstName());
            user.setLastName(userEntity.getLastName());
            user.setEmail(userEntity.getEmail());
            return userRepository.save(user);
        }).orElseGet(() -> {
            userEntity.setId(id);
            return userRepository.save(userEntity);
        });

        EntityModel<User> userModel = userAssembler.toModel(updatedUser);

        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }
    
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
