package de.dhbw.parprog;

import de.dhbw.parprog.processemu.Pipe;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static de.dhbw.parprog.processemu.ProcessEmu.fork;

public class JavaPipe {

    public static void main(final String[] args) {
        final var pipe = new Pipe();

        final var n = 100;
        for (int i = 0; i < n; i++) {
            fork(pipe, new CalcTask());
        }

        final var sum = new BufferedReader(new InputStreamReader(pipe.getIn()))
                .lines()
                .limit(n)
                .mapToInt(Integer::parseInt)
                .sum();
        System.out.println("Sum: " + sum);
    }
}
