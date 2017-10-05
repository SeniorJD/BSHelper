package bot.bs;

import bot.plugins.handlers.UsersHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.user.TLAbsUser;

/**
 * @author SeniorJD
 */
public class BSUsersHandler extends UsersHandler {

    public BSUsersHandler() {
        super();
    }

    @Override
    protected void onUser(@NotNull TLAbsUser absUser) {
//        super.onUser(absUser);
//        if (Util.getBattleSiegeBotId() != 0) {
//            return;
//        }
//
//        if (!(absUser instanceof TLUser)) {
//            return;
//        }
//
//        TLUser tlUser = (TLUser) absUser;
//
//        if (!tlUser.isBot()) {
//            return;
//        }
    }
}
