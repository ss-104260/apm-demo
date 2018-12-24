package io.terminus.demo.apmdemo.parser.utils;

public class NavigationTiming {
    public long loadTime = 0;
    public long readyStart = 0;
    public long domReadyTime = 0;
    public long scriptExecuteTime = 0;
    public long requestTime = 0;
    public long responseTime = 0;
    public long initDomTreeTime = 0;
    public long loadEventTime = 0;
    public long unloadEventTime = 0;
    public long appCacheTime = 0;
    public long connectTime = 0;
    public long lookupDomainTime = 0;
    public long redirectTime = 0;

    public static NavigationTiming parse(String nt) {
        NavigationTiming result = new NavigationTiming();
        if(nt==null || "".equals(nt)) return result;
        String[] times = nt.split("\\,");
        long[] ts = new long[13];
        ts[0]= NumberParser.parseLong(times[0],0, 36);
        for(int i = 1; i < 13 && i < times.length; i++) {
            ts[i] = NumberParser.parseLong(times[i],0, 36);
            if(ts[i] > ts[0]) {
                ts[i] = 0;
            }
        }
        result.loadTime = ts[0];
        result.readyStart = ts[1];
        result.domReadyTime = ts[2];
        result.scriptExecuteTime = ts[3];
        result.requestTime = ts[4];
        result.responseTime = ts[5];
        result.initDomTreeTime = ts[6];
        result.loadEventTime = ts[7];
        result.unloadEventTime = ts[8];
        result.appCacheTime = ts[9];
        result.connectTime = ts[10];
        result.lookupDomainTime = ts[11];
        result.redirectTime = ts[12];
        return result;
    }
}
