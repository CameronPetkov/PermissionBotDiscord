package me.name.bot;

        import com.jagrosh.jdautilities.commandclient.CommandClient;
        import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
        import me.name.bot.Commands.CourseSelect;
        import me.name.bot.Commands.Enrolment;
        import me.name.bot.Commands.Unenrolment;
        import me.name.bot.Commands.Helper;
        import me.name.bot.Common.JSONLoad;
        import me.name.bot.Models.Configuration;
        import net.dv8tion.jda.core.AccountType;
        import net.dv8tion.jda.core.JDABuilder;
        import net.dv8tion.jda.core.entities.Game;


public class Bot {
    public static void main(String[] arguments) throws Exception {
        CommandClientBuilder builder = new CommandClientBuilder();
        Configuration config = JSONLoad.LoadJSON("data/config.json", Configuration.class);

        builder.setOwnerId(config.getOwnerID()); //my userid
        builder.setPrefix("!");  //!help, !command
        builder.addCommands(new Enrolment(), new Unenrolment(), new CourseSelect(), new Helper()); //4 commands

        CommandClient client = builder.build();

        JDABuilder api = new JDABuilder(AccountType.BOT);
        api.setGame(Game.streaming("Videos of blonde chicks", "www.blondegirls.com"));
        api.setToken(config.getToken()); //token-id, not to be released
        api.addEventListener(client);
        api.buildAsync();
    }
}