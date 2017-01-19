This project aims at easing mysql query writing, efforts and time wise.
Some of the Features it provides:
1. AutoComplete:
	It has a smart autocomplete feature which tries to understand user intention.
	It autocompletes:
	a. Table names: 
		(syntax: <TABLE_PREFIX><TAB>)
	b. Table initals to table name: 
		(syntax: <TABLE_INITIALS><TAB>)
		Eg: table name is myql_app_properties then writing 'map' and then TAB would write 'myql_app_properties'
	c. Column suggestion:
		(syntax: <DOT><COLUMN_PREFIX><TAB>)
		If user it starts with a '.' then it will suggest all columns for all tables present in the command.
2. Query Understanding:
	Main power of jmsql is query understanding.
	It understands when you write:
	a. ls : It will show all the tables in the database
	b. ls *<TABLE_STRING>* : It will show all the tables with TABLE_STRING in them
	c. <TABLE_NAME> : Just a table name would describe the table structure
	d. <TABLE_NAME> <N> : It shows first N lines of the table
	e. <TABLE_NAME> <SEARCH_QUERY>: Search for some term in all columns of table. To Search number just use it in double quotes.
	f. <TABLE_NAME> <mysql_query_criteria>: IT will append all the criteria to the query as in simple mysql query.

3. History:
	So far maintains current sessions history. You can navigate history with up down arrows or Search with Ctrl+R like in normal unix shell.
	
4. Basic Unix Shell Editing Commands.

