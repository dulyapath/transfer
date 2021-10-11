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
    var currentdate = new Date();
    $('#search_from_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
    $('#search_to_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
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

    /*  $('#search_name').on('keyup', function (e) {
     var search_name = $('#search_name').val()
     if (e.keyCode === 13) {
     _getListData(search_name);
     }
     
     });*/
    $(document).delegate('.print_doc', 'click', function (event) {
        var code = $(this).attr('data-docno')
        var status = $(this).attr('data-status')
        var direct = $(this).attr('data-direct')
        if (status == '2') {
            if (direct == 1) {
                window.open('print.jsp' + "?docno=" + code, "_blank");
            } else {
                window.open('print.jsp' + "?docno=" + code, "_blank");
            }

        } else {
            window.open('../print/index.jsp' + "?docno=" + code, "_blank");
        }

    });

    $(document).delegate('.send_approve', 'click', function (event) {

        var status = $(this).attr('data-status')
        var code = $(this).attr('data-docno')
        var wid = $(this).attr('data-wid')
        swal({
            title: "ยืนยันการทำงาน",
            text: "ยกเลิกใบเบิก " + wid + " ใช่หรือไม่",
            icon: "warning",
            buttons: ["ปิด", "ตกลง"],
            dangerMode: true,
        })
                .then((willDelete) => {
                    if (willDelete) {
                        $.ajax({
                            url: serverURL + 'cancelDocSendDirect',
                            method: 'POST',
                            data: {doc_no: code,wid_doc:wid},
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
    
     $(document).delegate('.send_approve2', 'click', function (event) {

        var status = $(this).attr('data-status')
        var code = $(this).attr('data-docno')
        var wid = $(this).attr('data-wid')
        swal({
            title: "ยืนยันการทำงาน",
            text: "ยกเลิกใบเบิก " + wid + " ใช่หรือไม่",
            icon: "warning",
            buttons: ["ปิด", "ตกลง"],
            dangerMode: true,
        })
                .then((willDelete) => {
                    if (willDelete) {
                        $.ajax({
                            url: serverURL + 'cancelDocSendNm',
                            method: 'POST',
                            data: {doc_no: code,wid_doc:wid},
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
        if (status == '2') {
            window.location.href = "formpackshow.jsp?d=" + code + "&s=" + status
        } else {
            window.location.href = "form.jsp?d=" + code + "&s=" + status
        }
    });

});

function _getListData(data) {
    //var branch = $('#user_branch').val();
    var from_date = $('#search_from_date').val();
    var to_date = $('#search_to_date').val();
    $.ajax({
        url: serverURL + 'getReceiveDoc?fd=' + from_date + '&td=' + to_date + '&search=' + data,
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

        if (data[i].contactor == undefined) {
            data[i].contactor = '';
        }
        var statusText = "";
        var color = "blue";
        html += `<div class="col-12">`
        if (data[i].status == "0") {
            html += `<div class="card border-bottom-primary">`
            statusText = "รอส่ง"
            color = "blue";
        } else if (data[i].status == "1") {
            html += `<div class="card border-bottom-warning">`
            statusText = "รอเบิกออก"
            color = "orange";
        } else if (data[i].status == "2") {
            html += `<div class="card border-bottom-warning">`
            if (data[i].is_direct == '1') {
                statusText = "รอรับเข้า(ไม่มีใบขอเบิก)"
            } else {
                statusText = "รอรับเข้า"
            }

            color = "orange";
        } else if (data[i].status == "3") {
            html += `<div class="card border-bottom-success">`
            statusText = ""
            color = "info";
        } else if (data[i].status == "4") {
            html += `<div class="card border-bottom-success">`
            statusText = "รับสินค้าแล้ว"
            color = "green";
        }

        var doc_time_split = data[i].doc_time.split(':')
        var _doc_time = doc_time_split[0] + ":" + doc_time_split[1]
        html += `    <div class="card-body">
                <div class="row">
                    <div class="col-12">
                        <div class="text-header text-left">${data[i].doc_no} <span class="text-header text-right" style="color:${color}">${statusText}</span></div>
                    </div>

                </div>
                <div class="row" style="margin-top: 0.5rem;">
                    <div class="col-4">
                        <div class="text-body text-left">วันที่ : ${data[i].doc_date}</div>
                    </div>
                    <div class="col-4">
                        <div class="text-body text-left">เวลา : ${_doc_time}</div>
                    </div>
                    <div class="col-4">
                        <div class="text-body text-left">ผู้ขอ :  ${data[i].user_code}~${data[i].user_name}</div>
                    </div>
                </div>
                <div class="row" style="margin-top: 0.5rem;">
                    <div class="col-4">
                        <div class="text-body text-left">เบิกจาก สาขา :  ${data[i].branch_code}~${data[i].branch_name}</div>
                    </div>
                    <div class="col-4">
                        <div class="text-body text-left">คลัง : ${data[i].wh_code}~${data[i].wh_name}</div>
                    </div>
                    <div class="col-4">
                        <div class="text-body text-left">ที่เก็บ : ${data[i].shelf_code}~${data[i].shelf_name}</div>
                    </div>
                </div>
                <div class="row" style="margin-top: 0.5rem;">
                    <div class="col-4">
                        <div class="text-body text-left">รับเข้า สาขา : ${data[i].to_branch_code}~${data[i].to_branch_name}</div>
                    </div>
                    <div class="col-4">
                        <div class="text-body text-left">คลัง : ${data[i].to_wh_code}~ ${data[i].to_wh_name}</div>
                    </div>
                    <div class="col-4">
                        <div class="text-body text-left">ที่เก็บ :  ${data[i].to_shelf_code}~${data[i].to_shelf_name}</div>
                    </div>
                </div>
          <div class="row" style="margin-top: 0.5rem;">
           <div class="col-12">
                        <div class="text-body text-left">หมายเหตุ : ${data[i].remark}</div>
                    </div>
         </div>
                <div class="row" style="margin-top: 0.5rem;">`
        if (data[i].status == '4') {
            html += `   <div class="col-4">
                        <div class="text-body text-left">เลขที่ใบรับ : ${data[i].wid_doc}</div>
                    </div>`
        }
        html += `  
        
<div class="col-4">
                        <div class="text-body text-left">เลขที่ใบเบิก : ${data[i].wid_doc}</div>
                    </div>
        <div class="col-4">
                        <div class="text-body text-left">วันที่ : ${data[i].wid_date}</div>
                    </div>
 <div class="col-4">
                        <div class="text-body text-left">หมายเหตุ : ${data[i].wid_remark}</div>
                    </div>
        
             </div>
                <hr class="sidebar-divider">
                <div class="row" style="margin-top: 0.5rem;">
                    <div class="col-12">`
        if (data[i].status == "2") {
            if (data[i].is_direct == "1") {
                html += `<button class="btn btn-danger btn-icon-split send_approve" data-status="${data[i].status}" data-docno="${data[i].doc_no}" data-wid="${data[i].wid_doc}">
                            <span class="icon text-white-50">
                                <i class="fas fa-file"></i>
                            </span>
                            <span class="text">ยกเลิกใบเบิก</span>
                        </button>`
            } else {
                html += `<button class="btn btn-danger btn-icon-split send_approve2" data-status="${data[i].status}" data-docno="${data[i].doc_no}" data-wid="${data[i].wid_doc}">
                            <span class="icon text-white-50">
                                <i class="fas fa-file"></i>
                            </span>
                            <span class="text">ยกเลิกใบเบิก</span>
                        </button>`
            }
            html += ` <button  class="btn btn-info btn-icon-split show_detail" data-status="${data[i].status}" data-docno="${data[i].doc_no}">
                            <span class="icon text-white-50">
                                <i class="fas fa-info"></i>
                            </span>
                            <span class="text">รายละเอียด</span>
                        </button>`
            html += ` <button  class="btn btn-warning btn-icon-split print_doc" data-docno="${data[i].doc_no}" data-status="${data[i].status}" data-direct="${data[i].is_direct}">
                            <span class="icon text-white-50">
                                <i class="fas fa-print"></i>
                            </span>
                            <span class="text">พิมพ์เอกสาร</span>
                        </button>`
        } else if (data[i].status == "4") {
            html += `<button class="btn btn-info btn-icon-split show_detail" data-docno="${data[i].doc_no}" data-status="${data[i].status}">
                            <span class="icon text-white-50">
                                <i class="fas fa-info"></i>
                            </span>
                            <span class="text">รายละเอียด</span>
                        </button>`
            html += ` <button  class="btn btn-warning btn-icon-split print_doc" data-docno="${data[i].doc_no}" data-status="${data[i].status}">
                            <span class="icon text-white-50">
                                <i class="fas fa-print"></i>
                            </span>
                            <span class="text">พิมพ์เอกสาร</span>
                        </button>`
        }
        html += ` </div>

</div>
</div>
</div>

</div>`
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

