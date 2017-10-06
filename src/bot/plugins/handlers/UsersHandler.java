package bot.plugins.handlers;

import bot.bs.handler.BSDatabaseManager;
import bot.plugins.structure.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.services.BotLogger;

import java.util.List;

/**
 * @author SeniorJD
 */
public class UsersHandler implements IUsersHandler {
    private static final String LOGTAG = "USERSHANDLER";
    private static final int MAXTEMPORALUSERS = 4000;

    protected BSDatabaseManager databaseManager;

    public UsersHandler(BSDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Add a list of users to database
     * @param users List of users to add
     */
    public void onUsers(@NotNull List<TLAbsUser> users) {
        for (TLAbsUser tlAbsUser : users) {
            if (!(tlAbsUser instanceof TLUser)) {
                continue;
            }

            TLUser tlUser = (TLUser) tlAbsUser;

            if (databaseManager.getUserById(tlUser.getId()) == null) {
                User user = new User(tlUser.getId());
                user.setUserHash(tlUser.getAccessHash());
            }
        }

        users.forEach(this::onUser);
    }

    /**
     * Add a user to database
     * @param absUser User to add
     */
    protected void onUser(@NotNull TLAbsUser absUser) {
//        User currentUser = null;
//        User user = null;
//        if (absUser instanceof TLUser) {
//            final TLUser tlUser = (TLUser) absUser;
//            if (tlUser.isMutualContact()) {
//                currentUser = (User) databaseManager.getUserById(tlUser.getId());
//                user = onUserContact(currentUser, tlUser);
//            } else if (tlUser.isDeleted()) {
//                currentUser = (User) databaseManager.getUserById(tlUser.getId());
//                user = onUserDelete(currentUser, tlUser);
//            } else if (tlUser.isContact()) {
//                currentUser = (User) databaseManager.getUserById(tlUser.getId());
//                user = onUserRequest(currentUser, tlUser);
//            } else if (tlUser.isSelf() || !tlUser.isBot()) {
//                currentUser = (User) databaseManager.getUserById(tlUser.getId());
//                user = onUserForeign(currentUser, tlUser);
//            } else if (tlUser.isBot()) {
//                currentUser = (User) databaseManager.getUserById(tlUser.getId());
//                user = onBot(currentUser, tlUser);
//            } else {
//                BotLogger.info(LOGTAG, "Bot received");
//            }
//        }
//        if ((currentUser == null) && (user != null)) {
//            databaseManager.addUser(user);
//        } else if (user != null) {
//            databaseManager.updateUser(user);
//        }
    }

    /**
     * Create User from a delete user
     * @param currentUser Current use from database (null if not present)
     * @param userDeleted Delete user from Telegram Server
     * @return User information
     */
    private User onUserDelete(@Nullable User currentUser, @NotNull TLUser userDeleted) {
        final User user;
        if (currentUser == null) {
            user = new User(userDeleted.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(0L);
        BotLogger.debug(LOGTAG, "userdeletedid: " + user.getUserId());
        return user;
    }

    /**
     * Create User from a contact user
     * @param currentUser Current use from database (null if not present)
     * @param userContact Contact user from Telegram Server
     * @return User information
     */
    private User onUserContact(@Nullable User currentUser, @NotNull TLUser userContact) {
        final User user;
        if (currentUser == null) {
            user = new User(userContact.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(userContact.getAccessHash());
        BotLogger.debug(LOGTAG, "usercontactid: " + user.getUserId());
        return user;
    }

    /**
     * Create User from a request user
     * @param currentUser Current use from database (null if not present)
     * @param userRequest Request user from Telegram Server
     * @return User information
     */
    private User onUserRequest(@Nullable User currentUser, @NotNull TLUser userRequest) {
        final User user;
        if (currentUser == null) {
            user = new User(userRequest.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(userRequest.getAccessHash());
        BotLogger.debug(LOGTAG, "userRequestId: " + user.getUserId());
        return user;
    }

    /**
     * Create User from a foreign user
     * @param currentUser Current use from database (null if not present)
     * @param userForeign Foreign user from Telegram Server
     * @return User information
     */
    private User onUserForeign(@Nullable User currentUser, @NotNull TLUser userForeign) {
        final User user;
        if (currentUser == null) {
            user = new User(userForeign.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(userForeign.getAccessHash());
        BotLogger.debug(LOGTAG, "userforeignid: " + user.getUserId());
        return user;
    }

    private User onBot(@Nullable User currentUser, @NotNull TLUser bot) {
        final User user;
        if (currentUser == null) {
            user = new User(bot.getId());
        } else {
            user = new User(currentUser);
        }
        user.setUserHash(bot.getAccessHash());
        BotLogger.debug(LOGTAG, "botid: " + user.getUserId());
        return user;
    }
}
