function Request(baseUrl) {
    baseUrl = baseUrl || "";
    this.do = function get(path, options) {
        options = options || {}
        options.method = options.method || "GET";
        $.ajax({
            url: baseUrl+path,
            type: options.method,
            data: options.data,
            success: options.success,
            error: options.error,
            complete: options.complete,
        });
    }
}

$(function () {
    function ReqBox() {
        var request = new Request("/api");
        function reqAndShow(path, data, method) {
            return function() {
                MessageBox("请求中...", "response");
                request.do(path, {
                    method: method,
                    data: data,
                    complete: function (resp) {
                        MessageBox("Status : " + resp.status + "\n" + resp.responseText, "response");
                    }
                })
            }
        }
        this.request = request.do;
        
        this.time = reqAndShow("/time");
        this.slow = reqAndShow("/tick", { count : 10 });
        this.sleep = reqAndShow("/sleep", { seconds : 15 });
        this.badRequest = reqAndShow("/400");
        this.errorReqeust = reqAndShow("/500");
        var bigData = "";
        for(var i=0; i< 5000; i++) {
            bigData += i;
        }
        this.bigSend = reqAndShow("/hole", { data: bigData }, "POST");
        this.bigRecv = reqAndShow("/falls", { count: 3000 });
        this.post = reqAndShow("/echo", { time: new Date().getTime() }, "POST");
        this.mysqlDBs = reqAndShow("/mysql/dbs");
        this.mysqlTables = reqAndShow("/mysql/tables");
        this.mysqlNow = reqAndShow("/mysql/now");
        this.mysqlUsers = reqAndShow("/mysql/users");
        this.mysqlSleep = function(seconds) {
            reqAndShow("/mysql/sleep", {seconds: seconds})();
        }
        this.redisSleep = function(seconds) {
            reqAndShow("/redis/sleep", {seconds: seconds})();
        }
        this.redisWait = function(seconds) {
            reqAndShow("/redis/wait", {seconds: seconds})();
        }
        this.redisNow = reqAndShow("/redis/now");
        this.addMetricMeta = function(data) {
            reqAndShow("/mysql/metric", { data: JSON.stringify(data) }, "POST")();
        }
        this.setMetricMeta = function(data) {
            reqAndShow("/mysql/metric/edit", { data: JSON.stringify(data) }, "PUT")();
        }
        this.getMetricMetaList = function(options) {
            request.do("/mysql/metrics", options);
        }
        this.getMetricMetaById = function (options) {
            request.do("/mysql/metrics/" + options.id, options);
        }
        this.dubboMysqlUsers = reqAndShow("/dubbo/mysql/users");
        this.dubboRedisGet = function (key) {
            reqAndShow("/dubbo/redis/get?key=" + encodeURIComponent(key))();
        }
    }
    window.ReqBox = new ReqBox();
});

