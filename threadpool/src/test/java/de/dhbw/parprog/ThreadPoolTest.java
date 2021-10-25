package de.dhbw.parprog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreadPoolTest {

    @Test
    public void calculationReturnsCorrectResult() {
        assertThat(ThreadPool.doCalculation()).isEqualTo(10 * 42);
    }
}
