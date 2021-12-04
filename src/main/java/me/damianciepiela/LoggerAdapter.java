package me.damianciepiela;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerAdapter {
    private final Logger logger;

    public LoggerAdapter(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    public void info(Object toLog) {
        this.logger.info(toLog);
    }

    public void debug(Object toLog) {
        if(this.logger.isDebugEnabled()) this.logger.debug(toLog);
    }

    public void error(Object toLog) {
        this.logger.error(toLog);
    }



}
