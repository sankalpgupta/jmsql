package com.jmsql.jline.extension.completer;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.jmsql.start.Start;
import com.jmsql.utils.db.DbUtils;
import com.jmsql.utils.db.ForiegnKeyRelation;
import com.jmsql.utils.db.MetadataProcessor;

import jline.console.completer.Completer;

public class JmsqlCompleter implements Completer {

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        int index = 0;
        if (StringUtils.isBlank(buffer)) {
            candidates.addAll(MetadataProcessor.getTableNames());
        } else {
            index = buffer.lastIndexOf(' ', cursor) + 1;
            String firstWord = buffer.substring(index, cursor);
            if (firstWord.equals("")) {
                return -1;
            }
            if (firstWord.equals("join")) {
                joinLineShow(buffer);
            }
            if (firstWord.contains(".")) {
                index=buffer.lastIndexOf('.',cursor);
                int lastIndexOfDot = firstWord.lastIndexOf('.');
                candidates.addAll(findTablesAndTheirColumns(buffer, firstWord.substring(0, lastIndexOfDot), firstWord.substring(lastIndexOfDot + 1)));
            } else {
                candidates.addAll(getMappers(firstWord));
            }
        }
        return candidates.isEmpty() ? -1 : index;
    }

    private void joinLineShow(String buffer) {
        try {
            int indexOfJoin = buffer.indexOf(" join");
            if (indexOfJoin != -1) {
                String tables[] = buffer.substring(0, indexOfJoin).split(" ");
                Set<String> tableSet = new HashSet<>(Arrays.asList(tables));
                boolean allTables = tableSet.parallelStream().allMatch(table -> MetadataProcessor.isTable(table));
                if (allTables && tableSet.size() > 0) {
                    System.out.println();
                    List<ForiegnKeyRelation> foreignKeys =MetadataProcessor.getForeignKeys(tableSet);
                    for (ForiegnKeyRelation fk : foreignKeys) {
                            System.out.println("joining table " + fk.getReferrerTable() + " and " + fk.getPrimaryTable() + " on " + fk.getPrimaryTable() + "."
                                    + fk.getPrimaryColumn() + "=" + fk.getReferrerTable() + "." + fk.getReferrerColumn());
                    }
                    Start.getReader().drawLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> findTablesAndTheirColumns(String buffer, String tableName, String columnPrefix) {
        String[] words = buffer.split(" ");
        Set<String> columns = new HashSet<>();
        Set<String> tables = new HashSet<>();
        if(StringUtils.isBlank(tableName)){
            for (String word : words) {
                if (MetadataProcessor.isTable(word)) {
                    tables.add(word);
                }
            }
        }else if(MetadataProcessor.isTable(tableName)){
            tables.add(tableName);
        }
        boolean tableNameRequired = tables.size() > 1;
        for (String table : tables) {
            for (String column : MetadataProcessor.getColumns(table, columnPrefix)) {
                if (tableNameRequired) {
                    columns.add(table + "." + column);
                } else {
                    columns.add(column);
                }
            }
        }
        if (tables.size()<2 && (columns.size() > 1 || StringUtils.isNotBlank(tableName))) {
            return columns.stream().map(column -> "." + column).collect(Collectors.toSet());//to prefix . before them in case of multiple result to keep . intact in terminal line
        } else {
            return columns;
        }
    }

    public static Set<String> getMappers(String nickName) {
        Set<String> result = new HashSet<String>();
        if (nickName != null) {
            if (MetadataProcessor.getNickNameMapper().containsKey(nickName)) {
                result.addAll(MetadataProcessor.getNickNameMapper().get(nickName));
            }
        }
        if (nickName == null || result.isEmpty()) {
            for (String tableName : MetadataProcessor.getTableNames()) {
                if (tableName.startsWith(nickName)) {
                    result.add(tableName);
                }
            }
        }
        return result;
    }

}
