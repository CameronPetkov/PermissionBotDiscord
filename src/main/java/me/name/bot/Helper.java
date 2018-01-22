package me.name.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helper extends Command {

    public Helper() {
        this.name = "examples";
        this.aliases = new String[]{"helper"};
        this.help = "This command provides examples for user to learn how to use the bot.";
    }

    @Override
    protected void execute(CommandEvent event) {
        LocalDateTime timeStamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String time = timeStamp.format(formatter);

        String author = event.getMember().getEffectiveName();
        String message = event.getMessage().getContentDisplay();
        String userMsg = time + " - " + author + ": " + message;

        IO io = new IO();
        io.write(userMsg);
        event.replyInDm("-----------------------------------------------");
        event.replyInDm("**" + userMsg + "**");

        event.getMessage().delete().queue(); //Delete user message
        event.replyInDm("Hi there! This bot is designed to quickly and automatically \"enrol\" you into the server, giving you the permissions needed to view text and voice channels for the relevant units. \n");

        event.replyInDm("__First__, let's make sure other students know your major! \n" +
                "To do this, type **!course add** and then your degree (ee, cs, or eecs - for double degree students!) \n" +
                "For example, if I am a CS student, I would type: **!course add cs** \n" +
                "You can also use \"computer science\" or \"electrical engineering\" in place of cs and ee!");

        event.replyInDm("If you make a mistake, simply repeat the command, but change \"add\" to \"remove\".");

        event.replyInDm("\n __Now__ we can enrol into units we are taking this semester. \n" +
                "To do this, type **!enrol** and then the units you are taking, separated by commas. \n" +
                "You can enter units by 8-digit unitcode, full unit name, and sometimes (if you're lucky), common abbreviations or an acronym of the unit name. \n" +
                "Note that abbreviation/acronym does not work for all units, and if you're having trouble I suggest using the unitcode. \n" +
                "For example, if I take electronic fundamentals I could enter the following: \n" +
                "1) **!enrol eten2001** \n" +
                "2) **!enrol electronic fundamentals** \n" +
                "3) **!enrol ef** \n" +
                "\nAnd don't forget, you can enrol into all your units at once! (**!enrol ef, dsa, cmpe3006, object oriented program design**) \n");

        event.replyInDm("Made a mistake or perhaps unenrolled/finished a unit? No problem! \n" +
                "Use the unenrol function, and then the units you wish to remove from Discord. \n" +
                "**!unenrol ef** \n" +
                "**!unenrol ef, dsa, cmpe3006, oopd** \n" +
                "If you're feeling adventurous, why not try **!unenrol all** to clear your unit list! \n \n");

        event.replyInDm("\n \n Have a question or issue that was not answered here? Perhaps I don't have your unitcode/name/abbreviation entered in my database? \n" +
                "Ping me on discord using \"**@Admin <message>**\", PM me here, or contact me on Facebook @ https://www.facebook.com/cameron.petkov1 \n" +
                "Hope this helped! Now go study hard or die doing allnighters trying!");
        io.write("");
    }
}
