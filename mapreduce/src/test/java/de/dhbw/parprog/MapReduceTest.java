package de.dhbw.parprog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapReduceTest {

    @Test
    public void calculationReturnsCorrectResult() {
        var result = MapReduce.doAnalysis().join();
        assertThat(result.maleCount).isEqualTo(5);
        assertThat(result.maxLen).isEqualTo(12);
        assertThat(result.avgAge).isEqualTo(52.0);
    }
}
