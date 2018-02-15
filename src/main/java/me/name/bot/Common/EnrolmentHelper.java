package me.name.bot.Common;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.name.bot.Models.Unit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class EnrolmentHelper {
    public static int checkUnitInput(Unit foundUnit, String[] enrols, CommandEvent event) {
        int inputDecision;
        if (anyEquals(foundUnit.getUnitCode(), enrols)) {
            //calls below method that checks every array index with the unitcode
            inputDecision = 200; //i.e. the unitcode was found enrolled in this session/event
        } else if (event.getMember().getRoles().stream().anyMatch(x -> x.getName().equals(foundUnit.getUnitCode()))) {
            //if the argument was already found enrolled into previous to this session
            inputDecision = 100;
        } else { //otherwise the unit is not a duplicate
            inputDecision = 300;
        }
        return (inputDecision);
    }

    public static void unenrolAll(CommandEvent event) {
        Unit[] units = JSONLoad.LoadJSON("data/units.json", Unit[].class);    //load JSON
        for (int ii = 0; ii < event.getMember().getRoles().size(); ii++) {  //go through all roles of the member
            String role = event.getMember().getRoles().get(ii).getName();
            if (Arrays.stream(units).filter(x->x.getUnitCode().equalsIgnoreCase(role)).findFirst().orElse(null) != null)  {
                //if the member's role  matches the unitcode of any JSON unitcode, then remove that role to the member
                event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getMember().getRoles().get(ii)).queue();
            }
        }
    }

    public static void giveErrorMessage(String arg, CommandEvent event) {
        String msg;
        if (arg == null || arg.isEmpty()) {
            //if the argument is null, empty
            msg = "Unit code/name needs to start with a letter.";
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
        displayStatus(result, event);
    }

    public static void displayLookupStatus(boolean change, CommandEvent event) {
        String result;
        if(change) { //i.e. a unit has been unenrolled from
            result = "**Success!**";
        }
        else {
            result = "**Failure!** Lookup unsuccessful.";
        }
        displayStatus(result, event);
    }

    public static void displayStatus(String msg, CommandEvent event) {
        event.replyInDm(msg);
        IO.write(msg);
        IO.write("");
    }
}
