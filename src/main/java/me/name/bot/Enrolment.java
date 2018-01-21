package me.name.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDateTime;
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

        IO io = new IO();
        io.write(LocalDateTime.now(), event);

        event.getMessage().delete().queue();               //Delete user message
        Unit foundunit;
        Unit[] units = JSONLoad.LoadJSON("data/units.json", Unit[].class);                 //load JSON
        EnrolmentHelper eh = new EnrolmentHelper();        //helper class for enrolment/unenrolment class

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
        else {*/
            event.replyInDm("**Enrolling into units: **");

            Role ee = event.getGuild().getRolesByName("Electrical Plebs", true).get(0);
            Role cs = event.getGuild().getRolesByName("Comp Sci Noobs", true).get(0);

            List<Role> roles = event.getMember().getRoles(); //get a list of the member roles
            if (roles.stream().anyMatch(x-> x.getName().equals(ee.getName()) || x.getName().equals(cs.getName()))) {
                //if the member is enrolled into EE or CS, they can commence enrolment (otherwise don't proceed)
                for(int ii=0; ii<args.length; ii++) {          //for all arguments
                    enrols[ii] = "empty"; //initialise array to later be filled with successful enrolment

                    final String arg = args[ii].trim().toLowerCase(); //formatting
                    foundunit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z-> z.equalsIgnoreCase(arg))
                            || x.getFullName().equalsIgnoreCase(arg) || x.getUnitCode().equalsIgnoreCase(arg)).findFirst().orElse(null);
                    //if the argument matches to any JSON unit (by unitcode, name, or abbreviation)

                    int response = eh.checkInput(arg, foundunit, enrols, event); //pass the argument, the matched JSON unit, the successful enrolment array, and the trigger event
                    switch(response) {
                        case 100: //if the argument is already found enrolled into
                            event.replyInDm("Unit already enrolled into: " + WordUtils.capitalize(foundunit.getFullName()));
                            break;
                        case 200: //if the argument was already stated previously
                            event.replyInDm("Unit already enrolled into: " + WordUtils.capitalize(foundunit.getFullName()));
                            break;
                        case 300: //otherwise enrol
                            enrols[ii] = foundunit.getUnitCode(); //fill array with enrolled unit

                            Role role = event.getGuild().getRolesByName(foundunit.getUnitCode(), true).get(0); //get the role object that matches to the unitcode
                            event.getGuild().getController().addSingleRoleToMember(event.getMember(), role).queue(); //add that role to the user
                            event.replyInDm("Added unit: " + WordUtils.capitalize(foundunit.getFullName()));
                            changes = true;
                            break;
                        default: //this should never occur
                            break;
                    }
                }
            }
            else {
                event.replyInDm("Enrol into your major first (ee, cs or both). Use **!course add <major>**, where major is cs, ee or eecs.");
            }
       // }

        if(changes) { //i.e. a unit has been enrolled into
            event.replyInDm("**Success!** Datetime of changes: " + LocalDateTime.now());
        }
        else {
            event.replyInDm("**Failure!** No changes were made to enrolment.");
        }
    }
}