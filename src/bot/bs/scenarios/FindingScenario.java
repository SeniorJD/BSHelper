package bot.bs.scenarios;

import bot.bs.BSMediator;
import bot.bs.Helper;
import bot.bs.Settings;
import bot.bs.Util;
import bot.bs.handler.BSMessageHandler;
import bot.bs.player.Battles;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;

import java.util.Timer;
import java.util.TimerTask;

import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class FindingScenario implements RunningScenario {

    protected BSSender sender;
    protected String lastSentMessage;
    protected BSMessageHandler messageHandler;

    protected Timer timer;

    boolean foundByName = false;

    public FindingScenario(BSMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.sender = messageHandler.getSender();
    }

    protected void sendMessage(String message) {
        lastSentMessage = message;
        sender.sendMessage(message);
    }

    protected void sendHelperMessage(String message) {
        lastSentMessage = message;
        sender.sendHelperMessage(message);
    }

    @Override
    public void start() {
        sendMessage(CONTROL_UP);
    }

    @Override
    public void stop() {
        cancelTimer();
        if (foundByName) {
            Settings.setFindOpponent("");
        }
        messageHandler.setRunningScenario(null);
        messageHandler = null;
    }

    protected void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected void createTimer() {
        cancelTimer();
        timer = new Timer();
    }

    BSMediator getMediator() {
        return BSMediator.getInstance();
    }

    @Override
    public void handleMessage(TLMessage tlMessage) {
        cancelTimer();

        if (getMediator().inBattle) {
            stop();
        }

        switch (lastSentMessage) {
            case CONTROL_UP:
                handleControlUp(tlMessage.getMessage());
                break;
            case CONTROL_WAR:
                handleWar();
                break;
            case CONTROL_FIND_ALL:
                handleFindAll(tlMessage);
                break;
        }
    }

    private void handleControlUp(String message) {
        getMediator().parseMainState(message);
        sendMessage(CONTROL_WAR);
    }

    private void handleWar() {
        lastSentMessage = Util.CONTROL_FIND_ALL;
        createTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(CONTROL_FIND_ALL);
            }
        }, 5000);
    }

    private void handleFindAll(@NotNull TLMessage tlMessage) {
        String message = tlMessage.getMessage();

        String originalMessage = message;

        if (originalMessage.contains(NO_MONEY)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(CONTROL_FIND_ALL);
                }
            }, 1000* 60);
            return;
        } else if (originalMessage.contains(NO_FOOD)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(CONTROL_FIND_ALL);
                }
            }, 1000* 60);
            return;
        }

        if (!message.startsWith(Helper.EXPLORING_1)) {
            sendHelperMessage(message);
            stop();
            return;
        }

        message = message.substring(Helper.EXPLORING_1.length());

        int index = message.indexOf(Helper.EXPLORING_2);

        String playerName = message.substring(0, index);
        String playerAlliance;
        if (playerName.startsWith("[")) {
            playerAlliance = playerName.substring(playerName.indexOf("[") + 1, playerName.indexOf("]"));
            playerName = playerName.substring(playerAlliance.length() + 2);
        } else {
            playerAlliance = "";
        }

        if (isAllyOrFriend(playerAlliance, playerName)) {
            sendMessage(CONTROL_FIND_ALL);
            return;
        }

        if (!Settings.getFindOpponent().isEmpty()) {
            foundByName = playerName.contains(Settings.getFindOpponent());
            if (foundByName || playerAlliance.contains(Settings.getFindOpponent())) {
                stop();
                if (Settings.isAutoAttack()) {
                    sender.pressAttackButton(tlMessage);
                } else {
                    sendHelperMessage(originalMessage);
                }
                return;
            } else {
                sendMessage(CONTROL_FIND_ALL);
                return;
            }
        }

        if (Battles.getInstance().getGoldPerUnit(playerName) < 100) {
            sendMessage(CONTROL_FIND_ALL);
            return;
        }

        index = message.indexOf(Helper.EXPLORING_3);
        message = message.substring(index + Helper.EXPLORING_3.length());

        String territoryS = message.substring(0, message.indexOf(Util.TERRITORY_SIGN));
        int territory = Integer.parseInt(territoryS);

        if (territory > Math.min(20000, getMediator().territory / 2)) {
            sendMessage(CONTROL_FIND_ALL);
            return;
        }

        index = message.indexOf(Helper.EXPLORING_4);

        if (index != -1) {
            message = message.substring(index + Helper.EXPLORING_4.length());

            String karmaS = message.substring(0, message.indexOf(Util.KARMA_SIGN));
            int karma = Integer.parseInt(karmaS);

            if (karma == 0 || karma == 1) {
                stop();
                if (Settings.isAutoAttack()) {
                    sender.pressAttackButton(tlMessage);
                } else {
                    sendHelperMessage(originalMessage);
                }
            } else {
                sendMessage(CONTROL_FIND_ALL);
            }
        } else {
            stop();
            if (Settings.isAutoAttack()) {
                sender.pressAttackButton(tlMessage);
            } else {
                sendHelperMessage(originalMessage);
            }
        }

    }

    @Override
    public void handleMessage(TLUpdateShortMessage tlUpdateShortMessage) {
        String message = tlUpdateShortMessage.getMessage();
        if (message.contains(NO_MONEY)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(CONTROL_FIND_ALL);
                }
            }, 1000* 60);
            return;
        } else if (message.contains(NO_FOOD)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(CONTROL_FIND_ALL);
                }
            }, 1000* 60);
            return;
        }

        sendHelperMessage("Finding scenario got unexpected message: \n" + tlUpdateShortMessage.getMessage());
        stop();
    }

    private boolean isAllyOrFriend(String playerAlliance, String playerName) {
        if (!playerAlliance.isEmpty()) {
            for (String s : Settings.getAllyAlliances()) {
                if (playerAlliance.contains(s)) {
                    return true;
                }
            }
        }

        playerName = playerName.toLowerCase();
        for (String s : Settings.getAllyPlayers()) {
            if (playerName.contains(s)) {
                return true;
            }
        }

        return false;
    }
}
