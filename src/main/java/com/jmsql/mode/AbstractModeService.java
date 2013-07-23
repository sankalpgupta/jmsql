package com.jmsql.mode;

import java.util.HashMap;
import java.util.Map;

import com.jmsql.characters.ICharacterClass;

public abstract class AbstractModeService implements IModeService{

    protected static Map<Integer,ICharacterClass> characterClassMap=new HashMap<Integer, ICharacterClass>();

    
    @Override
    public void keyPressed(int c) {
        // TODO Auto-generated method stub
        
    }

    protected boolean isActionableCharacter(int c) {
        if(characterClassMap.containsKey(c)){
            return true;
        }
//        if(ModeManager.isActionableCharacter(c, this.getClass())){
//            return true;
//        }
        return false;
    } 
    
    protected ICharacterClass getActionalCharacterClass(int c) {
        if(characterClassMap.containsKey(c)){
            return characterClassMap.get(c);
        }
//        else if(ModeManager.isActionableCharacter(c, this.getClass())){
//            return ModeManager.getCharacterClass(c);
//        }
        return null;
    }
    
}
