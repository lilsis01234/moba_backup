package engine.process;

import java.io.IOException;

public class JsonDataProviderFactory {
    
    private static JsonDataProvider singletonInstance;
    
    private JsonDataProviderFactory() {}
    
    public static JsonDataProvider getInstance() throws IOException {
        if (singletonInstance == null) {
            singletonInstance = new JsonDataProvider();
        }
        return singletonInstance;
    }
    
    public static JsonDataProvider create() throws IOException {
        return new JsonDataProvider();
    }
    
    public static void reset() {
        singletonInstance = null;
    }
}