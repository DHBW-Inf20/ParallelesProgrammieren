package de.dhbw.parprog;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SpellCheckingInspection")
public class DirSize2Test {

    @Test
    public void correctStatsInTestDir() {
        var testDir = new File("src/test/resources/testdir");
        assertThat(testDir.isDirectory()).isTrue();

        var stats = DirSize.dirStats(testDir).join();
        assertThat(stats.fileCount).isEqualTo(38);
        assertThat(stats.totalSize).isEqualTo(3230);
    }
}
