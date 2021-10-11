var serverURL = "../";
var userbranch = '';
var cmd_status = "0";
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
        balance: 0.0,
        qty: 0.0
    }
]

$(document).ready(function () {
    // _getWhList();
    // _getBranchList();

    var cmd_mode = $('#form-mode').val();
    cmd_status = $('#form-status').val();

    $('#doc_no').val(cmd_mode);


    _getDocDetail(cmd_mode);




    $('#btn_create').on('click', function () {

        var currentdate = new Date();
        var dateString =
                ("0" + currentdate.getHours()).slice(-2) + ":" +
                ("0" + currentdate.getMinutes()).slice(-2) + ":" +
                ("0" + currentdate.getSeconds()).slice(-2);


        var datetime = dateString.split(':')[0] + ":" + dateString.split(':')[1]

        var doc_no = $('#rt_doc_no').val();
        var doc_date = $('#rt_doc_date').val()
        var remark = $('#rt_remark').val();
        var user_code = $('#rt_user_code').val();
        var from_bh = $('#rt_from_bh').val();
        var from_wh = $('#rt_from_wh').val();
        var from_sh = $('#rt_from_sh').val();
        var to_bh = $('#rt_to_bh').val();
        var to_wh = $('#rt_to_wh').val();
        var to_sh = $('#rt_to_sh').val();

        var wid_docno = $('#wid_docno').val();
        var wid_docdate = $('#wid_docdate').val();
        var wid_remark = $('#wid_remark').val();

        var details = [];
        var msg = '';
        if (wid_docno == '') {
            msg += 'เลขที่เอกสาร \n'
        }
        if (wid_docdate == '') {
            msg += 'วันที่เอกสาร \n'
        }

        if (msg != '') {
            alert('กรุณาเพิ่ม \n' + msg)
        } else {
            for (var i = 0; i < item_detail.length; i++) {
                if (item_detail[i].item_code != '') {
                    var json_detail = {
                        line_number: i,
                        item_code: item_detail[i].item_code,
                        item_name: item_detail[i].item_name,
                        unit_code: item_detail[i].unit_code,
                        balance: item_detail[i].balance,
                        qty: item_detail[i].qty,
                        balance_qty: item_detail[i].balance_qty,
                        event_qty: item_detail[i].event_qty,
                        average_cost: item_detail[i].average_cost,
                        sum_of_cost: parseFloat(item_detail[i].average_cost) * parseFloat(item_detail[i].event_qty)
                    }
                    details.push(json_detail);
                }
            }
            console.log(details)
            if (details.length > 0) {
                var json_data = {
                    doc_no: doc_no,
                    doc_date: doc_date,
                    remark: remark,
                    user_code: user_code,
                    from_bh: from_bh,
                    from_wh: from_wh,
                    from_sh: from_sh,
                    to_bh: to_bh,
                    to_wh: to_wh,
                    to_sh: to_sh,
                    wid_docno: wid_docno,
                    wid_docdate: wid_docdate,
                    wid_remark: wid_remark,
                    wid_doctime: datetime,
                    data: JSON.stringify(details)

                }
                $.ajax({
                    url: serverURL + 'saveDocWid',
                    method: 'POST',
                    data: json_data,
                    success: function (res) {
                        console.log(res)
                        var msc = 0;
                        var json_new = [];
                        for (var i = 0; i < item_detail.length; i++) {
                            console.log(item_detail[i].qty)
                            console.log(item_detail[i].event_qty)
                            if (parseFloat(item_detail[i].event_qty) < parseFloat(item_detail[i].qty)) {
                                msc += 1;
                                var json_data = {
                                    line_number: i,
                                    item_code: item_detail[i].item_code,
                                    item_name: item_detail[i].item_name,
                                    unit_code: item_detail[i].unit_code,
                                    balance: 0,
                                    qty: parseFloat(item_detail[i].qty) - parseFloat(item_detail[i].event_qty),
                                    balance_qty: 0,
                                    event_qty: 0,
                                    average_cost: 0,
                                    sum_of_cost: 0
                                }
                                json_new.push(json_data);
                                console.log(json_data)
                            }
                        }
                        var currentdatex = new Date();
                        var datetimec = 'MRT' + currentdate.getFullYear() + '' + (currentdate.getMonth() + 1) + '' + currentdate.getDate() + '' + currentdate.getHours() + '' + currentdate.getMinutes() + '' + currentdate.getSeconds() + '' + uuidv4().toUpperCase()

                        setTimeout(function () {
                            if (json_new.length > 0) {
                                swal({
                                    title: "ยืนยันการทำงาน",
                                    text: "จัดสินค้าไม่ครบ ต้องการสร้างใบขอเบิกหรือไม่",
                                    icon: "warning",
                                    buttons: ["ปิด", "ตกลง"],
                                    dangerMode: true,
                                })
                                        .then((willCreate) => {
                                            if (willCreate) {
                                                var json_data = {
                                                    doc_no: datetimec,
                                                    doc_date: currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2),
                                                    remark: remark + ' สร้างจาก ' + doc_no,
                                                    user_code: user_code,
                                                    from_bh: from_bh,
                                                    from_wh: from_wh,
                                                    from_sh: from_sh,
                                                    to_bh: to_bh,
                                                    to_wh: to_wh,
                                                    to_sh: to_sh,
                                                    data: JSON.stringify(json_new)

                                                }
                                                $.ajax({
                                                    url: serverURL + 'saveDocRequestSend',
                                                    method: 'POST',
                                                    data: json_data,
                                                    success: function (res) {
                                                        console.log(res)
                                                        swal("สร้างใขขอเบิกเลขที่ " + datetimec + " สำเร็จ", "", "success")
                                                        setTimeout(function () {
                                                            window.location.href = "index.jsp"
                                                        }, 1200);
                                                    },
                                                    error: function (res) {
                                                        console.log(res)
                                                    },
                                                });
                                            } else {
                                                swal("สร้างใบเบิกสินค้าสำเร็จ เอกสารเลขที่ " + wid_docno + " สำเร็จ", "", "success")
                                                setTimeout(function () {
                                                    window.location.href = "index.jsp"
                                                }, 1200);
                                            }
                                        });
                            } else {

                            }
                        }, 1000);
                        //swal("บันทึกข้อมูลสำเร็จ", "", "success")
                        /*setTimeout(function () {
                         window.location.href = "index.jsp"
                         }, 1200);*/
                    },
                    error: function (res) {
                        console.log(res)
                        swal("Error " + res, "", "warning")
                    },
                });
            } else {
                alert('กรุณาเพิ่มรายละเอียด')
            }
        }
    });

    $('#from_wh').on('change', function () {
        var data = $('#from_wh').val();
        if (data != '') {
            _getShList();

        } else {
            // $('.shelf_select').select2().destroy();
            $('#from_sh').html('');
            $('#from_sh').val('').trigger('change');
            $('#from_sh').attr('disabled', 'true');
        }
    });

    $('#to_wh').on('change', function () {
        var data = $('#to_wh').val();
        if (data != '') {
            _getShList2();

        } else {
            // $('.shelf_select').select2().destroy();
            $('#to_sh').html('');
            $('#to_sh').val('').trigger('change');
            $('#to_sh').attr('disabled', 'true');
        }
    });

    $(document).delegate('.btn-search', 'click', function (event) {
        var data = $(this).attr('index').split('_');
        var detail = data[0];
        var index = data[1];
        var to_bh = $('#to_bh').val();
        var to_wh = $('#to_wh').val();
        var msg = '';
        if (to_bh == '') {
            msg += 'สาขา \n'
        }
        if (to_wh == '') {
            msg += 'คลัง \n'
        }

        if (msg != "") {
            swal("กรุณาเลือก " + msg, "", "warning")
        } else {
            $('#line_index').val(index)
            $('#line_action').val(detail)
            $('#modalSearch').modal('show')
        }

    });

    $(document).delegate('.select-items', 'click', function (event) {
        var code = $(this).attr('data-code')
        var name = $(this).attr('data-name')
        var index = $('#line_index').val()
        var detail = $('#line_action').val()

        item_detail[index].item_code = code;
        item_detail[index].item_name = name;
        $.ajax({
            url: serverURL + 'unit_item?code=' + code,
            method: 'GET',
            cache: false,
            success: function (res) {

                console.log(res)
                if (res.length == 1) {
                    item_detail[index].unit_code = res[0].code;
                    item_detail[index].unit_name = res[0].name_1;
                    _getCost(index, code, res[0].code);
                } else {
                    var html = '';
                    for (var i = 0; i < res.length; i++) {
                        html += "<li class = 'list-group-item list-group-item-action select-unit' data-itemcode='" + code + "'  data-code='" + res[i].code + "' data-name='" + res[i].name_1 + "' > " + res[i].code + '~' + res[i].name_1 + " </li>"
                    }
                    $('#list_unit_item').html(html)
                    $('#modalSearch').modal('hide')
                    setTimeout(function () {
                        $('#modalUnit').modal('show')
                    }, 500);
                }

            },
            error: function (res) {
                console.log(res)
            },
        });
    });

    $(document).delegate('.qty_edit', 'input', function (event) {

        var index = $(this).attr('data-index')
        var data = $('.qty_value_' + index).val();
        item_detail[index].event_qty = data

    });

    $(document).delegate('.select-unit', 'click', function (event) {
        var code = $(this).attr('data-code')
        var name = $(this).attr('data-name')
        var itemcode = $(this).attr('data-itemcode')

        var index = $('#line_index').val()
        var detail = $('#line_action').val()

        item_detail[index].unit_code = code;
        item_detail[index].unit_name = name;

        $.ajax({
            url: serverURL + 'getBalance?code=' + itemcode + '&unit=' + code,
            method: 'GET',
            cache: false,
            success: function (res) {

                console.log(res)
                if (res.length == 1) {
                    item_detail[index].balance = res[0].balance_qty
                    _displayTable();
                    $('#modalUnit').modal('hide');
                } else {
                    item_detail[index].balance = 0
                    _displayTable();
                    $('#modalUnit').modal('hide');
                }

            },
            error: function (res) {
                console.log(res)
            },
        });


    });

});


function _getDocDetail(docno) {
    $('.loading').show();
    $.ajax({
        url: serverURL + 'getDocRequestHistoryDetail?docno=' + docno,
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res);
            if (res.length > 0) {
                $('#doc_no').text(res[0].doc_no);
                $('#doc_date').text(res[0].doc_date);
                $('#creator_code').text(res[0].user_code + '~' + res[0].user_name)


                $('#remark').text(res[0].remark);

                $('#from_bh').text(res[0].branch_name + '(' + res[0].branch_code + ')');
                $('#from_wh').text(res[0].wh_name + '(' + res[0].wh_code + ')')


                $('#to_bh').text(res[0].to_branch_name + '(' + res[0].to_branch_code + ')');
                $('#to_wh').text(res[0].to_wh_name + '(' + res[0].to_wh_code + ')');
                $('#from_sh').text(res[0].shelf_name + '(' + res[0].shelf_code + ')');
                $('#to_sh').text(res[0].to_sh_name + '(' + res[0].to_shelf_code + ')');


                $('#wid_docno').val(res[0].wid_doc);
                $('#wid_docdate').val(res[0].wid_docdate);
                $('#wid_remark').val(res[0].wid_remark);


                $('#rt_doc_no').val(res[0].doc_no);
                $('#rt_doc_date').val(res[0].date)
                $('#rt_remark').val(res[0].remark);
                $('#rt_user_code').val(res[0].user_code);
                $('#rt_from_bh').val(res[0].branch_code);
                $('#rt_from_wh').val(res[0].wh_code);
                $('#rt_from_sh').val(res[0].shelf_code);
                $('#rt_to_bh').val(res[0].to_branch_code);
                $('#rt_to_wh').val(res[0].to_wh_code);
                $('#rt_to_sh').val(res[0].to_shelf_code);

                $('#doc_wid').text(res[0].wid_doc)
                $('#doc_fg').text(res[0].fg_doc)
                $('#doc_rim').text(res[0].rim_doc)

                setTimeout(function () {

                    item_detail = res[0].detail;
                    _displayTable();

                    $('.loading').hide();
                }, 1000);



            } else {
                $('.loading').hide();
                setTimeout(function () {
                    swal("ไม่พบเอกสารนี้", "", "success")
                }, 300);
                setTimeout(function () {
                    window.location.href = "index.jsp"
                }, 1500);
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getBranchList() {

    var html = "";
    $.ajax({
        url: serverURL + 'getBranchList',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''></option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_bh').html(html);
                $('#to_bh').html(html);
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getWhList() {

    var html = "";
    $.ajax({
        url: serverURL + 'getWhList',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''></option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_wh').html(html);
                $('#to_wh').html(html);
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getShList() {
    var data = $('#from_wh').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getShList?whcode=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''></option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_sh').html(html);
                $('#from_sh').removeAttr('disabled');

            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getShList2() {
    var data = $('#to_wh').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getShList?whcode=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''></option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_sh').html(html);
                $('#to_sh').removeAttr('disabled');

            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _getCost(index, itemcode, unitcode) {
    var to_bh = $('#to_bh').val();
    var to_wh = $('#to_wh').val();
    var to_sh = $('#to_sh').val();

    $.ajax({
        url: serverURL + 'getBalance?code=' + itemcode + '&unit=' + unitcode + '&whcode=' + to_wh + '&branch=' + to_bh,
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            if (res.length == 1) {
                item_detail[index].balance = res[0].balance_qty
                _displayTable();
                $('#modalSearch').modal('hide')
            }

        },
        error: function (res) {
            console.log(res)
        },
    });

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
        balance: 0.0,
        qty: 0.0
    });
    _displayTable();
}



function _displayTable() {
    var html = '';
    for (var i = 0; i < item_detail.length; i++) {


        html += '<tr>'
        html += '<td class="text-center">' + (i + 1) + '</td>'

        html += '<td class="text-left "  >' + item_detail[i].item_code + ' </td>'


        html += '<td class="text-left">' + item_detail[i].item_name + '</td>'

        html += '<td class="text-center">' + item_detail[i].unit_code + '</td>'
        html += '<td class="text-right" >' + formatNumber(parseFloat(item_detail[i].qty)) + '</td>'
        html += '<td class="text-center" >' + formatNumber(parseFloat(item_detail[i].event_qty)) + '</td>'
        html += '<td class="text-center" >' + formatNumber(parseFloat(item_detail[i].receive_qty)) + '</td>'



        html += '<td class="text-center"></td>'


        html += '</tr>'
    }

    $('#item_detail').html(html);
}

function delLine(data) {
    console.log(data)
    item_detail.splice(data, 1);
    _displayTable()

}

