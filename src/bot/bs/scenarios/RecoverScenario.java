package bot.bs.scenarios;

import bot.bs.AttackManager;
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
public class RecoverScenario implements RunningScenario {
    protected static final int RETRIEVING = 1;
    protected static final int TRADING = 2;

    protected int stage;

    protected int woodToBuy;
    protected int stoneToBuy;

    protected BSSender sender;
    protected String lastSentMessage;
    protected BSMessageHandler messageHandler;
    private int armyToRecoverFirst;
    private boolean inWallMenu;

    int goldRepair = 0;
    int woodRepair = 0;
    int stoneRepair = 0;

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
//        if (getMediator().inBattle) {
//            finish();
//            return;
//        }

        stage = RETRIEVING;
        sendMessage(CONTROL_UP);
    }

    @Override
    public void stop() {
        cancelTimer();
        messageHandler.setRunningScenario(null);
        messageHandler = null;
    }

    private void finish() {
        if (!Settings.isAutoBuild()) {
            if (getAttackManager().isWaitingForRecover()) {
                FindingScenario findingScenario = new FindingScenario(messageHandler);
                messageHandler.setRunningScenario(findingScenario);
                getAttackManager().setWaitingForRecover(false);
                findingScenario.start();
                messageHandler = null;
                return;
            }
            stop();
        } else {
            cancelTimer();
            messageHandler.setRunningScenario(null);

            if (getAttackManager().isWaitingForRecover()) {
                FindingScenario findingScenario = new FindingScenario(messageHandler);
                messageHandler.setRunningScenario(findingScenario);
                getAttackManager().setWaitingForRecover(false);
                findingScenario.start();
                messageHandler = null;
                return;
            }

            BuildingScenario buildingScenario = new BuildingScenario(messageHandler);
            messageHandler.setRunningScenario(buildingScenario);
            buildingScenario.start();
            messageHandler = null;
        }
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
//        if (getMediator().inBattle) {
//            if (message.contains(Util.BATTLE_FINISHED)) {
//                getMediator().inBattle = false;
//                sendMessage(CONTROL_UP);
//            }
//
//            return;
//        }

        switch (lastSentMessage) {
            case CONTROL_UP:
                inWallMenu = false;
                handleControlUp(message);
                break;
            case CONTROL_BUILDINGS:
                inWallMenu = false;
                handleBuildings(message);
                return;
            case CONTROL_BARRACKS:
                sendMessage(CONTROL_RECRUIT);
                break;
            case CONTROL_WALL:
                inWallMenu = true;
                handleWall(message);
                break;
            case CONTROL_RECRUIT:
                if (inWallMenu) {
                    handleRecruitArcher(message);
                    break;
                }
                handleRecruit(message);
                break;
            case CONTROL_REPAIR:
                stage = RETRIEVING;
                getMediator().wallRuined = false;
                sendMessage(CONTROL_UP);
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

    protected void handleControlUp(String message) {
        if (stage == RETRIEVING) {
            getMediator().parseMainState(message);
            sendMessage(CONTROL_BUILDINGS);
            return;
        } else if (stage == TRADING) {
            getMediator().parseMainState(message);
            sendMessage(CONTROL_TRADE);
            return;
        }
    }

    private void handleBuildings(String message) {
        getMediator().parseBuildingsState(message);

        if (getMediator().barracksLevel == 0 || (getMediator().army >= 1 && Settings.isGiveImmun())) {
            finish();
            return;
        }

        if (!getMediator().wallRuined && getMediator().archers >= getMediator().wallLevel * 10 && !getMediator().inBattle) {
            if (getMediator().army >= getMediator().barracksLevel * 40) {
                finish();
                return;
            }
        }

        if (getMediator().wallLevel <= 0) {
            sendMessage(CONTROL_BARRACKS);
            return;
        }

        if (getMediator().inBattle) {
            if (getMediator().population > getMediator().houseLevel * 15) {
                if (getMediator().barracksLevel * 8 <= getMediator().houseLevel) {
                    armyToRecoverFirst = getMediator().barracksLevel * 40 - getMediator().army;
                } else {
                    armyToRecoverFirst = getMediator().houseLevel * 5 - getMediator().army;
                }
            } else {
                armyToRecoverFirst = 0;
            }
        } else {
            if (!getMediator().wallRuined && getMediator().archers >= getMediator().wallLevel * 10) {
                armyToRecoverFirst = getMediator().barracksLevel * 40 - getMediator().army;
            }
        }

        if (armyToRecoverFirst > 0) {
            sendMessage(CONTROL_BARRACKS);
            return;
        }

        sendMessage(CONTROL_WALL);
    }

    private void handleWall(String message) {
        parseWall(message);

        if (getMediator().inBattle) {
            if (getMediator().archers < getMediator().wallLevel * 10) {
                sendMessage(CONTROL_RECRUIT);
            } else {
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        lastSentMessage = CONTROL_WALL;
                        sendMessage(CONTROL_BACK, false);
                    }
                }, 60 * 1000);
            }
        } else if (getMediator().wallRuined) {
            repairWalls(message);
        } else if (getMediator().archers < getMediator().wallLevel * 10) {
            sendMessage(CONTROL_RECRUIT);
        } else {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
        }
    }

    private void repairWalls(String message) {
        int goldRequired = goldRepair;
        int woodRequired = woodRepair;
        int stoneRequired = stoneRepair;

        if (getMediator().gold >= goldRequired && getMediator().wood >= woodRequired && getMediator().stone >= stoneRequired) {
            sendMessage(CONTROL_REPAIR);
            return;
        } else {
            int goldDiff = goldRequired - getMediator().gold;
            int woodDiff = woodRequired - getMediator().wood;
            int stoneDiff = stoneRequired - getMediator().stone;

            goldDiff += (woodDiff > 0) ? woodDiff * 2 : 0;
            goldDiff += (stoneDiff > 0) ? stoneDiff * 2 : 0;

            getMediator().woodRequired = woodDiff > 0 ? woodDiff : 0;
            getMediator().stoneRequired = stoneDiff > 0 ? stoneDiff : 0;

            if (goldDiff <= 0) {
                stage = TRADING;
                sendMessage(CONTROL_UP);
            } else {
                sendHelperMessage("Waiting for wall repair...");

                int waitingTime = ((goldDiff / getMediator().goldPerMinute) + 1) * 1000 * 60;
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stage = TRADING;
                        sendMessage(CONTROL_UP);
                    }
                }, waitingTime);
            }
        }
    }

    private void parseWall(String message) {
        String[] lines = message.split("\n");

        boolean shouldRepair = false;
        goldRepair = 0;
        woodRepair = 0;
        stoneRepair = 0;

        for (String line : lines) {
            if (line.contains(ARCHERS_KEYWORD)) {
                line = line.substring(ARCHERS_KEYWORD.length());
                getMediator().archers = retrieveInt(line);
            } else if (line.contains(WALL_RUINED_KEYWORD)) {
                shouldRepair = true;
                getMediator().wallRuined = true;
            } else if (line.contains(POPULATION_KEYWORD)) {
                line = line.substring(POPULATION_KEYWORD.length());
                getMediator().population = retrieveInt(line);
            } else if (line.contains(GOLD_KEYWORD)) {
                line = line.substring(GOLD_KEYWORD.length());
                getMediator().gold = retrieveInt(line);
            } else if (shouldRepair) {
                if (goldRepair == 0) {
                    goldRepair = retrieveInt(line);
                } else if (woodRepair == 0) {
                    woodRepair = retrieveInt(line);
                } else if (stoneRepair == 0) {
                    stoneRepair = retrieveInt(line);
                }
            }
        }

        if (!shouldRepair) {
            getMediator().wallRuined = false;
        }
    }

    private int retrieveInt(String line) {
        String[] digits = line.split("\\D+");

        for (String digit : digits) {
            if (digit.isEmpty()) {
                continue;
            }

            return Integer.valueOf(digit);
        }

        throw new IllegalArgumentException();
    }

    private void handleRecruitArcher(String message) {
        int archers = getMediator().archers;
        int wallCapacity = getMediator().wallLevel * 10;
        int freeRecruits = getMediator().population;
        int recruitsToCall = wallCapacity - archers;

        if (archers >= wallCapacity) {
            if (getMediator().inBattle) {
                createTimer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        lastSentMessage = CONTROL_WALL;
                        sendMessage(CONTROL_BACK, false);
                    }
                }, 60 * 1000);
                return;
            } else {
                stage = RETRIEVING;
                sendMessage(CONTROL_UP);
                return;
            }
        } else if (recruitsToCall > freeRecruits) {
            recruitsToCall = freeRecruits;
        }

        if (getMediator().gold < recruitsToCall * 10) {
            recruitsToCall = getMediator().gold / 10;
        }

        if (recruitsToCall <= 0) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lastSentMessage = CONTROL_WALL;
                    sendMessage(CONTROL_BACK, false);
                }
            }, 60 * 1000);
            return;
        }

        getMediator().archers += recruitsToCall;
        getMediator().population -= recruitsToCall;
        getMediator().gold -= recruitsToCall * 10;
        sendMessage(String.valueOf(recruitsToCall), false);
    }

    private void handleRecruit(String message) {
        if (message.contains(RECRUITING_ARMY_DONE_WELL)) {
            if (getMediator().army >= getMediator().barracksLevel * 40) {
                finish();
                return;
            }
        } else if (message.startsWith(RECRUITING_NO_PEOPLE)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stage = RETRIEVING;
                    sendMessage(CONTROL_UP);
                }
            }, 60*1000);
            return;
        }

        if (armyToRecoverFirst == 0) {
            stage = RETRIEVING;
            sendMessage(CONTROL_UP);
            return;
        }

        int army = getMediator().army;
        int barracksCapacity = getMediator().barracksLevel * 40;
        int freeRecruits = getMediator().population;
        int recruitsToCall = armyToRecoverFirst;

        if (Settings.isGiveImmun()) {
            if (army >= 1) {
                finish();
                return;
            } else if (recruitsToCall > 0) {
                recruitsToCall = 1;
            }
        }

        if (recruitsToCall <= 0) {
            finish();
            return;
        }

        if (getMediator().gold < recruitsToCall * 10) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stage = RETRIEVING;
                    sendMessage(CONTROL_UP);
                }
            }, 60 * 1000); // 1 min
            return;
        }

        if (army >= barracksCapacity) {
            finish();
            return;
        }

        if (recruitsToCall < freeRecruits) {
            getMediator().army += recruitsToCall;
            armyToRecoverFirst -= recruitsToCall;
            getMediator().population -= recruitsToCall;
            getMediator().gold -= recruitsToCall * 10;
            sendMessage(String.valueOf(recruitsToCall), false);
        } else if (freeRecruits > 0) {
            getMediator().army += freeRecruits;
            armyToRecoverFirst -= freeRecruits;
            getMediator().population -= freeRecruits;
            getMediator().gold -= freeRecruits * 10;
            sendMessage(String.valueOf(freeRecruits), false);
        } else {
            if (Settings.isGiveImmun()) {
                finish();
                return;
            }

            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getMediator().population += getMediator().houseLevel;
                    handleRecruit(message);
                }
            }, 60*1000);
        }
    }

    private AttackManager getAttackManager() {
        return messageHandler.getAttackManager();
    }

    private void handleBuy(String message) {
        woodToBuy = 0;
        stoneToBuy = 0;

        int woodRequired = getMediator().woodRequired;
        int stoneRequired = getMediator().stoneRequired;

        if ((woodRequired <= 0 && stoneRequired <= 0) || getMediator().gold < 2) {
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
            stoneToBuy = gold / 2;
            sendMessage(CONTROL_BUY_STONE);
            return;
        }

        if (stoneRequired <= 0) {
            woodToBuy = gold / 2;
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
