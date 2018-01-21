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

        IO io = new IO();
        io.write(time, event);

        event.getMessage().delete().queue(); //Delete user message

        for(int ii=0; ii<args.length; ii++) {
            args[ii] = args[ii].toLowerCase(); //formatting
        }

        String msg;
        if(args[0].equals("add")) { //!course add
            int choose = 0;
            try {
                choose = majorChoice(args[1]); //pass through argument and match with one of the options
            }
            catch (IndexOutOfBoundsException e) {
                msg = "Input correct parameter (ee/cs/eecs). Timestamp: ";
            }
            switch(choose) {
                case 1: //CS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue();   //add CS
                    msg = "**Course is now CS.** Timestamp: ";
                    break;
                case 2: //EE
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue();  //remove CS
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //add EE
                    msg = "**Course is now EE.** Timestamp: ";
                    break;
                case 3: //EECS
                    event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //add EE and CS
                    msg = "**Course is now EECS.** Timestamp: ";
                    break;
                default:
                    msg = "Input correct parameter (ee/cs/eecs). Timestamp: ";
                    break;
            }
        }
        else if(args[0].equals("remove")) {
            int choose = 0;
            try {
                choose = majorChoice(args[1]); //pass through argument and match with one of the options
            }
            catch (IndexOutOfBoundsException e) {
                msg = "Input correct parameter (ee/cs/eecs). Timestamp: ";
            }
            switch(choose) {
                case 1: //CS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue(); //remove CS
                    msg = "**Removed CS from course.** Timestamp: ";
                    break;
                case 2: //EE
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE
                   msg = "**Removed EE from course.** Timestamp: ";
                    break;
                case 3: //EECS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE and CS
                    msg = "**Removed EECS from course.** Timestamp: ";
                    break;
                default:
                    msg = "Input correct parameter (ee/cs/eecs). Timestamp: ";
                    break;
            }
        }
        else {
            msg = "Command usage: !course <add>/<minus>. Timestamp: ";
        }

        event.replyInDm(msg + time);
        io.write(msg + time);
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