package log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class LoggerUtility {
    private static boolean isConfigured = false;

    public static Logger getLogger(Class<?> logClass) {
        if (!isConfigured) {
            // Configuration Text et HTML
            PropertyConfigurator.configure("src/log/log4j.properties");
            isConfigured = true;
        }
        return Logger.getLogger(logClass.getName());
    }
}