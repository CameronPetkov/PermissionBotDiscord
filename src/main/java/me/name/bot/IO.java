package me.name.bot;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.io.*;

public class IO {
    public void write(String msg) {
        FileOutputStream fileStrm = null;
        PrintWriter pw;

        try {
            fileStrm = new FileOutputStream("DiscordBotCommandUsage.txt", true);
            pw = new PrintWriter(fileStrm);

            pw.println(msg);
            pw.close();
        }
        catch (IOException e) {
            if (fileStrm != null) {
                try {
                    fileStrm.close();
                }
                catch (IOException e2) {}
            }
        }
    }
}
