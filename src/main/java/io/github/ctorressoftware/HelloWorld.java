package io.github.ctorressoftware;

import picocli.CommandLine;

@CommandLine.Command(name = "Hello", version = "Hello 1.0", mixinStandardHelpOptions = true)
public class HelloWorld implements Runnable {

    @CommandLine.Option(
            names = {"-a"},
            required = true,
            paramLabel = "FIRST",
            description = "First number of the sum",
            defaultValue = "100"
    )
    int firstNumber;

    @CommandLine.Option(
            names = {"-b"},
            required = true,
            paramLabel = "SECOND",
            description = "Second number of the sum",
            defaultValue = "20011"
    )
    int secondNumber;


    @CommandLine.Command(name = "World", description = "The other part of the hellow statement")
    void subCommandViaMethod(@CommandLine.Parameters(
            arity = "1..*", paramLabel = "<measure>", description = "measure") String measure) {
        System.out.println(measure);
    }



    @Override
    public void run() {
        System.out.println("RESULT: " + (firstNumber + secondNumber));
    }
}
