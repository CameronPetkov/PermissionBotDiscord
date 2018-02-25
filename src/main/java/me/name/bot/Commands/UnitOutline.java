package me.name.bot.Commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import me.name.bot.Common.EnrolmentHelper;
import me.name.bot.Common.IO;
import me.name.bot.Common.JSONLoad;
import me.name.bot.Models.Configuration;
import me.name.bot.Models.Unit;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.text.WordUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.Year;
import java.util.Arrays;

public class UnitOutline extends Command {
    private final String SAVE_DIRECTORY = "output/";
    private final long RETAIN_TIME = 2592000000L; //30 days in ms

    public UnitOutline() {
        this.name = "unitoutline";
        this.arguments = "<item>";
        this.aliases = new String[]{"uo"};
        this.help = "This command fetches the unit outline from the given argument OR Discord chat";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+"); //split by space
        EnrolmentHelper.logUserMessage(event);

        boolean change;

        Role ee = event.getGuild().getRolesByName("Electrical Plebs", true).get(0);
        Role cs = event.getGuild().getRolesByName("Comp Sci Noobs", true).get(0);
        if (event.getMember().getRoles().stream().anyMatch(x -> x.getName().equals(ee.getName()) || x.getName().equals(cs.getName()))) {
            change = parseInput(event, args);
            EnrolmentHelper.displayLookupStatus(change, event);
        }
        else {
            String msg = "Enrol into your major first (ee, cs or both). Use **!course add <major>**, where major is cs, ee or eecs.";
            event.replyInDm(msg);
            IO.write(msg);
            EnrolmentHelper.displayStatus("**Failure!**", event);
        }
    }

    private boolean parseInput(CommandEvent event, String[] args) {
        Unit foundUnit;
        Unit[] units = JSONLoad.LoadJSON("data/units.json", Unit[].class);   //load JSON

        boolean change = false;
        String arg = args[0].trim().toLowerCase();
        if(arg == null || arg.isEmpty()) {
            String name = event.getChannel().getName();
            foundUnit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z -> z.equalsIgnoreCase(name))).findFirst().orElse(null);
            if(foundUnit != null) {
                String unitcode = foundUnit.getUnitCode();
                change = checkUnit(unitcode, "Bentley Campus", "default", "default", event, foundUnit);
            }
            else {
                event.replyInDm("Unit code/name needs to start with a letter.");
            }
        }
        else if(arg.equals("update")) {
            try {
                String arg2 = args[1].trim().toLowerCase();
                foundUnit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z -> z.equalsIgnoreCase(arg2))
                        || x.getFullName().equalsIgnoreCase(arg2) || x.getUnitCode().equalsIgnoreCase(arg2)).findFirst().orElse(null);
                if (foundUnit != null) {
                    String unitcode = foundUnit.getUnitCode();
                    change = updateFileArchive(unitcode, "Bentley Campus", "default", "default", event, foundUnit);
                } else {
                    EnrolmentHelper.giveErrorMessage(arg, event);
                }
            } catch (ArrayIndexOutOfBoundsException e) { //no argument after update i.e. !uo update
                String name = event.getChannel().getName();
                foundUnit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z -> z.equalsIgnoreCase(name))).findFirst().orElse(null);
                if (foundUnit != null) {
                    String unitcode = foundUnit.getUnitCode();
                    change = updateFileArchive(unitcode, "Bentley Campus", "default", "default", event, foundUnit);
                } else {
                    event.replyInDm("Unit code/name needs to start with a letter.");
                }
            }
        }
        else {
            foundUnit = Arrays.stream(units).filter(x -> Arrays.stream(x.getAbbreviation()).anyMatch(z -> z.equalsIgnoreCase(arg))
                    || x.getFullName().equalsIgnoreCase(arg) || x.getUnitCode().equalsIgnoreCase(arg)).findFirst().orElse(null);
            if (foundUnit != null) {
                String unitcode = foundUnit.getUnitCode();
               // if(args[1] != null) {
                //    String campus = args[1].trim().toUpperCase();
                //    change = checkUnit(unitcode, campus, "default", "default", event, foundUnit);
               // }
               // else {
                    change = checkUnit(unitcode, "Bentley Campus", "default", "default",  event, foundUnit);
               // }
            }
            else {
                EnrolmentHelper.giveErrorMessage(arg, event);
            }
        }
        return change;
    }

    private boolean checkUnit(String unitcode, String campus, String year, String semester, CommandEvent event, Unit foundUnit) {
        if(!isFileArchived(unitcode, campus, year, semester)) { //IF FILE NOT ARCHIVED
            FirefoxDriver driver = loginCurtin();
            downloadUnitOutline(unitcode, campus, year, semester, driver);
        }

        if(year.equals("default") && semester.equals("default")) {
            String dir = SAVE_DIRECTORY + unitcode.toUpperCase() + "/" + campus.replaceAll("\\s+","") + "/" + getLatestOutline(unitcode, campus) + ".pdf";
            Message message = new MessageBuilder().append("Unit outline for **" + WordUtils.capitalize(foundUnit.getFullName()) + "**, *" + campus + " " + getLatestOutline(unitcode, campus) + "*: ").build();
            event.getChannel().sendFile(new File(dir), message).queue();
        }

        return true;
    }

    private boolean isFileArchived(String unitcode, String campus, String year, String semester) {
        boolean fileArchive = false;
        String dir = SAVE_DIRECTORY + unitcode.toUpperCase() + "/" + campus.replaceAll("\\s+","") + "/";
        File fileDir = new File(dir);
        File[] fileNames = fileDir.listFiles();
        try {
            if(year.equals("default") && semester.equals("default") && fileNames.length > 0) {
                File file = new File(dir + getLatestOutline(unitcode, campus) + ".pdf");
                long fileDate = file.lastModified();
                long currDate = Instant.now().toEpochMilli();
                if(currDate - fileDate < RETAIN_TIME) {
                    fileArchive = true;
                }
            }
        }
        catch (NullPointerException e) {
            fileArchive = false;
        }

       // long fileDate = file.lastModified();
      //  long currDate = Instant.now().toEpochMilli();
      // if(currDate - fileDate < RETAIN_TIME) {
         //   fileArchive = true;
     //   }
        return fileArchive;
    }

    private boolean updateFileArchive(String unitcode, String campus, String year, String semester, CommandEvent event, Unit foundUnit) {
        FirefoxDriver driver = loginCurtin();
        downloadUnitOutline(unitcode, campus, year, semester, driver);
        return true;
    }

    private String getLatestOutline(String unitcode, String campus) {
        String fileDate;
        String dir = SAVE_DIRECTORY + unitcode.toUpperCase() + "/" + campus.replaceAll("\\s+","") + "/";
        File fileDir = new File(dir);
        File[] fileNames = fileDir.listFiles();
        int maxFileYear = 0, maxFileSemester = 0;
        for(int ii=0; ii<fileNames.length; ii++) {
            int fileYear = Integer.parseInt(fileNames[ii].toString().substring(fileNames[ii].toString().indexOf("-") - 4, fileNames[ii].toString().indexOf("-")));
            int fileSemester = Integer.parseInt(fileNames[ii].toString().substring(fileNames[ii].toString().indexOf(".pdf") - 1, fileNames[ii].toString().indexOf(".pdf")));
            if (fileYear > maxFileYear) {
                maxFileYear = fileYear;
                maxFileSemester = fileSemester;
            }
            else if(fileYear == maxFileYear) {
                if (fileSemester > maxFileSemester) {
                    maxFileSemester = fileSemester;
                }
            }
        }
        fileDate = maxFileYear + "-S" + maxFileSemester;
        return fileDate;
    }

    private FirefoxDriver loginCurtin() {
        System.setProperty("webdriver.gecko.driver", "E:\\Libraries\\Downloads\\geckodriver.exe");
        Configuration config = JSONLoad.LoadJSON("data/config.json", Configuration.class);

        FirefoxDriver driver = new FirefoxDriver();
        driver.get("https://ctl.curtin.edu.au/teaching_learning_services/unit_outline_builder/search_published_UO.cfm");
        WebElement id = driver.findElement(By.xpath("//input[@name='username']"));
        id.sendKeys(config.getStudentID());
        WebElement pw = driver.findElement(By.xpath("//input[@name='password']"));
        pw.sendKeys(config.getStudentPassword());
        WebElement button = driver.findElement(By.xpath("//input[@name='loginSubmit']"));
        button.click();

        WebDriverWait wdw = new WebDriverWait(driver, 30, 500);
        wdw.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='unitCode']")));
        return driver;
    }

    private void downloadUnitOutline(String unitcode, String campus, String year, String semester, FirefoxDriver driver) {
        WebElement unit = driver.findElement(By.xpath("//input[@name='unitCode']"));
        unit.sendKeys(unitcode);
        WebElement button2 = driver.findElement(By.xpath("//input[@name='next']"));
        button2.click();

        int currYear = Year.now().getValue();
        WebDriverWait wdw = new WebDriverWait(driver, 30, 500);
        wdw.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("tbody")));

        int rowCount = driver.findElements(By.xpath("//table[@class='fullwidth']/tbody/tr")).size();
        //int colCount = driver.findElements(By.xpath("//table[@class='fullwidth']/thead/tr/th")).size();

        int maxYear = 0, maxSemester = 1;
        boolean exactMatch = false;
        WebElement linkElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[2]/td[5]"));

        for (int ii = 1; ii <= rowCount; ii++) {
            if (driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[3]")).getText().equals(campus)) {
                WebElement yearElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[5]"));
                int yearText = Integer.parseInt(yearElement.getText());
                if (exactMatch) {
                    WebElement semesterElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[4]"));
                    semester = semesterElement.getText().substring(semesterElement.getText().indexOf("Semester") + 9);
                    if (yearText - currYear == 0 && Integer.parseInt(semester) > maxSemester) {
                        maxSemester = Integer.parseInt(semester);
                        linkElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[2]/a"));
                    }
                } else if (yearText - currYear == 0) {
                    exactMatch = true;

                    WebElement semesterElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[4]"));
                    semester = semesterElement.getText().substring(semesterElement.getText().indexOf("Semester") + 9);
                    maxSemester = Integer.parseInt(semester);
                    maxYear = yearText;
                    year = Integer.toString(maxYear);
                    linkElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[2]/a"));
                   // WebElement semesterElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[4]"));
                    // semester = semesterElement.getText().substring(semesterElement.getText().indexOf("Semester") + 9);
                }
                else {
                    if (yearText >= maxYear) {
                        maxYear = yearText;
                        year = Integer.toString(maxYear);
                        linkElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[2]/a"));
                        WebElement semesterElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[4]"));
                        semester = semesterElement.getText().substring(semesterElement.getText().indexOf("Semester") + 9);
                        if (Integer.parseInt(semester) > maxSemester) {
                            maxSemester = Integer.parseInt(semester);
                        }
                    }
                }
            }
        }
        if(!(year.equals("default") && semester.equals("default"))) {
            for (int ii = 1; ii <= rowCount; ii++) {
                if (driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[3]")).getText().equals(campus)) {
                    WebElement yearElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[5]"));
                    String yearText = yearElement.getText();
                    WebElement semesterElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[4]"));
                    String semesterText = semesterElement.getText();
                    if (yearText.equals(year) && semesterText.equals(semester)) {
                        linkElement = driver.findElement(By.xpath("//table[@class='fullwidth']/tbody/tr[" + ii + "]/td[2]/a"));
                    }
                }
            }
        }

        String link = linkElement.getAttribute("href");
        driver.close();

        String dir = SAVE_DIRECTORY + unitcode.toUpperCase() + "/" + campus.replaceAll("\\s+","");
        new File(dir).mkdirs();

        try {
            URL url = new URL(link);
            InputStream in = url.openStream();
            Files.copy(in, Paths.get(dir + "/" + year + "-S" + semester + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
            in.close();
        }
        catch (MalformedURLException e) { System.out.println("MALFORMEDURL" + e.getMessage()); }
        catch (IOException e) { System.out.println("IOEXCEPTION" + e.getMessage()); }
    }
}
