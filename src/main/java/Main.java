import rest.RESTService;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(new Option("p", "port", true, "service port"));

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        Integer port = 0;

        try {
            cmd = parser.parse(options, args);
            port = Integer.parseInt(cmd.getOptionValue("port", "8080"));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("money-transfer", options);

            System.exit(1);
        }

        RESTService service = new RESTService(port);
    }
}