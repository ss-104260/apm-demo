const express = require('express');
var engine = require('ejs-locals');
const path = require('path');
var request = require('request');
const config = require(path.join(__dirname, 'config'));
const app = express();

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
app.get('/',function(req, res, next) {
    res.render('index', pageData);
});
app.get('/:page',function(req, res, next) {
    res.render(req.params.page, pageData);
});

function getClientIP(req) {
    return req.headers['x-forwarded-for'] ||
        req.connection.remoteAddress ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;
};
app.all('/api/*',function(req, res, next) {
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
    if(Buffer.isBuffer(req.body)) {
        options.body = req.body;
    }
    request(options, function(error, response, body) {
        if (error) {
            res.send({ success: false, error: error });
            return;
        }
        res.status(response.statusCode).send(response.body);
    });
});
app.listen(3000, () => console.log('apm-demo-ui listening on port 3000!'))