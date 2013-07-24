jmsql
=====

A smarter mysql console

HOW TO USE
-to see the help run (from target folder)
java -jar Jmsql-0.1.jar -help


Features:

1. <TABLE-NICKNAME><TAB> to get table name from its nickname. (nickname for table 'a_bc_de' is 'abd' i.e. the first character and immediate characters after underscores)
2. ls<ENTER> to get all the tables in that database
3. ls *v* to get all the table with character v in their names
4. <TABLE-NAME><ENTER> to get description of table
5. <TABLE-NAME> <NUMBER N> to get first N rows of TABLE (suitable to see content of domain tables)
6. <TABLE-NAME> <ADDITIONAL-QUERY> will prepend select * from in the query
