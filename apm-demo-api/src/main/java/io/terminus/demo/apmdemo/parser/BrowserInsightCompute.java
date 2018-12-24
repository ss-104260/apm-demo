package io.terminus.demo.apmdemo.parser;

import com.google.gson.JsonSyntaxException;
import io.terminus.demo.apmdemo.parser.model.metrics.RawMetric;
import io.terminus.demo.apmdemo.parser.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qiniu.ip17mon.LocationInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrowserInsightCompute implements java.io.Serializable {

    private final static Logger logger = LoggerFactory.getLogger(BrowserInsightCompute.class);
    private final static long MILLISECOND = 1;

    private static class IngoreException extends RuntimeException {}

    public void mapToMetric(String val, List<RawMetric> collector) throws Exception {
        if (val == null || "".equals(val)) return;
        try {
            Message msg = Message.parse(val);
            FormParams data = msg.getData();
            String name = data.getValue("t");
            RawMetric metric = new RawMetric();
            metric.setTimestamp(Long.parseLong(data.getValue("date")) * 1000 * 1000);
            // set rpc tags
            Map<String, String> tags = metric.getTags();
            setTag(tags, "tk", msg.getTk());
            // setTag(tags, "cid", msg.getCid());
            // setTag(tags, "uid", msg.getUid());
            switch (name) {
                case "req":
                case "ajax":
                case "request":
                    metric = toRequestMetric(msg, data, metric);
                    break;
                case "timing":
                    List<RawMetric> metrics = toTimingMetric(msg, data, metric);
                    for(RawMetric r : metrics) {
                        collector.add(r);
                    }
                    return;
                case "error":
                    metric = toErrorMetric(msg, data, metric);
                    break;
                case "device":
                    return;
                    // 暂时忽略
                    // metric = toDeviceMetric(msg, data, metric);
                    // break;
                case "browser":
                    return;
                    // 暂时忽略
                    // metric = toBrowserMetric(msg, data, metric);
                    // break;
                case "document":
                    return;
                    // 暂时忽略
                    // metric = toDocumentMetric(msg, data, metric);
                    // break;
                case "script":
                    return;
                    // 暂时忽略
                    // metric = toScriptMetric(msg, data, metric);
                    // break;
                case "event":
                    // TODO
                    return;
                default:
                    throw new Exception(String.format("unknown t=%s in data", name));
            }
            collector.add(metric);
        } catch (IngoreException e) {
            // do nothing
        } catch (Exception e) {
            throw new Exception("Deserialize browser insight message fail.", e);
        }
    }

    public void setTag(Map<String, String> tags, String key, String val) {
        if (key == null || "".equals(key)) return;
        if (val == null || "".equals(val)) val = "none";
        tags.put(key, val);
    }

    private RawMetric toRequestMetric(Message msg, FormParams data, RawMetric metric) throws Exception {
        metric.setName("ta_req");
        Map<String, Object> fields = metric.getFields();
        Map<String, String> tags = metric.getTags();
        appendMobileInfoIfNeed(data, metric);
        fields.put("tt", NumberParser.parseLong(data.getValue("tt"),0) * MILLISECOND);
        fields.put("req", NumberParser.parseDouble(data.getValue("req"), 0));
        fields.put("res", NumberParser.parseDouble(data.getValue("res"), 0));
        fields.put("status", NumberParser.parseInt(data.getValue("st"), 0));

        // setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
        String url = data.getValue("url");
        // setTag(tags, "url", url);
        setTag(tags, "req_path", getDecodedPath(url));
        setTag(tags, "host", data.getValue("dh"));
        setTag(tags, "status_code", data.getValue("st"));
        setTag(tags, "method", data.getValue("me"));
        // setTag(tags, "vid", data.getValue("vid"));
        return metric;
    }

    private List<RawMetric> toTimingMetric(Message msg, FormParams data, RawMetric metric) throws Exception {
        List<RawMetric> list = new ArrayList<>();
        metric.setName("ta_timing");
        Map<String, Object> fields = metric.getFields();
        Map<String, String> tags = metric.getTags();
        boolean isMobile = appendMobileInfoIfNeed(data, metric);
        boolean copyToSlow = false;
        if (isMobile) {
            long nt = NumberParser.parseLong(data.getValue("nt"), 0);
            fields.put("plt", nt * MILLISECOND);
        } else {
            UserAgent ua = UserAgent.parse(data.getValue("ua"));
            setTag(tags, "browser", ua.browser);
            // setTag(tags, "browser_version", ua.browserVersion);
            setTag(tags, "os", ua.os);
            // setTag(tags, "osv", ua.osVersion);
            setTag(tags, "device", ua.device);

            String timingStr = data.getValue("pt");
            long plt, rrt, put, act, dns, tcp, rqt, rpt, srt, dit, drt, clt, set, wst, fst, pct, rct, net, prt;
            if(timingStr == null || timingStr.length() == 0) {
                NavigationTiming nt = NavigationTiming.parse(data.getValue("nt"));
                plt = (nt.loadTime + nt.readyStart) * MILLISECOND; // ? loadTime
                rrt = nt.redirectTime * MILLISECOND;
                put = nt.unloadEventTime * MILLISECOND;
                act = nt.appCacheTime * MILLISECOND;
                dns = nt.lookupDomainTime * MILLISECOND;
                tcp = nt.connectTime * MILLISECOND;
                rqt = (nt.requestTime - nt.responseTime) * MILLISECOND; // ? requestTime
                rpt = nt.responseTime * MILLISECOND;
                srt = nt.requestTime * MILLISECOND; // ? requestTime + responseTime
                dit = nt.initDomTreeTime * MILLISECOND;
                drt = nt.domReadyTime * MILLISECOND;
                clt = nt.loadEventTime * MILLISECOND;
                set = nt.scriptExecuteTime * MILLISECOND;
                wst = (
                        nt.redirectTime + nt.appCacheTime + nt.lookupDomainTime + nt.connectTime + nt.requestTime - nt.responseTime
                        // ? redirectTime + appCacheTime + lookupDomainTime + connectTime
                ) * MILLISECOND;
                fst = (
                        (nt.loadTime + nt.readyStart) - (nt.initDomTreeTime + nt.domReadyTime + nt.loadEventTime)
                        // ? loadTime - (initDomTreeTime + domReadyTime + loadEventTime)
                ) * MILLISECOND;
                pct = (
                        (nt.loadTime + nt.readyStart) - (nt.domReadyTime + nt.loadEventTime)
                        // ? loadTime - (domReadyTime + loadEventTime)
                ) * MILLISECOND;
                rct = (
                        (nt.loadTime + nt.readyStart) - nt.loadEventTime
                        // ? loadTime - loadEventTime
                ) * MILLISECOND;
            } else {
                PerformanceTiming pt = PerformanceTiming.parse(timingStr);
                plt = pt.loadEventEnd - pt.navigationStart;
                rrt = pt.redirectEnd - pt.redirectStart;
                put = pt.unloadEventEnd - pt.unloadEventStart;
                act = pt.domainLookupStart - pt.fetchStart;
                dns = pt.domainLookupEnd - pt.domainLookupStart;
                tcp = pt.connectEnd - pt.connectStart;
                rqt = pt.responseStart - pt.requestStart;
                rpt = pt.responseEnd - pt.responseStart;
                srt = pt.responseEnd - pt.requestStart;
                dit = pt.domInteractive - pt.responseEnd;
                drt = pt.domComplete - pt.domInteractive;
                clt = pt.loadEventEnd - pt.loadEventStart;
                set = pt.domContentLoadedEventEnd - pt.domContentLoadedEventStart;
                wst = pt.responseStart - pt.navigationStart;
                fst = pt.firstPaintTime;
                pct = (pt.loadEventEnd - pt.fetchStart) + (pt.fetchStart - pt.navigationStart) -
                        ((pt.domComplete - pt.domInteractive) + (pt.loadEventEnd - pt.loadEventStart));
                rct = (pt.loadEventEnd - pt.fetchStart) + (pt.fetchStart - pt.navigationStart) - (pt.loadEventEnd - pt.loadEventStart);
            }
            net = act + tcp + dns;
            prt = plt - srt - net;

            List<ResourceTiming> rtList = null;
            try {
                rtList = ResourceTiming.parseToList(data.getValue("rt"));
            } catch (JsonSyntaxException e) {
                throw new InvalidMessageException("invalid timing.rt", data.getValue("rt"));
            }
            long rlt = ResourceTiming.resourceTiming(rtList) * MILLISECOND;
            long rdc = ResourceTiming.resourceDnsCount(rtList);
            fields.put("rlt", rlt);
            fields.put("rdc", rdc);
            if (plt < 0 || drt < 0 || set < 0 || srt < 0 || rpt < 0 || rqt < 0 || dit < 0 || clt < 0 ||
                    put < 0 || act < 0 || tcp < 0 || dns < 0 || rrt < 0 || wst < 0 || fst < 0 || pct < 0 || rct < 0 || net < 0 || prt < 0) {
                throw new InvalidMessageException("invalid timing.nt or timing.rt", "");
            }
            if(plt > 8000 * MILLISECOND) {
                copyToSlow = true;
            }

            fields.put("plt", plt);
            fields.put("rrt", rrt);
            fields.put("put", put);
            fields.put("act", act);
            fields.put("dns", dns);
            fields.put("tcp", tcp);
            fields.put("rqt", rqt);
            fields.put("rpt", rpt);
            fields.put("srt", srt);
            fields.put("dit", dit);
            fields.put("drt", drt);
            fields.put("clt", clt);
            fields.put("set", set);
            fields.put("wst", wst);
            fields.put("fst", fst);
            fields.put("pct", pct);
            fields.put("rct", rct);
            fields.put("net", net);
            fields.put("prt", prt);
            fields.put("rlt", rlt);
            fields.put("rdc", rdc);
        }
        setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
        setTag(tags, "host", data.getValue("dh"));
        // setTag(tags, "vid", data.getValue("vid"));
        setTag(tags, "country", "中国");
        setTag(tags, "province", "局域网");
        setTag(tags, "city", "局域网");
        // setTag(tags, "isp", "");
        String ip = msg.getIp();
        if (ip != null && ip.length() > 0 && !ip.contains(":")) {
            LocationInfo info = IPDB.find(ip);
            setTag(tags, "country", info.country);
            setTag(tags, "province", info.state);
            setTag(tags, "city", info.city);
            // setTag(tags, "isp", info.isp);
        }
        list.add(metric);
        if(copyToSlow) {
            RawMetric rm = metric.copy();
            rm.setName(metric.getName() + "_slow");
            list.add(rm);
        }
        return list;
    }

    private RawMetric toErrorMetric(Message msg, FormParams data, RawMetric metric) throws Exception {
        metric.setName("ta_error");
        metric.getFields().put("count", 1);
        Map<String, String> tags = metric.getTags();
        boolean isMobile = appendMobileInfoIfNeed(data, metric);
        if (!isMobile) {
//            setTag(tags, "source", data.getValue("ers"));
//            setTag(tags, "line_no", data.getValue(""));
//            setTag(tags, "column_no", data.getValue("erc"));
            UserAgent ua = UserAgent.parse(data.getValue("ua"));
            setTag(tags, "browser", ua.browser);
//            setTag(tags, "browser_version", ua.browserVersion);
//            setTag(tags, "os", ua.os);
//            setTag(tags, "osv", ua.osVersion);
//            setTag(tags, "device", ua.device);
        }
        setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
//        setTag(tags, "host", data.getValue("dh"));
//        setTag(tags, "vid", data.getValue("vid"));
        setTag(tags, "error", data.getValue("erm"));
//        setTag(tags, "stack_trace", data.getValue("sta"));
        return metric;
    }

    private RawMetric toDeviceMetric(Message msg, FormParams data, RawMetric metric) throws Exception {
        metric.setName("ta_device_mobile");
        metric.getFields().put("count", 1);
        Map<String, String> tags = metric.getTags();
        // setTag(metric.getTags(), "type", "mobile");
        appendMobileInfo(data, metric);
        setTag(tags, "channel", data.getValue("ch"));
        setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
        if ("1".equals(data.getValue("jb"))) {
            setTag(tags, "jb", "true");
        } else {
            setTag(tags, "jb", "false");
        }
        setTag(tags, "cpu", data.getValue("cpu"));
        setTag(tags, "sdk", data.getValue("sdk"));
        setTag(tags, "sd", data.getValue("sd"));
        setTag(tags, "mem", data.getValue("men"));
        setTag(tags, "rom", data.getValue("rom"));
        setTag(tags, "sr", data.getValue("sr"));
        setTag(tags, "host", data.getValue("dh"));
        // setTag(tags, "vid", data.getValue("vid"));
        return metric;
    }

    private RawMetric toBrowserMetric(Message msg, FormParams data, RawMetric metric) throws Exception {
        metric.setName("ta_browser");
        metric.getFields().put("count", 1);
        Map<String, String> tags = metric.getTags();
        // setTag(tags, "type", "browser");
        UserAgent ua = UserAgent.parse(data.getValue("ua"));
        setTag(tags, "browser", ua.browser);
        setTag(tags, "ce", data.getValue("ce"));
        setTag(tags, "vp", data.getValue("vp"));
        setTag(tags, "ul", data.getValue("ul"));
        setTag(tags, "sr", data.getValue("sr"));
        setTag(tags, "sd", data.getValue("sd"));
        setTag(tags, "fl", data.getValue("fl"));
        setTag(tags, "host", data.getValue("dh"));
        // setTag(tags, "vid", data.getValue("vid"));
        setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
        return metric;
    }

    private RawMetric toDocumentMetric(Message msg, FormParams data, RawMetric metric) throws Exception {
        metric.setName("ta_document");
        metric.getFields().put("count", 1);
        Map<String, String> tags = metric.getTags();
        appendMobileInfoIfNeed(data, metric);
        setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
        setTag(tags, "host", data.getValue("dh"));
        // setTag(tags, "vid", data.getValue("vid"));
        setTag(tags, "dl", data.getValue("dl"));
        setTag(tags, "dt", data.getValue("dt"));
        setTag(tags, "ds", data.getValue("ds"));
        setTag(tags, "dr", data.getValue("dr"));
        setTag(tags, "de", data.getValue("de"));
        setTag(tags, "dk", data.getValue("dk"));
        setTag(tags, "tp", data.getValue("tp"));
        return metric;
    }

    private RawMetric toScriptMetric(Message message, FormParams data, RawMetric metric) throws Exception {
        metric.setName("ta_script");
        metric.getFields().put("count", 1);
        Map<String, String> tags = metric.getTags();
        boolean isMobile = appendMobileInfoIfNeed(data, metric);
        if (!isMobile) {
            UserAgent ua = UserAgent.parse(data.getValue("ua"));
            setTag(tags, "browser", ua.browser);
            // setTag(tags, "browser_version", ua.browserVersion);
            setTag(tags, "os", ua.os);
            // setTag(tags, "osv", ua.osVersion);
            setTag(tags, "device", ua.device);
        }
        setTag(tags, "doc_path", getDecodedPath(data.getValue("dp")));
        setTag(tags, "host", data.getValue("dh"));
        // setTag(tags, "vid", data.getValue("vid"));
        String msg = data.getValue("msg");
        if (msg != null) {
            String[] lines = msg.split("\\n");
            setTag(tags, "error", lines[0]);
        }

        setTag(tags, "source", data.getValue("source"));
        setTag(tags, "line_no", data.getValue("lineno"));
        setTag(tags, "column_no", data.getValue("colno"));
        setTag(tags, "message", data.getValue("msg"));
        return metric;
    }

    private boolean appendMobileInfoIfNeed(FormParams data, RawMetric metric) {
        String ua = data.getValue("ua");
        if (isMobile(ua)) {
            throw  new IngoreException();
            // metric.setName(metric.getName() + "_mobile");
            // // setTag(metric.getTags(), "type", "mobile");
            // appendMobileInfo(data, metric);
            // return true;
        } else {
            // setTag(metric.getTags(), "type", "browser");
            return false;
        }
    }

    public boolean isMobile(String ua) {
        if (ua == null) return false;
        ua = ua.toLowerCase();
        if ("ios".equals(ua) || "android".equals(ua)) {
            return true;
        }
        return false;
    }

    private void appendMobileInfo(FormParams data, RawMetric metric) {
        Map<String, String> tags = metric.getTags();
        // setTag(tags, "ns", data.getValue("ns"));
        setTag(tags, "av", data.getValue("av"));
        // setTag(tags, "br", data.getValue("br"));
        // setTag(tags, "gps", data.getValue("gps"));
        // setTag(tags, "osv", data.getValue("osv"));
        String val = data.getValue("osn");
        if (val == null || "".equals(val)) {
            setTag(tags, "os", UserAgent.OTHER);
        } else {
            setTag(tags, "os", val);
        }
        val = data.getValue("md");
        if (val == null || "".equals(val)) {
            setTag(tags, "device", UserAgent.OTHER);
        } else {
            setTag(tags, "device", val);
        }
    }

    private static String getDecodedPath(String url) {
        return removeNumber(getPath(url));
    }

    // "/" -> /
    // "/abc" -> /abc
    // "/abc/123.0" -> /abc/{number}
    // "/abc/cef/" -> /abc/cef/
    // "/123" -> /{number}
    // "/123/456" -> /{number}/{number}
    // "/123/123/" -> /{number}/{number}/
    private static String removeNumber(String path) {
        String[] parts = path.split("\\/",-1);
        StringBuilder sb = new StringBuilder();
        for(int i=0, last = parts.length -1; i < parts.length; i++) {
            try {
                Double.parseDouble(parts[i]);
                sb.append("{number}");
            } catch (NumberFormatException e) {
                sb.append(parts[i]);
            }
            if(i != last) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    // "http://host/path1/path2?query1=value1&query2=value2" -> /path1/path2
    // "//host/path1/path2?query1=value1&query2=value2" -> /path1/path2
    // "/path1/path2?query1=value1&query2=value2" -> /path1/path2
    // "//?query1=value1&query2=value2" -> /
    // "//host?query1=value1&query2=value2" -> /
    // "path1/path2?query1=value1&query2=value2" -> /path1/path2
    private static String getPath(String url) {
        if (url == null || "".equals(url)) return "";
        int start = url.indexOf("//");
        if (start < 0) {
            start = 0;
        } else {
            url = url.substring(start + 2);
            start = url.indexOf("/");
            if (start < 0) {
                return "/";
            }
        }
        int end = url.indexOf("?");
        if (end < 0) {
            end = url.length();
        }
        url = url.substring(start, end);
        if (url.length() <= 0) {
            return "/";
        } else if (!url.startsWith("/")) {
            return "/" + url;
        }
        return url;
    }
}