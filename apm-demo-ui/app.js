const util = require('util');
const urlencode = require('urlencode');
const random = require('random');
const schedule = require('node-schedule');
const express = require('express');
var engine = require('ejs-locals');
const path = require('path');
var request = require('request');
const config = require(path.join(__dirname, 'config'));
const app = express();
const tlog = require('@terminus/log');
const log = new tlog('DEBUG');

app.engine('ejs', engine);
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use('/static', express.static(path.join(__dirname, 'static')));

const bodyParser = require('body-parser');
app.use(bodyParser.raw({
    inflate: true,
    limit: '100kb',
    type: "*/*",
}));
app.use(bodyParser.urlencoded({ extended: false }));

const pageData = { title: config.title, apiURL: config.apiURL };
app.get('/health/check',function (req, res, next){
    res.send({ success: true, data: "ok" });
});

app.get('/', function (req, res, next) {
    log.info('render index page.');
    res.render('index', pageData);
});
app.get('/:page', function (req, res, next) {
    log.info('render ' + req.params.page + ' page.');
    res.render(req.params.page, pageData);
});
function getClientIP(req) {
    return req.headers['x-forwarded-for'] ||
        req.connection.remoteAddress ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;
};
app.all('/api/*', function (req, res, next) {
    var options = {
        url: config.apiURL + req.url,
        method: req.method,
        headers: {
            'Content-Type': req.header('Content-Type'),
            'User-Agent': req.header('User-Agent'),
            'X-Forwarded-For': getClientIP(req),
            'X-Real-IP': getClientIP(req),
        },
    };
    if (Buffer.isBuffer(req.body)) {
        options.body = req.body;
    }
    log.info('begin request ' + options.url);
    request(options, function (error, response, body) {
        if (error) {
            log.info('request exception. ' + error);
            res.send({ success: false, error: error });
            return;
        }
        log.info('end request. status: ' + response.statusCode);
        res.status(response.statusCode).send(response.body);
    });
});


var globalIndex = 0

function getUA() {
    var uas = [
        "MSIE 11;rv:11.0",
        "Firefox/22 firefox",
        "Safari Version/11 AppleWebKit",
        "Chrome/77 chrome",
        "360SE qihu QIHU",
        "SE MetaSr",
        "TencentTraveler",
        "QQBrowser/5 qq"
    ];
    var index = globalIndex++ % uas.length;
    if (globalIndex > 10000) {
        globalIndex = 0;
    }
    return uas[index];
}


const uuid = require('uuid/v4')

function getUUID() {
    return uuid();
}

function getTime() {
    return Date.now();
}

function getTimeSinValue(max) {
    var myDate = new Date();
    var min = myDate.getMinutes() % 30;
    var ratio = min / 30 * Math.PI * 2;
    res = Math.floor(max * Math.sin(ratio) + max);
    return res
}

const everySecond = (func) => {
    schedule.scheduleJob('*/1 * * * * *', () => {
        func();
    });
}

const everyMinute = (func) => {
    schedule.scheduleJob('0 */1 * * * *', () => {
        func();
    });
}

const normalIndexPoisson = random.poisson(lambda = 5);
const maxPoisson = random.poisson(lambda = 10);
const minPoisson = random.poisson(lambda = 1);
const indexPoisson = () => {
    var rand = random.int(min = 0, max = 5)
    if (rand == 5) {
        return maxPoisson();
    }
    if (rand == 0) {
        return minPoisson();
    }
    return normalIndexPoisson();


}
const normalOtherPoisson = random.poisson(lambda = 1);
const maxOtherPoisson = random.poisson(lambda = 3);
const otherPoisson = () => {
    if (config.testMode == "on") {
        return 1;
    }
    var rand = random.int(min = 0, max = 5)
    if (rand == 5) {
        return maxOtherPoisson();
    }
    if (rand == 0) {
        return 0;
    }
    return normalOtherPoisson();
}

// 314,4,133,13,15,1,155,5
const timingCollect = 't=timing&nt=%s&rt=%s&dp=%s&dh=%s&ua=%s&vid=%s&date=%s&domain=%s';
// 6671,36,35,7
const rtStr = '{"%s/":{"api/ta":{"|":"%s,%s,%s,%s","/info":"%s,%s,%s,%s"},"static/":{"css/common.css":"%s,%s,%s,%s","js/request.js":"%s,%s,%s,%s"}}}';

// tt=51
const requestCollect = 't=request&tt=%s&url=%s&st=200&me=get&req=0.005859375&res=0.2666015625&dp=%s&dh=%s&ua=%s&vid=%s&date=%s&domain=%s';
const errCollect = "t=error&ers=%s/&erm=Uncaught TypeError: Cannot read property 'state' of undefined&erl=235&erc=60&sta=TypeError: Cannot read property 'state' of undefined\n \
at Object.success (%s/:235:60)\n \
at j (%s/static/libs/jquery/2.1.1/jquery.min.js:2:26860)\n \
at Object.fireWith [as resolveWith] (%s/static/libs/jquery/2.1.1/jquery.min.js:2:27673)\n \
at x (%s/static/libs/jquery/2.1.1/jquery.min.js:4:11120)\n \
at XMLHttpRequest.<anonymous> (%s/static/libs/jquery/2.1.1/jquery.min.js:4:14767)&dp=%s&dh=%s&ua=%s&vid=%s&date=%s&domain=%s";

// req=296 res=92343 tt=68
const appReqCollect = "av=1.0.16&br=SM-N9500&cid=cf27d575-b241-36df-8e05-6658cdea719f&date=%s&dh=%s&md=SM-N9500&me=&ns=Wifi&osv=7.1.1&req=%s&res=%s&st=200&t=request&tt=%s&ua=Android&url=http://%s&vid=%s"

const appTimeCollect = "av=1.0.16&br=iphone&cid=CAB7FF65-AB56-4B82-8D64-3CB80717D604&date=%s&dh=%s&dp=%s&gps=116.347795%2C39.925414&md=iPhone+7+Plus+%28CDMA%29&ns=4G&nt=%s&osn=iOS&osv=11.4.1&t=timing&ua=IOS&vid=%s"

const t1 = () => { return getTimeSinValue(3140/2) }
const t2 = () => { return getTimeSinValue(540/2) }
const t3 = () => { return getTimeSinValue(1330/2) }
const t4 = () => { return getTimeSinValue(630/2) }
const t5 = () => { return getTimeSinValue(650/2) }
const t6 = () => { return getTimeSinValue(510/2) }
const t7 = () => { return getTimeSinValue(1550/2) }
const t8 = () => { return getTimeSinValue(550/2) }

const r1 = () => { return getTimeSinValue(6671/2) }
const r2 = () => { return getTimeSinValue(360/2) }
const r3 = () => { return getTimeSinValue(350/2) }
const r4 = () => { return getTimeSinValue(570/2) };

const tt = () => { return getTimeSinValue(600/2) }

const reqt = () => { return getTimeSinValue(2960/2) }
const rest = () => { return getTimeSinValue(92343/2) }

function getAjax() {
    var uas = [
        "/api/ta/info",
        "/api/user/current",
        "/api/shops",
        "/collect",
        "/api/items"
    ];
    var index = globalIndex++ % uas.length;
    if (globalIndex > 10000) {
        globalIndex = 0;
    }
    return uas[index];
}



function getPath() {
    var uas = [
        "/",
        "/items/110800900000008",
        "/api/extend/recommend/findRecommendById",
        "/api/frontCategories/childrenTree",
        "/api/item/render/dynamic"
    ];
    var index = globalIndex++ % uas.length;
    if (globalIndex > 10000) {
        globalIndex = 0;
    }
    return uas[index];
}

function getTimeingCollect() {
    var rts = util.format(rtStr, config.endpointURL,
        r1().toString(36), r2().toString(36), r3().toString(36), r4().toString(36),
        r1().toString(36), r2().toString(36), r3().toString(36), r4().toString(36),
        r1().toString(36), r2().toString(36), r3().toString(36), r4().toString(36),
        r1().toString(36), r2().toString(36), r3().toString(36), r4().toString(36));
    return util.format(timingCollect, urlencode(t1().toString(36) + ',' + t2().toString(36) + ',' + t3().toString(36) + ',' + t4().toString(36) + ',' + t5().toString(36) + ',' + t6().toString(36) + ',' + t7().toString(36) + ',' + t8().toString(36)),
        urlencode(rts), urlencode(getPath()), config.domain, urlencode(getUA()), getUUID(), getTime(), config.domain);
}

function getErrorCollect() {
    return util.format(errCollect, urlencode(config.endpointURL), urlencode(config.endpointURL), urlencode(config.endpointURL), urlencode(config.endpointURL), urlencode(config.endpointURL), urlencode(config.endpointURL),
        urlencode(getPath()), config.domain, urlencode(getUA()), getUUID(), getTime(), config.domain);
}

function getRequestCollect() {
    return util.format(requestCollect, tt().toString(), urlencode(getAjax()), urlencode(getPath()), config.domain, urlencode(getUA()), getUUID(), getTime(), config.domain);
}

function getAppReqCollect() {
    return util.format(appReqCollect, getTime(), config.domain, reqt(), rest(), tt(), config.domain, getUUID());
}

function getAppTimeCollect() {
    return util.format(appTimeCollect, getTime(), config.domain, urlencode(getPath()), tt(), getUUID());
}

function getHeaders() {
    var ips = [
        "1.198.160.1",
        "14.104.188.1",
        "14.214.71.1",
        "27.28.252.1",
        "27.155.94.1",
        "27.207.210.1",
        "36.22.34.1",
        "36.22.57.1",
        "36.32.14.1",
        "36.192.202.192"
    ];
    var index = globalIndex++ % ips.length;
    if (globalIndex > 10000) {
        globalIndex = 0;
    }
    return {
        "x-forwarded-for": ips[index]
    };
}

function doTest(seq) {
    var formData = {
        data: getTimeingCollect(),
        cid: getUUID(),
        ak: config.tk
    }
    request.post({ url: config.collectURL, family: 4, headers: getHeaders() }).form(formData).on('response', function (resp) {
        log.info("timing collect code:" + resp.statusCode);
    });
    formData = {
        data: getRequestCollect(),
        cid: getUUID(),
        ak: config.tk
    }
    request.post({ url: config.collectURL, family: 4, headers: getHeaders() }).form(formData).on('response', function (resp) {
        log.info("req collect code:" + resp.statusCode);
    });
    formData = {
        data: getAppReqCollect(),
        cid: getUUID(),
        ak: config.tk
    }
    request.post({ url: config.collectURL, family: 4, headers: getHeaders() }).form(formData).on('response', function (resp) {
        log.info("app req collect code:" + resp.statusCode);
    });
    formData = {
        data: getAppTimeCollect(),
        cid: getUUID(),
        ak: config.tk
    }
    request.post({ url: config.collectURL, family: 4, headers: getHeaders() }).form(formData).on('response', function (resp) {
        log.info("app time collect code:" + resp.statusCode);
    });
    var index = Math.floor(globalIndex++ % 31);
    switch (index) {
        case 0:
            request.get({ url: config.endpointURL + "/items/110800900000008", famil: 4 }).on('response', function (resp) {
                log.info("request item code:" + resp.statusCode);
            });
            break;
        case 1:
            request.get({ url: config.endpointURL + "/api/extend/recommend/findRecommendById?recommendId=4&itemSize=6", family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request recommend code:" + resp.statusCode);
            });
            break;
        case 2:
            request.get({ url: config.endpointURL + "/api/item/render/dynamic?skuId=110800900000008&provinceId=110000&cityId=110100&regionId=110101", family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request sku code:" + resp.statusCode);
            });
            break;
        case 3:
            request.get({ url: config.endpointURL + "/api/item/mall/suggest?keyword=iphone", family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request sug code:" + resp.statusCode);
            });
            break;
        case 4:
            request.get({ url: config.endpointURL + "/api/frontCategories/childrenTree?ids=214%2C35%2C111%2C451%2C120%2C322%2C5%2C374", family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request category code:" + resp.statusCode);
            });
            break;
        case 5:
            request.get({ url: config.endpointURL + "/api/user/web/current-user", family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request user code:" + resp.statusCode);
            });
            break;
        case 6:
            request.get({ url: config.endpointURL + "/api/item/shop-items?itemIds=110800900000008,110800900000009,110800900000010,110800900000011,110800900000012,110800900000013&promotionActive=false", family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request shop-items code:" + resp.statusCode);
            });
            break;
        case 7:
            request.post({ url: config.endpointURL + "/api/user/web/login/buyer/identify", family: 4, json: { "password": "a9491db6006e3f3039702fe1573e159f061c888c", "identify": "18888888888" } }).on('response', function (resp) {
                var setcookie = resp.headers['set-cookie'];
                if (setcookie != null && setcookie.length > 0) {
                    var cookie = setcookie[0].replace(/;.*/, '');
                    request.post({
                        url: config.endpointURL + "/api/trade/buy/render-order",
                        json: { "deviceSource": "PC", "orderSource": "cart.default", "orderLineList": [{ "itemId": 110800900000003, "skuId": 110800900000003, "quantity": 2 }, { "itemId": 110800900000002, "skuId": 110800900000002, "quantity": 1 }, { "itemId": 110800900000010, "skuId": 110800900000010, "quantity": 1 }, { "itemId": 110800900000026, "skuId": 110800900000028, "quantity": 1 }, { "itemId": 110800900000008, "skuId": 110800900000008, "quantity": 1 }, { "itemId": 110800900000012, "skuId": 110800900000012, "quantity": 1 }, { "itemId": 110800900000032, "skuId": 110800900000034, "quantity": 1 }], "divisionIds": "110000,110100,110101", "addressId": null, "platformActivityId": null, "benefitId": null },
                        family: 4,
                        headers: { cookie: cookie },
                    }).on('response', function (nresp) {
                        log.info("request trade code:" + nresp.statusCode);
                    })
                }
            })
            break;
        default:
            request.get({ url: config.endpointURL, family: 4, headers: getHeaders() }).on('response', function (resp) {
                log.info("request index code:" + resp.statusCode);
            });
    }
}

module.exports = run = function () {
    app.listen(3000, () => log.info('apm-demo-ui listening on port 3000!'));
    // log.info("cc" + getTimeingCollect());
    // log.info("cc" + getErrorCollect());
    // log.info("cc" + getRequestCollect());
    // log.info("cc" + getAppReqCollect());
    // log.info("cc" + getAppTimeCollect());
    if (config.endpointURL != "") {
        if (config.testMode == "on") {
            everyMinute(() => {
                var total = getTimeSinValue(25);
                for (var i = 0; i < total; i++) {
                    doTest(i);
                }
            });
        } else {
            everySecond(() => {
                try {
                    var total = indexPoisson();
                    for (var i = 0; i < total; i++) {
                        doTest();
                    }

                    total = indexPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL, family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request index code:" + resp.statusCode);
                        });
                        request.get({ url: config.endpointURL + "/items/110800900000008", famil: 4 }).on('response', function (resp) {
                            log.info("request item code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL + "/api/extend/recommend/findRecommendById?recommendId=4&itemSize=6", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request recommend code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL + "/api/item/render/dynamic?skuId=110800900000008&provinceId=110000&cityId=110100&regionId=110101", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request sku code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL + "/api/item/mall/suggest?keyword=iphone", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request sug code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL + "/api/frontCategories/childrenTree?ids=214%2C35%2C111%2C451%2C120%2C322%2C5%2C374", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request category code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL + "/api/user/web/current-user", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request user code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    for (var i = 0; i < total; i++) {

                        request.get({ url: config.endpointURL + "/api/item/shop-items?itemIds=110800900000008,110800900000009,110800900000010,110800900000011,110800900000012,110800900000013&promotionActive=false", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request shop-items code:" + resp.statusCode);
                        });
                    }
                    total = otherPoisson();
                    request.post({ url: config.endpointURL + "/api/user/web/login/buyer/identify", family: 4, json: { "password": "a9491db6006e3f3039702fe1573e159f061c888c", "identify": "18888888888" } }).on('response', function (resp) {
                        var setcookie = resp.headers['set-cookie'];
                        if (setcookie != null && setcookie.length > 0) {
                            var cookie = setcookie[0].replace(/;.*/, '');
                            for (var i = 0; i < total; i++) {
                                request.post({
                                    url: config.endpointURL + "/api/trade/buy/render-order",
                                    json: { "deviceSource": "PC", "orderSource": "cart.default", "orderLineList": [{ "itemId": 110800900000003, "skuId": 110800900000003, "quantity": 2 }, { "itemId": 110800900000002, "skuId": 110800900000002, "quantity": 1 }, { "itemId": 110800900000010, "skuId": 110800900000010, "quantity": 1 }, { "itemId": 110800900000026, "skuId": 110800900000028, "quantity": 1 }, { "itemId": 110800900000008, "skuId": 110800900000008, "quantity": 1 }, { "itemId": 110800900000012, "skuId": 110800900000012, "quantity": 1 }, { "itemId": 110800900000032, "skuId": 110800900000034, "quantity": 1 }], "divisionIds": "110000,110100,110101", "addressId": null, "platformActivityId": null, "benefitId": null },
                                    family: 4,
                                    headers: { cookie: cookie },
                                }).on('response', function (nresp) {
                                    log.info("request trade code:" + nresp.statusCode);
                                })
                            }
                        }
                    })
                    for (var i = 0; i < total; i++) {
                        request.get({ url: config.endpointURL + "/api/item/shop-items?itemIds=110800900000008,110800900000009,110800900000010,110800900000011,110800900000012,110800900000013&promotionActive=false", family: 4, headers: getHeaders() }).on('response', function (resp) {
                            log.info("request shop-items code:" + resp.statusCode);
                        });
                    }

                } catch (e) {
                    log.info('Error caught: ' + e);
                }
            });
        }
        everyMinute(() => {
            try {
                var total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/exception/NullPointer", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request null ptr exception code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {
                    var formData = {
                        data: getErrorCollect(),
                        cid: getUUID(),
                        ak: config.tk
                    }
                    request.post({ url: config.collectURL, family: 4, headers: getHeaders() }).form(formData).on('response', function (resp) {
                        log.info("error collect code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/tick?count=5", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request slow http code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/500", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request server error code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/400", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request client error code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/tick?count=5", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request slow http code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/sleep?seconds=3", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request other slow http code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/mysql/sleep?seconds=5", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request slow mysql code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/redis/sleep?seconds=2", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request slow redis code:" + resp.statusCode);
                    });
                }
                total = otherPoisson();
                for (var i = 0; i < total; i++) {

                    request.get({ url: config.apiURL + "/api/redis/wait?seconds=2", family: 4, headers: getHeaders() }).on('response', function (resp) {
                        log.info("request other slow redis code:" + resp.statusCode);
                    });
                }
            } catch (e) {
                log.info('Error caught: ' + e);
            }
        });
    }
}