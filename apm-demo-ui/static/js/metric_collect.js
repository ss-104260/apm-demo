$(function () {
    $(".btn-now").click(function (){
        $(".text-now").val(new Date().getTime());
    });
    $(".btn-add").click(function () {
         var row = $('                            <tr>\n' +
            '                                <td><input type="text" name="key" class="form-control"></td>\n' +
            '                                <td class="eq">=</td>\n' +
            '                                <td><input type="text" name="value" class="form-control"></td>\n' +
            '                                <td><button type="button" class="btn btn-default center-block btn-remove">-</button></td>\n' +
            '                            </tr>');
        row.find(".btn-remove").click(function () {
            $(this).parent().parent().remove();
        })
        $(this).parent().parent().parent().parent().find("tbody").append(row);
    });
    var request = new Request("http://localhost:7076");
    $(".btn-push").click(function () {
        // ...
        request.do("/collect")
    })
})
