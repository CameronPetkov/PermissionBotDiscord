package me.name.bot.Common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IO {
    public static void write(String msg) {
        FileOutputStream fileStrm = null;
        PrintWriter pw;

        try {
            Files.createDirectory(Paths.get("output"));
        } catch (IOException e) {  }

        try {
            fileStrm = new FileOutputStream("output/DiscordBotCommandUsage.txt", true);
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
