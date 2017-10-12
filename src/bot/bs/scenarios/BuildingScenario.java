package bot.bs.scenarios;

import bot.bs.BSMediator;
import bot.bs.Settings;
import bot.bs.handler.BSMessageHandler;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;

import java.util.Timer;
import java.util.TimerTask;

import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class BuildingScenario implements RunningScenario {

    protected static final int RETRIEVING = 1;
    protected static final int TRADING = 2;
    protected static final int BUILDING = 3;
    protected static final int WAITING = 4;

    protected BSSender sender;
    protected String lastSentMessage;
    protected BSMessageHandler messageHandler;

    protected int stage;

    protected int woodToBuy;
    protected int stoneToBuy;


    protected Timer timer;

    protected Timer findingTimer = new Timer();

    public BuildingScenario(BSMessageHandler messageHandler) {
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
        stage = RETRIEVING;
        sendMessage(CONTROL_UP);

        if (!Settings.isAutoSearch()) {
            return;
        }

        BSMessageHandler messageHandler = this.messageHandler;

        findingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                FindingScenario scenario = new FindingScenario(messageHandler);
                stop();
                messageHandler.setRunningScenario(scenario);
                scenario.start();
            }
        }, (Settings.isGiveImmun() ? 60 : 11)*1000*60);
    }

    @Override
    public void stop() {
        cancelTimer();
        findingTimer.cancel();
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
        handleMessage(tlMessage.getMessage());
    }

    @Override
    public void handleMessage(TLUpdateShortMessage tlUpdateShortMessage) {
        handleMessage(tlUpdateShortMessage.getMessage());
    }

    protected void handleMessage(String message) {
        cancelTimer();

        if (getMediator().inBattle) {
            stop();
        }

        switch (lastSentMessage) {
            case CONTROL_UP:
                handleControlUp(message);
                break;
            case CONTROL_BUILDINGS:
                handleControlBuildings(message);
                break;
            case CONTROL_TOWN:
            case CONTROL_STOCK:
            case CONTROL_HOUSE:
            case CONTROL_FARM:
            case CONTROL_SAWMILL:
            case CONTROL_MINES:
            case CONTROL_BARRACKS:
            case CONTROL_WALL:
                sendMessage(CONTROL_UPGRADE);
                break;
            case CONTROL_UPGRADE:
                handleUpgradeMessage(message);
                break;
            case CONTROL_TRADE:
                sendMessage(CONTROL_BUY);
                break;
            case CONTROL_BUY:
                handleBuy(message);
                break;
            case CONTROL_BUY_WOOD:
                handleBuyWood(message);
                break;
            case CONTROL_BUY_STONE:
                handleBuyStone(message);
                break;
            case CONTROL_BUY_FOOD:
                handleBuyFood(message);
                break;

        }
    }

    private void handleControlUp(String message) {
        switch (stage) {
            case RETRIEVING:
                parseMainState(message);
                break;
            case TRADING:
                getMediator().parseMainState(message);
                getMediator().refreshUpgradeList();

                sendMessage(CONTROL_TRADE);
                break;
        }
    }

    private void parseMainState(String message) {
        if (!message.contains(SEASON)) {
            sendHelperMessage("Building failed: \n" + message);
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stage = RETRIEVING;
                    sendMessage(CONTROL_UP);
                }
            }, 60 * 1000);

            return;
        }

        getMediator().parseMainState(message);

        sendMessage(CONTROL_BUILDINGS);
    }

    private void handleControlBuildings(String message) {
        switch (stage) {
            case RETRIEVING:
                parseBuildingsState(message);
                break;
            case BUILDING: {
                if (getMediator().nextBuildingToUpgrade == TOWN) {
                    sendMessage(CONTROL_TOWN);
                } else if (getMediator().nextBuildingToUpgrade == STOCK) {
                    sendMessage(CONTROL_STOCK);
                } else if (getMediator().nextBuildingToUpgrade == HOUSE) {
                    sendMessage(CONTROL_HOUSE);
                } else if (getMediator().nextBuildingToUpgrade == FARM) {
                    sendMessage(CONTROL_FARM);
                } else if (getMediator().nextBuildingToUpgrade == SAWMILL) {
                    sendMessage(CONTROL_SAWMILL);
                } else if (getMediator().nextBuildingToUpgrade == MINES) {
                    sendMessage(CONTROL_MINES);
                } else if (getMediator().nextBuildingToUpgrade == BARRACKS) {
                    sendMessage(CONTROL_BARRACKS);
                } else if (getMediator().nextBuildingToUpgrade == WALL) {
                    sendMessage(CONTROL_WALL);
                }
                break;
            }
        }
    }

    private void parseBuildingsState(String message) {
        if (!message.contains(BUILDINGS_MENU)) {
            sendHelperMessage("Something gone wrong: \n" + message);

            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stage = RETRIEVING;
                    sendMessage(CONTROL_UP);
                }
            }, 60 * 1000);

            return;
        }

        getMediator().parseBuildingsState(message);

        if (getMediator().timeToWait == 0) {
            stage = BUILDING;
            handleControlBuildings(message);
        } else if (getMediator().timeToWait < 0) {
            getMediator().timeToWait = 0;
            stage = TRADING;
            sendMessage(CONTROL_UP);
        } else {
            stage = WAITING;
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stage = RETRIEVING;
                    sendMessage(CONTROL_UP);
                }
            }, getMediator().timeToWait);
        }
    }

    private void handleUpgradeMessage(String message) {
        if (!message.contains(CANNOT_BUILD)) {
            if (getMediator().nextBuildingToUpgrade == TOWN) {
                getMediator().townLevel++;
            } else if (getMediator().nextBuildingToUpgrade == HOUSE) {
                getMediator().houseLevel++;
            } else if (getMediator().nextBuildingToUpgrade == STOCK) {
                getMediator().stockLevel++;
            } else if (getMediator().nextBuildingToUpgrade == FARM) {
                getMediator().farmLevel++;
            } else if (getMediator().nextBuildingToUpgrade == SAWMILL) {
                getMediator().sawmillLevel++;
            } else if (getMediator().nextBuildingToUpgrade == MINES) {
                getMediator().minesLevel++;
            } else if (getMediator().nextBuildingToUpgrade == BARRACKS) {
                getMediator().barracksLevel++;
            } else if (getMediator().nextBuildingToUpgrade == WALL) {
                getMediator().wallLevel++;
            }
        }

        stage = RETRIEVING;
        sendMessage(CONTROL_UP);
    }

    private void handleBuy(String message) {
        woodToBuy = 0;
        stoneToBuy = 0;

        if (getMediator().foodRequired > 0) {
            sendMessage(CONTROL_BUY_FOOD);
            return;
        }

        int woodRequired = getMediator().woodRequired;
        int stoneRequired = getMediator().stoneRequired;

        if (woodRequired <= 0 && stoneRequired <= 0) {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
            return;
        }

        int gold = getMediator().gold;

        int goldRequired = woodRequired * 2 + stoneRequired * 2;

        if (gold >= goldRequired) {
            woodToBuy = woodRequired;
            stoneToBuy = stoneRequired;
            if (woodToBuy > 0) {
                sendMessage(CONTROL_BUY_WOOD);
            } else if (stoneToBuy > 0) {
                sendMessage(CONTROL_BUY_STONE);
            } else {
                stage = RETRIEVING;
                sendMessage(CONTROL_UP);
            }
            return;
        }

        if (woodRequired <= 0) {
            woodToBuy = 0;
            stoneToBuy = gold / 4;
            sendMessage(CONTROL_BUY_STONE);
            return;
        }

        if (stoneRequired <= 0) {
            woodToBuy = gold / 4;
            stoneToBuy = 0;
            sendMessage(CONTROL_BUY_WOOD);
            return;
        }

        if (woodRequired == stoneRequired) {
            woodToBuy = gold / 4;
            stoneToBuy = woodToBuy;
        } else if (woodRequired > stoneRequired) {
            int diff = woodRequired - stoneRequired;

            if (diff > gold / 2) {
                diff = gold / 2;
            }

            int goldTemp = gold - diff * 2;
            int fourthPart = goldTemp / 4;

            woodToBuy = fourthPart + diff;
            stoneToBuy = fourthPart;
        } else {
            int diff = stoneRequired - woodRequired;

            if (diff > gold / 2) {
                diff = gold / 2;
            }

            int goldTemp = gold - diff * 2;
            int fourthPart = goldTemp / 4;

            woodToBuy = fourthPart;
            stoneToBuy = fourthPart + diff;
        }

        if (woodToBuy > 0) {
            sendMessage(CONTROL_BUY_WOOD);
        } else if (stoneToBuy > 0) {
            sendMessage(CONTROL_BUY_STONE);
        } else {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
        }
    }

    private void handleBuyWood(String message) {
        if (message.startsWith(NO_MONEY_NO_HONEY) || message.startsWith(NO_STOCK_PLACE)) {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
        }

        if (getMediator().woodRequired <= 0 || woodToBuy <= 0) {
            lastSentMessage = CONTROL_BUY;
            sendMessage(CONTROL_BACK, false);
            return;
        }

        int amount = woodToBuy;

        getMediator().woodRequired -= amount;
        getMediator().gold -= amount * 2;
        woodToBuy -= amount;

        sendMessage(String.valueOf(amount), false);
    }

    private void handleBuyStone(String message) {
        if (message.startsWith(NO_MONEY_NO_HONEY) || message.startsWith(NO_STOCK_PLACE)) {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
        }

        if (getMediator().stoneRequired <= 0 || stoneToBuy <= 0) {
            lastSentMessage = CONTROL_BUY;
            sendMessage(CONTROL_BACK, false);
            return;
        }

        int amount = stoneToBuy;

        getMediator().stoneRequired -= amount;
        getMediator().gold -= amount * 2;
        stoneToBuy -= amount;

        sendMessage(String.valueOf(amount), false);
    }

    private void handleBuyFood(String message) {
        if (message.startsWith(NO_MONEY_NO_HONEY) || message.startsWith(NO_STOCK_PLACE)) {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
            return;
        }

        if (getMediator().foodRequired <= 0) {
            lastSentMessage = CONTROL_BUY;
            sendMessage(CONTROL_BACK, false);
            return;
        }

        if (getMediator().gold <= 2) {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
            return;
        }

        int amount = Math.min(getMediator().foodRequired, getMediator().gold / 2);

        getMediator().foodRequired -= amount;
        getMediator().gold -= amount * 2;

        sendMessage(String.valueOf(amount), false);
    }
}
