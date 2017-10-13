package bot.bs.scenarios;

import bot.bs.handler.BSMessageHandler;
import bot.bs.Util;
import bot.plugins.structure.ChatImpl;
import bot.plugins.structure.User;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.engine.RpcException;
import org.telegram.api.functions.messages.TLRequestMessagesGetBotCallbackAnswer;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.input.peer.TLInputPeerUser;
import org.telegram.api.keyboard.button.TLKeyboardButtonCallback;
import org.telegram.api.keyboard.replymarkup.TLReplayInlineKeyboardMarkup;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLMessagesBotCallbackAnswer;
import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;
import org.telegram.tl.StreamingUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author SeniorJD
 */
public class BSSender {
    protected Random random = new Random();

    protected IKernelComm kernelComm;
    protected BSMessageHandler messageHandler;

    protected User bsBot;
    protected ChatImpl helperChat;

    public BSSender(BSMessageHandler messageHandler, IKernelComm kernelComm) {
        this.messageHandler = messageHandler;
        this.kernelComm = kernelComm;
    }

    private void init() {
        TLRequestMessagesGetDialogs getDialogs = new TLRequestMessagesGetDialogs();
        getDialogs.setOffsetPeer(new TLInputPeerSelf());

        try {
            TLAbsDialogs dialogs = kernelComm.getApi().doRpcCall(getDialogs);

            for (TLAbsChat tlAbsChat : dialogs.getChats()) {
                if (tlAbsChat instanceof TLChat) {
                    TLChat chat = (TLChat) tlAbsChat;

                    if (Util.BS_HELPER_NAME.equals(chat.getTitle())) {
//                        Util.setBattleSiegeHelperId(chat.getId());
                        helperChat = new ChatImpl(chat.getId());
//                        if (databaseManager.getChatById(chat.getId()) == null) {
//                            databaseManager.addChat(helperChat);
//                        }
                    }
                }
            }

            for (TLAbsUser user : dialogs.getUsers()) {
                if (user instanceof TLUser) {
                    TLUser tlUser = (TLUser) user;

                    if (Util.BS_BOT_NAME.equals(tlUser.getUserName())) {
//                        Util.setBattleSiegeBotId(tlUser.getId());
                        bsBot = new User(tlUser.getId());
                        bsBot.setUserHash(tlUser.getAccessHash());
//                        if (databaseManager.getUserById(tlUser.getId()) == null) {
//                            databaseManager.addUser(bsBot);
//                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public IUser getUser() {
        if (bsBot == null) {
            init();
        }

        return bsBot;
    }

    public Chat getHelperChat() {
        if (helperChat == null) {
            init();
        }

        return helperChat;
    }

    public void sendMessage(String message) {
        System.out.println("SENDING CUSTOM MESSAGE: " + message);

        try {
            Thread.sleep(500 + random.nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IUser user = getUser();
        if (user == null) {
            return;
        }

        try {
            kernelComm.sendMessage(user, message);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    public void sendHelperMessage(String message) {
        if (getHelperChat() == null) {
            return;
        }

        try {
            Thread.sleep(500 + random.nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageHandler.setIgnoreHelperMessage(true);

        try {
            kernelComm.sendChannelMessageWithMarkdown(getHelperChat(), message, false);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }

    protected void pressAttackButton(TLMessage message) {
        TLReplayInlineKeyboardMarkup replyMarkup = (TLReplayInlineKeyboardMarkup) message.getReplyMarkup();
        TLKeyboardButtonCallback button = (TLKeyboardButtonCallback) replyMarkup.getRows().get(0).buttons.get(0);
        TLInputPeerUser peer = new TLInputPeerUser();
        peer.setUserId(message.getFromId());
        peer.setAccessHash(getUser().getUserHash());

        TLRequestMessagesGetBotCallbackAnswer answer = new TLRequestMessagesGetBotCallbackAnswer() {
            @Override
            public void serializeBody(OutputStream stream) throws IOException {
                StreamingUtils.writeInt(1, stream);
                StreamingUtils.writeTLObject(peer, stream);
                StreamingUtils.writeInt(message.getId(), stream);
                StreamingUtils.writeTLBytes(button.getData(), stream);
            }
        };
        answer.setData(button.getData());
        answer.setPeer(peer);
        answer.setMsgId(message.getId());
        try {
            TLMessagesBotCallbackAnswer tlMessagesBotCallbackAnswer = kernelComm.doRpcCallSync(answer);
            System.out.println(tlMessagesBotCallbackAnswer.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void pressAttackAllianceButton(TLMessage message) {
        TLReplayInlineKeyboardMarkup replyMarkup = (TLReplayInlineKeyboardMarkup) message.getReplyMarkup();
        if (replyMarkup.getRows().size() == 1) {
            pressAttackButton(message);
            return;
        }
        TLKeyboardButtonCallback button = (TLKeyboardButtonCallback) replyMarkup.getRows().get(1).buttons.get(0);
        TLInputPeerUser peer = new TLInputPeerUser();
        peer.setUserId(message.getFromId());
        peer.setAccessHash(getUser().getUserHash());

        TLRequestMessagesGetBotCallbackAnswer answer = new TLRequestMessagesGetBotCallbackAnswer() {
            @Override
            public void serializeBody(OutputStream stream) throws IOException {
                StreamingUtils.writeInt(1, stream);
                StreamingUtils.writeTLObject(peer, stream);
                StreamingUtils.writeInt(message.getId(), stream);
                StreamingUtils.writeTLBytes(button.getData(), stream);
            }
        };
        answer.setData(button.getData());
        answer.setPeer(peer);
        answer.setMsgId(message.getId());
        try {
            TLMessagesBotCallbackAnswer tlMessagesBotCallbackAnswer = kernelComm.doRpcCallSync(answer);
            System.out.println(tlMessagesBotCallbackAnswer.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
