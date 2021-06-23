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
    data['from_group'] = $('#from_group').val();
    data['to_group'] = $('#to_group').val();
    data['from_item'] = $('#from_item').val();
    data['to_item'] = $('#to_item').val();
    
    
    $.ajax({
        url: serverURL + 'dimension-list',
        method: 'GET',
        cache: false,
        data: data,
        success: function (res) {
           console.log(res);
            $('#load').hide();
            if (res.success) {
      
                  var old_group="";
                  var old_group2="";
                  var _html = "";
                
                  var cnxz=0;
                
                    var arr = [];
                    for (var prop in res.data) {
                        arr.push(res.data[prop].dimension_1);
                    }
            
                  
                
                  $.each(res.data, function (key, obj) {
                      var countz = 0;
                      $.each(res.data, function (key, objx) {
                          
                            if(obj.dimension_1 != old_group2){
                              
                               countz++;
                            }
                               old_group2 = obj.dimension_1;
                     
                      });
                   
                  
                      if(obj.dimension_1 != old_group){
                         
                          old_group = obj.dimension_1;
                          if(key!=0){
                            _html +="<tr style='background-color:#FFCCBC'>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="<td><b>รวม</b></td>";
                            _html +="<td style='text-align:right'><b>"+numberFormat2(cnxz,2)+"</b></td>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="</tr>";
                            cnxz=0;
                          }
                          
                            _html +="<tr>";
                            _html +="<td><b>"+obj.dimension_1+"</b></td>";
                            _html +="<td><b>"+obj.group_name+"</b></td>";
                            _html +="<td>"+obj.ic_code+"</td>";
                            _html +="<td>"+obj.ic_name+"</td>";
                            _html +="<td style='text-align:right'>"+numberFormat2(obj.balance_qty,2)+"</td>";
                            _html +="<td>"+obj.wh_code+"/"+obj.wh_name+"</td>";
                            _html +="<td>"+obj.shelf_code+"/"+obj.shelf_name+"</td>";
                            _html +="</tr>";
                          
                      }else{
                         
                           old_group = obj.dimension_1;
                            _html +="<tr>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="<td>"+obj.ic_code+"</td>";
                            _html +="<td>"+obj.ic_name+"</td>";
                            _html +="<td style='text-align:right'>"+numberFormat2(obj.balance_qty,2)+"</td>";
                            _html +="<td>"+obj.wh_code+"/"+obj.wh_name+"</td>";
                            _html +="<td>"+obj.shelf_code+"/"+obj.shelf_name+"</td>";
                            _html +="</tr>";
                      }
                         cnxz += obj.balance_qty;
                         console.log(key+" - "+res.data.length);
                       if(key==(res.data.length-1)){
                            _html +="<tr style='background-color:#FFCCBC'>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="<td><b>รวม</b></td>";
                            _html +="<td style='text-align:right'><b>"+numberFormat2(cnxz,2)+"</b></td>";
                            _html +="<td></td>";
                            _html +="<td></td>";
                            _html +="</tr>";
                          }
                      
                        console.log(cnxz);
              
                  });
                  
                  $("#resultx").html(_html);
                
                
            }
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
        if (!obj.hasOwnProperty(i)) continue;
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
        url: serverURL + 'get-item',
        method: 'GET',
        dataType: 'json',
        success: function (response) {
           // console.log(response)
            if (response.success) {
                var data = "<option value='noting'>เลือกรหัสสินค้า</option>";
                $.each(response.data, function (key, obj) {
                    data += "<option value='" + obj['item_code'] + "'>" + obj['item_code'] + ' ~ ' + obj['item_name'] + "</option>";

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