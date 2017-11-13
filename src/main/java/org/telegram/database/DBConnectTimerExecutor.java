/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.telegram.database;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.telegram.services.CustomTimerTask;
import org.telegram.telegrambots.logging.BotLogger;


/**
 *
 * @author moiseev
 */
public class DBConnectTimerExecutor {
    private static final String LOGTAG = "DBCONNECTTIMEREXECUTOR";
    
    private static volatile DBConnectTimerExecutor instance; ///< Instance
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1); ///< Thread to execute operations

    
    private DBConnectTimerExecutor() {
    }

    /**
     * Singleton pattern
     *
     * @return Instance of the executor
     */
    public static DBConnectTimerExecutor getInstance() {
        final DBConnectTimerExecutor currentInstance;
        if (instance == null) {
            synchronized (DBConnectTimerExecutor.class) {
                if (instance == null) {
                    instance = new DBConnectTimerExecutor();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }

        return currentInstance;
    }

    /**
     * Add a new CustomTimerTask to be executed
     * @param task       Task to execute
     */
    public void startDBConnectExecutionEveryHour(CustomTimerTask task) {
        BotLogger.warn(LOGTAG, "Posting new task" + task.getTaskName());
        final Runnable taskWrapper = () -> {
            try {
                task.execute();
//                task.reduceTimes();
//                startDBConnectExecutionEveryHour(task);
            } catch (Exception e) {
                BotLogger.severe(LOGTAG, "Bot threw an unexpected exception at DBConnecTimerExecutor", e);
            }
        };
        executorService.scheduleAtFixedRate(taskWrapper, 0, 60L, TimeUnit.MINUTES);
                //scheduleAtFixedRate(taskWrapper, 1L, ,1L, TimeUnit.SECONDS);
//        if (task.getTimes() != 0) {
//            final long delay = computNextDilay(targetHour, targetMin, targetSec);
//            executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
//        }
    }

}
