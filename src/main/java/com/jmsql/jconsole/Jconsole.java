package com.jmsql.jconsole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jconsole {

    private static Jconsole     jconsole;

    private int cursorPosition=0;
    
    private static final Logger LOG = LoggerFactory.getLogger(Jconsole.class);

    private JconsoleData        jcd;

    private Jconsole() {
        jcd = new JconsoleData();
        Command command = new Command();
        jcd.setCurrentCommand(command);
    }

    public static Jconsole getInstance() {
        if (jconsole == null) {
            jconsole = new Jconsole();
        }
        return jconsole;
    }

    public void startCommandPrompt() {
        LOG.info("String Jconsole");
        System.out.println("Welcome to mysql Jconsole :)");
        System.out.print(">");
    }

    public static void eraseLastCharacter() {
        System.out.print("\b \b");

    }

    public static Command getCurrentCommand() {
        return getInstance().jcd.getCurrentCommand();
    }
    
    public static String getCurrentCommandString() {
        return getInstance().jcd.getCurrentCommand().getCommand();
    }
    
    public static String lastWord(String delimiter) {
        int lastSpaceIndex = getCurrentCommandString().substring(0,getInstance().cursorPosition).lastIndexOf(delimiter) + 1;
        LOG.info("\nLast space at:" + lastSpaceIndex);
        if (lastSpaceIndex < 0) {
            lastSpaceIndex = 0;
        }
        return getCurrentCommandString().substring(lastSpaceIndex,getInstance().cursorPosition).trim();
    }

    public static int replaceLastWord(String delimiter, String wordToReplaceWith) {
        int lastSpaceIndex = getCurrentCommandString().substring(0,getInstance().cursorPosition).lastIndexOf(delimiter) + 1;
        LOG.info("\nLast space at:" + lastSpaceIndex);
        if (lastSpaceIndex < 0) {
            lastSpaceIndex = 0;
        }
        StringBuilder command = getCurrentCommand().getBuilderCommand();
        command.delete(lastSpaceIndex, getInstance().cursorPosition).insert(lastSpaceIndex,wordToReplaceWith).toString();
        return lastSpaceIndex+wordToReplaceWith.length();
    }

    public static void newCommand() {
        getInstance().jcd.saveCommand();
        System.out.print(">"+getCurrentCommandString());
        getInstance().cursorPosition=getCurrentCommandString().length();
    }

    public static void prevCommand() {
        int length=getCurrentCommandString().length();
        int cursorPosition=getInstance().cursorPosition;
        if(cursorPosition<length){
            for(int i=0;i<length-cursorPosition;i++){
                System.out.print(" ");
            }
            for(int i=0;i<length-cursorPosition;i++){
                System.out.print("\b");
            }
        }
        for(int i=0;i<cursorPosition;i++){
            System.out.print("\b \b");
        }
        getInstance().jcd.prevCommand();
        System.out.print(getCurrentCommandString());
        getInstance().cursorPosition=getCurrentCommandString().length();
    }

    public static void nextCommand() {
        int length=getCurrentCommandString().length();
        int cursorPosition=getInstance().cursorPosition;
        if(cursorPosition<length){
            for(int i=0;i<length-cursorPosition;i++){
                System.out.print(" ");
            }
            for(int i=0;i<length-cursorPosition;i++){
                System.out.print("\b");
            }
        }
        for(int i=0;i<cursorPosition;i++){
            System.out.print("\b \b");
        }
        getInstance().jcd.nextCommand();
        System.out.print(getCurrentCommandString());
        getInstance().cursorPosition=getCurrentCommandString().length();
    }
    
    public static int getCurrentCursorPosition() {
        return getInstance().cursorPosition;
    }
    
    public static void setCurrentCursorPosition(int position) {
        getInstance().cursorPosition=position;
    }

    public static void decreaseCurrentCursorPosition() {
        getInstance().cursorPosition--;
    }

    public static void increaseCurrentCursorPosition() {
        getInstance().cursorPosition++;
    }
    
    //to use when cursor comes at the end by sysout command
    public static void moveCursorToCursorPosition(){
        int cursorPostion=getInstance().cursorPosition;
        int commandLength=getInstance().getCurrentCommandString().length();
        if(cursorPostion>commandLength){
            getInstance().cursorPosition=commandLength;
        }else{
            int movesLeft=commandLength-cursorPostion;
            for(int i=0;i<movesLeft;i++){
                System.out.print((char)(int)27);
                System.out.print((char)(int)91);
                System.out.print((char)(int)68);
            }
        }
    }
}
