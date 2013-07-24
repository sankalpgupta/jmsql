package com.jmsql.characters;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.jconsole.Jconsole;

public class CharacterBackspace implements ICharacterClass {

    private static final Logger              LOG = LoggerFactory.getLogger(CharacterBackspace.class);

    private CharacterBackspace(){
        try {
//            ModeManager.addCharacterClass(ENTER, this);
//            ModeManager.addGlobalActionableCharacters(ENTER, "all");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final CharacterBackspace instance=new CharacterBackspace();
    
    public static CharacterBackspace getInstance(){
        return instance;
    }
    
    protected final static Integer BACKSPACE=127;
    
    
    @Override
    public Set<Integer> getMyCharacters() {
        Set<Integer> set=new HashSet<Integer>();
        set.add(BACKSPACE);
        return set;
    }


    @Override
    public void keyPressed(int c) {
        int length=Jconsole.getCurrentCommandString().length();
        int currentPosition=Jconsole.getCurrentCursorPosition();
        if(currentPosition>0 && length>0){
            System.out.print("\b \b");
            LOG.info("to delete character at position:"+(currentPosition-1)+" for command:"+Jconsole.getCurrentCommandString()+" length:"+length);
            Jconsole.deleteCharAtCurrentPosition();
        }
    }

}
