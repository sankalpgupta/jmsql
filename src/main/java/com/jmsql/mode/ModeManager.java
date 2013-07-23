package com.jmsql.mode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.characters.ICharacterClass;

public abstract class ModeManager {

    private static final Logger              LOG = LoggerFactory.getLogger(ModeManager.class);
    
    private static ICharacterClass activatedKey=null;
    
    public static boolean activateKey(ICharacterClass clazz){
        if(activatedKey==null){
            activatedKey=clazz;
            return true;
        }
        return false;
    }
    public static void deactivateKey(){
        activatedKey=null;
    }
    
    public static ICharacterClass getActivatedKeyClass(){
        return activatedKey;
    }
    
    public static boolean isAnykeyActivated(){
        if(activatedKey==null){
            return false;
        }
        return true;
    }
    
    
    
    private static Map<Integer, Set<String>> globalActionableCharacters = new HashMap<Integer, Set<String>>();
    private static Map<Integer,ICharacterClass> globalCharacterClassMap=new HashMap<Integer, ICharacterClass>();
    
    protected static boolean isActionableCharacter(int c, Class<? extends IModeService> clazz) {
        if (globalActionableCharacters.containsKey(c) && globalCharacterClassMap.containsKey(c)) {
            if (globalActionableCharacters.get(c).contains("all") || globalActionableCharacters.get(c).contains(clazz.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static void addCharacterClass(Integer character, ICharacterClass clazz) throws Exception {
        if(globalCharacterClassMap.containsKey(character)){
            LOG.error("Character: "+character+"("+((char)(int)character)+")"+"is already mapped");
            throw new Exception("Character:"+character+"("+((char)(int)character)+")"+" is already mapped with some class");
        }
        globalCharacterClassMap.put(character, clazz);
    }

    public static ICharacterClass getCharacterClass(Integer character) {
        return globalCharacterClassMap.get(character);
    }
    
    public static void addGlobalActionableCharacters(Integer character, String allowedModeClassName) throws Exception {
        if(globalCharacterClassMap.get(character)==null){
            LOG.error("Character:"+character+"("+((char)(int)character)+")"+" is not mapped with any class.Do that first.");
            throw new Exception("Character:"+character+"("+((char)(int)character)+")"+"is to be mapped first before telling the modes");
        }
        if (!globalActionableCharacters.containsKey(character)) {
            globalActionableCharacters.put(character, new HashSet<String>());
        }
        if (!globalActionableCharacters.get(character).contains("all")) {
            globalActionableCharacters.get(character).add(allowedModeClassName);
        } else if (globalActionableCharacters.get(character).size() > 1) {
            globalActionableCharacters.put(character, new HashSet<String>());
            globalActionableCharacters.get(character).add("all");
        }
    }
}
