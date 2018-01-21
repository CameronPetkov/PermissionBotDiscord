package me.name.bot;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Role;

public class EnrolmentHelper {

    public int checkInput(String arg, Unit foundunit, String[] enrols, CommandEvent event) {
        int inputDecision = 0;

        if (foundunit != null) { //if the argument matched any JSON unit
            Role role = event.getGuild().getRolesByName(foundunit.getUnitCode(), true).get(0); //get the role object that matches to the unitcode

            if (anyEquals(foundunit.getUnitCode(), enrols)) {
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
            if (arg.equals(null) || arg.isEmpty()) {
                //if the argument is null, empty, or does not start with a character i.e. "123" or "@@@"
                msg = "Unit code/name needs to start with a letter: ";
            }
            else if (!Character.isLetter(arg.charAt(0))) {
                msg = "Unit code/name needs to start with a letter: " + arg;
            }
            else { //otherwise the unit just does not exist and is in the format "asdfsd" or "sksk1111", etc
                msg = "Unit does not exist, double check the unit code/name: " + arg;
            }
            event.replyInDm(msg);
            IO io = new IO();
            io.write(msg);
        }

        return(inputDecision);
    }

    public boolean anyEquals(String arg, String[] acceptable) {
        boolean equal = false; //assume its not equal
        for(int ii=0; ii<acceptable.length; ii++) {  //iterate through the enrolled array
            if(arg.equals(acceptable[ii])) {
                //if any argument is equal to an enrolled unit, then argument has been enrolled in this session
                equal = true;
            }
        }
        return(equal);
    }
}
