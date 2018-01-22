package me.name.bot;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Role;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EnrolmentHelper {

    public static int checkInput(String arg, Unit foundUnit, String[] enrols, CommandEvent event) {
        int inputDecision = 0;

        if (foundUnit != null) { //if the argument matched any JSON unit
            Role role = event.getGuild().getRolesByName(foundUnit.getUnitCode(), true).get(0); //get the role object that matches to the unitcode

            if (anyEquals(foundUnit.getUnitCode(), enrols)) {
                //calls below method that checks every array index with the unitcode
                inputDecision = 200; //i.e. the unitcode was found enrolled in this session/event
            }
            else if (event.getMember().getRoles().stream().anyMatch(x -> x.getName().equals(role.getName()))) {
                //if the argument was already found enrolled into previous to this session
                inputDecision = 100;
            }
            else { //otherwise the unit is not a duplicate
                inputDecision = 300;
            }
        }
        else {
            String msg;
            if (arg == null || arg.isEmpty()) {
                //if the argument is null, empty
                msg = "Unit code/name needs to start with a letter: ";
            }
            else if (!Character.isLetter(arg.charAt(0))) { //does not start with a character i.e. "123" or "@@@"
                msg = "Unit code/name needs to start with a letter: " + arg;
            }
            else { //otherwise the unit just does not exist and is in the format "asdfsd" or "sksk1111", etc
                msg = "Unit does not exist, double check the unit code/name: " + arg;
            }
            event.replyInDm(msg);
            IO.write(msg);
        }

        return(inputDecision);
    }

    public static boolean anyEquals(String arg, String[] acceptable) {
        boolean equal = false; //assume its not equal
        for(int ii=0; ii<acceptable.length; ii++) {  //iterate through the enrolled array
            if(arg.equals(acceptable[ii])) {
                //if any argument is equal to an enrolled unit, then argument has been enrolled in this session
                equal = true;
            }
        }
        return(equal);
    }

    public static void logUserMessage(CommandEvent event) {
        LocalDateTime timeStamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String time = timeStamp.format(formatter);

        String author = event.getMember().getEffectiveName();
        String message = event.getMessage().getContentDisplay();
        String userMsg = time + " - " + author + ": " + message;

        IO.write(userMsg);
        event.replyInDm("-----------------------------------------------");
        event.replyInDm("**" + userMsg + "**");

        event.getMessage().delete().queue();    //Delete user message
    }

    public static void displayChangeStatus(boolean change, CommandEvent event) {
        String result;
        if(change) { //i.e. a unit has been unenrolled from
            result = "**Success!**";
        }
        else {
            result = "**Failure!** No changes were made to enrolment.";
        }
        event.replyInDm(result);
        IO.write(result);
        IO.write("");
    }
}
