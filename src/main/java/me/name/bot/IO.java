package me.name.bot;

import com.jagrosh.jdautilities.commandclient.CommandEvent;

import java.time.LocalDateTime;
import java.io.*;
import java.time.format.DateTimeFormatter;

public class IO {
    public void write(LocalDateTime timeStamp, CommandEvent command) {
        FileOutputStream fileStrm = null;
        PrintWriter pw;

        try {
            fileStrm = new FileOutputStream("DiscordBotCommandUsage.txt");
            pw = new PrintWriter(fileStrm);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String time = timeStamp.format(formatter);

            String author = command.getMember().getEffectiveName();
            String message = command.getMessage().getContentDisplay();

            pw.println(time + " - " + author + ": " + message);
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
