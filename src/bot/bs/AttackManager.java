package bot.bs;

import bot.bs.handler.BSMessageHandler;
import bot.bs.scenarios.FindingScenario;
import bot.bs.scenarios.RecoverScenario;
import bot.bs.scenarios.RunningScenario;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static bot.bs.Util.*;

/**
 * @author SeniorJD
 */
public class AttackManager {
    public static final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    public static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000;
    public static final long MINUTE_IN_MILLIS = 60 * 1000;

    private Map<String, Long> battlesTime = new HashMap<>();

    private long lastBattleTime;
    private boolean isAttacking = false;
    private boolean waitingForRecover = false;
    private Timer timer;
    private volatile boolean timerCancelled;

    protected BSMessageHandler messageHandler;

    public AttackManager(BSMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void start() {
        schedule(2);
        lastBattleTime = System.currentTimeMillis();
        waitingForRecover = false;
    }

    protected void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected void createTimer() {
        cancelTimer();
        timer = new Timer() {
            @Override
            public void cancel() {
                timerCancelled = true;
                super.cancel();
            }
        };
    }

    public void battleStarted(String message) {
        if (message.contains(ATTACK_STARTED)) {
            isAttacking = true;
            waitingForRecover = false;
            cancelTimer();
        } else if (message.contains(ALLIANCE_ATTACK_JOINED)) {
            isAttacking = true;
            waitingForRecover = false;
            cancelTimer();
        } else if (message.contains(ALLIANCE_DEFENCE_JOINED)) {
            isAttacking = false;
            waitingForRecover = false;
            cancelTimer();
        } else if (message.contains(DEFENCE_STARTED)) {
            isAttacking = false;
            waitingForRecover = false;
            cancelTimer();
        }
    }

    public void battleFinished(String message) {
        long waitTime = getWaitTime(message);
        schedule(waitTime);
    }

    public void schedule(long waitTime) {
        isAttacking = false;
        waitingForRecover = false;

        if (waitTime < 0) {
            // just to be sure
            waitTime = 10;
        }

        waitTime *= MINUTE_IN_MILLIS;

        createTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerCancelled = false;

                if (!Settings.isAutoSearch()) {
                    return;
                }

                RunningScenario runningScenario = messageHandler.getRunningScenario();
                if (runningScenario != null) {
                    if (runningScenario instanceof RecoverScenario) {
                        waitingForRecover = true;
                        return;
                    } else if (runningScenario instanceof FindingScenario) {
                        return;
                    }

                    runningScenario.stop();
                    messageHandler.setRunningScenario(null);
                }
                FindingScenario scenario = new FindingScenario(messageHandler);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (timerCancelled) {
                    return;
                }

                messageHandler.setRunningScenario(scenario);
                scenario.start();
            }
        }, waitTime);
    }

    private long getWaitTime(String message) {
        long waitTime = 0;
        if (!isAttacking) {
            waitTime = (TEN_MINUTES_IN_MILLIS - (System.currentTimeMillis() - lastBattleTime)) / MINUTE_IN_MILLIS;

            if (waitTime < 0) {
                waitTime = 0;
            }

            return waitTime;
        }

        waitTime = 10;

        lastBattleTime = System.currentTimeMillis();

        if (!message.contains(BATTLE_FINISHED) || !Settings.isGiveImmun() || Settings.getFindOpponent().isEmpty() || message.contains(BATTLE_FINISHED_ALLIANCE)) {
            return waitTime;
        }

        String opponents[] = Settings.getFindOpponent().split(";");
        int opponentCount = opponents.length;
        String opponent = getOpponentName(message);

        battlesTime.put(opponent, System.currentTimeMillis());

        if (battlesTime.size() < opponentCount) {
            return waitTime;
        } else if (opponentCount == 1) {
            waitTime = 60;
            return waitTime;
        }

        long min = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : battlesTime.entrySet()) {
            min = Long.min(min, entry.getValue());
        }

        waitTime = (HOUR_IN_MILLIS - (lastBattleTime - min)) / MINUTE_IN_MILLIS;

        if (waitTime < 10) {
            return 10;
        }

        return waitTime;
    }

    private String getOpponentName(String message) {
        message = message.substring(message.indexOf(BATTLE_FINISHED) + BATTLE_FINISHED.length() + 1);
        if (message.contains("[")) {
            message = message.substring(message.indexOf("]") + 1);
        }

        return message.substring(0, message.indexOf(FINISHED) - 1);
    }

    public void clearBattlesList() {
        battlesTime.clear();
    }

    public boolean isWaitingForRecover() {
        return waitingForRecover;
    }

    public void setWaitingForRecover(boolean waitingForRecover) {
        this.waitingForRecover = waitingForRecover;
    }

    public void stop() {
        cancelTimer();
        waitingForRecover = false;
    }

    public long getLastAttackTime() {
        return lastBattleTime;
    }

    public void setLastAttackTime(long lastBattleTime) {
        this.lastBattleTime = lastBattleTime;
    }
}
