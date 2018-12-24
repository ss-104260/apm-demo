
function showOptions(elem, options) {
    var row, num = 0;
    for(var i = 0; i < options.length; i++) {
        var opt = options[i];
        if("string" == typeof opt.name) {
            if(Array.isArray(opt.data)) {
                var select = $('<select class="form-control" name="' + (opt.id?opt.id:"") + '"></select>');
                var html = $('<div class="form-group col-md-6">\n' +
                    '    <label class="col-sm-4 control-label">' + $("<div/>").text(opt.name).html() + '</label>\n' +
                    '    <div class="col-sm-8 input-box"></div>\n' +
                    '</div>');
                html.find(".input-box").append(select);
                var data = opt.data;
                for(var d=0; d < data.length; d++) {
                    select.append($("<option></option>").val(data[d]).text(data[d]));
                }
            } else {
                var input = $('<input type="text" class="form-control" name="' + (opt.id?opt.id:"") + '">');
                var html = $('<div class="form-group col-md-6">\n' +
                    '    <label class="col-sm-4 control-label">' + $("<div/>").text(opt.name).html() + '</label>\n' +
                    '    <div class="col-sm-8 input-box"></div>\n' +
                    '</div>');
                input.val($("<div/>").text(opt.data).html());
                html.find(".input-box").append(input);
            }
            if(num++ % 2 == 0) {
                row = $('<div class="row"></div>');
                elem.append(row);
            }
            row.append(html);
        } else if(Array.isArray(opt.name)) {
            var data = opt;
            var pelem = null;
            var selected = [];
            for(var n=0, last=opt.name.length-1; n < opt.name.length; n++) {
                var select = $('<select class="form-control" name="' + ((opt.id && opt.id[n])? opt.id[n] : "") + '"></select>');
                var html = $('<div class="form-group col-md-6">\n' +
                    '    <label class="col-sm-4 control-label">' + $("<div/>").text(opt.name[n]).html() + '</label>\n' +
                    '    <div class="col-sm-8 input-box"></div>\n' +
                    '</div>');
                html.find(".input-box").append(select);
                if(num++ % 2 == 0) {
                    row = $('<div class="row"></div>');
                    elem.append(row);
                }
                row.append(html);
                var val = { value: "" };
                data = data.data;
                if(data && data.length > 0) {
                    for(var d=0; d < data.length; d++) {
                        val = data[d];
                        if(n >= last) val = { value: val };
                        select.append($("<option></option>").val(val.value).text(val.value));
                    }
                    val = data[0];
                    if(n >= last)  val = { value: val };
                    select.val(val.value);
                }
                selected.push(0);
                if(pelem) {
                    (function(pelem, index, select, opt, last, selected) {
                        pelem.change(function () {
                            var data = opt.data;
                            var key = $(this).val();
                            if(index > 0) {
                                for(var i=0; i < index; i ++) {
                                    data = data[selected[i]];
                                }
                                data = data.data;
                            }
                            select.html("");
                            var find = false, i =0;
                            for(; i<data.length; i ++) {
                                if(index < last) {
                                    if(key == data[i].value) {
                                        find = true; break;
                                    }
                                } else if(key == data[i]) {
                                    find = true; break;
                                }
                            }
                            if(find) {
                                selected[index] = i;
                                data = data[i].data;
                                for(var i=0; i<data.length; i++) {
                                    if(index+1 < last) {
                                        select.append($("<option></option>").val(data[i].value).text(data[i].value));
                                    } else {
                                        val.value = [data[i]];
                                        select.append($("<option></option>").val(data[i]).text(data[i]));
                                    }
                                }
                            }
                            select.change();
                        });
                    })(pelem, n - 1, select, opt, last, selected);
                }
                data = val;
                pelem = select;
            }
        }
    }
}