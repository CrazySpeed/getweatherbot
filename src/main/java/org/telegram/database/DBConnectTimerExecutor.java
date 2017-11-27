/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.telegram.database;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.telegram.services.CustomTimerTask;
import org.telegram.telegrambots.logging.BotLogger;


/**
 *
 * @author Crazy Speed
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
        final Runnable taskWrapper = () -> {
            try {
                task.execute();
            } catch (Exception e) {
                BotLogger.error(LOGTAG, "Bot threw an unexpected exception at DBConnecTimerExecutor", e);
            }
        };
        executorService.scheduleAtFixedRate(taskWrapper, 0, 10L, TimeUnit.MINUTES);
        
    }

}
