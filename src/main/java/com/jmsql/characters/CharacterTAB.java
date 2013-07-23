package com.jmsql.characters;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.jconsole.Jconsole;
import com.jmsql.utils.db.MetadataProcessor;

public class CharacterTAB implements ICharacterClass {

    private static final Logger              LOG = LoggerFactory.getLogger(CharacterTAB.class);

    //this is not working....need to introduce some other concepts for this
    private CharacterTAB(){
        try {
//            ModeManager.addCharacterClass(TAB, this);
//            ModeManager.addGlobalActionableCharacters(TAB, "all");
//            LOG.info("added global Character:"+TAB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final CharacterTAB instance=new CharacterTAB();
    
    public static CharacterTAB getInstance(){
        return instance;
    }
    
    protected static final Integer TAB=(int)(char)'\t';
    
    
    @Override
    public Set<Integer> getMyCharacters() {
        Set<Integer> set=new HashSet<Integer>();
        set.add(TAB);
        return set;
    }


    @Override
    public void keyPressed(int c) {
      /*Jconsole.replaceLastWord(" ",replaceLastWord);*/
      String lastWord=Jconsole.lastWord(" ");
      LOG.info("Last word:"+lastWord);
      
      Set<String> processedWords = MetadataProcessor.getMappers(lastWord);
      int length=Jconsole.getCurrentCommandString().length();
      for(int i=0;i<length;i++){
          System.out.print("\b \b");
      }
      System.out.print("\b \b");
      if(processedWords.size()==1){
          int cursorPosition=Jconsole.replaceLastWord(" ",processedWords.iterator().next());
          System.out.print(">"+Jconsole.getCurrentCommand());
          Jconsole.setCurrentCursorPosition(cursorPosition);
          Jconsole.moveCursorToCursorPosition();
      }
      else if(processedWords.size()>1){
          for(String suggestedName:processedWords){
              System.out.print(suggestedName+"\t");
          }
          System.out.println();
          System.out.print(">"+Jconsole.getCurrentCommand());
      }else{
          System.out.print(">"+Jconsole.getCurrentCommand());
      }
    }

}
