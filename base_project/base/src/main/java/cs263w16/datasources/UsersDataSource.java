package cs263w16.datasources;

import cs263w16.model.AppUser;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface UsersDataSource {

    /**
     * Retrieve the data entry for the specified user.
     * @param userId
     * @return An AppUser object representing the user or null if the user was not found.
     */
    AppUser getUser(String userId);

    /**
     * Add a data entry for a new user.
     * @param appUser
     */
    void addUser(AppUser appUser);


}
