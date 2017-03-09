public class Debug
{
    private static boolean debug = false;
    public static void enable() { debug = true; }
    public static void disable() { debug = false; }
    public static boolean enabled() { return debug; }
}