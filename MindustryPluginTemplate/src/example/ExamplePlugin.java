package example;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.Core;
import mindustry.game.EventType.*;
import mindustry.gen.Groups;
import mindustry.mod.Plugin;

import java.io.FileWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExamplePlugin extends Plugin {

    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private final Object lock = new Object();

    @Override
    public void init() {
        Events.on(PlayerJoin.class, e -> {
            Core.app.post(() -> log("join"));
        });

        Events.on(PlayerLeave.class, e -> {
            Core.app.post(() -> log("leave"));
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("logpoint", "Добавить точку логирования вручную", args -> log("manual"));
    }

    private void log(String type) {
        synchronized (lock) {
            FileWriter w = null;
            try {
                // Простой путь - файл будет в папке с сервером
                File logFile = new File("log.csv");
                w = new FileWriter(logFile, true);

                String timestamp = format.format(new Date());
                int playerCount = Groups.player.size();
                w.write(timestamp + "," + type + "," + playerCount + "\n");
                w.flush(); // Гарантируем запись
            } catch (Exception e) {
                Log.err("ExamplePlugin", "Ошибка записи лога: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (w != null) {
                    try {
                        w.close();
                    } catch (Exception e) {
                        // Игнорируем ошибку закрытия
                    }
                }
            }
        }
    }
}