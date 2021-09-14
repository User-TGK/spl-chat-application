package assignment_2_task_4;

public class Config
{
    public static boolean EnableLogging = false;
    public static boolean EnableAuthentication = false;
    public static EncryptionMethod Encryption = EncryptionMethod.BASE64REVERSE;
    public static String Host = "localhost";
    public static int Port = 1234;

    public static void usage()
    {
        String newline = System.getProperty("line.separator");
        System.out.println("usage: PROGRAM [OPTION]" + newline
            + "-h NAME: hostname, defaults to localhost" + newline
            + "-p PORT: port number, defaults to 1234" + newline
            + "-l: enable logging, defaults to disabled" + newline
            + "-a: enable authentication, " + newline
            + "-e METHOD: encryption method, one of [BASE64, REVERSE, BASE64REVERSE]" + newline
            );
    }

    public static void parseArgs(String[] args) throws IllegalArgumentException
    {
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-h":
                    ++i;
                    if (i >= args.length) {
                        throw new IllegalArgumentException("Missing argument for option -h");
                    }

                    Config.Host = args[i];
                    break;

                case "-p":
                    ++i;
                    if (i >= args.length) {
                        throw new IllegalArgumentException("Missing argument for option -p");
                    }

                    Config.Port = Integer.parseInt(args[i]);
                    break;

                case "-l":
                    Config.EnableLogging = true;
                    break;

                case "-a":
                    Config.EnableAuthentication = true;
                    break;

                case "-e":
                    ++i;
                    if (i >= args.length) {
                        throw new IllegalArgumentException("Missing argument for option -e");
                    }

                    Config.Encryption = EncryptionMethod.valueOf(args[i]);
                    break;

                default:
                    System.err.println("Unrecognized argument: " + args[i]);
                    break;
            }
        }
    }
}
