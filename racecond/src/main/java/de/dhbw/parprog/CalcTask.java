package de.dhbw.parprog;

import de.dhbw.parprog.processemu.Pipe;
import de.dhbw.parprog.processemu.ProcessWithPipe;

import java.io.PrintStream;

public class CalcTask implements ProcessWithPipe {

    @Override
    public void main(final Pipe pipe) {
        try {
            Thread.sleep(1000);
            new PrintStream(pipe.getOut()).println(42);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
