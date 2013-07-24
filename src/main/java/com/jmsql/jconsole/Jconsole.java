package com.jmsql.jconsole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jconsole {

    private static Jconsole     jconsole;

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
        int lastSpaceIndex = getCurrentCommandString().substring(0, getInstance().jcd.getCurrentCursorPosition()).lastIndexOf(delimiter) + 1;
        LOG.info("\nLast space at:" + lastSpaceIndex);
        if (lastSpaceIndex < 0) {
            lastSpaceIndex = 0;
        }
        return getCurrentCommandString().substring(lastSpaceIndex, getInstance().jcd.getCurrentCursorPosition()).trim();
    }

    public static int replaceLastWord(String delimiter, String wordToReplaceWith) {
        int lastSpaceIndex = getCurrentCommandString().substring(0, getInstance().jcd.getCurrentCursorPosition()).lastIndexOf(delimiter) + 1;
        LOG.info("\nLast space at:" + lastSpaceIndex);
        if (lastSpaceIndex < 0) {
            lastSpaceIndex = 0;
        }
        StringBuilder command = getCurrentCommand().getBuilderCommand();
        command.delete(lastSpaceIndex, getInstance().jcd.getCurrentCursorPosition()).insert(lastSpaceIndex, wordToReplaceWith).toString();
        int cursorPosition = lastSpaceIndex + wordToReplaceWith.length();
        getInstance().jcd.setCurrentCursorPosition(cursorPosition);
        return cursorPosition;
    }

    public static void newCommand() {
        getInstance().jcd.saveCommand();
        System.out.print(">" + getCurrentCommandString());
        getInstance().jcd.setCurrentCursorPosition(getCurrentCommandString().length());
    }

    public static void prevCommand() {
        int length = getCurrentCommandString().length();
        int cursorPosition = getInstance().jcd.getCurrentCursorPosition();
        if (cursorPosition < length) {
            for (int i = 0; i < length - cursorPosition; i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < length - cursorPosition; i++) {
                System.out.print("\b");
            }
        }
        for (int i = 0; i < cursorPosition; i++) {
            System.out.print("\b \b");
        }
        getInstance().jcd.prevCommand();
        System.out.print(getCurrentCommandString());
        getInstance().jcd.setCurrentCursorPosition(getCurrentCommandString().length());
    }

    public static void nextCommand() {
        int length = getCurrentCommandString().length();
        int cursorPosition = getInstance().jcd.getCurrentCursorPosition();
        for (int i = 0; i < length - cursorPosition; i++) {
            System.out.print(" ");
        }
        for (int i = 0; i < length - cursorPosition; i++) {
            System.out.print("\b");
        }
        for (int i = 0; i < cursorPosition; i++) {
            System.out.print("\b \b");
        }
        getInstance().jcd.nextCommand();
        System.out.print(getCurrentCommandString());
        getInstance().jcd.setCurrentCursorPosition(getCurrentCommandString().length());
    }

    //to use when cursor comes at the end by sysout command
    private static void moveCursorToCursorPosition() {
        int cursorPostion = getInstance().jcd.getCurrentCursorPosition();
        int commandLength = getCurrentCommandString().length();
        if (cursorPostion > commandLength) {
            getInstance().jcd.setCurrentCursorPosition(commandLength);
        } else {
            int movesLeft = commandLength - cursorPostion;
            for (int i = 0; i < movesLeft; i++) {
                System.out.print((char) (int) 27);
                System.out.print((char) (int) 91);
                System.out.print((char) (int) 68);
            }
        }
    }

    public static int getCurrentCursorPosition() {
        return getInstance().jcd.getCurrentCursorPosition();
    }

    //TODO throw exception when cannot delete due to out of bound
    public static void deleteCharAtCurrentPosition() {
        System.out.print("\b \b");
        int currentPosition = getInstance().jcd.getCurrentCursorPosition();
        Jconsole.getCurrentCommand().getBuilderCommand().deleteCharAt(currentPosition - 1);
        Jconsole.getInstance().jcd.decreaseCurrentCursorPosition();
        if (currentPosition < getCurrentCommandString().length()) {
            System.out.print(Jconsole.getCurrentCommandString().substring(Jconsole.getCurrentCursorPosition()));
            System.out.print(" \b");
            Jconsole.moveCursorToCursorPosition();
        }
    }

    public static void moveCursorLeft() {
        if (Jconsole.getCurrentCursorPosition() > 0) {
            System.out.print((char) (int) 27);
            System.out.print((char) (int) 91);
            System.out.print((char) (int) 68);
        }
        getInstance().jcd.decreaseCurrentCursorPosition();
    }

    public static void moveCursorRight() {
        if (getCurrentCursorPosition() < getCurrentCommandString().length()) {
            System.out.print((char) (int) 27);
            System.out.print((char) (int) 91);
            System.out.print((char) (int) 67);
        }
        getInstance().jcd.increaseCurrentCursorPosition();
    }

    public static void insertAtCurrentPosition(char[] chars) {
        System.out.print(chars);
        if (Jconsole.getCurrentCursorPosition() == Jconsole.getCurrentCommandString().length()) {
            Jconsole.getCurrentCommand().getBuilderCommand().append(chars);
            getInstance().jcd.setCurrentCursorPosition(Jconsole.getCurrentCommandString().length());
        } else {
            getCurrentCommand().getBuilderCommand().insert(Jconsole.getCurrentCursorPosition(), chars);
            getInstance().jcd.increaseCurrentCursorPosition();
            int length = Jconsole.getCurrentCommandString().length();
            int cursorPosition = getCurrentCursorPosition();
            for (int i = 0; i < length - cursorPosition; i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < length - cursorPosition; i++) {
                System.out.print("\b");
            }
            System.out.print(getCurrentCommandString().substring(getCurrentCursorPosition()));
            moveCursorToCursorPosition();
        }
    }

    public static void wipeCommand() {
        int length=Jconsole.getCurrentCommandString().length();
        for(int i=0;i<length;i++){
            System.out.print("\b \b");
        }
        System.out.print("\b \b");
    }

    public static void printCurrentCommand() {
        System.out.print(">"+Jconsole.getCurrentCommand());
        moveCursorToCursorPosition();
    }
}
