package de.dhbw.parprog;

public final class DirStats {
    public final int fileCount;
    public final long totalSize;

    public DirStats() {
        this(0, 0);
    }

    public DirStats(int fileCount, long totalSize) {
        this.fileCount = fileCount;
        this.totalSize = totalSize;
    }

    public DirStats add(DirStats other) {
        return new DirStats(this.fileCount + other.fileCount, this.totalSize + other.totalSize);
    }

    @Override
    public String toString() {
        return "DirStats(fileCount=" + fileCount + ", totalSize=" + totalSize + ")";
    }
}
