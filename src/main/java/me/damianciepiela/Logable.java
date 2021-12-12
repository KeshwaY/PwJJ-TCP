package me.damianciepiela;

import org.apache.logging.log4j.Level;

public interface Logable {

    default void disableLogging(LoggerAdapter logger) {
        logger.changeLevel(Level.OFF);
    }

}
