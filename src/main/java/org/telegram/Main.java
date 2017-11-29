package org.telegram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.logging.BotsFileHandler;
import org.telegram.updateshandlers.WeatherHandlers;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import org.telegram.database.DBConnectTimerExecutor;
import org.telegram.database.DatabaseManager;
import org.telegram.services.CustomTimerTask;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Main class to create all bots
 * @date 20 of June of 2015
 */
public class Main {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {
        BotLogger.setLevel(Level.ALL);
        BotLogger.registerLogger(new ConsoleHandler());
        try {
            BotLogger.registerLogger(new BotsFileHandler());
        } catch (IOException e) {
            BotLogger.severe(LOGTAG, e);
        }

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = createTelegramBotsApi();
            try {
                telegramBotsApi.registerBot(new WeatherHandlers());
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
        
        startDBConnectionTimer();
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi;
            // Default (long polling only)
            telegramBotsApi = createLongPollingTelegramBotsApi();
        return telegramBotsApi;
    }

    /**
     * @brief Creates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     * @return TelegramBotsApi to register the bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }

    
        private static void startDBConnectionTimer() {
        DBConnectTimerExecutor.getInstance().startDBConnectExecutionEveryHour(new CustomTimerTask("Check DB Connection every HOUR", -1) {
            @Override
            public void execute() {
                if (!DatabaseManager.getInstance().checkDBConnect()) { 
                    BotLogger.error(LOGTAG, "DB check CONNECT = ERROR.");
                } else {
                    BotLogger.info(LOGTAG, "DB check CONNECT = OK.");
                }
            }
        });
    }

}
