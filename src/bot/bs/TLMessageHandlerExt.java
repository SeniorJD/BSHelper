package bot.bs;

import bot.plugins.handlers.MessageHandler;
import bot.plugins.handlers.TLMessageHandler;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.bot.services.BotLogger;

/**
 * @author SeniorJD
 */
public class TLMessageHandlerExt extends TLMessageHandler {

    public TLMessageHandlerExt(MessageHandler messageHandler) {
        super(messageHandler);
    }

    public void onTLMessage(TLMessage message) {
        final TLAbsPeer absPeer = message.getToId();
        if (absPeer instanceof TLPeerUser) {
            onTLMessageForUser(message);
        } else if (absPeer instanceof TLPeerChat) {
            onTLMessageForChat(message);
        } else {
            BotLogger.severe(LOGTAG, "Unsupported Peer: " + absPeer.toString());
        }
    }

    private void onTLMessageForChat(TLMessage message) {
        TLPeerChat peerChat = (TLPeerChat) message.getToId();
        this.messageHandler.handleMessage(peerChat.getId(), message);
    }
}
