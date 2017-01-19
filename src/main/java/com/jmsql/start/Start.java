package com.jmsql.start;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.command.EnterCommand;
import com.jmsql.jline.extension.completer.JmsqlCompleter;
import com.jmsql.utils.db.DbUtils;
import com.jmsql.utils.db.MetadataProcessor;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;

public class Start {

    private static final Logger  LOG    = LoggerFactory.getLogger(Start.class);
    private static ConsoleReader reader = null;

    private static void databaseInit(String[] args) {
        try {
            commandLineArgumentsProcessing(args);
            MetadataProcessor.init();
        } catch (IOException e) {
            LOG.error("Exception encountered", e);
            System.out.println("Failed to start:" + e.getMessage());
        }
    }

    private static void commandLineArgumentsProcessing(String[] args) throws IOException {
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
        if (ip == null || "".equals(ip)) {
            ip = "127.0.0.1";
        }
        DbUtils.setDbIp(ip);
        String password = null;
        if (null != commandLine && commandLine.hasOption("p")) {
            password = commandLine.getOptionValue("p");
        } else {
            Character c = 0;
            password = reader.readLine("Please enter password > ", c);
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

    public static void main(String[] args) {
        System.out.println("Welcome to Jmsql :)");
        try {
            reader = new ConsoleReader();
            databaseInit(args);
            refreshPrompt();
            List<Completer> completors = new LinkedList<Completer>();
            completors.add(new JmsqlCompleter());
            for (Completer c : completors) {
                reader.addCompleter(c);
            }
            String line;
            //TODO learn key binding reader.getKeys().bind("pop", null);
            PrintWriter out = new PrintWriter(reader.getOutput());
            while ((line = reader.readLine()) != null) {
                try {
                    String output = new EnterCommand().getCommandOutput(line);
                    if(output.length()>0){
                        out.println(output);
                    }
                    out.flush();
                } catch (Exception e) {
                    LOG.error("Encountered exception", e);
                    out.println("Encountered Some Error");
                    e.printStackTrace();
                }
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                }
                if (line.equalsIgnoreCase("cls") || line.equalsIgnoreCase("clear")) {
                    reader.clearScreen();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void refreshPrompt() {
        reader.setPrompt("jmsql "+DbUtils.getDbIp()+":"+DbUtils.getDbName()+"> ");
    }

    public static ConsoleReader getReader() {
        return reader;
    }
    
    
}
