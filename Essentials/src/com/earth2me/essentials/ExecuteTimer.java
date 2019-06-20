package com.earth2me.essentials;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * <p>ExecuteTimer class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class ExecuteTimer {
    private final transient List<ExecuteRecord> times;
    private final transient DecimalFormat decimalFormat = new DecimalFormat("#0.000", DecimalFormatSymbols.getInstance(Locale.US));


    /**
     * <p>Constructor for ExecuteTimer.</p>
     */
    public ExecuteTimer() {
        times = new ArrayList<ExecuteRecord>();
    }

    /**
     * <p>start.</p>
     */
    public void start() {
        times.clear();
        mark("start");

    }

    /**
     * <p>mark.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public void mark(final String label) {
        if (!times.isEmpty() || "start".equals(label)) {
            times.add(new ExecuteRecord(label, System.nanoTime()));
        }
    }

    /**
     * <p>end.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String end() {
        final StringBuilder output = new StringBuilder();
        output.append("execution time: ");
        String mark;
        long time0 = 0;
        long time1 = 0;
        long time2 = 0;
        double duration;

        for (ExecuteRecord pair : times) {
            mark = pair.getMark();
            time2 = pair.getTime();
            if (time1 > 0) {
                duration = (time2 - time1) / 1000000.0;
                output.append(mark).append(": ").append(decimalFormat.format(duration)).append("ms - ");
            } else {
                time0 = time2;
            }
            time1 = time2;
        }
        duration = (time1 - time0) / 1000000.0;
        output.append("Total: ").append(decimalFormat.format(duration)).append("ms");
        times.clear();
        return output.toString();
    }


    private static class ExecuteRecord {
        private final String mark;
        private final long time;

        public ExecuteRecord(final String mark, final long time) {
            this.mark = mark;
            this.time = time;
        }

        public String getMark() {
            return mark;
        }

        public long getTime() {
            return time;
        }
    }
}
