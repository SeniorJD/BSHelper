package bot.plugins.handlers;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.engine.RpcException;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.IUser;

/**
 * @author SeniorJD
 */
public class MessageHandler {
    private static final String LOGTAG = "MESSAGEHANDLER";

    public MessageHandler() {
    }

    /**
     * Handler for the request of a contact
     *
     * @param user    User to be answered
     * @param message TLMessage received
     */
    public void handleMessage(@NotNull TLMessage message) {
        try {
            handleMessageInternal(message.getMessage());
        } catch (RpcException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

    /**
     * Handler for the requests of a contact
     *
     * @param user    User to be answered
     * @param message Message received
     */
    public void handleMessage(@NotNull TLUpdateShortMessage message) {
        try {
            handleMessageInternal(message.getMessage());
        } catch (RpcException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }

    public void handleMessage(int chatId, @NotNull TLMessage message) {}

    /**
     * Handle a message from an user
     * @param user User that sent the message
     * @param message Message received
     */
    private void handleMessageInternal(String message) throws RpcException {
    }
}