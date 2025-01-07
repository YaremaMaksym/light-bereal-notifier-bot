package org.yaremax;

import io.javalin.Javalin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.nio.charset.Charset;

import static org.yaremax.Util.getEnvVar;

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
        String primaryMessage = getEnvVar("TELEGRAM_PRIMARY_MESSAGE");

        Javalin app = Javalin.create(config -> config.bundledPlugins.enableDevLogging())
                .start(Integer.parseInt(portStr));

        app.get("/api/v1/hello", ctx -> ctx.result("Hello from server!"));

        app.post("/api/v1/events", ctx -> {
            myTelegramBot.sendMessageToPrimaryChat(primaryMessage);
            ctx.result("Success");
        });

        System.out.println(primaryMessage);
        System.out.println("Default Charset = " + Charset.defaultCharset());
        System.out.println("file.encoding = " + System.getProperty("file.encoding"));
    }
}