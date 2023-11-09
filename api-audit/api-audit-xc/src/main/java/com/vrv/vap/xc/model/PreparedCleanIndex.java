package com.vrv.vap.xc.model;

import com.vrv.vap.xc.pojo.DataDumpStrategy;

import java.util.Date;

public class PreparedCleanIndex implements Comparable<PreparedCleanIndex> {

    private String indexPrefi;

    private String indexName;

    private Date indexDate;

    private String indexDateStr;

    private boolean isMonth;

    private DataDumpStrategy strategy;

    public PreparedCleanIndex(String indexPrefi, String indexName, Date indexDate, String indexDateStr, boolean isMonth) {
        this.indexPrefi = indexPrefi;
        this.indexName = indexName;
        this.indexDate = indexDate;
        this.indexDateStr = indexDateStr;
        this.isMonth = isMonth;
    }

    public String getIndexPrefi() {
        return indexPrefi;
    }

    public void setIndexPrefi(String indexPrefi) {
        this.indexPrefi = indexPrefi;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Date getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(Date indexDate) {
        this.indexDate = indexDate;
    }

    public String getIndexDateStr() {
        return indexDateStr;
    }

    public void setIndexDateStr(String indexDateStr) {
        this.indexDateStr = indexDateStr;
    }

    public boolean isMonth() {
        return isMonth;
    }

    public void setMonth(boolean month) {
        isMonth = month;
    }

    public DataDumpStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(DataDumpStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(PreparedCleanIndex o) {
        if (indexDateStr.length() == o.getIndexDateStr().length()) {
            return indexDateStr.compareTo(o.getIndexDateStr());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "PreparedCleanIndex{" +
                "indexPrefi='" + indexPrefi + '\'' +
                ", indexName='" + indexName + '\'' +
                ", indexDate=" + indexDate +
                ", indexDateStr='" + indexDateStr + '\'' +
                ", isMonth=" + isMonth +
                ", strategy=" + strategy +
                '}';
    }
}
