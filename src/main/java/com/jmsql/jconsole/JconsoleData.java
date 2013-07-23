package com.jmsql.jconsole;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JconsoleData {

    private static final Logger LOG = LoggerFactory.getLogger(JconsoleData.class);

    private List<Command> commandList=new ArrayList<Command>();

    private Command currentCommand;
    private int currentIndex=0;
    
    public List<Command> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<Command> commandList) {
        this.commandList = commandList;
    }

    public void addCommand(Command command) {
        this.commandList.add(command);
    }
    
    public Command getCurrentCommand() {
        return currentCommand;
    }

    public void setCurrentCommand(Command currentCommand) {
        this.currentCommand = currentCommand;
    }

    public void saveCommand() {
        LOG.info("save command:"+currentCommand.toString()+" current Index:"+currentIndex+" current List Size:"+commandList.size());
        if("".equalsIgnoreCase(currentCommand.getCommand().toString().trim()) || (!commandList.isEmpty() && commandList.get(commandList.size()-1).toString().equalsIgnoreCase(currentCommand.getCommand()))){
            LOG.info("already present or empty");
        }else{
            commandList.add(currentCommand);
            currentCommand=new Command();
        }
        currentIndex=commandList.size();
        LOG.info("save command exit:"+currentCommand.toString()+" current Index:"+currentIndex+" current List Size:"+commandList.size());
    }
    
    public void prevCommand() {
        LOG.info("prev command:"+currentCommand.toString()+" current Index:"+currentIndex+" current List Size:"+commandList.size());
        if(currentIndex>0){
            currentIndex--;
            currentCommand=new Command(commandList.get(currentIndex).getCommand());
        }
        LOG.info("prev command exit:"+currentCommand.toString()+" current Index:"+currentIndex+" current List Size:"+commandList.size());
    }
    public void nextCommand() {
        LOG.info("next command:"+currentCommand.toString()+" current Index:"+currentIndex+" current List Size:"+commandList.size());
        if(currentIndex<commandList.size()-1){
            currentIndex++;
            currentCommand=new Command(commandList.get(currentIndex).getCommand());
        }else if(currentIndex==commandList.size()-1){
            currentCommand=new Command();
            currentIndex=commandList.size();
        }
        LOG.info("next command exit:"+currentCommand.toString()+" current Index:"+currentIndex+" current List Size:"+commandList.size());
    }
    
}

