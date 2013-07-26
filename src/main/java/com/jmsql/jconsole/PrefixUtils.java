package com.jmsql.jconsole;

import com.jmsql.starter.ProcessWideContext;
import com.jmsql.utils.db.DbUtils;

public abstract class PrefixUtils {

    public static String getPrefix() {
        return ProcessWideContext.getInstance().getModeService().getModeName() + ":" + DbUtils.getDbIp() 
        + ConsoleConstant.ANSI_BOLD+":" + DbUtils.getDbName() + ConsoleConstant.ANSI_RESET+">";
    }

}
