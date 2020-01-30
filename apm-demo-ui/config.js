var endpointURL = process.env['ENDPOINT_URL'] || ""
var scheme = process.env['DICE_PROTOCOL'] || "https"
var apiURL = process.env['API_URL'] || "https://tmallx-portal.captain.terminus.io";
var testMode = process.env['TEST_MODE'] || "on"
var tk = process.env['TERMINUS_KEY'] || "cb734d2bcbcc940178d5685d98f6c2c92";
var collectURL = process.env['TERMINUS_TA_COLLECTOR_URL'] || "//analytics.terminus.io/collect";
module.exports = config = {
    "title" : "监控测试器",
    "apiURL" : apiURL,
    "endpointURL" : endpointURL,
    "domain" : endpointURL.replace(/http:\/\//, "").replace(/https:\/\//, ""),
    "collectURL" : scheme + ":" + collectURL,
    "tk" : tk,
    "testMode": testMode,
}
