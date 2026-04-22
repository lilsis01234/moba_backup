package util;

public abstract class Resettable {
    private static Resettable instance;

    protected Resettable() {}

    public static <T extends Resettable> T getInstance(Class<T> cls) {
        try {
            return cls.cast(instance);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static void resetAll() {
        instance = null;
    }

    protected static <T extends Resettable> T setInstance(T inst) {
        instance = inst;
        return inst;
    }
}