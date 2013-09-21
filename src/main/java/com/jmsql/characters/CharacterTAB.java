package com.jmsql.characters;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.jconsole.ConsoleConstant;
import com.jmsql.jconsole.Jconsole;
import com.jmsql.utils.db.DbUtils;
import com.jmsql.utils.db.MetadataProcessor;

public class CharacterTAB implements ICharacterClass {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterTAB.class);

    //this is not working....need to introduce some other concepts for this
    private CharacterTAB() {
        try {
            //            ModeManager.addCharacterClass(TAB, this);
            //            ModeManager.addGlobalActionableCharacters(TAB, "all");
            //            LOG.info("added global Character:"+TAB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final CharacterTAB instance = new CharacterTAB();

    public static CharacterTAB getInstance() {
        return instance;
    }

    protected static final Integer TAB = (int) (char) '\t';

    @Override
    public Set<Integer> getMyCharacters() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(TAB);
        return set;
    }

    @Override
    public void keyPressed(int c) {
        /*Jconsole.replaceLastWord(" ",replaceLastWord);*/
        String lastWord = Jconsole.lastWord(" ");
        LOG.info("Last word:" + lastWord);
        if(lastWord==null || lastWord.length()==0){
            wordCompletion("");
        }else if (lastWord.charAt(lastWord.length() - 1) == '+') {
            printInsertStatement(lastWord);
        } else if (lastWord.charAt(lastWord.length() - 1) == '~') {
            printUpdateStatement(lastWord);
        } else {
            wordCompletion(lastWord);
        }
    }

    private void printUpdateStatement(String lastWord) {
        lastWord = lastWord.substring(0, lastWord.length() - 1);
        if (MetadataProcessor.isTable(lastWord)) {
            Jconsole.wipeCommand();
            Jconsole.replaceLastWord(" ", "update "+lastWord+" set " + lastWord + ".");
            Jconsole.printCurrentCommand();
        }
    }

    private void printInsertStatement(String lastWord) {
        lastWord = lastWord.substring(0, lastWord.length() - 1);
        if (MetadataProcessor.isTable(lastWord)) {
            String csvColumn = "";
            for (String column : MetadataProcessor.getColumn(lastWord, null)) {
                csvColumn += ("".equals(csvColumn) ? "" : ",") + column;
            }
            Jconsole.wipeCommand();
            Jconsole.replaceLastWord(" ", "insert into " + lastWord + " (" + csvColumn + ") values ();");
            Jconsole.printCurrentCommand();
        }
    }

    private void wordCompletion(String lastWord) {
        Set<String> processedWords = new HashSet<String>();
        String lastWordDelimiter = "";
        String actualLastWord=lastWord;
        if (lastWord.contains(".")) {
            String[] splittedLastWord = lastWord.split("\\.");
            LOG.debug("length of splitted last word:" + splittedLastWord.length);
            if (splittedLastWord.length == 3) {
                if (splittedLastWord[0].equalsIgnoreCase(DbUtils.getDbName())) {
                    if (MetadataProcessor.isTable(splittedLastWord[1])) {
                        processedWords = getSuggestedColumnNames(splittedLastWord[1], splittedLastWord[2]);
                        lastWordDelimiter = ".";
                        actualLastWord=splittedLastWord[2];
                    }
                }
            } else if (splittedLastWord.length == 2) {
                if (splittedLastWord[0].equalsIgnoreCase(DbUtils.getDbName())) {
                    processedWords = MetadataProcessor.getMappers(splittedLastWord[1]);
                    lastWordDelimiter = ".";
                    actualLastWord=splittedLastWord[1];
                } else if (MetadataProcessor.isTable(splittedLastWord[0])) {
                    processedWords = getSuggestedColumnNames(splittedLastWord[0], splittedLastWord[1]);
                    lastWordDelimiter = ".";
                    actualLastWord=splittedLastWord[1];
                }
                //TODO support for alias in future
            } else if (splittedLastWord.length == 1) {
                if (splittedLastWord[0].equalsIgnoreCase(DbUtils.getDbName())) {
                    processedWords = MetadataProcessor.getMappers("");
                    lastWordDelimiter = ".";
                    actualLastWord="";
                } else if (MetadataProcessor.isTable(splittedLastWord[0])) {
                    processedWords = getSuggestedColumnNames(splittedLastWord[0], null);
                    lastWordDelimiter = ".";
                    actualLastWord="";
                }
            }
        } else {
            processedWords = MetadataProcessor.getMappers(lastWord);
            lastWordDelimiter = " ";
        }

        Jconsole.wipeCommand();
        if (processedWords.size() == 1) {
            Jconsole.replaceLastWord(lastWordDelimiter, processedWords.iterator().next());
            Jconsole.printCurrentCommand();
        } else if (processedWords.size() > 1) {
            int counter = 0;
            System.out.println(ConsoleConstant.ANSI_RESET);
            for (String suggestedName : processedWords) {
                if (counter == 0) {
                    System.out.print(suggestedName + "\t");
                    counter = 1;
                } else if (counter == 1) {
                    System.out.print(ConsoleConstant.ANSI_GREEN + suggestedName + ConsoleConstant.ANSI_RESET + "\t");
                    counter = 2;
                }/*else if (counter == 2) {
                    System.out.print(suggestedName + "\t");
                    counter = 3;
                 }  */else {
                    System.out.print(ConsoleConstant.ANSI_RED + suggestedName + ConsoleConstant.ANSI_RESET + "\t");
                    counter = 0;
                }

            }
            System.out.println(ConsoleConstant.ANSI_RESET);
            Jconsole.printCurrentCommand();
            String commonPrefix = getCommonPrefix(processedWords);
            if(commonPrefix.length()>actualLastWord.length() && commonPrefix.startsWith(actualLastWord)){
                Jconsole.insertAtCurrentPosition(commonPrefix.substring(actualLastWord.length()).toCharArray());
            }
        } else {
            Jconsole.printCurrentCommand();
        }
    }

    private String getCommonPrefix(Set<String> processedWords) {
        if (processedWords.size() > 0) {
            StringBuilder sb = new StringBuilder();
            int minLength = Integer.MAX_VALUE;
            String shortestWord=null;
            for (String word : processedWords) {
                minLength = Math.min(minLength, word.length());
                if(word.length()==minLength){
                    shortestWord=word;
                }
            }
            for (int i = 0; i < minLength; i++) {
                boolean common=true;
                for (String word : processedWords) {
                    if(shortestWord.charAt(i)!=word.charAt(i)){
                        common=false;
                        break;
                    }
                }
                if(!common){
                    break;
                }else{
                    sb.append(shortestWord.charAt(i));
                }
            }
           return sb.toString(); 
        }
        return "";
    }

    private Set<String> getSuggestedColumnNames(String tableName, String columnName) {
        return MetadataProcessor.getColumn(tableName, columnName);
    }

}
