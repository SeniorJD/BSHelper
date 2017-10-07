package bot.bs.handler;

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
    private Map<Integer, int[]> differencesData = new HashMap<>();
    private Map<Integer, ChatImpl> chats = new HashMap<>();
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public @Nullable Chat getChatById(int i) {
        return chats.get(i);
    }

    public void addChat(ChatImpl chat) {
        chats.put(chat.getId(), chat);
    }

    @Override
    public @Nullable IUser getUserById(int i) {
        return users.get(i);
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public @NotNull Map<Integer, int[]> getDifferencesData() {
        return new HashMap<>(differencesData);
    }


    @Override
    public boolean updateDifferencesData(int i, int i1, int i2, int i3) {
        boolean result = false;

        if (!differencesData.containsKey(i)) {
            result = true;

            differencesData.put(i, new int[]{i1, i2, i3});
        } else {
            int[] oldData = differencesData.get(i);

            if (oldData[0] != i1) {
                result = true;
                oldData[0] = i1;
            }

            if (oldData[1] != i2) {
                result = true;
                oldData[1] = i2;
            }

            if (oldData[2] != i3) {
                result = true;
                oldData[2] = i3;
            }
        }

        return result;
    }
}
