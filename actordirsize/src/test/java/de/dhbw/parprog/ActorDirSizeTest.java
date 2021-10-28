package de.dhbw.parprog;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SpellCheckingInspection")
public class ActorDirSizeTest {

    @Test
    public void testResourceSizeIsCorrect() {
        var testDir = new File("src/test/resources/testdir");
        assertThat(testDir.isDirectory()).isTrue();

        var stats = ActorDirSize.dirStats(testDir);
        assertThat(stats.fileCount).isEqualTo(38);
        assertThat(stats.totalSize).isEqualTo(3230);
    }
}
