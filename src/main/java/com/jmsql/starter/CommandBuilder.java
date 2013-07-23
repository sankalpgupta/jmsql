package com.jmsql.starter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.jconsole.Jconsole;
import com.jmsql.utils.db.DbUtils;
import com.jmsql.utils.db.MetadataProcessor;

public class CommandBuilder {

    private static String       ttyConfig;

    private static final Logger LOG = LoggerFactory.getLogger(CommandBuilder.class);

    private static Thread       mainThread;

    public static void main(String[] args) {
        consoleInit();
        databaseInit(args);
        commmandThread();
    }

    private static void databaseInit(String[] args) {
        commandLineArgumentsProcessing(args);
        MetadataProcessor.init();
    }

    private static void commandLineArgumentsProcessing(String[] args) {
        CLIOptionsUtils.createOptions();
        CommandLine commandLine = null;
        try {
            commandLine = CLIOptionsUtils.parseCLIArgs(args);
        } catch (ParseException e1) {
            commandLine = null;
            System.out.println("Error while parsing command Line arguments...Use -help argument for help");
        }
        if (null != commandLine && commandLine.hasOption("help")) {
            CLIOptionsUtils.outputCommandLineHelp(CLIOptionsUtils.options);
            System.exit(1);
        }
        Scanner scanner = new Scanner(System.in);
        String username = null;
        if (null != commandLine && commandLine.hasOption("u")) {
            username = commandLine.getOptionValue("u");
        } else {
            System.out.println("Please input the username");
            username = scanner.next();
        }
        if (username != null && !"".equals(username)) {
            DbUtils.setUsername(username);
        } else {
            System.out.println("Username can not be empty");
            System.exit(2);
        }
        String ip = null;
        if (null != commandLine && commandLine.hasOption("h")) {
            ip = commandLine.getOptionValue("h");
        }
        if (ip==null || "".equals(ip)) {
            ip = "127.0.0.1";
        }
        DbUtils.setDbIp(ip);
        String password = null;
        if (null != commandLine && commandLine.hasOption("p")) {
            password = commandLine.getOptionValue("p");
        } else {
            System.out.println("Please enter your password");
            password = getPasswordByTerminal();
        }
        if (password != null && !"".equals(password)) {
            DbUtils.setPassword(password);
        } else {
            System.out.println("Password can not be empty");
            System.exit(4);
        }
        String dbName = null;
        if (null != commandLine && commandLine.hasOption("d")) {
            dbName = commandLine.getOptionValue("d");
        } else {
            System.out.println("Please input the db name");
            dbName = scanner.next();
        }
        if (dbName != null && !"".equals(dbName)) {
            DbUtils.setDbName(dbName);
        } else {
            System.out.println("db name can not be empty");
            System.exit(5);
        }
        String port = null;
        if (null != commandLine && commandLine.hasOption("P")) {
            port = commandLine.getOptionValue("P");
        }
        if (port == null || "".equals(port)) {
            port = "3306";
        }
        DbUtils.setDbPort(port);
        DbUtils.init();
    }

    private static void commmandThread() {
        try {
            setTerminalToCBreak();
            Jconsole.getInstance().startCommandPrompt();
            while (true) {
                if (System.in.available() != 0) {
                    int c = System.in.read();
                    LOG.info("You pressed:" + c + "(" + (char) c + ")");
                    ProcessWideContext.getInstance().getModeService().keyPressed(c);
                    LOG.info("Pressed:" + c + "(" + (char) c + ") Command:" + Jconsole.getCurrentCommandString() + " length:" + Jconsole.getCurrentCommandString().length()
                            + " cursor Position:" + Jconsole.getCurrentCursorPosition());
                }
            }
        } catch (IOException e) {
            System.err.println("IOException");
        } catch (InterruptedException e) {
            System.err.println("InterruptedException");
        } finally {

        }
    }
    
    private static String getPasswordByTerminal() {
        StringBuilder password = new StringBuilder("");
        try {
            setTerminalToCBreak();
            while (true) {
                if (System.in.available() != 0) {
                    int c = System.in.read();
                    if (c == 10) {
                        break;
                    } else if (c == 127) {
                        if (password.length() > 0) {
                            password.deleteCharAt(password.length() - 1);
                        }
                    } else {
                        password.append((char)c);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception encountered...exiting.");
            System.exit(2);
        }
        try {
            if (ttyConfig != null) {
                stty(ttyConfig.trim());
            }
        } catch (Exception e) {
            System.err.println("Exception restoring config");
            System.exit(3);
        }
        return password.toString();
    }

    

    

    private static void setTerminalToCBreak() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");
    }

    /**
     * Execute the stty command with the specified arguments against the current active terminal.
     */
    private static String stty(final String args) throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] { "sh", "-c", cmd });
    }

    /**
     * Execute the specified command and return the output (both stdout and stderr).
     */
    private static String exec(final String[] cmd) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }
    private static void consoleInit() {
        mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("\nshutting down...");
                try {
                    if (ttyConfig != null) {
                        stty(ttyConfig.trim());
                    }
                } catch (Exception e) {
                    System.err.println("Exception restoring tty config");
                    e.printStackTrace();
                }
            }
        });
    }

}
