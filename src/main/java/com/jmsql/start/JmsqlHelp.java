package com.jmsql.start;

public class JmsqlHelp {

    public static String getHelpContent(){
        return "1. AutoComplete:"
                +            "\n\ta. Table names: "
                +                "\n\t\t(syntax: <TABLE_PREFIX><TAB>)"
                +            "\n\tb. Table initals to table name: "
                +                "\n\t\t(syntax: <TABLE_INITIALS><TAB>)"
                +                "\n\t\tEg: table name is myql_app_properties then writing 'map' and then TAB would write 'myql_app_properties'"
                +            "\n\tc. Column suggestion:"
                +                "\n\t\t(syntax: <DOT><COLUMN_PREFIX><TAB>)"
                +                "\n\t\tIf user it starts with a '.' then it will suggest all columns for all tables present in the command."
                +      "\n2. Query Understanding:"
                +            "\n\ta. ls : It will show all the tables in the database"
                +            "\n\tb. ls *<TABLE_STRING>* : It will show all the tables with TABLE_STRING in them"
                +            "\n\tc. <TABLE_NAME> : Just a table name would describe the table structure"
                +            "\n\td. <TABLE_NAME> <N> : It shows first N lines of the table"
                +            "\n\te. <TABLE_NAME> <SEARCH_QUERY>: Search for some term in all columns of table. To Search number just use it in double quotes."
                +            "\n\tf. <TABLE_NAME> <mysql_query_criteria>: IT will append all the criteria to the query as in simple mysql query."
                +       "\n3. History:"
                +            "\n\tSo far only maintains current sessions history. You can navigate history with up down arrows or Search with Ctrl+R like in normal unix shell."
                +       "\n4. Basic Unix Shell Editing Commands.";
          
    }
}
