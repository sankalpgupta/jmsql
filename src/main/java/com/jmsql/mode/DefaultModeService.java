package com.jmsql.mode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmsql.characters.CharacterBackspace;
import com.jmsql.characters.CharacterDirectionKeys;
import com.jmsql.characters.CharacterEnter;
import com.jmsql.characters.CharacterTAB;
import com.jmsql.characters.ICharacterClass;
import com.jmsql.jconsole.Jconsole;

public class DefaultModeService extends AbstractModeService {

    private static final Logger    LOG       = LoggerFactory.getLogger(DefaultModeService.class);

    protected static final Integer TAB       = (int) (char) '\t';
    protected final static Integer ENTER     = (int) (char) '\n';
    protected final static Integer BACKSPACE = 127;
    protected final static Integer DIRECTION=27;

    static {
        characterClassMap.put(BACKSPACE, CharacterBackspace.getInstance());
        characterClassMap.put(ENTER, CharacterEnter.getInstance());
        characterClassMap.put(TAB, CharacterTAB.getInstance());        
        characterClassMap.put(DIRECTION, CharacterDirectionKeys.getInstance());
    }

    private DefaultModeService() {
    }

    private static DefaultModeService dms;

    public static DefaultModeService getInstance() {
        if (dms == null) {
            dms = new DefaultModeService();
        }
        return dms;
    }

    @Override
    public void keyPressed(int c) {
        if (ModeManager.isAnykeyActivated()) {
            LOG.info("in already activated mode.Pressed:"+c+"("+(int) (char) c+")");
            ICharacterClass characterClass = ModeManager.getActivatedKeyClass();
            if (characterClass != null) {
                LOG.info("Found the activated class:"+characterClass.getClass().getSimpleName());
                characterClass.keyPressed(c);
            }
        } else if (isActionableCharacter(c)) {
            LOG.info(c + " character is an actional character for this mode");
            ICharacterClass characterClass = getActionalCharacterClass(c);
            if (characterClass != null) {
                LOG.info("Found the actionable class");
                characterClass.keyPressed(c);
            }
        } else {
            Jconsole.insertAtCurrentPosition(Character.toChars(c));
            LOG.info("current command string:{}",Jconsole.getCurrentCommandString());
            LOG.info("cursor position:{}",Jconsole.getCurrentCursorPosition());
        }
    }
}
