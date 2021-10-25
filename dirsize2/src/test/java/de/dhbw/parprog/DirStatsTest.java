package de.dhbw.parprog;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class DirStatsTest {

    @Test
    public void correctStatsInTestDir() throws Exception {
        var testDir = new File("src/test/resources/testdir");
        assertThat(testDir.isDirectory()).isTrue();

        var stats = DirSize.dirStats(testDir).get();
        assertThat(stats.fileCount).isEqualTo(38);
        assertThat(stats.totalSize).isEqualTo(3230);
    }
}
