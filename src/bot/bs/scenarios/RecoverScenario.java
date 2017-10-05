package bot.bs.scenarios;

import bot.bs.BSMediator;
import bot.bs.handler.BSMessageHandler;
import bot.bs.Util;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;

import java.util.Timer;
import java.util.TimerTask;

import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class RecoverScenario implements RunningScenario {

    protected BSSender sender;
    protected String lastSentMessage;
    protected BSMessageHandler messageHandler;

    protected Timer timer;

    public RecoverScenario(BSMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.sender = messageHandler.getSender();
    }

    protected void sendMessage(String message) {
        sendMessage(message, true);
    }

    protected void sendMessage(String message, boolean rewriteLastSent) {
        if (rewriteLastSent) {
            lastSentMessage = message;
        }
        sender.sendMessage(message);
    }

    protected void sendHelperMessage(String message) {
        lastSentMessage = message;
        sender.sendHelperMessage(message);
    }

    @Override
    public void start() {
        if (getMediator().inBattle) {
            return;
        }

        sendMessage(CONTROL_UP);
    }

    @Override
    public void stop() {
        cancelTimer();
        messageHandler.setRunningScenario(null);

        if (!getMediator().autoBuild) {
            messageHandler = null;
            return;
        }

        BuildingScenario buildingScenario = new BuildingScenario(messageHandler);
        messageHandler.setRunningScenario(buildingScenario);
        buildingScenario.start();
        messageHandler = null;
    }

    BSMediator getMediator() {
        return BSMediator.getInstance();
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

    @Override
    public void handleMessage(TLMessage tlMessage) {
        handleMessage(tlMessage.getMessage());
    }

    @Override
    public void handleMessage(TLUpdateShortMessage tlUpdateShortMessage) {
        handleMessage(tlUpdateShortMessage.getMessage());
    }

    protected void handleMessage(String message) {
        if (getMediator().inBattle) {
            if (message.contains(Util.BATTLE_FINISHED)) {
                getMediator().inBattle = false;
                sendMessage(CONTROL_UP);
            }

            return;
        }

        switch (lastSentMessage) {
            case CONTROL_UP:
                getMediator().parseMainState(message);
                sendMessage(CONTROL_BUILDINGS);
                break;
            case CONTROL_BUILDINGS:
                getMediator().parseBuildingsState(message);
                if (getMediator().army == getMediator().barracksLevel * 40) {
                    stop();
                    return;
                }
                sendMessage(CONTROL_BARRACKS);
                break;
            case CONTROL_BARRACKS:
                sendMessage(CONTROL_RECRUIT);
                break;
            case CONTROL_RECRUIT:
                handleRecruit(message);
                break;
        }
    }

    private void handleRecruit(String message) {
        if (message.contains(RECRUITING_ARMY_DONE_WELL)) {
            if (getMediator().army >= getMediator().barracksLevel * 40) {
                stop();
                return;
            }
        }

        int army = getMediator().army;
        int barracksCapacity = getMediator().barracksLevel * 40;
        int freeRecruits = getMediator().population;
        int recruitsToCall = barracksCapacity - army;

        if (recruitsToCall == 0) {
            stop();
            return;
        }

        if (getMediator().gold < recruitsToCall * 10) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(CONTROL_UP);
                }
            }, (recruitsToCall * 10 - getMediator().gold) / getMediator().goldPerMinute);

            return;
        }

        if (army >= barracksCapacity) {
            stop();
            return;
        }

        if (recruitsToCall < freeRecruits) {
            getMediator().army += recruitsToCall;
            getMediator().population -= recruitsToCall;
            getMediator().gold -= recruitsToCall * 10;
            sendMessage(String.valueOf(recruitsToCall), false);
        } else if (freeRecruits > 0) {
            getMediator().army += freeRecruits;
            getMediator().population -= freeRecruits;
            getMediator().gold -= freeRecruits * 10;
            sendMessage(String.valueOf(freeRecruits), false);
        } else {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getMediator().population += getMediator().houseLevel;
                    handleRecruit(message);
                }
            }, 80*1000);
        }
    }
}
