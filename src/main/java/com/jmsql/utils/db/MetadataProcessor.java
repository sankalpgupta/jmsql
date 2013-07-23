package com.jmsql.utils.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MetadataProcessor {

    private static Map<String,Set<String>> nickNameMapper;
    private static List<String> tableNames;
    
    public static void init() {
        nickNameMapper=new HashMap<String, Set<String>>();
        tableNames=new ArrayList<String>();
        try {
            Set<String> tables = DbUtils.getAllTables(DbUtils.getConnection());
            for (String tableName : tables) {
                MetadataProcessor.process(tableName);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
    public static void addName(String nickName,String mapperValue){
        if(!nickNameMapper.containsKey(nickName)){
            nickNameMapper.put(nickName, new HashSet<String>());
        }
        //System.out.println("adding:"+nickName+" for:"+mapperValue);
        nickNameMapper.get(nickName).add(mapperValue);
    }
    
    public static Set<String> getMappers(String nickName){
        if(nickNameMapper.containsKey(nickName)){
            return nickNameMapper.get(nickName);
        }
        return new HashSet<String>();
    }
    
    public static boolean isTable(String tableName){
       return tableNames.contains(tableName.toLowerCase());
    }
    
    public static void process(String name){
        String nickName="";
        String processName=name;
        name=name.toLowerCase();
        if(!tableNames.contains(name)){
            tableNames.add(name);
        }
        while(processName!=null && processName.length()>0){
            nickName+=processName.charAt(0);
            if(processName.contains("_")){
                processName=processName.substring(processName.indexOf("_")+1);
            }
            else{
                break;
            }
        }
        if(!"".equals(nickName)){
            addName(nickName, name);
        }
    }
}
