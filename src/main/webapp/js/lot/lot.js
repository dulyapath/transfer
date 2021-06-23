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
var search_arr=[];
$(function () {
    $("#from_date").val(_formatDate(new Date()));
    $("#to_date").val(_formatDate(new Date()));
    __getItemCode();
    objBox = $('#root').clone();
    //  $('#stage').html('');
    $('#load').hide();





});
$('#search_itemz').on('click',function (){
    search_arr=[];
    $('.id_check').prop('checked',false);
});

$('#btn_ok').on('click',function (){
//console.log(search_arr);

$('#item_code').val(search_arr);

$('.sh_modal').modal('hide');
});
$('.modal-content').delegate('.id_check', 'change', function () {
    
    var data=$(this);
    var code=data.val();
    if(this.checked){
        search_arr.push(code);
    }else{
        search_arr = jQuery.grep(search_arr, function(value) {
            return value != code;
          });
        
    }
  
    
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

$('#btn_sumbit').on('click', function () {
    getDatax();
});
$('#btn_codesearch').on('click', function () {
    getDatax();
});


function getDatax() {
    $('#resultx').html('');
    var data = {};
    if ($('#from_date').val() == "" || $('#to_date').val() == "") {
        $('#alertmsg').show();
        return;
    } else {
        $('#alertmsg').hide();
    }
    $('#load').show();
    var item_codez="";
    var splitdata = $('#item_code').val().split(',');
    var action="0";
    if(splitdata.length>1){
        for(var i=0;i<splitdata.length;i++){
            if(i==0){
               item_codez +=  "'"+splitdata[i]+"'";
            }else{
              item_codez += ",'"+splitdata[i]+"'";
            }
           
        }
        
        action = "1";
    }else{
        action = "0";
        item_codez = splitdata[0];
    }
    console.log(item_codez);
    
    data['item_code'] = item_codez;
    data['action'] = action;
    data['from_date'] = $('#from_date').val();
    data['to_date'] = $('#to_date').val();
    $.ajax({
        url: serverURL + 'lot-list',
        method: 'GET',
        cache: false,
        data: data,
        success: function (res) {
           
            $('#load').hide();
            if (res.success) {
                $('#resultx').html(res.data);
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
        url: serverURL + 'get-item2',
        method: 'GET',
        dataType: 'json',
        success: function (response) {
            if (response.success) {
              $("#showdataz").html(response.data);
            }
             $('#datatables').DataTable({
                "language": {
                    "lengthMenu": "แสดง _MENU_ รายการ",
                    "zeroRecords": "ไม่พบข้อมูล",
                    "info": "แสดงหน้า _PAGE_ / _PAGES_",
                    "infoEmpty": "ไม่พบข้อมูล",
                    "infoFiltered": "(จากทั้งหมด _MAX_ รายการ)",
                    "search":"ค้นหา"
                }
            });
       
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

    return [year].join('-');
}