var serverURL = "../";
var userbranch = '';

function uuidv4() {
    return 'xxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function formatNumber(num) {
    return num.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}
function removeCommas(str) {

    if (str != '0') {
        while (str.search(",") >= 0) {
            str = (str + "").replace(',', '');
        }
    }
    return str;
}



var item_detail = [
    {
        item_code: '',
        item_name: '',
        unit_code: '',
        unit_name: '',
        stand_value: '0',
        divide_value: '0',
        purchases: 0.00,
        ratio: '0',
        cost_qty: 0.00,
        cost_weight: 0.00,
        cost_sum_weight: 0.00,
        cost_factory_price: 0.00,
        cost_sum_cost: 0.00,
        exp_discount: 0.00,
        exp_car: 0.00,
        exp_other: 0.00,
        exp_doc: 0.00,
        exp_tax: 0.00,
        exp_other2: 0.00,
        exp_each: 0.00,
        sale_sum_lao_cost: 0.00,
        sale_sum_cost: 0.00,
        sale_price: 0.00,
        sale_sum_amount: 0.00,
        sale_profit: 0.00,
        sale_percent_profit: 0.00,
        selling_price: 0.00,
        sale_discount: 0.00
    }
]

$(document).ready(function () {
    $.ajax({
        url: serverURL + 'getUserStorage',
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log('checkstore success')
            _getListData('');
        },
        error: function (res) {
            console.log(res)
        },
    });
    $('.btn-add').on('click', function () {

        window.location.href = "form.jsp";
    });
    $('#btn-search').on('click', function () {
        var search_name = $('#search_name').val()
        _getListData(search_name);
    });

    $('#search_name').on('keyup', function (e) {
        var search_name = $('#search_name').val()
        if (e.keyCode === 13) {
            _getListData(search_name);
        }

    });
    $(document).delegate('.print_doc', 'click', function (event) {
        var code = $(this).attr('data-docno')
        var status = $(this).attr('data-status')


        window.open('print.jsp' + "?docno=" + code, "_blank");


    });

    $(document).delegate('.send_approve', 'click', function (event) {
        var code = $(this).attr('data-docno')
        var status = $(this).attr('data-status')
        window.location.href = "pack.jsp?d=" + code + "&s=" + status

    });
    $(document).delegate('.cancel_doc', 'click', function (event) {
        var code = $(this).attr('data-docno')
        swal({
            title: "ยืนยันการทำงาน",
            text: "ไม่อนุมัติเอกสาร " + code + " ใช่หรือไม่",
            icon: "warning",
            buttons: ["ปิด", "ตกลง"],
            dangerMode: true,
        })
                .then((willDelete) => {
                    if (willDelete) {
                        $.ajax({
                            url: serverURL + 'cancelDocSend',
                            method: 'POST',
                            data: {doc_no: code},
                            success: function (res) {
                                swal("การทำรายการทำเสร็จ", {
                                    icon: "success",
                                });
                                _getListData('');
                            },
                            error: function (res) {
                                console.log(res)
                            },
                        });
                    }
                });
    });
    $(document).delegate('.show_detail', 'click', function (event) {
        var code = $(this).attr('data-docno')
        var status = $(this).attr('data-status')

        window.location.href = "formpack.jsp?d=" + code + "&s=" + status

    });

});

function _getListData(data) {
    //var branch = $('#user_branch').val();
    $.ajax({
        url: serverURL + 'getWaitWhList?search=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            _showListDetail(res)

        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _showListDetail(data) {
    var html = '';
    for (var i = 0; i < data.length; i++) {

        var statusText = "";
        var color = "blue";
        html += `<div id="accordion">
                    <div class="card">
                        <div class="card-header" id="headingOne">
                          <h5 class="mb-0">
                            <button class="btn btn-link" data-toggle="collapse" data-target="#collapse_${i}" aria-expanded="true" aria-controls="collapseOne">
                             สาขา: ${data[i].branch_name}(${data[i].branch_code})  คลัง: ${data[i].wh_name}(${data[i].wh_code})  ที่เก็บ: ${data[i].shelf_name}(${data[i].shelf_code})
                            </button>
                          </h5>
                        </div>

                        <div id="collapse_${i}" class="collapse " aria-labelledby="heading_${i}" data-parent="#accordion">
                          <div class="card-body">
                          <div class="table-responsive">
                           <table class="table table-striped">
                            <thead>
                                <tr>
        <th>#</th>
                                    <th>รหัสสินค้า</th>
                                    <th>ชื่อสินค้า</th>
                                    <th>หน่วยนับ</th>
                                    <th>คงเหลือ</th>
                                    <th>จำนวนขอ</th>
                                </tr>
                            </thead>
                            <tbody>`
        data[i].detail.forEach(function (ele, index) {
            html += ` <tr>
                        <td>${index + 1}</td>
            <td>${ele.item_code}</td>
            <td>${ele.item_name}</td>
            <td>${ele.unit_code}</td>
            <td>${formatNumber(parseFloat(ele.balance))}</td>
            <td>${ele.qty}</td>
</tr>`;
        });

        html += `         </tbody> </table></div></div>
                        </div>
                      </div>
                 </div>`;
    }
    $('#show_list_detail').html(html);
}

function delay(callback, ms) {
    var timer = 0;
    return function () {
        var context = this, args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () {
            callback.apply(context, args);
        }, ms || 0);
    };
}



function _searchItem() {
    var search_name = $('#search_name').val()
    console.log(search_name)

    $.ajax({
        url: serverURL + 'search_item?name=' + search_name,
        method: 'GET',
        cache: false,
        success: function (res) {
            $('#list_search_item').html(res)

        },
        error: function (res) {
            console.log(res)
        },
    });
}
function _addLine() {
    item_detail.push({
        item_code: '',
        item_name: '',
        unit_code: '',
        purchases: 0.00,
        cost_qty: 0.00,
        cost_weight: 0.00,
        cost_sum_weight: 0.00,
        cost_factory_price: 0.00,
        cost_sum_cost: 0.00,
        exp_discount: 0.00,
        exp_car: 0.00,
        exp_other: 0.00,
        exp_doc: 0.00,
        exp_tax: 0.00,
        exp_other2: 0.00,
        exp_each: 0.00,
        sale_sum_lao_cost: 0.00,
        sale_sum_cost: 0.00,
        sale_price: 0.00,
        sale_sum_amount: 0.00,
        sale_profit: 0.00,
        sale_percent_profit: 0.00,
        selling_price: 0.00,
        sale_discount: 0.00
    });
    _displayTable();
}



function delLine(data) {
    console.log(data)
    item_detail.splice(data, 1);
    _displayTable()

}

