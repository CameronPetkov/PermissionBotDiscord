package me.name.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CourseSelect extends Command {
    public CourseSelect() {
        this.name = "course";
        this.arguments = "<item>";
        this.aliases = new String[]{"courses", ""};
        this.help = "This command provides course selection permissions";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+"); //split by space

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

        for(int ii=0; ii<args.length; ii++) {
            args[ii] = args[ii].toLowerCase(); //formatting
        }

        String msg;
        boolean changes = false;
        if(args[0].equals("add") || args[0].equals("enrol") || args[0].equals("enroll") || args[0].equals("enrols") || args[0].equals("enrolls")) {
            int choose = 0;
            try {
                choose = majorChoice(args[1]); //pass through argument and match with one of the options
            }
            catch (IndexOutOfBoundsException e) {
                msg = "Input correct parameter (ee/cs/eecs).";
            }
            switch(choose) {
                case 1: //CS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue();   //add CS
                    msg = "Course is now CS.";
                    changes = true;
                    break;
                case 2: //EE
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue();  //remove CS
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //add EE
                    msg = "Course is now EE.";
                    changes = true;
                    break;
                case 3: //EECS
                    event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //add EE and CS
                    msg = "Course is now EECS.";
                    changes = true;
                    break;
                default:
                    msg = "Input correct parameter (ee/cs/eecs).";
                    break;
            }
        }
        else if(args[0].equals("remove") || args[0].equals("unenrol") || args[0].equals("unenroll") || args[0].equals("unenrols") || args[0].equals("unenrolls")) {
            int choose = 0;
            try {
                choose = majorChoice(args[1]); //pass through argument and match with one of the options
            }
            catch (IndexOutOfBoundsException e) {
                msg = "Input correct parameter (ee/cs/eecs).";
            }
            switch(choose) {
                case 1: //CS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue(); //remove CS
                    msg = "Removed CS from course.";
                    changes = true;
                    break;
                case 2: //EE
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE
                    msg = "Removed EE from course.";
                    changes = true;
                    break;
                case 3: //EECS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE and CS
                    msg = "Removed EECS from course.";
                    changes = true;
                    break;
                default:
                    msg = "Input correct parameter (ee/cs/eecs).";
                    break;
            }
        }
        else {
            msg = "Command usage: !course <add>/<remove>.";
        }

        event.replyInDm(msg);
        io.write(msg);

        String result;
        if (changes) { //i.e. a unit has been unenrolled from
            result = "**Success!**";
        }
        else {
            result = "**Failure!** No changes were made to enrolment.";
        }
        event.replyInDm(result);
        io.write(result);
        io.write("");
    }

    private int majorChoice(String choice) {
        int major;
        switch(choice) {
            case "1": case "cs": case "computer science": case "comp sci": //CS
                major = 1;
                break;
            case "2": case "ee": case "electrical engineering": case "electrical": //EE
                major = 2;
                break;
            case "3": case "csee": case "eecs": case "cs+ee": case "ee+cs": //EECS or double degree
                major = 3;
                break;
            default:
                major = 0;
                break;
        }
        return(major);
    }
}