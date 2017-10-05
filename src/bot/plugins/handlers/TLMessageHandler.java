package bot.plugins.handlers;

import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.bot.services.BotLogger;

/**
 * @author SeniorJD
 */
public class TLMessageHandler {
    protected static final String LOGTAG = "TLMESSAGEHANDLER";
    protected final MessageHandler messageHandler;

    public TLMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void onTLMessage(TLMessage message) {
        final TLAbsPeer absPeer = message.getToId();
        if (absPeer instanceof TLPeerUser) {
            onTLMessageForUser(message);
        } else {
            BotLogger.severe(LOGTAG, "Unsupported Peer: " + absPeer.toString());
        }
    }

    protected void onTLMessageForUser(TLMessage message) {
        if (!message.isSent()) {
            this.messageHandler.handleMessage(message);
        }
    }
}

