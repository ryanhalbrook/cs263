package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.*;

import java.util.Date;

/**
 * Created by ryanhalbrook on 3/15/16.
 */
public class ModelTranslator {

    /** Community **/

    public static Community unboxCommunity(Entity entity) {
        Community community = new Community(
                (String)entity.getProperty("name"),
                (String)entity.getProperty("description"),
                (Date)entity.getProperty("creation_date"),
                (String)entity.getProperty("admin_user_id")
        );

        return community;
    }

    public static Entity boxCommunity(Community community) {
        Entity entity = new Entity("Community", community.getId());
        entity.setProperty("name", community.getName());
        entity.setProperty("description", community.getDescription());
        entity.setProperty("creation_date", community.getCreationDate());
        entity.setProperty("admin_user_id", community.getAdminUserId());

        return entity;
    }



    /** Event **/

    public static Event unboxEvent(Entity entity) {
        Event event = new Event(
                (String)entity.getProperty("name"),
                (String)entity.getProperty("description"),
                (String)entity.getProperty("community_name")
        );

        if (entity.hasProperty("date")) {
            event.setDate((Date)entity.getProperty("date"));
        }

        return event;
    }

    public static Entity boxEvent(Event event) {
        Entity entity = new Entity("Event", event.getId());
        entity.setProperty("name", event.getName());
        entity.setProperty("description", event.getDescription());
        entity.setProperty("community_name", event.getCommunityName());

        if (event.getDate() != null) {
            entity.setProperty("date", event.getDate());
        }

        return entity;
    }



    /** Membership **/

    public static Membership unboxMembership(Entity entity) {
        Membership membership = new Membership(
                (String)entity.getProperty("community_id"),
                (String)entity.getProperty("user_id")
        );

        return membership;

    }

    public static Entity boxMembership(Membership membership) {
        Entity entity = new Entity("Membership", membership.getId());
        entity.setProperty("community_id", membership.getCommunityId());
        entity.setProperty("user_id", membership.getUserId());

        return entity;

    }



    /** MembershipEvent **/

    public static MembershipEvent unboxMembershipEvent(Entity entity) {
        MembershipEvent membershipEvent = new MembershipEvent(
                (String)entity.getProperty("user_id"),
                (String)entity.getProperty("event_id"),
                (String)entity.getProperty("community_id"),
                (Date)entity.getProperty("event_date"),
                (Boolean)entity.getProperty("hidden")
        );

        return membershipEvent;
    }

    public static Entity boxMembershipEvent(MembershipEvent membershipEvent) {
        Entity entity = new Entity("MembershipEvent", membershipEvent.getId());
        entity.setProperty("user_id", membershipEvent.getUserId());
        entity.setProperty("event_id", membershipEvent.getEventId());
        entity.setProperty("community_id", membershipEvent.getCommunityId());
        entity.setProperty("event_date", membershipEvent.getEventDate());
        entity.setProperty("hidden", membershipEvent.isHidden());

        return entity;
    }


    /** AppUser **/

    public static AppUser unboxAppUser(Entity entity) {
        AppUser appUser = new AppUser(
                (String)entity.getProperty("user_id"),
                (String)entity.getProperty("email_address"),
                (String)entity.getProperty("user_name"),
                (String)entity.getProperty("first_name"),
                (String)entity.getProperty("last_name"),
                (Date)entity.getProperty("signup_date")
        );

        return appUser;
    }

    public static Entity boxAppUser(AppUser appUser) {
        Entity entity = new Entity("AppUser", appUser.getId());
        entity.setProperty("user_id", appUser.getUserId());
        entity.setProperty("email_address", appUser.getEmailAddress());
        entity.setProperty("user_name", appUser.getUserName());
        entity.setProperty("first_name", appUser.getFirstName());
        entity.setProperty("last_name", appUser.getLastName());
        entity.setProperty("signup_date", appUser.getSignupDate());

        return entity;
    }


}
