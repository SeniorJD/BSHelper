package bot.bs;

import bot.plugins.structure.ChatImpl;
import bot.plugins.structure.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SeniorJD
 */
public class BSDatabaseManager implements DatabaseManager {
    private static final ChatImpl chat = new ChatImpl(0){{
        setAccessHash(1L);
    }};

    private static final IUser user = new User(0){{
        setUserHash(1L);
    }};
    @Override
    public @Nullable Chat getChatById(int i) {
        return chat;
    }

    @Override
    public @Nullable IUser getUserById(int i) {
        return user;
    }

    @Override
    public @NotNull Map<Integer, int[]> getDifferencesData() {
        return new HashMap<>();
    }

    @Override
    public boolean updateDifferencesData(int i, int i1, int i2, int i3) {
        return true;
    }
}
