package com.jmsql.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.start.JmsqlHelp;
import com.jmsql.start.Start;
import com.jmsql.utils.db.DbUtils;
import com.jmsql.utils.db.ForiegnKeyRelation;
import com.jmsql.utils.db.MetadataProcessor;

public class EnterCommand {

    private static final Logger LOG = LoggerFactory.getLogger(EnterCommand.class);

    public String getCommandOutput(String currentCommand) throws IOException, InterruptedException, SQLException {
        boolean explain = false;
        if (currentCommand.trim().equals("help")) {
            return JmsqlHelp.getHelpContent();
        } else if (currentCommand.trim().equals("mysql.help")) {
            currentCommand = "help";
        } else if (currentCommand.startsWith("jmsql.explain ")) {
            explain = true;
            currentCommand = currentCommand.substring("jmsql.explain ".length());
        }
        String mysqlCommandToExecute = getCommandToExecute(currentCommand);
        if (explain) {
            return mysqlCommandToExecute;
        } else {
            LOG.info("command to be executed:{}", mysqlCommandToExecute);
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", " echo '" + mysqlCommandToExecute + "' | mysql -vv -A -t -u" + DbUtils.getUsername() + " -p" + DbUtils.getPassword()
                    + " -P" + DbUtils.getDbPort() + " -h" + DbUtils.getDbIp() + " " + DbUtils.getDbName());
            String result = getOutput(pb.start());
            result = getEmptyOutputResult(mysqlCommandToExecute, result);
            result=result.replaceAll("--------------\n.*\n--------------\n*", "");
            result=result.replaceAll("\n*\\s*\n*Bye\n", "");
            return result.replaceAll("Warning: Using a password on the command line interface can be insecure.", "").trim();
        }
    }

    private String getEmptyOutputResult(String mysqlCommandToExecute, String result) {
        if ("".equals(result.trim())) {
            String firstWord = mysqlCommandToExecute.split(" ")[0];
            if ("select".equalsIgnoreCase(firstWord)) {
                result = "Empty Set\n";
            }
        }
        return result;
    }

    protected static String getOutput(Process p) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int c;
        InputStream in = p.getInputStream();
        p.waitFor();
        while ((c = in.read()) != -1) {
            bout.write(c);
        }
        in = p.getErrorStream();
        while ((c = in.read()) != -1) {
            bout.write(c);
        }
        String result = new String(bout.toByteArray());
        return result;
    }

    private String getCommandToExecute(String currentCommandString) throws SQLException {
        String format = "";
        String result = "";
        if (currentCommandString.endsWith("\\G")) {
            currentCommandString.substring(0, currentCommandString.length() - 2);
            format = "\\G";
        }
        String[] currentCommandArray = currentCommandString.split(" ");
        if (currentCommandArray.length > 0) {
            String firstWord = currentCommandArray[0];
            if (currentCommandArray.length == 1 && firstWord.toLowerCase().trim().startsWith("db")) {
                result = "show databases";
            } else if (firstWord.toLowerCase().trim().startsWith("ls")) {
                if (currentCommandArray.length == 1) {
                    result = "show tables";
                } else {
                    String arg = currentCommandArray[1].replace('*', '%');
                    result = "show tables like \"" + arg + "\"";
                }
            } else if (firstWord.trim().equalsIgnoreCase("use")) {
                if (currentCommandArray.length == 2) {
                    if (currentCommandArray[1].indexOf(';') != -1) {
                        currentCommandArray[1] = currentCommandArray[1].substring(0, currentCommandArray[1].indexOf(';'));
                    }
                    DbUtils.setDbName(currentCommandArray[1]);
                    System.out.println("connecting to database " + currentCommandArray[1]);
                    DbUtils.init();
                    System.out.println("connected.");
                    MetadataProcessor.init();
                    Start.refreshPrompt();
                    //TODO change the console reader completers
                }
            } else if (MetadataProcessor.isTable(firstWord.replace(';', ' ').trim())) {
                result = getFirstWordTableResult(currentCommandString, currentCommandArray, firstWord);
            }
        }
        if (StringUtils.isBlank(result)) {
            return currentCommandString;
        }
        return result.trim() + format;
    }

    private String getFirstWordTableResult(String currentCommandString, String[] currentCommandArray, String firstWord) throws SQLException {
        String result;
        firstWord = firstWord.replace(';', ' ').trim();
        if (currentCommandArray.length == 1) {
            result = "desc " + firstWord;
        } else if (NumberUtils.isNumber(currentCommandArray[1])) {
            result = "select * from " + firstWord + " limit " + currentCommandArray[1];
        } else {
            int joinIndex = currentCommandString.indexOf(" join ");
            String tableString=firstWord;
            String[] tables = {firstWord};
            String processedCommandString=currentCommandString.substring(firstWord.length()).trim();
            if(joinIndex!=-1){
                tableString=currentCommandString.substring(0,joinIndex).trim().replaceAll(" ", ",");
                if(StringUtils.isNotBlank(tableString)){
                    tables=tableString.split(",");
                    if(Arrays.asList(tables).stream().allMatch(table->MetadataProcessor.isTable(table))){
                        Set<String> tableSet=new HashSet<>(Arrays.asList(tables));
                        List<ForiegnKeyRelation> foreignKeys = MetadataProcessor.getForeignKeys(tableSet);
                        StringBuilder joinQuery=new StringBuilder();
                        boolean firstJoin=true;
                        for(ForiegnKeyRelation fk:foreignKeys){
                            if(!firstJoin){
                                joinQuery.append(" and ");
                            }else{
                                joinQuery.append(" on ");
                                firstJoin=false;
                            }
                           joinQuery.append(fk.getPrimaryTable()+"."+fk.getPrimaryColumn()+"="+fk.getReferrerTable()+"."+fk.getReferrerColumn());
                        }
                        tableString=StringUtils.join(tableSet,  " join ")+joinQuery.toString();
                        processedCommandString=currentCommandString.substring(joinIndex+" join ".length()).trim();
                    }else{
                        tableString=firstWord;
                        String[] tablesTemp = {firstWord};
                        tables=tablesTemp;
                    }
                }
            }
            currentCommandArray=processedCommandString.trim().split(" +");
            if (currentCommandArray.length==1 || (processedCommandString.startsWith("\"") && currentCommandArray.length>0)) {
                StringBuilder columnSearchQuery = new StringBuilder();
                String []searchKeywords=currentCommandArray[0].split("\",\"");
                boolean firstKeyword=true;
                for(String searchKeyword:searchKeywords){
                    if(!firstKeyword){
                        columnSearchQuery.append(" and ");
                    }else{
                        firstKeyword=false;
                    }
                    columnSearchQuery.append("(");
                    boolean firstOrQuery=true;
                    for(String table:tables){
                        for (String column : DbUtils.getColumnsByTable(DbUtils.getConnection(), table)) {
                            if (!firstOrQuery) {
                                columnSearchQuery.append(" or ");
                            }else{
                                firstOrQuery=false;
                            }
                            columnSearchQuery.append(table+"."+column + " like \"%" + searchKeyword.replaceAll("\"", "") + "%\"");
                        }
                    }
                    columnSearchQuery.append(")");
                }
                int defaultLimit =100;
                if(currentCommandArray.length>1 && NumberUtils.isNumber(currentCommandArray[1])){
                    defaultLimit=Integer.parseInt(currentCommandArray[1]);
                }
                result = "select * from " + tableString + " where " + columnSearchQuery+" limit "+defaultLimit;
            }else{
                result = "select * from " + tableString+" "+processedCommandString;
            }
        }
        return result;
    }
}
