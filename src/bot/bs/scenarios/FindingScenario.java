package bot.bs.scenarios;

import bot.bs.*;
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
    private static final String RECRUITING = "recruiting";
    private static final String SEMICOLON = ";";

    protected BSSender sender;
    protected String lastSentMessage;
    protected BSMessageHandler messageHandler;

    protected Timer timer;

    boolean foundByName = false;

    int searchCount = 0;

    int shouldRecoverTrebuchet;

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

    protected void attack(TLMessage message) {
//        if (Settings.isGiveImmun()) {
//            sender.pressAttackAllianceButton(message);
//        } else {
//            sender.pressAttackButton(message);
//        }
        sender.pressAttackButton(message);
    }

    protected void delayAttack(TLMessage tlMessage) {
        long lastAttackTime = messageHandler.getAttackManager().getLastAttackTime();
        long difference = System.currentTimeMillis() - lastAttackTime;
        createTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stop();
                attack(tlMessage);
            }
        }, AttackManager.TEN_MINUTES_IN_MILLIS - difference);
    }

    protected boolean canAttack() {
        long lastAttackTime = messageHandler.getAttackManager().getLastAttackTime();
        long difference = System.currentTimeMillis() - lastAttackTime;

        return difference >= AttackManager.TEN_MINUTES_IN_MILLIS;
    }

    @Override
    public void start() {
        sendMessage(CONTROL_UP);
    }

    @Override
    public void stop() {
        cancelTimer();
//        if (foundByName && !Settings.isGiveImmun()) {
//            Settings.setOpponent("");
//        }
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
                handleWar(tlMessage.getMessage());
                break;
            case CONTROL_FIND_ALL:
            case CONTROL_FIND_APPROPRIATE:
                handleFindAll(tlMessage);
                break;
            case CONTROL_RECRUIT:
                handleRecruit();
                break;
            case CONTROL_TREBUCHET2:
                handleRecruitTrebuchet2();
                break;
            case RECRUITING:
                sendMessage(CONTROL_UP);
                break;
        }
    }

    private void handleRecruitTrebuchet2() {
        if (getMediator().population < shouldRecoverTrebuchet) {
            int deficit = shouldRecoverTrebuchet - getMediator().population;

            long mins = deficit / getMediator().houseLevel + 1;

            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lastSentMessage = RECRUITING;
                    sender.sendMessage(String.valueOf(shouldRecoverTrebuchet));
                }
            }, mins * AttackManager.MINUTE_IN_MILLIS);
        } else {
            lastSentMessage = RECRUITING;
            sender.sendMessage(String.valueOf(shouldRecoverTrebuchet));
        }
    }

    private void handleRecruit() {
        sendMessage(CONTROL_TREBUCHET2);
    }

    private void handleControlUp(String message) {
        getMediator().parseMainState(message);

        int shouldRecoverArmy = shouldRecoverArmy(message);
        if (shouldRecoverArmy > 0) {
            BSMessageHandler messageHandler = this.messageHandler;
            stop();
            messageHandler.getAttackManager().setWaitingForRecover(true);
            RunningScenario runningScenario = new RecoverScenario(messageHandler);
            messageHandler.setRunningScenario(runningScenario);
            runningScenario.start();
            return;
        }
        sendMessage(CONTROL_WAR);
    }

    private void handleWar(String message) {
        if (message.contains(NEXT_ATTACK)) {
            String[] lines = message.split("\n");
            for (String line : lines) {
                if (!line.contains(NEXT_ATTACK)) {
                    continue;
                }

                String[] split = line.split("\\D+");

                long waitTime = Integer.valueOf(split[1]);

                String s = line.substring(line.indexOf(split[1]) + split[1].length() + 1);

                if (s.equalsIgnoreCase(NEXT_ATTACK_MINS)) {
                    // is ok
                } else if (s.equalsIgnoreCase(NEXT_ATTACK_SECS)) {
                    waitTime = 1;
                } else {
                    sendHelperMessage("smth gone wrong, attack delayed for 10 minutes");
                    waitTime = 10;
                }

                waitTime *= AttackManager.MINUTE_IN_MILLIS;
                waitTime = AttackManager.TEN_MINUTES_IN_MILLIS - waitTime;
                waitTime = System.currentTimeMillis() - waitTime;

                messageHandler.getAttackManager().setLastAttackTime(waitTime);
                break;
            }
        } else {
            messageHandler.getAttackManager().setLastAttackTime(System.currentTimeMillis() - AttackManager.TEN_MINUTES_IN_MILLIS - AttackManager.MINUTE_IN_MILLIS);
        }

        shouldRecoverTrebuchet = shouldRecoverTrebuchet(message);
        if (shouldRecoverTrebuchet > 0) {
            sendMessage(CONTROL_RECRUIT);
            return;
        }

        lastSentMessage = getFindMessage();
        createTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(getFindMessage());
            }
        }, 5000);
    }

    private int shouldRecoverArmy(String message) {
        try {
            if (!getMediator().buildingsParsed) {
                return 1;
            }

            if (!message.contains(ARMY_WORD) || getMediator().barracksLevel <= 0) {
                return 0;
            }

            message = message.substring(message.indexOf(ARMY_WORD) + ARMY_WORD.length());
            message = message.substring(0, message.indexOf(ARMY_SIGN));

            message = message.trim();

            String[] data = message.split("\\D+");

            int army = Integer.valueOf(data[0]);

            if (Settings.isGiveImmun()) {
                return 1 - army;
            } else {
                return getMediator().barracksLevel - army;
            }
        } catch (Throwable t) {
            return 0;
        }
    }

    private int shouldRecoverTrebuchet(String message) {
        try {
            if (!message.contains(CONTROL_TREBUCHET) || !getMediator().buildingsParsed) {
                return 0;
            }

            message = message.substring(message.indexOf(CONTROL_TREBUCHET) + CONTROL_TREBUCHET.length());
            message = message.substring(0, message.indexOf(POPULATION_SIGN));

            message = message.trim();

            String[] data = message.split("\\D+");

            int[] people = new int[data.length];

            for (int i = 0; i < data.length; i++) {
                people[i] = Integer.valueOf(data[i]);
            }

            return people[1] - people[0];
        } catch (Throwable t) {
            return 0;
        }
    }

    private void handleFindAll(@NotNull TLMessage tlMessage) {
        if (searchCount == Settings.getMaxSearch() && Settings.getFindOpponent() != null && !Settings.getFindOpponent().isEmpty() && !Settings.isGiveImmun()) {
            sendHelperMessage("maximum searches amount reached, switching to default");
        }
        searchCount++;

        String message = tlMessage.getMessage();

        String originalMessage = message;

        if (originalMessage.contains(NO_MONEY)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(getFindMessage());
                }
            }, AttackManager.MINUTE_IN_MILLIS);
            return;
        } else if (originalMessage.contains(NO_FOOD)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(getFindMessage());
                }
            }, AttackManager.MINUTE_IN_MILLIS);
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
        if (playerName.startsWith(Helper.CONQUEROR)) {
            if (Settings.isAttackConqueror()) {
                if (canAttack()) {
                    stop();
                    attack(tlMessage);
                } else {
                    delayAttack(tlMessage);
                }
                return;
            }
            playerName = playerName.substring(playerName.indexOf(Helper.CONQUEROR) + Helper.CONQUEROR.length());
        } else if (playerName.startsWith(Helper.EXPLORING_6)) {
            playerName = playerName.substring(playerName.indexOf(Helper.EXPLORING_6) + Helper.EXPLORING_6.length());
        }

        if (playerName.startsWith("[")) {
            playerAlliance = playerName.substring(playerName.indexOf("[") + 1, playerName.indexOf("]"));
            playerName = playerName.substring(playerAlliance.length() + 2);

            playerAlliance = Util.translateAllianceIfNeeded(playerAlliance);
        } else {
            playerAlliance = "";
        }

        if (isAllyOrFriend(playerAlliance, playerName)) {
            sendMessage(getFindMessage());
            return;
        }

        if (shouldAttackIfMeet(playerName, playerAlliance)) {
            if (Settings.isAutoAttack()) {
                if (canAttack()) {
                    stop();
                    attack(tlMessage);
                } else {
                    delayAttack(tlMessage);
                }
            } else {
                stop();
                sendHelperMessage(originalMessage);
            }
            return;
        }

        if (!Settings.getFindOpponent().isEmpty() && (searchCount < Settings.getMaxSearch() || Settings.getMaxSearch() < 0)) {
            foundByName = isEnemyByName(playerName);
            boolean foundByAlliance = isEnemyByAlliance(playerAlliance);
            if (foundByName || foundByAlliance) {
                if (Settings.isAutoAttack()) {
                    if (canAttack()) {
                        stop();
                        attack(tlMessage);
                    } else {
                        delayAttack(tlMessage);
                    }
                } else {
                    stop();
                    sendHelperMessage(originalMessage);
                }
                return;
            } else {
                sendMessage(getFindMessage());
                return;
            }
        }

        if (Battles.getInstance().getGoldPerUnit(playerName) < 100) {
            sendMessage(getFindMessage());
            return;
        }

        index = message.indexOf(Helper.EXPLORING_3);
        message = message.substring(index + Helper.EXPLORING_3.length());

        String territoryS = message.substring(0, message.indexOf(Util.TERRITORY_SIGN));
        int territory = Integer.parseInt(territoryS);

//        if (territory > Math.min(20000, getMediator().territory / 2)) {
//            sendMessage(getFindMessage());
//            return;
//        }

        index = message.indexOf(Helper.EXPLORING_4);

        if (index != -1) {
            message = message.substring(index + Helper.EXPLORING_4.length());

            String karmaS = message.substring(0, message.indexOf(Util.KARMA_SIGN));
            int karma = Integer.parseInt(karmaS);

            if (Settings.isRiskyAttackOnlyEnabled() && Settings.isRiskyAttackEnabled()) {
                if (territory < 4000 && karma >= 0) {
                    if (canAttack()) {
                        stop();
                        attack(tlMessage);
                    } else {
                        delayAttack(tlMessage);
                    }
                } else {
                    sendMessage(getFindMessage());
                }
            } else if (karma == 0 || karma == 1) {
                if (Settings.isAutoAttack()) {
                    if (canAttack()) {
                        stop();
                        attack(tlMessage);
                    } else {
                        delayAttack(tlMessage);
                    }
                } else {
                    stop();
                    sendHelperMessage(originalMessage);
                }
            } else if (Settings.isRiskyAttackEnabled() && territory < 4000 && karma >= 0) {
                if (canAttack()) {
                    stop();
                    attack(tlMessage);
                } else {
                    delayAttack(tlMessage);
                }
            } else {
                sendMessage(getFindMessage());
            }
        } else {
            if (territory > Math.min(20000, getMediator().territory / 2)) {
                sendMessage(getFindMessage());
                return;
            }

            if (Settings.isAutoAttack()) {
                if (canAttack()) {
                    stop();
                    attack(tlMessage);
                } else {
                    delayAttack(tlMessage);
                }
            } else {
                stop();
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
                    sendMessage(getFindMessage());
                }
            }, AttackManager.MINUTE_IN_MILLIS);
            return;
        } else if (message.contains(NO_FOOD)) {
            createTimer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(getFindMessage());
                }
            }, AttackManager.MINUTE_IN_MILLIS);
            return;
        }

        sendHelperMessage("Finding scenario got unexpected message: \n" + tlUpdateShortMessage.getMessage());
        stop();
    }

    private boolean isAllyOrFriend(String playerAlliance, String playerName) {
        if (!playerAlliance.isEmpty()) {
            for (String s : Settings.getAllyAlliances()) {
                if (playerAlliance.equals(s)) {
                    return true;
                }
            }
        }

        for (String s : Settings.getAllyPlayers()) {
            if (playerName.equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;
    }

    private boolean isEnemyByName(String playerName) {
        String opponentS = Settings.getFindOpponent();

        String[] opponents = opponentS.split(SEMICOLON);
        for (String opponent : opponents) {
            if (playerName.equals(opponent)) {
                return true;
            }
        }

        return false;
    }

    private boolean isEnemyByAlliance(String playerAlliance) {
        String opponentS = Settings.getFindOpponent();

        String[] opponents = opponentS.split(SEMICOLON);
        for (String opponent : opponents) {
            if (playerAlliance.equals(opponent)) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldAttackIfMeet(String playerName, String playerAlliance) {
        if (Settings.getAttackIfMeet().isEmpty()) {
            return false;
        }

        String opponentS = Settings.getAttackIfMeet();

        String[] opponents = opponentS.split(SEMICOLON);
        for (String opponent : opponents) {
            if (playerName.equals(opponent)) {
                return true;
            }

            if (playerAlliance.equals(opponent)) {
                return true;
            }
        }

        return false;
    }

    protected String getFindMessage() {
        return Settings.isSearchAppropriate() ? CONTROL_FIND_APPROPRIATE : CONTROL_FIND_ALL;
    }
}
