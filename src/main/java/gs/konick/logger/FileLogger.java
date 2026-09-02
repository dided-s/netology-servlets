package gs.konick.logger;

import gs.konick.utils.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FileLogger implements Logger {

    private final String fileName;
    private final String loggerName;
    private final AtomicInteger counter;

    // Очередь сообщений в файл
    private final BlockingQueue<String> messagesQueue;
    private final Thread writerThread;

    private final Thread shutdownHook;

    private static final String POISON_PILL = "__END__";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public FileLogger(String fileName, String loggerName) {
        this.fileName = fileName;
        this.loggerName = loggerName;
        this.messagesQueue = new ArrayBlockingQueue<>(100);
        this.writerThread = new Thread(this::writeToFile, loggerName);
        this.writerThread.setDaemon(true);
        this.counter = new AtomicInteger(0);

        this.shutdownHook = new Thread(this::close, loggerName + "-shutdown-hook");
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        FileUtils.createFileIfNotExists(fileName);

        this.writerThread.start();
    }

    public FileLogger(String loggerName) {
        this(System.getProperty("user.dir") + "/target/file.log", loggerName);
    }

    // Только кладем сообщение в очередь
    public void log(Level level, String message) {
        String logMessage = String.format("%s [%s]: %s.[%s|%d]",
                LocalDateTime.now().format(FMT),
                level,
                message,
                loggerName,
                counter.incrementAndGet()
        );

        try {
            messagesQueue.put(logMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Отдельный поток будет записывать сообщения из очереди
    private void writeToFile() {
        try (FileOutputStream fos = new FileOutputStream(fileName, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(osw)) {

            bufferedWriter.write("Старт логгера '" + loggerName + "' [" + LocalDateTime.now().format(FMT) + "]");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            while (true) {
                String message = messagesQueue.take();
                if (POISON_PILL.equals(message)) {
                    bufferedWriter.write("Завершение логгера '" + loggerName + "' [" + LocalDateTime.now().format(FMT) + "]");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                }

                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            messagesQueue.put(POISON_PILL);
            writerThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getLoggerName() {
        return loggerName;
    }
}