package com.jmsql.jconsole;

public class Command {

    private static int counter;
    static{
        counter=0;
    }
    private Integer       id;
    private StringBuilder command;

    public Command() {
        this.id=counter++;
        this.command = new StringBuilder("");
    }

    public Command(String command) {
        this.id=counter++;
        this.command = new StringBuilder(command);
    }

    public String getCommand() {
        return command.toString();
    }

    public void setCommand(String command) {
        this.command = new StringBuilder(command);
    }

    public StringBuilder getBuilderCommand() {
        return command;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return command.toString();
    }

    
}
