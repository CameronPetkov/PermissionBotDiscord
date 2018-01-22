package me.name.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.text.WordUtils;
import java.util.Arrays;
import java.util.List;

public class Enrolment extends Command {
    public Enrolment() {
        this.name = "enrols";
        this.arguments = "<item>";
        this.aliases = new String[]{"enrolment", "enrol", "enroll"};
        this.help = "This command adds permissions and enrolment";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(",");   //Every argument
        String[] enrols = new String[args.length];         //Accepted units that actually exist

        EnrolmentHelper.logUserMessage(event);

        Unit foundUnit;
        Unit[] units = JSONLoad.LoadJSON("data/units.json", Unit[].class);                 //load JSON

        boolean changes = false;
        /* Uncomment for testing enrolment into ALL units (using format !enrol all)

        if (args[0].toLowerCase().equals("all")) { //if argument is "all" i.e. !enrol all
            event.replyInDm("**Enrolling into all available units.**");
            for (int ii = 0; ii < event.getGuild().getRoles().size(); ii++) {               //go through all roles in the server
                String role = event.getGuild().getRoles().get(0).getName();
                if (Arrays.stream(units).filter(x -> x.getUnitCode().equalsIgnoreCase(role)).findFirst().orElse(null) != null) {
                //if the guild's role matches the unitcode of any JSON unitcode, then add that role to the member
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoles().get(ii)).queue();
                    changes = true;  //Used to let user know if a unit was enrolled into
                }
            }
        }
        else { */// Uncomment for testing enrolment into ALL units

        Role ee = event.getGuild().getRolesByName("Electrical Plebs", true).get(0);
        Role cs = event.getGuild().getRolesByName("Comp Sci Noobs", true).get(0);

        List<Role> roles = event.getMember().getRoles(); //get a list of the member roles
        String msg;
        if (roles.stream().anyMatch(x -> x.getName().equals(ee.getName()) || x.getName().equals(cs.getName()))) {
            //if the member is enrolled into EE or CS, they can commence enrolment (otherwise don't proceed)
            for (int ii = 0; ii < args.length; ii++) {          //for all arguments
                enrols[ii] = "empty"; //initialise array to later be filled with successful enrolment

                final String arg = args[ii].trim().toLowerCase(); //formatting
                foundUnit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z -> z.equalsIgnoreCase(arg))
                        || x.getFullName().equalsIgnoreCase(arg) || x.getUnitCode().equalsIgnoreCase(arg)).findFirst().orElse(null);
                //if the argument matches to any JSON unit (by unitcode, name, or abbreviation)

                int response = EnrolmentHelper.checkInput(arg, foundUnit, enrols, event); //pass the argument, the matched JSON unit, the successful enrolment array, and the trigger event
                switch (response) {
                    case 100: //if the argument is already found enrolled into
                        msg = "Unit already enrolled into: " + WordUtils.capitalize(foundUnit.getFullName());
                        event.replyInDm(msg);
                        IO.write(msg);
                        break;
                    case 200: //if the argument was already stated previously
                        msg = "Unit already enrolled into: " + WordUtils.capitalize(foundUnit.getFullName());
                        event.replyInDm(msg);
                        IO.write(msg);
                        break;
                    case 300: //otherwise enrol
                        enrols[ii] = foundUnit.getUnitCode(); //fill array with enrolled unit

                        Role role = event.getGuild().getRolesByName(foundUnit.getUnitCode(), true).get(0); //get the role object that matches to the unitcode
                        event.getGuild().getController().addSingleRoleToMember(event.getMember(), role).queue(); //add that role to the user
                        msg = "Added unit: " + WordUtils.capitalize(foundUnit.getFullName());
                        event.replyInDm(msg);
                        IO.write(msg);
                        changes = true;
                        break;
                    default: //this should never occur
                        break;
                }
            }
        }
        else {
            msg = "Enrol into your major first (ee, cs or both). Use **!course add <major>**, where major is cs, ee or eecs.";
            event.replyInDm(msg);
            IO.write(msg);
        }

        EnrolmentHelper.displayChangeStatus(changes, event);
    }
}