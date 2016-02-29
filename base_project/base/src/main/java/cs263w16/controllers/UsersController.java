package cs263w16.controllers;

import cs263w16.model.AppUser;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface UsersController {
    public AppUser getUser(String id);
}
