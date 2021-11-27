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


    _getWhList();
    _getBranchList();
    _getToWhList()
    _getToBranchList()

    $('#btn-showdetail').on('click', function () {
        $('.detail2').toggle()
    });

    $('#btn-copy').on('click', function () {

        let el = document.getElementById("tableId");
        let body = document.body;
        let range;
        let sel;
        if (document.createRange && window.getSelection) {
            range = document.createRange();
            sel = window.getSelection();
            sel.removeAllRanges();
            try {
                range.selectNodeContents(el);
                sel.addRange(range);
            } catch (e) {
                range.selectNode(el);
                sel.addRange(range);
            }
        }
        document.execCommand("Copy");
        alert("Copy Table to Clipboard");

    });

    $('#from_shelf_code').select2({
        theme: 'bootstrap'
    })
    $('#to_shelf_code').select2({
        theme: 'bootstrap'
    })
    $.ajax({
        url: serverURL + 'getUserStorage',
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log('checkstore success')
            //_getListData('');
        },
        error: function (res) {
            console.log(res)
        },
    });
    $('.btn-add').on('click', function () {

        window.location.href = "form.jsp";
    });
    $('#btn-search').on('click', function () {
        $('#btn-search').attr('disabled', 'true');
        $('#btn-search').text('กำลังประมวลผล...')

        _getListData();
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
    $(document).delegate('.btn_detail', 'click', function (event) {
        var code = $(this).attr('linenumber')
        $('.detail_item_' + code).toggle();



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

$('#from_wh_code').on('change', function () {
    var data = $('#from_wh_code').val();
    if (data != '') {
        _getShList();

    } else {
        // $('.shelf_select').select2().destroy();
        $('#from_shelf_code').html('');
        $('#from_shelf_code').val('').trigger('change');
        $('#from_shelf_code').attr('disabled', 'true');
    }
});

$('#to_wh_code').on('change', function () {
    var data = $('#to_wh_code').val();
    if (data != '') {
        _getShList2();

    } else {
        // $('.shelf_select').select2().destroy();
        $('#to_shelf_code').html('');
        $('#to_shelf_code').val('').trigger('change');
        $('#to_shelf_code').attr('disabled', 'true');
    }
});

function _getBranchList() {

    //var sess_branch = $('#session_branch_code').val();

    var html = "";
    $.ajax({
        url: serverURL + 'getBranchListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---ทั้งหมด---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_branch_code').html(html);
                setTimeout(function () {
                    $('#from_branch_code').select2({
                        theme: 'bootstrap'
                    })

                }, 500)
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getToBranchList() {
    //var sess_branch = $('#session_to_branch_code').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getToBranchListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---ทั้งหมด---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_branch_code').html(html);
                setTimeout(function () {
                    $('#to_branch_code').select2({
                        theme: 'bootstrap'
                    })

                }, 500)
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getWhList() {
    //var sess_wh = $('#session_wh_code').val();

    var html = "";
    $.ajax({
        url: serverURL + 'getWhListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---ทั้งหมด---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_wh_code').html(html);
                setTimeout(function () {
                    $('#from_wh_code').select2({
                        theme: 'bootstrap'
                    })


                }, 500)
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getToWhList() {
    //var sess_wh = $('#session_to_wh_code').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getToWhListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---ทั้งหมด---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_wh_code').html(html);
                setTimeout(function () {
                    $('#to_wh_code').select2({
                        theme: 'bootstrap'
                    })

                }, 500)
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getShList() {
    var data = $('#from_wh_code').val();
    //var sess_sh = $('#session_shelf_code').val();

    var html = "";
    $.ajax({
        url: serverURL + 'getShListUser?whcode=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---ทั้งหมด---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_shelf_code').html(html);
                $('#from_shelf_code').removeAttr('disabled');
                setTimeout(function () {
                    $('#from_shelf_code').select2({
                        theme: 'bootstrap'
                    })


                }, 500)

            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getShList2() {
    var data = $('#to_wh_code').val();
    //  var sess_sh = $('#session_to_shelf_code').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getToShListUser?whcode=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---ทั้งหมด---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_shelf_code').html(html);
                $('#to_shelf_code').removeAttr('disabled');
                setTimeout(function () {
                    $('#to_shelf_code').select2({
                        theme: 'bootstrap'
                    })


                }, 500)
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getListData(data) {

    var search = $('#search').val();
    var itemcode = $('#itemcode').val();
    var doc_status = $('#doc_status').val();
    var req_from_date = $('#req_from_date').val();
    var req_to_date = $('#req_to_date').val();
    var send_from_date = $('#send_from_date').val();
    var send_to_date = $('#send_to_date').val();
    var from_branch_code = $('#from_branch_code').val();
    var from_wh_code = $('#from_wh_code').val();
    var from_shelf_code = $('#from_shelf_code').val();
    var to_branch_code = $('#to_branch_code').val();
    var to_wh_code = $('#to_wh_code').val();
    var to_shelf_code = $('#to_shelf_code').val();

    $.ajax({
        url: serverURL + 'getRequestSendByitemReport?item=' + itemcode + '&docst=' + doc_status + '&search=' + search.trim() + '&rfd=' + req_from_date + '&rtd=' + req_to_date + '&sfd=' + send_from_date + '&std=' + send_to_date + '&fbc=' + from_branch_code + '&fwc=' + from_wh_code + '&fsc=' + from_shelf_code
                + '&tbc=' + to_branch_code + '&twc=' + to_wh_code + '&tsc=' + to_shelf_code,
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            _showListDetail(res)
            $('#btn-search').removeAttr('disabled');
            $('#btn-search').text('ประมวลผล')

        },
        error: function (res) {
            console.log(res)
            $('#btn-search').removeAttr('disabled');
            $('#btn-search').text('ประมวลผล')
        },
    });
}

function _showListDetail(data) {
    var html = '';
    var html2 = '';
    for (var i = 0; i < data.length; i++) {
        var doc_status = "0";
        var statusText = "";
        var color = "blue";
        html += ` <tr>`
        if (data[i].is_direct == '1') {
            html += ` <td></td>`
            html += ` <td></td>`
        } else {
            html += ` <td>${data[i].doc_no}</td>`
            html += ` <td>${data[i].doc_date}</td>`
        }

        if (data[i].wid_date_format == undefined) {
            html += `<td>รออนุมัติ</td>`
            html += `<td></td>`
        } else {
            html += `<td>${data[i].wid_date_format}</td>`
            html += `<td>${data[i].wid_doc}</td>`

        }
        if (data[i].fg_doc == '') {
            html += `<td nowrap></td>`
        } else {
            html += `<td nowrap>${data[i].fg_doc}</td>`
        }
        if (data[i].rim_doc == '') {
            html += `<td nowrap></td>`
        } else {
            html += `<td nowrap>${data[i].rim_doc}</td>`
        }
        if (data[i].wid_creator_code == undefined) {
            html += `<td nowrap></td>`
        } else {
            html += `<td nowrap>${data[i].wid_creator_name}(${data[i].wid_creator_code})</td>`
        }
        html += `<td nowrap>${data[i].item_code}~${data[i].item_name}</td>`
        html += `
                        <td nowrap>${data[i].branch_name}(${data[i].branch_code})</td>
                        <td nowrap>${data[i].wh_name}(${data[i].wh_code})</td>
                        <td nowrap>${data[i].shelf_name}(${data[i].shelf_code})</td>
                        <td nowrap>${data[i].to_branch_name}(${data[i].to_branch_code})</td>
                        <td nowrap>${data[i].to_wh_name}(${data[i].to_wh_code})</td>
                        <td nowrap>${data[i].to_shelf_name}(${data[i].to_shelf_code})</td>`
        html += `<td class="text-right">${data[i].request_qty}</td>`
        html += `<td class="text-right">${data[i].send_qty}</td>`
        html += `<td class="text-right">${data[i].receive_qty}</td>
              <td class="text-right">${data[i].return_qty}</td>
                        <td class="text-right">${data[i].wait_return_qty}</td>
                  
                        <td class="text-right">${data[i].wait_receive_qty}</td>`


        if (data[i].doc_status == '1') {
            doc_status = "0"
            html += `<td><span style='color:green'>Complete</span></td>`
        } else {
            doc_status = "0"
            html += `<td><span style='color:red'>Incomplete</span></td>`
        }



        html += `</tr>`;
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

