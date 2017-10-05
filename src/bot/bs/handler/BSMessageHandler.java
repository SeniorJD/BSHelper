package bot.bs.handler;

import bot.bs.BSMediator;
import bot.bs.Helper;
import bot.bs.player.Battles;
import bot.bs.scenarios.*;
import bot.plugins.handlers.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import static bot.bs.Helper.COMMAND_RECOVER;
import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class BSMessageHandler extends MessageHandler {

    boolean ignoreHelperMessage = false;
    private boolean started = false;
    private BSMediator mediator = BSMediator.getInstance();
    private BSSender sender;

    private RunningScenario runningScenario;

    public BSMessageHandler() { }

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

        if (tlMessage.getMessage().startsWith("exit")) {
            System.exit(0);
            return;
        }

        if (ignoreHelperMessage) {
            ignoreHelperMessage = false;
            return;
        }

        String message = tlMessage.getMessage().toLowerCase();

        if (message.startsWith(Helper.COMMAND_STOP)) {
            if (runningScenario != null) {
                try {
                    runningScenario.stop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                runningScenario = null;
            }
            return;
        }

        if (runningScenario != null) {
            return;
        }

        if (message.startsWith(Helper.COMMAND_FIND)) {
            String task = tlMessage.getMessage().substring(Helper.COMMAND_FIND.length());
            if (!task.isEmpty()) {
                if (task.startsWith(" ")) {
                    task = task.substring(1);
                }
            }

            mediator.findOpponent = task;
            runningScenario = new FindingScenario(this);
            runningScenario.start();
            return;
        } else if (message.startsWith(Helper.COMMAND_SETGOLD)) {
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
                }

                mediator.goldToChange = value;
                break;
            }

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
                }

                mediator.autoAttack = value;
                break;
            }

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
                }

                mediator.autoSearch = value;
                break;
            }

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
                }

                mediator.autoBuild = value;
                break;
            }

            getSender().sendHelperMessage("auto build: " + value);
            return;
        } else if (message.startsWith(Helper.COMMAND_BUILD)) {
            if (mediator.inBattle) {
                sendHelperMessage("Cannot build while the battle");
                return;
            }

            runningScenario = new BuildingScenario(this);
            runningScenario.start();
            return;
        } else if (message.startsWith(COMMAND_RECOVER)) {
            runningScenario = new RecoverScenario(this);
            runningScenario.start();
            return;
        } else if (message.startsWith(Helper.COMMAND_HELP)) {
            sendHelperMessage(Helper.RESPONSE_HELP);
        }
    }

    @Override
    public void handleMessage(@NotNull TLUpdateShortMessage message) {
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
                runningScenario = new RecoverScenario(this);
                runningScenario.start();
            }
            return;
        }

        if (runningScenario != null) {
            runningScenario.handleMessage(message);
            return;
        }

        handleMessage(message.getMessage());
    }

    private boolean shouldRecover(String message) {
        return message.contains(BATTLE_FINISHED);
    }

    @Override
    public void handleMessage(@NotNull TLMessage message) {
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
                runningScenario = new RecoverScenario(this);
                runningScenario.start();
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

        handleMessage(message.getMessage());
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
            return true;
        } else if (message.contains(ATTACK_STARTED)) {
            mediator.inBattle = true;
            return true;
        } else if (message.contains(YOU_WIN)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(YOU_LOST)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(BATTLE_FINISHED)) {
            mediator.inBattle = false;
            return true;
        } else if (message.contains(HINT)) {
            return true;
        }

        return false;
    }

    protected void handleMessage(@NotNull String message) {
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
}
