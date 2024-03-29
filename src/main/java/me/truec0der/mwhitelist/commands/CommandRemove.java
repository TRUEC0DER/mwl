package me.truec0der.mwhitelist.commands;

import me.truec0der.mwhitelist.database.Database;
import me.truec0der.mwhitelist.models.ConfigModel;
import me.truec0der.mwhitelist.models.UserModel;
import me.truec0der.mwhitelist.utils.MessageUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CommandRemove {
    private final MessageUtil messageUtil;
    private ConfigModel configModel;
    private Database database;

    public CommandRemove(ConfigModel configModel, Database database, MessageUtil messageUtil) {
        this.configModel = configModel;
        this.messageUtil = messageUtil;
        this.database = database;
    }

    public boolean execute(Audience sender, String[] args) {
        if (args.length < 2) {
            sendNeedMoreArgsMessage(sender);
            return true;
        }

        String playerName = args[1];

        CompletableFuture<UserModel> findUserInWhitelist = database.getUser(playerName);

        findUserInWhitelist.thenAcceptAsync(user -> {
            if (user == null) {
                sendNotInWhitelist(sender, playerName);
            } else {
                removeFromWhitelist(sender, playerName);
            }
        });

        return true;
    }

    private void sendNeedMoreArgsMessage(Audience sender) {
        Component needMoreArgs = messageUtil.create(configModel.getMessageWhitelistRemoveNeedMoreArgs());
        sender.sendMessage(needMoreArgs);
    }

    private void sendNotInWhitelist(Audience sender, String playerName) {
        Map<String, String> notInWhitelistPlaceholders = new HashMap<>();
        notInWhitelistPlaceholders.put("player_name", playerName);
        Component notInWhitelist = messageUtil.create(configModel.getMessageWhitelistRemoveNotInWhitelist(), notInWhitelistPlaceholders);
        sender.sendMessage(notInWhitelist);
    }

    private void removeFromWhitelist(Audience sender, String playerName) {
        Map<String, String> removeFromWhitelistPlaceholders = new HashMap<>();
        removeFromWhitelistPlaceholders.put("player_name", playerName);

        database.deleteUser(playerName);

        Component removeFromWhitelist = messageUtil.create(configModel.getMessageWhitelistRemoveInfo(), removeFromWhitelistPlaceholders);
        sender.sendMessage(removeFromWhitelist);
    }
}
