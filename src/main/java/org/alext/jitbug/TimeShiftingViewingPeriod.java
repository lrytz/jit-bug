package org.alext.jitbug;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * This is the enumeration which represents Time shifting viewing period
 *
 * @author Alexey Tsiunchik
 */
public enum TimeShiftingViewingPeriod {
    
    // NOTE: do not change ordering (it is used while composing SQL query) or 
    // while changing please ensure they sorted from lower to higher or rewrite getMax method
    LIVE(       (byte) 1, "Live",   new int[]{1}, null, 1),
    LIVE_SD(    (byte) 2, "Live+SD",new int[]{1, 2}, null, 1),
    LIVE_1(     (byte) 3, "Live+1", null, 1620, 1),
    LIVE_2(     (byte) 4, "Live+2", null, 3060, 2),
    LIVE_3(     (byte) 5, "Live+3", null, 4500, 3),
    LIVE_4(     (byte) 6, "Live+4", null, 5760, 4),
    LIVE_5(     (byte) 7, "Live+5", null, 7200, 5),
    LIVE_6(     (byte) 8, "Live+6", null, 8640, 6),
    LIVE_7(     (byte) 9, "Live+7", null, 10080, 7);
    
    public static final String ALLOWED_DATA_STREAM = "Live,Live+SD,Live+1,Live+2,Live+3,Live+4,Live+5,Live+6,Live+7";
//    public static final String ALLOWED_DATA_STREAM = "Live,Live+SD,Live+7";

    private String alias;
    
    private Byte id;
    /**
     * For sql-like engines
     */
    private int[] allowedCodes;
    
    private String viewingCodeRestriction;
    
    private Integer playDelayMins;

    private Integer playDelayDays;

    TimeShiftingViewingPeriod(byte id, String alias, int[] allowedCodes, Integer playDelayMins, int delayDays) {
        this.alias = alias;
        this.id = id;
        this.allowedCodes = allowedCodes;
        this.viewingCodeRestriction = allowedCodes != null ? 
                    " in (" + Arrays.stream(allowedCodes)
                    .mapToObj(v->String.valueOf(v)).collect(Collectors.joining(",")) +")" :
                    null;
        this.playDelayMins = playDelayMins;
        this.playDelayDays = delayDays;
    }
    
    public String getAlias() {
        return alias;
    }
    
//    @Override
//    public int compareTo(TimeShiftingViewingPeriod o) {
//        int res = playDelayMins.compareTo(o.playDelayMins);
//        if (res == 0 && this != o) {
//            // LIVE and LIVE+SD case (either this = LIVE o = LIVE_SD or otherwise)  
//            res = this == LIVE ? -1 : 1;  
//        }
//        return res;
//    }
    
    public Byte getId() {
        return id;
    }

    public String getViewingCodeRestriction() {
        return viewingCodeRestriction;
    }
    
    public Integer getPlayDelayMins() {
        return playDelayMins;
    }

    public Integer getPlayDelayDays() {
        return playDelayDays;
    }
    
    public boolean matchDay(int timeShiftingViewingCode, int delayDays) {
        return match(timeShiftingViewingCode, delayDays, true);
    }
    /**
     * Checks whether period match given pair viewing code and playDelayMinutes
     * @param timeShiftingViewingCode
     * @param delayMinutes
     * @return
     */
    public boolean match(int timeShiftingViewingCode, int delayMinutes) {
        return match(timeShiftingViewingCode, delayMinutes, false);
    }
    
    private boolean match(int timeShiftingViewingCode, int delay, boolean useDays) {
        boolean res = true;
        Integer delayToMatch = useDays ? this.playDelayDays : this.playDelayMins;
        if (allowedCodes != null) {
            res = false;
            for (int code : allowedCodes) {
                if (code == timeShiftingViewingCode) {
                    res = true;
                    break;
                }
            }
        }
        if (delayToMatch != null) {
            res &= delay <= delayToMatch;
        }
        return res;
    }
    
    /**
     * Returns max element in collections of timeshifting periods. Logically Live contained by LIVE_SD contained by LIVE+1 and so on 
     * For now implementation is based on ordinal enum value but if it should be changed or is not valid anymore this method should be
     * reimplemented to return max element which contains all others
     * @param periods
     * @return
     */
    public static TimeShiftingViewingPeriod getMax(Collection<TimeShiftingViewingPeriod> periods) {
        return Collections.max(periods);
    }
    
    public static Collection<TimeShiftingViewingPeriod> fromStringCollection(Collection<String> aliases) {
        return aliases.stream().map(TimeShiftingViewingPeriod::fromString).collect(Collectors.toSet());
    }
    
    public static TimeShiftingViewingPeriod fromString(String alias) {
        for (TimeShiftingViewingPeriod p : values()) {
            if (p.alias.equalsIgnoreCase(alias)) {
                return p;
            }
        }
        throw new IllegalArgumentException(
                String.format("Field 'dataStream' has wrong value: '%s'. Allowed values: %s",
                        alias, ALLOWED_DATA_STREAM));
    }
}
