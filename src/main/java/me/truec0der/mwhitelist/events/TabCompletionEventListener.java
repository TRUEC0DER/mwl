package me.truec0der.mwhitelist.events;

import me.truec0der.mwhitelist.database.Database;
import me.truec0der.mwhitelist.models.UserModel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class TabCompletionEventListener implements TabCompleter {
    private Database database;

    public TabCompletionEventListener(Database database) {
        this.database = database;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("mwl.admin")) return null;

        switch (args.length) {
            case 1:
                completions.addAll(asList("toggle", "add", "addtemp", "remove", "list", "reload"));
                break;
            case 2:
                if (args[0].equalsIgnoreCase("toggle")) {
                    completions.addAll(asList("on", "off"));
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    completions.addAll(getUserNamesFromDatabase().join());
                }
                break;
        }

        String currentArg = args[args.length - 1].toLowerCase();
        completions = completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .collect(Collectors.toList());

        return completions;
    }

    private CompletableFuture<List<String>> getUserNamesFromDatabase() {
        return database.getUsers().thenApplyAsync(users -> {
            List<String> userNames = users.stream()
                    .map(UserModel::getNickname)
                    .collect(Collectors.toList());
            return userNames;
        });
    }
}
