package me.name.bot;

import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import me.name.bot.Commands.*;
import me.name.bot.Common.JSONLoad;
import me.name.bot.Models.Configuration;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class Bot {
    public static void main(String[] arguments) throws Exception {
        Configuration config = JSONLoad.LoadJSON("data/config.json", Configuration.class);

        CommandClientBuilder builder = new CommandClientBuilder();

        builder.setOwnerId(config.getOwnerID()); //user-id
        builder.setPrefix("#");
        builder.addCommands(new Enrolment(), new Unenrolment(), new CourseSelect(), new Helper(), new UnitOutline());
        builder.setGame(null);
        CommandClient client = builder.build();

        JDABuilder api = new JDABuilder(AccountType.BOT);
        api.setToken(config.getToken()); //token-id, not to be released
        api.setGame(Game.watching(config.getGame()));

        api.addEventListener(client);
        api.buildAsync();
    }
}