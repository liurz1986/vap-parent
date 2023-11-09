package com.vrv.vap.xc.tools;

public enum DataUnit {

    /**
     * Bytes, represented by suffix {@code B}.
     */
    BYTES {
        public long toBytes(long b) { return b; }
        public long toKilobytes(long b) { return b/(KB/B); }
        public long toMegabytes(long b) { return b/(MB/B); }
        public long toGigabytes(long b) { return b/(GB/B); }
        public long toTerabytes(long b) {
            return b/(TB/B);
        }
        public long convert(long b, DataUnit u) { return u.toBytes(b); }
    },

    /**
     * Kilobytes, represented by suffix {@code KB}.
     */
    KILOBYTES {
        public long toBytes(long b) { return x(b, KB/B, MAX/(KB/B)); }
        public long toKilobytes(long b) { return b; }
        public long toMegabytes(long b) { return b/(MB/KB); }
        public long toGigabytes(long b) { return b/(GB/KB); }
        public long toTerabytes(long b) {
            return b/(TB/KB);
        }
        public long convert(long b, DataUnit u) { return u.toKilobytes(b); }
    },

    /**
     * Megabytes, represented by suffix {@code MB}.
     */
    MEGABYTES{
        public long toBytes(long b) { return x(b, MB/B, MAX/(MB/B)); }
        public long toKilobytes(long b) { return x(b, MB/KB, MAX/(MB/KB)); }
        public long toMegabytes(long b) { return b; }
        public long toGigabytes(long b) { return b/(GB/MB); }
        public long toTerabytes(long b) {
            return b/(TB/MB);
        }
        public long convert(long b, DataUnit u) { return u.toMegabytes(b); }
    },

    /**
     * Gigabytes, represented by suffix {@code GB}.
     */
    GIGABYTES{
        public long toBytes(long b) { return x(b, GB/B, MAX/(GB/B)); }
        public long toKilobytes(long b) { return x(b, GB/KB, MAX/(GB/KB)); }
        public long toMegabytes(long b) { return x(b, GB/MB, MAX/(GB/MB)); }
        public long toGigabytes(long b) { return b; }
        public long toTerabytes(long b) {
            return b/(TB/GB);
        }
        public long convert(long b, DataUnit u) { return u.toGigabytes(b); }
    },

    /**
     * Terabytes, represented by suffix {@code TB}.
     */
    TERABYTES{
        public long toBytes(long b) { return x(b, TB/B, MAX/(TB/B)); }
        public long toKilobytes(long b) { return x(b, TB/KB, MAX/(TB/KB)); }
        public long toMegabytes(long b) { return x(b, TB/MB, MAX/(TB/MB));  }
        public long toGigabytes(long b) { return x(b, TB/GB, MAX/(TB/GB));  }
        public long toTerabytes(long b) {
            return b;
        }
        public long convert(long b, DataUnit u) { return u.toTerabytes(b); }
    };

    // Handy constants for conversion methods
    private static final long B = 1L;//1b
    private static final long KB = B * 1024; //kb
    private static final long MB = KB * 1024; //mb
    private static final long GB = MB * 1024; //gb
    private static final long TB = GB * 1024; //tb

    private static final long MAX = Long.MAX_VALUE;

    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    private static long x(long d, long m, long over) {
        if (d > over) return Long.MAX_VALUE;
        if (d < -over) return Long.MIN_VALUE;
        return d * m;
    }

    public long convert(long b, DataUnit dataUnit) {
        throw new AbstractMethodError();
    }

    /**
     * Return the number of bytes in this instance.
     *
     * @return the number of bytes
     */
    public long toBytes(long b) {
        throw new AbstractMethodError();
    }

    /**
     * Return the number of kilobytes in this instance.
     *
     * @return the number of kilobytes
     */
    public long toKilobytes(long b) {
        throw new AbstractMethodError();
    }

    /**
     * Return the number of megabytes in this instance.
     *
     * @return the number of megabytes
     */
    public long toMegabytes(long b) {
        throw new AbstractMethodError();
    }

    /**
     * Return the number of gigabytes in this instance.
     *
     * @return the number of gigabytes
     */
    public long toGigabytes(long b) {
        throw new AbstractMethodError();
    }

    /**
     * Return the number of terabytes in this instance.
     *
     * @return the number of terabytes
     */
    public long toTerabytes(long b) {
        throw new AbstractMethodError();
    }
}