package org.yaremax;

import io.javalin.Javalin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        MyTelegramBot myTelegramBot;

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            myTelegramBot = new MyTelegramBot();
            botsApi.registerBot(myTelegramBot);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String portStr = getEnvVar("PORT");

        Javalin app = Javalin.create(config -> config.bundledPlugins.enableDevLogging())
                .start(Integer.parseInt(portStr));

        app.get("/api/v1/hello", ctx -> ctx.result("Hello from server!"));

        app.post("/api/v1/events", ctx -> {
            myTelegramBot.sendMessageToPrimaryChat("Hello!");
            ctx.result("Success");
        });
    }

    private static String getEnvVar(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Environment variable " + key + " is not set.");
        }
        return value;
    }
}