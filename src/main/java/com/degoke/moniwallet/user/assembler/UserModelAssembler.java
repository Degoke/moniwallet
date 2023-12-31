package com.degoke.moniwallet.user.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.degoke.moniwallet.user.controller.UserController;
import com.degoke.moniwallet.user.entity.User;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {
    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(user,
            linkTo(methodOn(UserController.class).oneUser(user.getId())).withSelfRel(),
            linkTo(methodOn(UserController.class).allUsers()).withRel("users")
        );
    }
}
