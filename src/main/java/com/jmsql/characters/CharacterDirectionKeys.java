package com.jmsql.characters;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.jconsole.Jconsole;
import com.jmsql.mode.ModeManager;

public class CharacterDirectionKeys implements ICharacterClass {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterDirectionKeys.class);

    private CharacterDirectionKeys() {
        try {
            //            ModeManager.addCharacterClass(ENTER, this);
            //            ModeManager.addGlobalActionableCharacters(ENTER, "all");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final CharacterDirectionKeys instance = new CharacterDirectionKeys();

    public static CharacterDirectionKeys getInstance() {
        return instance;
    }

    protected final static Integer DIRECTION = 27;

    @Override
    public Set<Integer> getMyCharacters() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(DIRECTION);
        return set;
    }

    private String tempString = "";

    @Override
    public void keyPressed(int c) {
        LOG.info("Pressed characters for directions:" + c);
        tempString += (char) (int) c;
        if (c == DIRECTION) {
            ModeManager.activateKey(this);

        } else if (c >= 65 && c <= 68) {
            ModeManager.deactivateKey();
            if (c == 68) {
                if (Jconsole.getCurrentCursorPosition() > 0) {
                    Jconsole.decreaseCurrentCursorPosition();
                    System.out.print(tempString);
                }
            } else if (c == 67) {
                if (Jconsole.getCurrentCursorPosition() < Jconsole.getCurrentCommandString().length()) {
                    Jconsole.increaseCurrentCursorPosition();
                    System.out.print(tempString);
                }
            } else if (c == 65) {
                Jconsole.prevCommand();
            } else if (c == 66) {
                Jconsole.nextCommand();
            }
            tempString = "";
        }
    }

}
