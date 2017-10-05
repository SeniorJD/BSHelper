package bot.plugins.structure;

import bot.plugins.handlers.MessageHandler;
import bot.plugins.handlers.TLMessageHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;

import java.util.List;

/**
 * @author SeniorJD
 */
public class CustomUpdatesHandler extends DefaultUpdatesHandler {
    private static final String LOGTAG = "CHATUPDATESHANDLER";

    private BotConfig botConfig;
    private MessageHandler messageHandler;
    private IUsersHandler usersHandler;
    private IChatsHandler chatsHandler;
    private TLMessageHandler tlMessageHandler;

    public CustomUpdatesHandler(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, DatabaseManager databaseManager) {
        super(kernelComm, differenceParametersService, databaseManager);
    }

    public void setConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void setHandlers(MessageHandler messageHandler, IUsersHandler usersHandler, IChatsHandler chatsHandler, TLMessageHandler tlMessageHandler) {
        this.messageHandler = messageHandler;
        this.chatsHandler = chatsHandler;
        this.usersHandler = usersHandler;
        this.tlMessageHandler = tlMessageHandler;
    }

    @Override
    public void onTLUpdateShortMessageCustom(TLUpdateShortMessage update) {
        BotLogger.info(LOGTAG, "Received message from: " + update.getUserId());
        messageHandler.handleMessage(update);
    }

    @Override
    public void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
        onTLAbsMessageCustom(update.getMessage());
    }

    @Override
    protected void onTLAbsMessageCustom(TLAbsMessage message) {
        if (message instanceof TLMessage) {
            BotLogger.debug(LOGTAG, "Received TLMessage");
            onTLMessage((TLMessage) message);
        } else {
            BotLogger.debug(LOGTAG, "Unsupported TLAbsMessage -> " + message.toString());
        }
    }

    @Override
    protected void onUsersCustom(List<TLAbsUser> users) {
        usersHandler.onUsers(users);
    }

    @Override
    protected void onChatsCustom(List<TLAbsChat> chats) {
        chatsHandler.onChats(chats);
    }

    /**
     * Handles TLMessage
     * @param message Message to handle
     */
    private void onTLMessage(@NotNull TLMessage message) {
        if (message.hasFromId()) {
            this.tlMessageHandler.onTLMessage(message);
        }
    }
}
