package com.jmsql.utils.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MetadataProcessor {

    private static Map<String, Set<String>> nickNameMapper;
    private static List<String>             tableNames;
    private static Map<String, Set<String>> columnMap;
    private static final Logger             LOG = LoggerFactory.getLogger(MetadataProcessor.class);

    public static void init() {
        nickNameMapper = new HashMap<String, Set<String>>();
        columnMap = new HashMap<String, Set<String>>();
        getTables();
        getImportedKeysFromDatabase();
        Thread t = new Thread() {
            public void run() {
                MetadataProcessor.getColumns();
            }
        };
        t.start();
        //getColumns();
    }

    private static void getTables() {
        long startTime = System.currentTimeMillis();
        tableNames = new ArrayList<String>();
        try {
            Set<String> tables = DbUtils.getAllTables(DbUtils.getConnection());
            for (String tableName : tables) {
                MetadataProcessor.process(tableName);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOG.debug("Time taken in hashing tables names:{}ms", estimatedTime);
    }
    
    private static void getImportedKeysFromDatabase() {
        long startTime = System.currentTimeMillis();
        tableNames = new ArrayList<String>();
        try {
            Set<String> tables = DbUtils.getAllTables(DbUtils.getConnection());
            for (String tableName : tables) {
                MetadataProcessor.process(tableName);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOG.debug("Time taken in hashing tables names:{}ms", estimatedTime);
    }

    private static void getColumns() {
        long startTime = System.currentTimeMillis();
        try {
            Set<String> columns = DbUtils.getAllColumns(DbUtils.getConnection());
            for (String column : columns) {
                String[] array = column.split(":");
                if (!columnMap.containsKey(array[0])) {
                    columnMap.put(array[0], new HashSet<String>());
                }
                columnMap.get(array[0]).add(array[1]);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        LOG.debug("Time taken in getting column names:{}ms", estimatedTime);
        for (String tableName : columnMap.keySet()) {
            for (String column : columnMap.get(tableName)) {
                LOG.info("table:" + tableName + " column:" + column);
            }
        }
    }

    public static void addName(String nickName, String mapperValue) {
        if (!nickNameMapper.containsKey(nickName)) {
            nickNameMapper.put(nickName, new HashSet<String>());
        }
        //System.out.println("adding:"+nickName+" for:"+mapperValue);
        nickNameMapper.get(nickName).add(mapperValue);
    }

    public static Set<String> getMappers(String nickName) {
        Set<String> result = new HashSet<String>();
        if(nickName!=null){
            if (nickNameMapper.containsKey(nickName)) {
                result.addAll(nickNameMapper.get(nickName));
            }
        }
        if(nickName==null || result.isEmpty()){
            for(String tableName:tableNames){
                if(tableName.startsWith(nickName)){
                    result.add(tableName);
                }
            }
        }
        return result;
    }

    public static boolean isTable(String tableName) {
        return tableNames.contains(tableName.toLowerCase());
    }

    public static Set<String> getColumn(String tableName, String prefix) {
        Set<String> result = new HashSet<String>();
        if (prefix == null) {
            if (columnMap.containsKey(tableName)) {
                return columnMap.get(tableName);
            }
        } else {
            for (String column : columnMap.get(tableName)) {
                if (column.startsWith(prefix)) {
                    result.add(column);
                }
            }
        }
        return result;
    }

    public static void process(String name) {
        String nickName = "";
        String processName = name;
        name = name.toLowerCase();
        if (!tableNames.contains(name)) {
            tableNames.add(name);
        }
        while (processName != null && processName.length() > 0) {
            nickName += processName.charAt(0);
            if (processName.contains("_")) {
                processName = processName.substring(processName.indexOf("_") + 1);
            } else {
                break;
            }
        }
        if (!"".equals(nickName)) {
            addName(nickName, name);
        }
    }
}
