package bot.bs.handler;

import bot.bs.*;
import bot.bs.player.Battles;
import bot.bs.scenarios.*;
import bot.plugins.handlers.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.keyboard.button.TLKeyboardButtonCallback;
import org.telegram.api.keyboard.replymarkup.TLReplayInlineKeyboardMarkup;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static bot.bs.Helper.*;
import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class BSMessageHandler extends MessageHandler {

    private boolean ignoreHelperMessage = false;
    private boolean ignoreBotMessage = false;
    private boolean started = false;
    private BSMediator mediator = BSMediator.getInstance();
    private AttackManager attackManager = new AttackManager(this);
    private BSSender sender;

    private RunningScenario runningScenario;

    private SnowballThrower snowballThrower;

    private Timer joiningTimer;

    public BSMessageHandler() {
        attackManager.start();
        snowballThrower = new SnowballThrower(this);
    }

    public void setSender(BSSender sender) {
        this.sender = sender;
    }

    public void setIgnoreHelperMessage(boolean ignoreHelperMessage) {
        this.ignoreHelperMessage = ignoreHelperMessage;
    }

    private void sendHelperMessage(String message) {
        ignoreHelperMessage = true;
        sender.sendHelperMessage(message);
    }

    public void handleMessage(int chatId, @NotNull TLMessage tlMessage) {
        Chat helperChat = getSender().getHelperChat();

        if (helperChat == null) {
            System.out.println("helper chat is null");
            return;
        }

        if (chatId != helperChat.getId()) {
            return;
        }

        String message = tlMessage.getMessage().toLowerCase();

        if (message.startsWith("/")) {
            message = message.substring(1);
        }

        if (message.equals(COMMAND_EXIT)) {
            System.exit(0);
            return;
        }

        if (ignoreHelperMessage) {
            ignoreHelperMessage = false;
            return;
        }


        if (message.equals(Helper.COMMAND_STOP)) {
            if (runningScenario != null) {
                try {
                    runningScenario.stop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                runningScenario = null;
            }
            attackManager.stop();
            started = false;
            mediator.inBattle = false;
            return;
        }

        if (message.startsWith(Helper.COMMAND_SETGOLD)) {
            String[] parsed = message.split("\\D");

            int value = 100000;
            for (String s : parsed) {
                if (s.isEmpty()) {
                    continue;
                }

                try {
                    value = Integer.parseInt(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return;
                }

                if (value <= 0) {
                    getSender().sendHelperMessage("wrong value: " + value);
                    return;
                }

                break;
            }

            Settings.setGoldToChange(value);

            getSender().sendHelperMessage("new gold value: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_AUTOATTACK)) {
            String[] parsed = message.split("\\s");

            boolean value = true;
            for (String s : parsed) {
                if (s.isEmpty() || s.equals(Helper.COMMAND_AUTOATTACK)) {
                    continue;
                }

                try {
                    value = Boolean.parseBoolean(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return;
                }

                break;
            }

            Settings.setAutoAttack(value);

            getSender().sendHelperMessage("auto attack: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_AUTOSEARCH)) {
            String[] parsed = message.split("\\s");

            boolean value = true;
            for (String s : parsed) {
                if (s.isEmpty() || s.equals(Helper.COMMAND_AUTOSEARCH)) {
                    continue;
                }

                try {
                    value = Boolean.parseBoolean(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return;
                }

                break;
            }

            Settings.setAutoSearch(value);

            getSender().sendHelperMessage("auto search: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_AUTOBUILD)) {
            String[] parsed = message.split("\\s");

            boolean value = true;
            for (String s : parsed) {
                if (s.isEmpty() || s.equals(Helper.COMMAND_AUTOBUILD)) {
                    continue;
                }

                try {
                    value = Boolean.parseBoolean(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                    return;
                }

                break;
            }

            Settings.setAutoBuild(value);

            getSender().sendHelperMessage("auto build: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_ADD_ALLY_ALLIANCE)) {
            if (message.equals(Helper.COMMAND_ADD_ALLY_ALLIANCE)) {
                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_ADD_ALLY_ALLIANCE.length() + 1);
                alliance = alliance.trim();

                if (alliance.contains(";")) {
                    String[] opponents = alliance.split(";");
                    for (String opponent : opponents) {
                        opponent = opponent.trim();
                        opponent = Util.translateAllianceIfNeeded(opponent);

                        if (opponent.isEmpty()) {
                            continue;
                        }

                        Settings.addAllyAlliance(opponent);
                    }
                } else {
                    alliance = Util.translateAllianceIfNeeded(alliance);
                    Settings.addAllyAlliance(alliance);
                }
                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_REMOVE_ALLY_ALLIANCE)) {
            if (message.equals(Helper.COMMAND_REMOVE_ALLY_ALLIANCE)) {
                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_REMOVE_ALLY_ALLIANCE.length() + 1);
                alliance = alliance.trim();

                if (alliance.contains(";")) {
                    String[] opponents = alliance.split(";");
                    for (String opponent : opponents) {
                        opponent = opponent.trim();
                        opponent = Util.translateAllianceIfNeeded(opponent);

                        if (opponent.isEmpty()) {
                            continue;
                        }

                        Settings.removeAllyAlliance(opponent);
                    }
                } else {
                    alliance = Util.translateAllianceIfNeeded(alliance);
                    Settings.removeAllyAlliance(alliance);
                }

                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_ADD_ALLY_PLAYER)) {
            if (message.equals(Helper.COMMAND_ADD_ALLY_PLAYER)) {
                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            } else {
                String player = tlMessage.getMessage().substring(Helper.COMMAND_ADD_ALLY_PLAYER.length() + 1);
                player = player.trim();

                if (player.contains(";")) {
                    String[] opponents = player.split(";");
                    for (String opponent : opponents) {
                        opponent = opponent.trim();

                        if (opponent.isEmpty()) {
                            continue;
                        }

                        Settings.addAllyPlayer(opponent);
                    }
                } else {
                    Settings.addAllyPlayer(player);
                }

                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_REMOVE_ALLY_PLAYER)) {
            if (message.equals(Helper.COMMAND_REMOVE_ALLY_PLAYER)) {
                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            } else {
                String player = tlMessage.getMessage().substring(Helper.COMMAND_REMOVE_ALLY_PLAYER.length() + 1);
                player = player.trim();

                if (player.contains(";")) {
                    String[] opponents = player.split(";");
                    for (String opponent : opponents) {
                        opponent = opponent.trim();

                        if (opponent.isEmpty()) {
                            continue;
                        }

                        Settings.removeAllyPlayer(opponent);
                    }
                } else {
                    Settings.removeAllyPlayer(player);
                }

                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_BUILDING_SCENARIO)) {
            if (message.equals(Helper.COMMAND_BUILDING_SCENARIO)) {
                getSender().sendHelperMessage("building scenario: " + Settings.getBuildingScenario());
                return;
            }
            String scenario = message.substring(Helper.COMMAND_BUILDING_SCENARIO.length() + 1);
            String[] parsed = scenario.split("\\D+");

            int value = -1;
            StringBuilder sb = new StringBuilder();
            for (String s : parsed) {
                if (s.isEmpty()) {
                    continue;
                }

                try {
                    value = Integer.parseInt(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                    value = -1;
                }

                if (value < 0 || value > 8) {
                    getSender().sendHelperMessage("only values 0-8 are OK");
                    return;
                }

                sb.append(String.valueOf(value));
                sb.append(" ");
            }

            Settings.setBuildingScenario(sb.toString());

            getSender().sendHelperMessage("building scenario: " + sb.toString());
            return;
        } else if (message.startsWith(Helper.COMMAND_RISKY_ATTACK_ONLY)) {
            if (message.equals(Helper.COMMAND_RISKY_ATTACK_ONLY)) {
                getSender().sendHelperMessage("risky attack only " + Settings.isRiskyAttackOnlyEnabled());
                return;
            }
            String valueS = message.substring(Helper.COMMAND_RISKY_ATTACK_ONLY.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setRiskyAttackOnly(value);

            getSender().sendHelperMessage("risky attack only " + value);
            if (value) {
                Settings.setRiskyAttack(true);
                getSender().sendHelperMessage("risky attack enabled as well");
            }
            return;
        } else if (message.startsWith(Helper.COMMAND_RISKY_ATTACK)) {
            if (message.equals(Helper.COMMAND_RISKY_ATTACK)) {
                getSender().sendHelperMessage("risky attack " + Settings.isRiskyAttackEnabled());
                return;
            }
            String valueS = message.substring(Helper.COMMAND_RISKY_ATTACK.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setRiskyAttack(value);

            getSender().sendHelperMessage("risky attack " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_MAX_SEARCH)) {

            if (message.equals(Helper.COMMAND_MAX_SEARCH)) {
                getSender().sendHelperMessage("max search " + Settings.getMaxSearch());
                return;
            }

            String valueS = message.substring(Helper.COMMAND_MAX_SEARCH.length() + 1);
            int value = 100;
            try {
                value = Integer.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setMaxSearch(value);

            getSender().sendHelperMessage("max search " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_OPPONENT)) {
            String task = tlMessage.getMessage().substring(Helper.COMMAND_OPPONENT.length());
            task = task.trim();

            if (task.contains(";")) {
                String[] opponents = task.split(";");
                StringBuilder sb = new StringBuilder();

                for (String opponent : opponents) {
                    opponent = opponent.trim();
                    opponent = Util.translateAllianceIfNeeded(opponent);

                    if (opponent.isEmpty()) {
                        continue;
                    }

                    sb.append(opponent);
                    sb.append(";");
                }

                task = sb.toString();
            } else {
                task = Util.translateAllianceIfNeeded(task);;
            }

            Settings.setOpponent(task);
            attackManager.clearBattlesList();
            if (task.isEmpty()) {
                getSender().sendHelperMessage("next opponent could be any weak player");
            } else {
                getSender().sendHelperMessage("next opponent(s): " + task);
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_ATTACK_IF_MEET)) {
            String task = tlMessage.getMessage().substring(Helper.COMMAND_ATTACK_IF_MEET.length());
            task = task.trim();

            if (task.contains(";")) {
                String[] opponents = task.split(";");
                StringBuilder sb = new StringBuilder();

                for (String opponent : opponents) {
                    opponent = opponent.trim();
                    opponent = Util.translateAllianceIfNeeded(opponent);

                    if (opponent.isEmpty()) {
                        continue;
                    }
                    sb.append(opponent);
                    sb.append(";");
                }

                task = sb.toString();
            } else {
                task = Util.translateAllianceIfNeeded(task);
            }

            Settings.setAttackIfMeet(task);
            if (task.isEmpty()) {
                getSender().sendHelperMessage("no attack if meet");
            } else {
                getSender().sendHelperMessage("next attack if meet: " + task);
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_SEARCH_APPROPRIATE)) {
            if (message.equals(Helper.COMMAND_SEARCH_APPROPRIATE)) {
                getSender().sendHelperMessage("search appropriate " + Settings.isSearchAppropriate());
                return;
            }

            String valueS = message.substring(Helper.COMMAND_SEARCH_APPROPRIATE.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setSearchAppropriate(value);

            getSender().sendHelperMessage("search appropriate " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_GIVE_IMMUN)) {
            if (message.equals(Helper.COMMAND_GIVE_IMMUN)) {
                getSender().sendHelperMessage("give immun " + Settings.isGiveImmun());
                return;
            }

            String valueS = message.substring(Helper.COMMAND_GIVE_IMMUN.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setGiveImmun(value);

            getSender().sendHelperMessage("give immun " + value);

            return;
        } else if (message.equals(Helper.COMMAND_HELP)) {
            sendHelperMessage(Helper.RESPONSE_HELP);
            return;
        } else if (message.startsWith(Helper.COMMAND_ATTACK_CONQUEROR)) {
            if (message.equals(Helper.COMMAND_ATTACK_CONQUEROR)) {
                getSender().sendHelperMessage("attack conqueror " + Settings.isAttackConqueror());
                return;
            }

            String valueS = message.substring(Helper.COMMAND_ATTACK_CONQUEROR.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setAttackConqueror(value);

            getSender().sendHelperMessage("attack conqueror " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_JOIN_ALLIANCE_BATTLES)) {
            if (message.equals(Helper.COMMAND_JOIN_ALLIANCE_BATTLES)) {
                getSender().sendHelperMessage("join alliance battles " + Settings.shouldJoinAllianceBattles());
                return;
            }

            String valueS = message.substring(Helper.COMMAND_JOIN_ALLIANCE_BATTLES.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setJoinAllianceBattles(value);

            getSender().sendHelperMessage("join alliance battles " + value);

            return;
        } else if (message.startsWith(Helper.COMMAND_NO_SNOWBALLS_FOR)) {
            String task = tlMessage.getMessage().substring(Helper.COMMAND_NO_SNOWBALLS_FOR.length());
            task = task.trim();

            if (task.contains(";")) {
                String[] opponents = task.split(";");
                StringBuilder sb = new StringBuilder();

                for (String opponent : opponents) {
                    opponent = opponent.trim();
                    opponent = Util.translateAllianceIfNeeded(opponent);

                    if (opponent.isEmpty()) {
                        continue;
                    }

                    sb.append(opponent);
                    sb.append(";");
                }

                task = sb.toString();
            } else {
                task = Util.translateAllianceIfNeeded(task);
                ;
            }

            Settings.setNoSnowballsFor(task);
            if (task.isEmpty()) {
                getSender().sendHelperMessage("lets snowwar begin!");
            } else {
                getSender().sendHelperMessage("snowpeace with next player(s): " + task);
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_PLAY_SNOWBALLS)) {
            if (message.equals(Helper.COMMAND_PLAY_SNOWBALLS)) {
                getSender().sendHelperMessage("play snowballs: " + Settings.isPlaySnowballs());
                return;
            }

            String valueS = message.substring(Helper.COMMAND_PLAY_SNOWBALLS.length() + 1);
            boolean value = false;
            try {
                value = Boolean.valueOf(valueS);
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }

            Settings.setPlaySnowballs(value);

            getSender().sendHelperMessage("play snowballs " + value);
            snowballThrower.refresh();

            return;
        }

        if (runningScenario != null) {
            try {
                runningScenario.stop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            runningScenario = null;
            if (attackManager.isWaitingForRecover()) {
                attackManager.start();
            }
//            return;
        }

        if (message.equals(Helper.COMMAND_START)) {
            started = true;
            attackManager.start();
            snowballThrower.refresh();

            Settings.printSettings(sender);
            return;
        }

//        if (!started) {
//            sendHelperMessage("use start command in order to start the bot");
//            return;
//        }

        if (message.equals(Helper.COMMAND_FIND)) {
            started = true;
            runningScenario = new FindingScenario(this);
            runningScenario.start();
            return;
        } else if (message.equals(Helper.COMMAND_BUILD)) {
            started = true;
            if (mediator.inBattle) {
                sendHelperMessage("Cannot build while the battle");
                return;
            }

            runningScenario = new BuildingScenario(this);
            runningScenario.start();
            return;
        } else if (message.equals(COMMAND_RECOVER)) {
            started = true;
            runningScenario = new RecoverScenario(this);
            runningScenario.start();
            return;
        }

        sendHelperMessage("Unknown command. Try one of next:\n" +
                COMMAND_START + "\n" +
                COMMAND_STOP + "\n" +
                COMMAND_BUILD + "\n" +
                COMMAND_FIND + "\n" +
                COMMAND_HELP + "\n" +
                COMMAND_RECOVER + "\n" +
                COMMAND_EXIT
        );
    }

    @Override
    public void handleMessage(@NotNull TLUpdateShortMessage message) {
        if (!started) {
            return;
        }
        IUser bsBot = sender.getUser();

        if (bsBot == null) {
            return;
        }

        if (message.getUserId() != bsBot.getUserId()) {
            return;
        }

        if (shouldIgnore(message.getMessage())) {
            processIgnore(message.getMessage());
            return;
        }

        if (runningScenario != null) {
            runningScenario.handleMessage(message);
            return;
        }
    }

    private void processIgnore(String message) {
        if (ignoreBotMessage) {
            ignoreBotMessage = false;
        } else if (shouldRecover(message)) {
            if (message.contains(BATTLE_FINISHED)) {
                Battles.getInstance().addBattle(message);
                if (!mediator.buildingsParsed || mediator.wallLevel > 0) {
                    mediator.wallRuined = true;
                }
            }

            if (runningScenario != null) {
                runningScenario.stop();
            }

            runningScenario = new RecoverScenario(this);
            runningScenario.start();
        } else if (campaignFinished(message)) {
            if (mediator.inBattle) {
                return;
            }

            if (!message.contains(CAMPAIGN_SUCCESSFUL)) {
                return;
            }

            if (runningScenario != null) {
                runningScenario.stop();
            }

            runningScenario = new BuildingScenario(this);
            runningScenario.start();

            if (attackManager.isWaitingForRecover()) {
                attackManager.start();
            }
        } else if (message.contains(ENEMY_UNDER_IMMUN)) {
            if (runningScenario != null) {
                runningScenario.stop();
            }

            ignoreBotMessage = true;

            getSender().sendMessage(CONTROL_FIND_ALL);

            runningScenario = new FindingScenario(this);
            runningScenario.start();
        }
    }

    private boolean shouldRecover(String message) {
        return message.contains(BATTLE_FINISHED) || message.contains(DEFENCE_STARTED) || message.contains(WATCHERS_GONE);
    }

    @Override
    public void handleMessage(@NotNull TLMessage message) {
        if (joiningTimer != null) {
            joiningTimer.cancel();
            joiningTimer = null;
        }

        if (!started) {
            return;
        }
        IUser bsBot = sender.getUser();

        if (bsBot == null) {
            return;
        }

        if (message.getFromId() != bsBot.getUserId()) {
            return;
        }

        if (snowballThrower.canThrowSnowball()) {
            if (snowballThrower.isThrowingSnowball()) {
                snowballThrower.processThrowingSnowballMessage(message);
            } else {
                snowballThrower.throwSnowball();
            }
            if (!shouldIgnore(message.getMessage())) {
                return;
            }
        } else if (message.getMessage().startsWith("❄")) {
            return;
        }

        if (shouldIgnore(message.getMessage())) {
            processIgnore(message.getMessage());
            return;
        }

        if (shouldJoinAllianceBattle(message)) {
            if (runningScenario != null) {
                runningScenario.stop();
                runningScenario = null;
            }

            if (message.getMessage().contains(ALLY_ATTACKS)) {
                sender.pressAttackButton(message);
                if (joiningTimer != null) {
                    joiningTimer.cancel();
                    joiningTimer = null;
                }
                return;
            }

            if (joiningTimer != null) {
                joiningTimer.cancel();
            }
            joiningTimer = new Timer();
            joiningTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    joiningTimer = null;
                    if (!started) {
                        return;
                    }

                    if (mediator.inBattle) {
                        return;
                    }

                    if (!Settings.shouldJoinAllianceBattles()) {
                        return;
                    }

                    if (runningScenario != null) {
                        runningScenario.stop();
                        runningScenario = null;
                    }
                    sender.pressAttackButton(message);
                }
            }, (new Random().nextInt(55) + 5) * 1000);
            return;
        } else if (message.getMessage().contains("Похоже ты опоздал")) {
            if (runningScenario != null) {
                return;
            }

            if (Settings.isAutoSearch()) {
                FindingScenario scenario = new FindingScenario(this);
                setRunningScenario(scenario);
                scenario.start();
                return;
            }
            if (Settings.isAutoBuild()) {
                BuildingScenario scenario = new BuildingScenario(this);
                setRunningScenario(scenario);
                scenario.start();
                return;
            }
            RecoverScenario scenario = new RecoverScenario(this);
            setRunningScenario(scenario);
            scenario.start();
            return;
        }

        if (runningScenario != null) {
            try {
                runningScenario.handleMessage(message);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }
    }

    private boolean shouldJoinAllianceBattle(TLMessage tlMessage) {
        String message = tlMessage.getMessage();
        if (!Settings.shouldJoinAllianceBattles()) {
            return false;
        }

        String playerName = null;
        if (message.contains(ALLY_ATTACKED) || message.contains(ALLY_ATTACKS2)) {
            try {
                playerName = message.substring(message.indexOf("]") + 1, message.indexOf(" атаковал"));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (message.contains(ALLY_ATTACKS)) {
            try {
                TLReplayInlineKeyboardMarkup replyMarkup = (TLReplayInlineKeyboardMarkup) tlMessage.getReplyMarkup();
                TLKeyboardButtonCallback button = (TLKeyboardButtonCallback) replyMarkup.getRows().get(0).buttons.get(0);

                message = new String(button.getData().getData());

                playerName = message.substring(message.indexOf("]") + 1, message.indexOf(" против"));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (playerName == null) {
            return false;
        }

        playerName = playerName.trim();

        if (!Util.isAllyOrFriend(null, playerName)) {
            return false;
        }

        return (System.currentTimeMillis() - attackManager.getLastAttackTime()) >= AttackManager.TEN_MINUTES_IN_MILLIS;
    }

    protected boolean shouldIgnore(String message) {
        if (ignoreBotMessage) {
            return true;
        } else if (!Settings.shouldJoinAllianceBattles()) {
            if (message.contains(ALLY_ATTACKS)) {
                return true;
            } else if (message.contains(ALLY_ATTACKS2)) {
                return true;
            } else if (message.contains(ALLY_ATTACKED)) {
                return true;
            }
        }

        if (message.contains(DEFENCE_STARTED)) {
            mediator.inBattle = true;
            attackManager.battleStarted(message);
            return true;
        } else if (message.contains(ATTACK_STARTED)) {
            mediator.inBattle = true;
            attackManager.battleStarted(message);
            return true;
        } else if (message.contains(ALLIANCE_ATTACK_JOINED)) {
            mediator.inBattle = true;
            attackManager.battleStarted(message);
            return true;
        } else if (message.contains(ALLIANCE_DEFENCE_JOINED)) {
            mediator.inBattle = true;
            attackManager.battleStarted(message);
            return true;
        } else if (message.contains(BATTLE_FINISHED)) {
            mediator.inBattle = false;
            attackManager.battleFinished(message);
            return true;
        } else if (message.contains(YOU_WIN)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(YOU_LOST)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(BATTLE_UNFINISHED)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(HINT)) {
            return true;
        } else if (message.contains(REPORT_SENT)) {
            return true;
        } else if (message.startsWith(CAMPAIGN_FINISHED)) {
            return true;
        } else if (message.startsWith(ALREADY_IN_BATTLE)) {
            mediator.inBattle = true;
            if (runningScenario != null) {
                runningScenario.stop();
                runningScenario = null;
            }
            if (attackManager.isWaitingForRecover()) {
                attackManager.start();
            }
            return true;
        } else if (message.contains(ARMY_JOINED_DEFENCE)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(ARMY_JOINED_ATTACK)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(ENEMY_UNDER_IMMUN)) {
            mediator.inBattle = false;
            return true;
        } else if (message.equals(WATCHERS_GONE)) {
            mediator.inBattle = false;
            return true;
        }

        return false;
    }

    protected boolean campaignFinished(String message) {
        return message.startsWith(CAMPAIGN_FINISHED);
    }

    public void setRunningScenario(RunningScenario runningScenario) {
        this.runningScenario = runningScenario;
    }

    public RunningScenario getRunningScenario() {
        return runningScenario;
    }

    public BSSender getSender() {
        return sender;
    }

    public AttackManager getAttackManager() {
        return attackManager;
    }
}
