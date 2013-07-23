package com.jmsql.starter;

import com.jmsql.mode.DefaultModeService;
import com.jmsql.mode.IModeService;

public class ProcessWideContext {

    private ProcessWideContext(){
        modeService=DefaultModeService.getInstance();
    }
    private static ProcessWideContext pwc;
    public static ProcessWideContext getInstance(){
       if(pwc==null){
           pwc=new ProcessWideContext();
       }
       return pwc;
    }
    
    private IModeService modeService;

    public IModeService getModeService() {
        return modeService;
    }

    public void setModeService(IModeService modeService) {
        this.modeService = modeService;
    }
    
    
}
