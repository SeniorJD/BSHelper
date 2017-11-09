package bot.bs.handler;

import bot.bs.AttackManager;
import bot.bs.BSMediator;
import bot.bs.Helper;
import bot.bs.Settings;
import bot.bs.player.Battles;
import bot.bs.scenarios.*;
import bot.plugins.handlers.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import static bot.bs.Helper.*;
import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class BSMessageHandler extends MessageHandler {

    private boolean ignoreHelperMessage = false;
    private boolean started = false;
    private BSMediator mediator = BSMediator.getInstance();
    private AttackManager attackManager = new AttackManager(this);
    private BSSender sender;

    private RunningScenario runningScenario;

    public BSMessageHandler() {
        attackManager.start();
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
                Settings.addAllyAlliance(alliance);

                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_REMOVE_ALLY_ALLIANCE)) {
            if (message.equals(Helper.COMMAND_REMOVE_ALLY_ALLIANCE)) {
                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_REMOVE_ALLY_ALLIANCE.length() + 1);
                alliance = alliance.toLowerCase();
                Settings.removeAllyAlliance(alliance);

                getSender().sendHelperMessage(Settings.generateAllyAlliancesValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_ADD_ALLY_PLAYER)) {
            if (message.equals(Helper.COMMAND_ADD_ALLY_PLAYER)) {
                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_ADD_ALLY_PLAYER.length() + 1);
                alliance = alliance.toLowerCase();
                Settings.addAllyPlayer(alliance);

                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            }

            return;
        } else if (message.startsWith(Helper.COMMAND_REMOVE_ALLY_PLAYER)) {
            if (message.equals(Helper.COMMAND_REMOVE_ALLY_PLAYER)) {
                getSender().sendHelperMessage(Settings.generateAllyPlayersValues());
            } else {
                String alliance = message.substring(Helper.COMMAND_REMOVE_ALLY_PLAYER.length() + 1);
                Settings.removeAllyPlayer(alliance);

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
            }

            Settings.setBuildingScenario(scenario);

            getSender().sendHelperMessage("building scenario: " + scenario);
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
            if (!task.isEmpty()) {
                if (task.startsWith(" ")) {
                    task = task.substring(1);
                }
            }

            task = task.toLowerCase();

            Settings.setOpponent(task);
            attackManager.clearBattlesList();
            if (task.isEmpty()) {
                getSender().sendHelperMessage("next opponent could be any weak player");
            } else {
                getSender().sendHelperMessage("next opponent(s): " + task);
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
            if (shouldRecover(message.getMessage())) {
                Battles.getInstance().addBattle(message.getMessage());
                if (runningScenario != null) {
                    runningScenario.stop();
                }

                runningScenario = new RecoverScenario(this);
                runningScenario.start();
            } else if (campaignFinished(message.getMessage())) {
                if (mediator.inBattle) {
                    return;
                }

                if (!message.getMessage().contains(CAMPAIGN_SUCCESSFUL)) {
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
            }
            return;
        }

        if (runningScenario != null) {
            runningScenario.handleMessage(message);
            return;
        }
    }

    private boolean shouldRecover(String message) {
        return message.contains(BATTLE_FINISHED);
    }

    @Override
    public void handleMessage(@NotNull TLMessage message) {
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

        if (shouldIgnore(message.getMessage())) {
            if (shouldRecover(message.getMessage())) {
                Battles.getInstance().addBattle(message.getMessage());
                if (runningScenario != null) {
                    runningScenario.stop();
                }

                runningScenario = new RecoverScenario(this);
                runningScenario.start();
            } else if (campaignFinished(message.getMessage())) {
                if (mediator.inBattle) {
                    return;
                }

                if (!message.getMessage().contains(CAMPAIGN_SUCCESSFUL)) {
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
            }
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

    protected boolean shouldIgnore(String message) {
        if (message.contains(ALLY_ATTACKS)) {
            return true;
        } else if (message.contains(ALLY_ATTACKS2)) {
            return true;
        } else if (message.contains(ALLY_ATTACKED)) {
            return true;
        } else if (message.contains(DEFENCE_STARTED)) {
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
