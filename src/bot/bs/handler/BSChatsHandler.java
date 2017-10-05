package bot.bs.handler;

import bot.plugins.handlers.ChatsHandler;
import org.telegram.api.chat.TLAbsChat;

import java.util.List;

/**
 * @author SeniorJD
 */
public class BSChatsHandler extends ChatsHandler {

    public BSChatsHandler() {
        super();
    }

    @Override
    public void onChats(List<TLAbsChat> list) {
    }
}
