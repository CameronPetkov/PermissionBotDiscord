package me.name.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import java.time.LocalDateTime;

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

        IO io = new IO();
        io.write(LocalDateTime.now(), event);

        event.getMessage().delete().queue(); //Delete user message

        for(int ii=0; ii<2; ii++) { //only 2 arguments to cycle through
            args[ii] = args[ii].toLowerCase(); //formatting
        }

        if(args[0].equals("add")) { //!course add
            int choose = majorChoice(args[1]); //pass through argument and match with one of the options
            switch(choose) {
                case 1: //CS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue();   //add CS
                    event.replyInDm("**Course is now CS.** Datetime of changes: " + LocalDateTime.now());
                    break;
                case 2: //EE
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue();  //remove CS
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //add EE
                    event.replyInDm("**Course is now EE.** Datetime of changes: " + LocalDateTime.now());
                    break;
                case 3: //EECS
                    event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //add EE and CS
                    event.replyInDm("**Course is now EECS.** Datetime of changes: " + LocalDateTime.now());
                    break;
                default:
                    event.replyInDm("Input correct parameter.");
                    break;
            }
        }
        else if(args[0].equals("remove")) {
            int choose = majorChoice(args[1]);
            switch(choose) {
                case 1: //CS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0)).queue(); //remove CS
                    event.replyInDm("**Removed CS from course.** Datetime of changes: " + LocalDateTime.now());
                    break;
                case 2: //EE
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE
                    event.replyInDm("**Removed EE from course.** Datetime of changes: " + LocalDateTime.now());
                    break;
                case 3: //EECS
                    event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("comp sci noobs", true).get(0), event.getGuild().getRolesByName("electrical plebs", true).get(0)).queue(); //remove EE and CS
                    event.replyInDm("**Removed EECS from course.** Datetime of changes: " + LocalDateTime.now());
                    break;
                default:
                    event.replyInDm("Input correct parameter.");
                    break;
            }
        }
        else {
            event.replyInDm("Command usage: !course <add>/<minus>.");
        }
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