var objBox = null;
var serverURL = "../";
var sql_date = 'YYYY-MM-DD';

var inp = "";
var doc_no = "";
var ic_code = "";
var qty = "";
var whcode = "";
var shelfcode = "";
var value = "";

$(function () {
    $("#from_date").val(_formatDate(new Date()));
    $("#to_date").val(_formatDate(new Date()));
    __getItemCode();
    __getGroup();
    objBox = $('#root').clone();
    //  $('#stage').html('');
    $('#load').hide();





});



$('#resultx').delegate('.oldlot', 'change', function () {
    var data = $(this);
    var ic_code = data.attr('code');
    var lot = data.val();

    if (lot == 'all') {
        $('.qtyold_' + ic_code).hide();
        $('.qtyold_' + ic_code + '_' + 'all').show();

    } else {
        $('.qtyold_' + ic_code).hide();
        $('.qtyold_' + ic_code + '_' + 'all').hide();
        $('.qtyold_' + ic_code + '_' + lot).show();
    }


});

$('#btn_process').on('click', function () {
    getDatax();
});



function getDatax() {
    $('#resultx').html('');
    var data = {};
    if ($('#from_date').val() == "") {
        $('#alertmsg').show();
        return;
    } else {
        $('#alertmsg').hide();
    }
    $('#load').show();

    data['from_date'] = $('#from_date').val();
    data['to_date'] = $('#to_date').val();
    data['from_group'] = $('#from_group').val();
    data['to_group'] = $('#to_group').val();
    data['from_item'] = $('#from_item').val();
    data['to_item'] = $('#to_item').val();

    $.ajax({
        url: serverURL + 'dimension-list2',
        method: 'GET',
        cache: false,
        data: data,
        success: function (res) {
            console.log(res);
            $('#load').hide();
           
                var _html = "";

              
                $("#resultxz").html(res);


            
        },
        error: function (res) {
            swal({
                title: 'เกิดข้อผิดพลาด',
                text: res.responseText,
                type: "error",
                timer: 2000,
                showConfirmButton: false});
        },
        complete: function () {
            $('#load').hide();
        }
    });
}




function getObjects(obj, key, val) {
    var objects = [];
    for (var i in obj) {
        if (!obj.hasOwnProperty(i))
            continue;
        if (typeof obj[i] == 'object') {
            objects = objects.concat(getObjects(obj[i], key, val));
        } else if (i == key && obj[key] == val) {
            objects.push(obj);
        }
    }
    return objects;
}

function getMax(numArray) {
    return Math.max.apply(null, numArray);
}
function getMin(numArray) {
    return Math.min.apply(null, numArray);
}

function detail(e, id, code) {



    if ($("#R" + id).html() != "")
    {
        $("#R" + id).empty();
        $("#RR" + id).hide();
    } else {



        $.ajax({
            url: serverURL + 'balance-detail',
            method: 'POST',
            cache: false,
            data: {code: code, mode: "2"},
            success: function (res) {

                console.log(res);
                $("#R" + id).html(res);
                $("#RR" + id).show();
            },
            error: function () {

            }

        });
    }
}

function __getItemCode() {
    $.ajax({
        url: serverURL + 'get-cust',
        method: 'GET',
        dataType: 'json',
        success: function (response) {
            // console.log(response)
            if (response.success) {
                var data = "<option value='noting'>เลือกรหัสลูกค้า</option>";
                $.each(response.data, function (key, obj) {
                    data += "<option value='" + obj['code'] + "'>" + obj['code'] + ' ~ ' + obj['name_1'] + "</option>";

                });
                $('.item_select').html(data);

                $(".item_select").select2();
            }

        },
        error: function () {

        }

    });
}


function __getGroup() {
    $.ajax({
        url: serverURL + 'get-group',
        method: 'GET',
        dataType: 'json',
        success: function (response) {
            // console.log(response)
            if (response.success) {
                var data = "<option value='noting'>เลือกกลุ่มสินค้า</option>";
                $.each(response.data, function (key, obj) {
                    data += "<option value='" + obj['code'] + "'>" + obj['code'] + ' ~ ' + obj['name_1'] + "</option>";

                });
                $('.group_select').html(data);

                $(".group_select").select2();
            }

        },
        error: function () {

        }

    });
}


function numberFormat(number, fix) {
    number = parseFloat(number);
    number = isNaN(number) ? 0.00 : number;
    var tmp = 0.00;
    if (fix > 0) {
        tmp = number.toFixed(fix).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
    } else {
        number = Math.round(number);
        tmp = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
    return tmp == 0 ? '' : tmp;
}

function numberFormat2(number, fix) {
    number = parseFloat(number);
    number = isNaN(number) ? 0.00 : number;
    var tmp = 0.00;
    if (fix > 0) {
        tmp = number.toFixed(fix).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
    } else {
        number = Math.round(number);
        tmp = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
    return tmp;
}

var id_use = [];
function genid() {
    var $gen_length = 4;
    var $lip_text = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var $lip_length = $lip_text.length;

    var $gen_text = '';
    for (var $i = 0; $i < $gen_length; $i++) {
        $gen_text += $lip_text.charAt(Math.floor(Math.random() * ($lip_length - 0 + 1)) + 0);
    }

    if (!checkTempId($gen_text)) {
        $gen_text = genid();
    }
    id_use.push($gen_text);
    return $gen_text;
}

function checkTempId(id_new) {
    for (var id_temp in id_use) {
        if (id_temp == id_new) {
            return false;
        }
    }
    return true;
}

function _formatDate(date) {
    var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}