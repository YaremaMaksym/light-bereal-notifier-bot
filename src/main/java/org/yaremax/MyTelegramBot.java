package org.yaremax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyTelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    private final String botUsername;
    private final String primaryChatId;

    public MyTelegramBot() {
        super(getEnvVar("TELEGRAM_BOT_TOKEN"));
        this.botUsername = getEnvVar("TELEGRAM_BOT_USERNAME");
        this.primaryChatId = getEnvVar("TELEGRAM_PRIMARY_CHAT_ID");
        logger.info("TelegramBot initialized with username: {}", botUsername);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMessageToPrimaryChat(String text) {
        sendMessage(primaryChatId, text);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String receivedText = update.getMessage().getText();
            String senderChatId = update.getMessage().getChatId().toString();
            logger.info("Received message from {}: {}", senderChatId, receivedText);

            sendMessage(senderChatId, "You said: " + receivedText);
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            execute(message);
            logger.info("Sent message to {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to {}: {}", chatId, text, e);
        }
    }

    private static String getEnvVar(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Environment variable " + key + " is not set.");
        }
        return value;
    }
}
