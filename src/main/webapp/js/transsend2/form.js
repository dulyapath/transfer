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


var send_instock = "0";

var item_detail = []

$(document).ready(function () {

    $.ajax({
        url: serverURL + 'getSetting',
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res);
            if (res.length > 0) {
                send_instock = res[0].send_instock;
            }
        },
        error: function (res) {
            console.log(res)
        },
    });

    _getWhList();
    _getBranchList();
    _getToWhList()
    _getToBranchList()
    var currentdate = new Date();
    var datetime = 'MWID' + currentdate.getFullYear() + '' + (currentdate.getMonth() + 1) + '' + currentdate.getDate() + '' + currentdate.getHours() + '' + currentdate.getMinutes() + '' + currentdate.getSeconds() + '' + uuidv4().toUpperCase()

    var cmd_mode = $('#form-mode').val();
    cmd_status = $('#form-status').val();
    if (cmd_mode != '') {
        console.log('here')
        $('#doc_no').val(cmd_mode);

        setTimeout(function () {
            _getDocDetail(cmd_mode);
        }, 1500)
    } else {
        _displayTable();

        $('#doc_no').val(datetime);
        $('#doc_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
        $('#remark').val('');
    }
    $('#inp-scanner').keyup(function (e) {
        if (e.keyCode == 13)
        {
            var data = $('#inp-scanner').val().trim();
            if (data != '') {
                var row = item_detail.filter(
                        c => c.item_code == data
                );

                if (row.length == 0) {

                    var from_bh = $('#from_bh').val();
                    var from_wh = $('#from_wh').val();
                    var from_sh = $('#from_sh').val();
                    var to_bh = $('#to_bh').val();
                    var to_wh = $('#to_wh').val();
                    var msg = '';
                    if (from_bh == '') {
                        msg += 'สาขา \n'
                    }
                    if (from_wh == '') {
                        msg += 'คลัง \n'
                    }
                    if (from_sh == '') {
                        msg += 'ที่เก็บ \n'
                    }
                    if (msg != "") {
                        swal("กรุณาเลือก " + msg, "", "warning")
                    } else {

                        $.ajax({
                            url: serverURL + 'getItemScan?code=' + data,
                            method: 'GET',
                            cache: false,
                            success: function (res) {
                                console.log(res)
                                if (res.length > 0) {

                                    item_detail.push({
                                        item_code: res[0].item_code,
                                        item_name: res[0].item_name,
                                        unit_code: res[0].unit_code,
                                        unit_name: res[0].unit_name,
                                        balance: 0.0,
                                        balance_qty: 0.0,
                                        average_cost: 0.0,
                                        price: 0.0,
                                        qty: 1
                                    });
                                    _getCost((item_detail.length - 1), res[0].item_code, res[0].unit_code);
                                    $('#inp-scanner').val('');
                                } else {
                                    swal("ไม่พบข้อมูลสินค้า", data, "warning")
                                }

                            },
                            error: function (res) {
                                console.log(res)
                            },
                        });
                    }
                } else {
                    swal("พบสินค้าในรายการแล้ว", data, "warning")
                }
            }
        }
    });

    $('#btn_create').on('click', function () {

        var currentdate = new Date();
        var datetime = currentdate.getHours() + ':' + currentdate.getMinutes();

        var doc_no = '';
        var doc_date = $('#doc_date').val()
        var remark = ''

        var wid_docno = $('#doc_no').val();
        var wid_docdate = $('#doc_date').val()
        var wid_remark = $('#remark').val();
        var user_code = $('#user_code').val();
        var from_bh = $('#from_bh').val();
        var from_wh = $('#from_wh').val();
        var from_sh = $('#from_sh').val();
        var to_bh = $('#to_bh').val();
        var to_wh = $('#to_wh').val();
        var to_sh = $('#to_sh').val();
        var price_formula = $('#price_formula').val();
        var details = [];
        var msg = '';
        if (wid_docno == '') {
            msg += 'เลขที่เอกสาร \n'
        }
        if (wid_docdate == '') {
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
            var checkqty = "";
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
                        event_qty: item_detail[i].qty,
                        average_cost: item_detail[i].average_cost,
                        sum_of_cost: parseFloat(item_detail[i].average_cost) * parseFloat(item_detail[i].qty)
                    }
                    console.log(item_detail[i].balance_qty,item_detail[i].qty)
                    if (parseFloat(item_detail[i].balance_qty) < parseFloat(item_detail[i].qty)) {
                        checkqty += "จำนวนสินค้าไม่เพียงพอ ";
                    }
                    details.push(json_detail);
                }
            }
            if (send_instock == '1') {
                if (checkqty != '') {
                    swal("ไม่สามารถทำรายการได้ " + checkqty, "", "warning");
                    return;
                }
            }
            console.log(send_instock)
            if (details.length > 0) {
                var json_data = {
                    doc_no: doc_no,
                    doc_date: doc_date,
                    remark: remark,
                    price_formula: price_formula,
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
                    url: serverURL + 'saveDocSend2',
                    method: 'POST',
                    data: json_data,
                    success: function (res) {
                        console.log(res)
                        setTimeout(function () {

                            //window.open('print.jsp' + "?docno=" + wid_docno, "_blank");
                            swal("บันทึกใบเบิก สำเร็จ", "", "success")
                                    .then((value) => {
                                        window.location.href = "index.jsp"
                                    });

                        }, 1000);
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
    /*
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
     url: serverURL + 'saveDocSend2',
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
     });*/

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


    $('#to_bh').on('change', function () {
        var data = $('#to_bh').val();
        if (data != '') {
            _getWarehouseShelf();

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

        var row = item_detail.filter(
                c => c.item_code == code
        );

        if (row.length == 0) {

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
        } else {
            swal("พบสินค้าในรายการแล้ว", code, "warning")
        }
    });

    $(document).delegate('.qty_edit', 'input', function (event) {

        var index = $(this).attr('data-index')
        var data = $('.qty_value_' + index).val();


        if (parseFloat(data) > 0) {
            item_detail[index].qty = data
        } else {
            item_detail[index].qty = 1
        }

        $('.qty_value_' + index).val(item_detail[index].qty);

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

    $('#price_formula').on('change', function () {
        var data = $('#price_formula').val();

        if (data != '') {

            $('.loading').show();
            var price_formula = $('#price_formula').val();
            for (var i = 0; i < item_detail.length; i++) {

                _getFormular(item_detail[i].item_code, item_detail[i].unit_code, i);

            }
            setTimeout(function () {
                console.log(item_detail)
                _displayTable();
                $('.loading').hide();
            }, 3000);

        }
    });

});

function _getFormular(item_code, unit_code, index) {
    var price_formula = $('#price_formula').val();
    var price = '0'
    $.ajax({
        url: serverURL + 'getPriceformula?code=' + item_code + '&unit=' + unit_code + '&p=' + price_formula,
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            if (res.length == 1) {
                if (res[0].price == '') {
                    price = '0'
                } else {
                    price = res[0].price;
                }

            } else {
                price = '0'
            }
            item_detail[index].price = price

        },
        error: function (res) {
            console.log(res)
        },
    });

}

function _getDocDetail(docno) {
    $('.loading').show();
    var price = $('#price_formula').val();
    $.ajax({
        url: serverURL + 'getDocRequestSendDetail?docno=' + docno + '&p=' + price,
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res);
            if (res.length > 0) {
                $('#doc_no').val(res[0].doc_no);
                $('#doc_no').attr("readonly", "true");
                $('#doc_date').val(res[0].date);
                $('#remark').val(res[0].remark);
                $('#price_formula').val(res[0].price_formula).trigger("change");

                $('#from_bh').val(res[0].branch_code).trigger("change");
                $('#from_wh').val(res[0].wh_code).trigger("change");


                $('#to_bh').val(res[0].to_branch_code).trigger("change");
                $('#to_wh').val(res[0].to_wh_code).trigger("change");


                setTimeout(function () {
                    $('#from_sh').val(res[0].shelf_code).trigger("change");
                    $('#to_sh').val(res[0].to_shelf_code).trigger("change");
                    item_detail = res[0].detail;
                    _displayTable();

                    if (cmd_status != "6") {
                        $('#price_formula').attr("disabled", "true");
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
    var sess_branch = $('#session_direct_branch_code').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getBranchListUser2',
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
                    setTimeout(function () {
                        $('#from_bh').val(sess_branch).trigger('change');
                    }, 500)
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
        url: serverURL + 'getBranchList',
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
    var sess_wh = $('#session_direct_wh_code').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getWhListUser2',
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
                    setTimeout(function () {
                        $('#from_wh').val(sess_wh).trigger('change');
                    }, 500)
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
        url: serverURL + 'getWhList',
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
    var sess_sh = $('#session_direct_shelf_code').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getShListUser2?whcode=' + data,
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
                    setTimeout(function () {
                        $('#from_sh').val(sess_sh).trigger('change');
                    }, 500)
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
        url: serverURL + 'getShList?whcode=' + data,
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

function _getWarehouseShelf() {
    var data = $('#to_bh').val();
    var html = "";
    $.ajax({
        url: serverURL + 'getWarehouseShelf?bh=' + data,
        method: 'GET',
        cache: false,
        success: function (res) {

            if (res.length > 0) {

                res.forEach(function (ele, index) {
                    $('#to_wh').val(ele.wh_code).trigger('change');
                    setTimeout(function () {
                        $('#to_sh').val('01').trigger('change');
                    }, 700)

                });


            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}


function _getPriceformula(index, itemcode, unitcode) {
    var to_bh = $('#from_bh').val();
    var to_wh = $('#from_wh').val();
    var to_sh = $('#from_sh').val();
    var price_formula = $('#price_formula').val();
    $.ajax({
        url: serverURL + 'getPriceformula?code=' + itemcode + '&unit=' + unitcode + '&p=' + price_formula,
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            if (res.length == 1) {
                item_detail[index].price = res[0].price
                _displayTable();
                $('#modalSearch').modal('hide')
            } else {
                item_detail[index].price = '0'
                _displayTable();
                $('#modalSearch').modal('hide')
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
                item_detail[index].balance_qty = res[0].balance_qty
                item_detail[index].average_cost = res[0].average_cost
            } else {
                item_detail[index].balance = '0'
                item_detail[index].balance_qty = '0'
                item_detail[index].average_cost = '0'
            }
            _getPriceformula(index, itemcode, unitcode);
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
        qty: 1,
        price: 0
    });
    _displayTable();
}



function _displayTable() {
    var html = '';
    for (var i = 0; i < item_detail.length; i++) {


        html += '<tr>'
        html += '<td class="text-center">' + (i + 1) + '</td>'
        if (cmd_status == "6") {
            html += '<td class="text-left btn-search" style="cursor:pointer" index="item_' + i + '">' + item_detail[i].item_code + ' <i class="fa fa-search"></i></td>'
        } else {
            html += '<td class="text-left " style="cursor:pointer" index="item_' + i + '">' + item_detail[i].item_code + '</td>'
        }

        html += '<td class="text-left">' + item_detail[i].item_name + '</td>'

        html += '<td class="text-center">' + item_detail[i].unit_code + '</td>'
        html += '<td class="text-right " >' + formatNumber(parseFloat(item_detail[i].balance_qty)) + '</td>'
        if (cmd_status == "6") {
            html += '<td class="text-center" ><input type="number" style="text-align:center" class="qty_edit  qty_value_' + i + '" data-index="' + i + '" min="1" value="' + item_detail[i].qty + '"></td>'

        } else {
            html += '<td class="text-right" >' + item_detail[i].qty + '</td>'

        }
        html += '<td class="text-right" >' + formatNumber(parseFloat(item_detail[i].price)) + '</td>'
        if (cmd_status == "6") {
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

