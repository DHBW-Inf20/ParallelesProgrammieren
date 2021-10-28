package de.dhbw.parprog;

public final class CalcResult {
    public final double avgAge;
    public final int maxLen;
    public final int maleCount;

    public CalcResult(double avgAge, int maxLen, int maleCount) {
        this.avgAge = avgAge;
        this.maxLen = maxLen;
        this.maleCount = maleCount;
    }

    @Override
    public String toString() {
        return "CalcResult{" +
                "avgAge=" + avgAge +
                ", maxLen=" + maxLen +
                ", maleCount=" + maleCount +
                '}';
    }
}
