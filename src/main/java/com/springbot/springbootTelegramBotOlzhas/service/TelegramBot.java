    package com.springbot.springbootTelegramBotOlzhas.service;

    import com.google.cloud.speech.v1.*;
    import com.google.protobuf.ByteString;
    import com.springbot.springbootTelegramBotOlzhas.config.BotConfig;
    import com.springbot.springbootTelegramBotOlzhas.model.Messages;//class
    import com.springbot.springbootTelegramBotOlzhas.model.MessagesRepository;
    import com.springbot.springbootTelegramBotOlzhas.model.User;
    import com.springbot.springbootTelegramBotOlzhas.model.UserRepository;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    import org.telegram.telegrambots.bots.TelegramLongPollingBot;
    import org.telegram.telegrambots.meta.api.methods.GetFile;
    import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
    import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
    import org.telegram.telegrambots.meta.api.objects.File;
    import org.telegram.telegrambots.meta.api.objects.Message;//api
    import org.telegram.telegrambots.meta.api.objects.Update;
    import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
    import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
    import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
    import java.io.IOException;
    import java.io.InputStream;
    import java.net.URL;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.StandardCopyOption;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    @Slf4j
    @Component
    public class TelegramBot extends TelegramLongPollingBot {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private MessagesRepository messagesRepository;

        @Autowired
        private CurrencyConversionService currencyConversionService;

        final BotConfig config;

        static final String HELP_TEXT = "Hello! I'm your helpful Telegram bot, designed to assist with currency conversion tasks. Here are the commands you can use:\n" +
                "\n" +
                "/start - Get a welcome message when you start a conversation with me. \n" +
                "/mydata - Retrieve your stored data from our database.\n" +
                "/delete - Delete your data from our database. Use this with caution.\n" +
                "/help - Get a list of commands and instructions on how to use me.\n" +
                "/voice - To send a voice message, but this currently doesn't work.\n" +
                "\n" +
                "Currency conversion:\n" +
                "To convert currency, simply send a message in the following format:\n" +
                "\"[Amount] [Currency Code]\"\n" +
                "For example: \"100 USD\" to convert US Dollars to Tenge, or \"457 KZT\" to convert Tenge to US Dollars. I'll respond with the converted amount according to current exchange rates.\n" +
                "\n" +
                "Please note: I'm still learning, so I might not understand everything you say. I can only work with EUR and dollars If you encounter any issues, please report them to my creators. Thanks for using me!\n ";

        public TelegramBot(BotConfig config){
            this.config = config;
            List<BotCommand> listOfCommands = new ArrayList<>();
            listOfCommands.add(new BotCommand("/start", "get a welcome message"));
            listOfCommands.add(new BotCommand("/mydata", "get my data"));
            listOfCommands.add(new BotCommand("/delete", "delete data my data"));
            listOfCommands.add(new BotCommand("/help", "how you use this bot"));
            listOfCommands.add(new BotCommand("/voice", "send a voice message"));
            try{
                this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
            }catch (TelegramApiException e){
                    log.error("Error setting bot's command list: " + e.getMessage());
            }
        }
        @Override
        public String getBotUsername() {
            return config.getBotName();
        }

        @Override
        public String getBotToken() {
            return config.getToken();
        }

        @Override
        public void onUpdateReceived(Update update) {

            if(update.hasMessage() && update.getMessage().hasText()){
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                if(messageText.startsWith("/")){
                    switch (messageText){
                        case "/start":
                            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                            registerUser(update.getMessage());
                            break;
                        case "/help":
                            sendMessage(chatId, HELP_TEXT);
                            break;
                        case "/mydata":
                            sendMessage(chatId, "...");
                            mydataCommandReceived(chatId);
                            break;
                        case "/delete":
                            deleteCommandReceived(chatId);
                            break;
                        case "/voice":
                            sendMessage(chatId, "Please send a voice message with the amount and currency code.");
                            break;
                        default:
                            sendMessage(chatId, "Sorry, command was not recognized");
                    }
                }else{
                    String[] parts = messageText.split(" ");
                    if (parts.length == 2) {
                        // Assuming the format of the message is "100 USD"
                        String amountStr = parts[0];
                        String currencyCode = parts[1];
                        if(currencyCode.equals("USD") || currencyCode.equals("usd")){
                            try {
                                double amount = Double.parseDouble(amountStr);
                                saveMessageByUserChatId(update.getMessage());
                                // Now you have the amount and currency code, you can handle the conversion here
                                handleCurrencyConversionUSDtoEUR(chatId, amount, currencyCode);
                            } catch (NumberFormatException e) {
                                // The first part of the message wasn't a number
                                sendMessage(chatId, "Sorry, I didn't understand that. Please send a message in the format \"[Amount] [Currency Code]\".");
                            }
                        } else if (currencyCode.equals("EUR") || currencyCode.equals("eur")) {
                            try {
                                double amount = Double.parseDouble(amountStr);
                                saveMessageByUserChatId(update.getMessage());
                                // Now you have the amount and currency code, you can handle the conversion here
                                handleCurrencyConversionEURtoUSD(chatId, amount, currencyCode);
                            } catch (NumberFormatException e) {
                                // The first part of the message wasn't a number
                                sendMessage(chatId, "Sorry, I didn't understand that. Please send a message in the format \"[Amount] [Currency Code]\".");
                            }

                        }else{
                            sendMessage(chatId, "Only convert currencies USD or EUR");
                        }

                    } else {
                        sendMessage(chatId, "Sorry, I didn't understand that. Please send a message in the format \"[Amount] [Currency Code]\".");
                    }
                }

            }
            if (update.hasMessage() && update.getMessage().hasVoice()) {
                // Voice message handling code
                String fileId = update.getMessage().getVoice().getFileId();
                File file;
                try {
                    GetFile getFileMethod = new GetFile(); // Initialize a new GetFile method
                    getFileMethod.setFileId(fileId); // Set the fileId on the method
                    file = execute(getFileMethod);

                } catch (TelegramApiException e) {
                    log.error("Failed to get file: " + e.getMessage());
                    return;
                }

                String filePath = file.getFilePath();
                String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

                long chatId = update.getMessage().getChatId();
                int messageId = update.getMessage().getMessageId();
                String outputFilePath = "/app/voice_messages/voice_" + chatId + "_" + messageId + ".ogg";


                try (InputStream in = new URL(fileUrl).openStream()) {
                    Path outputPath = Paths.get(outputFilePath);
                    Files.createDirectories(outputPath.getParent()); // Create the directory if it doesn't exist
                    Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Failed to download voice message: " + e.getMessage());
                    return;
                }

                try (SpeechClient speech = SpeechClient.create()) {


                    // transcribing audio
                    RecognitionConfig recConfig =
                            RecognitionConfig.newBuilder()
                                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                    .setSampleRateHertz(16000)
                                    .setLanguageCode("en-US")
                                    .build();

                    RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                            .setContent(ByteString.copyFrom(Files.readAllBytes(Paths.get(outputFilePath))))
                            .build();

                    RecognizeResponse response = speech.recognize(recConfig, recognitionAudio);

                    for (SpeechRecognitionResult result : response.getResultsList()) {
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        String transcript = alternative.getTranscript();
                        processTextMessage(transcript, update);
                    }
                } catch (IOException e) {
                    log.error("Failed to transcribe audio: " + e.getMessage());

                }

            }

        }



        private void processTextMessage(String messageText, Update update) {
            long chatId = update.getMessage().getChatId();


                String[] parts = messageText.split(" ");
                if (parts.length == 2) {
                    String amountStr = parts[0];
                    String currencyCode = parts[1];
                    if (currencyCode.equalsIgnoreCase("USD")) {
                        try {
                            double amount = Double.parseDouble(amountStr);
                            // Now you have the amount and currency code, you can handle the conversion here
                            handleCurrencyConversionUSDtoEUR(chatId, amount, currencyCode);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "Sorry, I didn't understand that. Please send a message in the format \"[Amount] [Currency Code]\".");
                        }
                    } else if (currencyCode.equalsIgnoreCase("EUR")) {
                        try {
                            double amount = Double.parseDouble(amountStr);
                            // Now you have the amount and currency code, you can handle the conversion here
                            handleCurrencyConversionEURtoUSD(chatId, amount, currencyCode);
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "Sorry, I didn't understand that. Please send a message in the format \"[Amount] [Currency Code]\".");
                        }
                    } else {
                        sendMessage(chatId, "Sorry, only conversions for USD or EUR are supported.");
                    }
                } else {
                    sendMessage(chatId, "Sorry, I didn't understand that. Please send a message in the format \"[Amount] [Currency Code]\".");
                }

        }

        private void deleteCommandReceived(long chatId) {
            // Fetch the user from the database by their chatId
            Optional<User> optionalUser = userRepository.findById(chatId);

            // Check if a User with the provided chatId exists
            if (optionalUser.isPresent()) {
                // Get the User object
                User user = optionalUser.get();

                // Fetch all the messages for this user from the database
                List<Messages> userMessages = messagesRepository.findByUser(user);

                // Delete all user messages
                messagesRepository.deleteAll(userMessages);

                // Send the user a confirmation message
                sendMessage(chatId, "All your messages have been deleted.");
            } else {
                sendMessage(chatId, "No user found with the provided chatId.");
            }
        }

        private void handleCurrencyConversionEURtoUSD(long chatId, double amount, String currencyCode) {

            try {
                double convertedAmount = currencyConversionService.convertCurrency(amount, "EUR", "USD");
                sendMessage(chatId, amount + " EUR  is equivalent to " + convertedAmount + " " + "USD");
            } catch (RuntimeException e) {
                log.error("Currency conversion failed: " + e.getMessage());
                sendMessage(chatId, "Failed to perform currency conversion. Please try again later.");
            }
        }

        private void handleCurrencyConversionUSDtoEUR(long chatId, double amount, String currencyCode) {
            try {
                double convertedAmount = currencyConversionService.convertCurrency(amount, "USD", "EUR");
                sendMessage(chatId, amount + " USD  is equivalent to " + convertedAmount + " " + "EUR");
            } catch (RuntimeException e) {
                log.error("Currency conversion failed: " + e.getMessage());
                sendMessage(chatId, "Failed to perform currency conversion. Please try again later.");
            }
        }

        private void registerUser(Message msg) {
            if(userRepository.findById(msg.getChatId()).isEmpty()){

                var chatId = msg.getChatId();
                var chat = msg.getChat();

                User user = new User();
                user.setChatId(chatId);
                user.setUsername((chat.getUserName()));
                user.setRegisteredAt(LocalDateTime.now());

                userRepository.save(user);
                log.info("User saved " + user);

            }

        }

        private void saveMessageByUserChatId(Message msg){
            var chatId = msg.getChatId();
            var userOptional = userRepository.findById(chatId);
            if(userOptional.isPresent()){
                var user = userOptional.get();
                var messageText = msg.getText();

                // Create a new Message entity and save it in the database
                var message = new Messages();
                message.setMessageText(messageText);
                message.setUser(user);
                message.setUsername(user.getUsername());
                messagesRepository.save(message);

                log.info("Message saved " + message);
            } else {
                log.error("Failed to save message, user not found. ChatId: " + chatId);
            }
        }



        private void startCommandReceived(long chatId, String name){
            String answer = "Hi, " + name + ", nice to meet you!";
            sendMessage(chatId, answer);
        }

        private void mydataCommandReceived(long chatId){
            // Fetch the user from the database by their chatId
            Optional<User> optionalUser = userRepository.findById(chatId);

            // Check if a User with the provided chatId exists
            if (optionalUser.isPresent()) {
                // Get the User object
                User user = optionalUser.get();

                // Fetch all the messages for this user from the database
                List<Messages> userMessages = messagesRepository.findByUser(user);

                // Check if the user has any messages
                if (userMessages.isEmpty()) {
                    sendMessage(chatId, "You haven't sent any messages yet.");
                } else {
                    // Create a StringBuilder to build the message
                    StringBuilder messageText = new StringBuilder("Here are your messages:\n");

                    // Iterate over the user's messages and add them to the message text
                    for (Messages message : userMessages) {
                        messageText.append("- ").append(message.getMessageText()).append("\n");
                    }

                    // Send the user's messages back to them
                    sendMessage(chatId, messageText.toString());
                }
            } else {
                sendMessage(chatId, "No user found with the provided chatId.");
            }
        }

        private void sendMessage(long chatId, String textToSend)  {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(textToSend);
            try{
                execute(message);
            }catch (TelegramApiException e){
                log.error("Error occured: " + e.getMessage());
            }
        }
    }
