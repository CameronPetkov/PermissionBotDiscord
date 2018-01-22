package me.name.bot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.text.WordUtils;
import java.util.Arrays;

public class Unenrolment extends Command {
    public Unenrolment() {
        this.name = "unenrols";
        this.arguments = "<item>";
        this.aliases = new String[]{"unenrolment", "unenrol", "unenroll", "unenrolls"};
        this.help = "This command removes permissions and enrolment";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(",");     //Every argument
        String[] unenrols = new String[args.length];         //Accepted units that actually exist

        EnrolmentHelper.logUserMessage(event);

        Unit foundUnit;
        Unit[] units = JSONLoad.LoadJSON("data/units.json", Unit[].class);               //load JSON

        boolean changes = false;
        if (args[0].toLowerCase().equals("all")) { //if argument is "all" i.e. !unenrol all
            for (int ii = 0; ii < event.getMember().getRoles().size(); ii++) {  //go through all roles of the member
                String role = event.getMember().getRoles().get(ii).getName();
                if (Arrays.stream(units).filter(x->x.getUnitCode().equalsIgnoreCase(role)).findFirst().orElse(null) != null)  {
                    //if the member's role  matches the unitcode of any JSON unitcode, then remove that role to the member
                    event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getMember().getRoles().get(ii)).queue();
                    changes = true; //Used to let user know if a unit was unenrolled from
                }
            }
        }
        else {
                for (int ii = 0; ii < args.length; ii++) {      //for all arguments
                    unenrols[ii] = "empty"; //initialise array to later be filled with successful enrolment

                    final String arg = args[ii].trim().toLowerCase(); //formatting
                    foundUnit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z -> z.equalsIgnoreCase(arg))
                            || x.getFullName().equalsIgnoreCase(arg) || x.getUnitCode().equalsIgnoreCase(arg)).findFirst().orElse(null);
                    //if the argument matches to any JSON unit (by unitcode, name, or abbreviation)

                int response = EnrolmentHelper.checkInput(arg, foundUnit, unenrols, event); //pass the argument, the matched JSON unit, the successful enrolment array, and the trigger event
                String msg = null;
                switch (response) {
                    case 100: //if the argument is enrolled into and is to be unenrolled from
                        unenrols[ii] = foundUnit.getUnitCode(); //fill array with unenrolled unit

                        Role role = event.getGuild().getRolesByName(foundUnit.getUnitCode(), true).get(0); //get the role object that matches to the unitcode
                        event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), role).queue(); //remove that role from the user
                        msg = "Removed unit: " + WordUtils.capitalize(foundUnit.getFullName());
                        event.replyInDm(msg);
                        IO.write(msg);
                        changes = true;
                        break;
                    case 200: //if the argument was already stated previously
                        msg = "Unit already unenrolled from: " + WordUtils.capitalize(foundUnit.getFullName());
                        event.replyInDm(msg);
                        IO.write(msg);
                        break;
                    case 300: //if the argument was not enrolled into anyway
                        msg = "Unit already unenrolled from: " + WordUtils.capitalize(foundUnit.getFullName());
                        event.replyInDm(msg);
                        IO.write(msg);
                        break;
                    default:   //this should never occur
                        break;
                }
            }
        }

        EnrolmentHelper.displayChangeStatus(changes, event);
    }
}