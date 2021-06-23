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
    _getWhList();
    _getBranchList();
    _getToWhList()
    _getToBranchList()
    var currentdate = new Date();
    var datetime = 'RT' + currentdate.getFullYear() + '' + (currentdate.getMonth() + 1) + '' + currentdate.getDate() + '' + currentdate.getHours() + '' + currentdate.getMinutes() + '' + currentdate.getSeconds() + '' + uuidv4().toUpperCase()

    var cmd_mode = $('#form-mode').val();
    cmd_status = $('#form-status').val();
    if (cmd_mode != '') {
        console.log('here')
        $('#doc_no').val(cmd_mode);

        _getDocDetail(cmd_mode);
    } else {
        _displayTable();

        $('#doc_no').val(datetime);
        $('#doc_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
        $('#remark').val('');
    }




    $('#btn_create').on('click', function () {
        var doc_no = $('#doc_no').val();
        var doc_date = $('#doc_date').val()
        var remark = $('#remark').val();
        var user_code = $('#user_code').val();
        var from_bh = $('#from_bh').val();
        var from_wh = $('#from_wh').val();
        var from_sh = $('#from_sh').val();
        var to_bh = $('#to_bh').val();
        var to_wh = $('#to_wh').val();
        var to_sh = $('#to_sh').val();
        var details = [];
        var msg = '';
        if (doc_no == '') {
            msg += 'เลขที่เอกสาร \n'
        }
        if (doc_date == '') {
            msg += 'วันที่เอกสาร \n'
        }
        if (from_bh == '') {
            msg += 'จากสาขา \n'
        }
        if (from_wh == '') {
            msg += 'จากคลัง \n'
        }
        if (from_sh == '') {
            msg += 'จากที่เก็บ \n'
        }
        if (to_bh == '') {
            msg += 'สาขารับ \n'
        }
        if (to_wh == '') {
            msg += 'คลังรับ \n'
        }
        if (to_sh == '') {
            msg += 'ที่เก็บรับ \n'
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
                        event_qty: item_detail[i].qty,
                        qty: item_detail[i].qty
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
                    data: JSON.stringify(details)

                }
                $.ajax({
                    url: serverURL + 'saveDocRequest',
                    method: 'POST',
                    data: json_data,
                    success: function (res) {
                        console.log(res)
                        swal("บันทึกข้อมูลสำเร็จ", "", "success")
                        setTimeout(function () {
                            window.location.href = "index.jsp"
                        }, 2500);
                    },
                    error: function (res) {
                        console.log(res)
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
        item_detail[index].qty = data

    });

    $(document).delegate('.select-unit', 'click', function (event) {
        var code = $(this).attr('data-code')
        var name = $(this).attr('data-name')
        var itemcode = $(this).attr('data-itemcode')
        var to_bh = $('#from_bh').val();
        var to_wh = $('#from_wh').val();
        var to_sh = $('#from_sh').val();

        var index = $('#line_index').val()
        var detail = $('#line_action').val()

        item_detail[index].unit_code = code;
        item_detail[index].unit_name = name;

        $.ajax({
            url: serverURL + 'getBalance?code=' + itemcode + '&unit=' + code + '&whcode=' + to_wh + '&branch=' + to_bh,
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
        url: serverURL + 'getDocRequestDetail?docno=' + docno,
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res);
            if (res.length > 0) {
                $('#doc_no').val(res[0].doc_no);
                $('#doc_no').attr("readonly", "true");
                $('#doc_date').val(res[0].date);
                $('#remark').val(res[0].remark);

                $('#from_bh').val(res[0].branch_code);
                $('#from_wh').val(res[0].wh_code).trigger("change");


                $('#to_bh').val(res[0].to_branch_code);
                $('#to_wh').val(res[0].to_wh_code).trigger("change");


                setTimeout(function () {
                    $('#from_sh').val(res[0].shelf_code).trigger("change");
                    $('#to_sh').val(res[0].to_shelf_code).trigger("change");
                    item_detail = res[0].detail;
                    _displayTable();

                    if (cmd_status != "0") {
                        $('#doc_date').attr("disabled", "true");
                        $('#remark').attr("disabled", "true");
                        $('#from_bh').attr("disabled", "true");
                        $('#from_wh').attr("disabled", "true");
                        $('#to_bh').attr("disabled", "true");
                        $('#to_wh').attr("disabled", "true");
                        $('#from_sh').attr("disabled", "true");
                        $('#to_sh').attr("disabled", "true");
                        $('.btn-addline').hide();
                        $('#btn_create').hide();
                        $('#btn_create').hide();
                        $('.btn-back').text('กลับสู่หน้าจอหลัก')
                    }
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
        url: serverURL + 'getBranchListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---เลือกสาขา---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_bh').html(html);
                setTimeout(function () {
                    $('#from_bh').select2({
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

    var html = "";
    $.ajax({
        url: serverURL + 'getToBranchListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---เลือกสาขา---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_bh').html(html);
                setTimeout(function () {
                    $('#to_bh').select2({
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

    var html = "";
    $.ajax({
        url: serverURL + 'getWhListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---เลือกคลัง---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_wh').html(html);
                setTimeout(function () {
                    $('#from_wh').select2({
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

    var html = "";
    $.ajax({
        url: serverURL + 'getToWhListUser',
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---เลือกคลัง---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_wh').html(html);
                setTimeout(function () {
                    $('#to_wh').select2({
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
    var data = $('#from_wh').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getShListUser?whcode=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---เลือกที่เก็บ---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#from_sh').html(html);
                $('#from_sh').removeAttr('disabled');
                setTimeout(function () {
                    $('#from_sh').select2({
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
    var data = $('#to_wh').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getToShListUser?whcode=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {
                html += `<option value=''>---เลือกที่เก็บ---</option>`;
                res.forEach(function (ele, index) {
                    html += `<option value='${ele.code}'>`;
                    html += `${ele.code}~${ele.name_1}`;
                    html += "</option>";
                });
                $('#to_sh').html(html);
                $('#to_sh').removeAttr('disabled');
                setTimeout(function () {
                    $('#to_sh').select2({
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

function _getCost(index, itemcode, unitcode) {
    var to_bh = $('#from_bh').val();
    var to_wh = $('#from_wh').val();
    var to_sh = $('#from_sh').val();

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
            } else {
                item_detail[index].balance = '0'
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
    var search_barcode = $('#search_barcode').val()
    console.log(search_name)

    $.ajax({
        url: serverURL + 'search_item?name=' + search_name + '&barcode=' + search_barcode,
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
        if (cmd_status == "0") {
            html += '<td class="text-left btn-search" style="cursor:pointer" index="item_' + i + '">' + item_detail[i].item_code + ' <i class="fa fa-search"></i></td>'
        } else {
            html += '<td class="text-left " style="cursor:pointer" index="item_' + i + '">' + item_detail[i].item_code + '</td>'
        }

        html += '<td class="text-left">' + item_detail[i].item_name + '</td>'

        html += '<td class="text-center">' + item_detail[i].unit_code + '</td>'
        html += '<td class="text-right " >' + formatNumber(parseFloat(item_detail[i].balance)) + '</td>'
        if (cmd_status == "0") {
            html += '<td class="text-center" ><input type="number" style="text-align:center" class="qty_edit  qty_value_' + i + '" data-index="' + i + '" value="' + item_detail[i].qty + '"></td>'

        } else {
            html += '<td class="text-right" >' + item_detail[i].qty + '</td>'

        }
        if (cmd_status == "0") {
            html += '<td class="text-center"><i class="fa fa-minus" style="cursor:pointer;color:red" onclick="delLine(' + i + ')"></i></td>'
        } else {
            html += '<td class="text-center"></td>'
        }

        html += '</tr>'
    }

    $('#item_detail').html(html);
}

function delLine(data) {
    console.log(data)
    item_detail.splice(data, 1);
    _displayTable()

}

