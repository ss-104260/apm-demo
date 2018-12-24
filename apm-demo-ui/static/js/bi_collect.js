var options = [
    {
        name: [ "浏览器", "浏览器版本" ],
        id: [ "browser", "broswer_version" ],
        data: [
            {
                value: "Chrome",
                data: [
                    "69.8.1500",
                    "69.9.1470",
                    "70.0.1538",
                    "70.0.3538"
                ]
            },
            {
                value: "FireFox",
                data: [
                    "42.1",
                    "43.2",
                    "44.5",
                    "46.6"
                ]
            }
        ]
    },
    {
        name: [ "操作系统", "系统版本" ],
        id: [ "os", "os_version" ],
        data: [
            {
                value: "Max OS",
                data: [
                    "10.11.0",
                    "10.12.6",
                    "10.13.5",
                    "10.14.0"
                ]
            },
            {
                value: "Windows",
                data: [
                    "7",
                    "8",
                    "9",
                    "10"
                ]
            },
        ]
    },
    {
        name: [ "国家", "省", "城市" ],
        id: [ "country", "province", "city" ],
        data: [
            {
                value: "中国",
                data: [
                    {
                        value: "浙江",
                        data: [
                            "杭州",
                            "温州",
                            "金华",
                        ]
                    },
                    {
                        value: "广东省",
                        data: [
                            "深圳"
                        ]
                    }
                ]
            }
        ]
    },
    {
        name:  "供应商",
        id: "isp",
        data: [
            "电信",
            "移动",
            "网通"
        ]
    },

];

var options_timing = [
    {
        name: "loadTime（ms）",
        data: 10,
    },
    {
        name: "readyStart（ms）",
        data: 10,
    },
    {
        name: "domReadyTime（ms）",
        data: 10,
    },
    {
        name: "scriptExecuteTime（ms）",
        data: 10,
    },
    {
        name: "requestTime（ms）",
        data: 10,
    },
    {
        name: "responseTime（ms）",
        data: 10,
    },
    {
        name: "initDomTreeTime（ms）",
        data: 10,
    },
    {
        name: "loadEventTime（ms）",
        data: 10,
    },
    {
        name: "unloadEventTime（ms）",
        data: 10,
    },
    {
        name: "appCacheTime（ms）",
        data: 10,
    },
    {
        name: "connectTime（ms）",
        data: 10,
    },
    {
        name: "lookupDomainTime（ms）",
        data: 10,
    },
    {
        name: "redirectTime（ms）",
        data: 10,
    }
]
var options_request = [
    {
        name: "tt（ms）",
        data: 12
    },
    {
        name: "req（kb）",
        data: 12
    },
    {
        name: "res（kb）",
        data: 12
    },
]

var options_error = [
    {
        name: "错误",
        data: [
            "script error 1",
            "script error 2",
            "script error 3",
            "script error 4",
            "script error 5"
        ]
    }
]

rt={"http://":{"pmp.terminus.io/":{"23.806ab6d2955f529a1873.chunk.js":"258,5b,26,n","6.806ab6d2955f529a1873.chunk.js":"258,6g,1o,m","styles.806ab6d2955f529a1873.c":{"ss":"21t,2c,g,3","hunk.js":"21t,f,b,4"},"app.806ab6d2955f529a1873.js":"21t,3w,v,e,e,,6,6,4","vendors~app.806ab6d2955f529a1873.c":{"ss":"21u,2u,m,a,9,,4,4,3","hunk.js":"21v,g6,x,d"},"ta":"31z,37,37,c,c,,1,1,1","commons.806ab6d2955f529a1873.chunk.js":"258,4p,1g,l","images/logo_reverse.png":"46c,1j,1i,2"},"at.alicdn.com/t/font_215482_drbuyml5cl.":{"css":"21z,13,d,5","js":"320,4k,1t,g,g,,6,6,4"},"static.terminus.io/ta.js":"358,b"}}

$(function () {
    $(".type-select input[type=radio]").click(function () {
        var opts = window["options_"+$(this).val()];
        var list = [];
        for(var i=0; i < options.length; i++) {
            list.push(options[i]);
        }
        for(var i=0; i < opts.length; i++) {
            list.push(opts[i]);
        }
        var elem = $("#option-box");
        $("#option-box").html("");
        showOptions(elem, list);
    });
    $(".type-select input[value=timing]").click();
    $(".btn-send").click(function () {
        $()
    })
})
