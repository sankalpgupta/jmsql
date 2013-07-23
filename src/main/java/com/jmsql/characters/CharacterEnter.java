package com.jmsql.characters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.jconsole.Jconsole;
import com.jmsql.utils.db.DbUtils;
import com.jmsql.utils.db.MetadataProcessor;

public class CharacterEnter implements ICharacterClass {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterEnter.class);

    private CharacterEnter() {
        try {
            //            ModeManager.addCharacterClass(ENTER, this);
            //            ModeManager.addGlobalActionableCharacters(ENTER, "all");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final CharacterEnter instance = new CharacterEnter();

    public static CharacterEnter getInstance() {
        return instance;
    }

    protected final static Integer ENTER = (int) (char) '\n';

    @Override
    public Set<Integer> getMyCharacters() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(ENTER);
        return set;
    }

    @Override
    public void keyPressed(int c) {
        try {
            String mysqlCommandToExecute = getCommandToExecute(Jconsole.getCurrentCommandString());
            LOG.info("command to be executed:{}", mysqlCommandToExecute);
            System.out.println();
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", " echo '" + mysqlCommandToExecute + "' | mysql -A -t -u"+DbUtils.getUsername()+" -p"+DbUtils.getPassword()+" -P"+DbUtils.getDbPort()+" -h"+DbUtils.getDbIp()+" "+DbUtils.getDbName());
            System.out.print(getOutput(pb.start()));
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        Jconsole.newCommand();
    }

    private String getCommandToExecute(String currentCommandString) {
        String[] currentCommandArray = currentCommandString.split(" ");
        if (currentCommandArray.length > 0) {
            String firstWord=currentCommandArray[0];
            if (firstWord.toLowerCase().trim().startsWith("ls")) {
                if (currentCommandArray.length == 1) {
                    return "show tables";
                } else {
                    String arg = currentCommandArray[1].replace('*', '%');
                    return "show tables like \"" + arg + "\"";
                }
            }
            else if(firstWord.trim().equalsIgnoreCase("use")){
                if(currentCommandArray.length==2){
                    DbUtils.setDbName(currentCommandArray[1]);
                    DbUtils.init();
                    MetadataProcessor.init();
                }
            }
            else if(MetadataProcessor.isTable(firstWord.replace(';', ' ').trim())){
                firstWord=firstWord.replace(';', ' ').trim();
                if(currentCommandArray.length==1){
                    return "desc "+firstWord;
                }else if(currentCommandArray.length==2){
                    return "select * from "+firstWord+" limit "+currentCommandArray[1];
                }else{
                    return "select * from "+currentCommandString;
                }
            }
        }
        return currentCommandString;
    }

    protected static String getOutput(Process p) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }
}
