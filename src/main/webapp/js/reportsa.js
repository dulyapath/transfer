var serverURL = "../";
var userbranch = '';
var item_detail = [

]

function uuidv4() {
    return 'xxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function _getUserBranch() {

    $.ajax({
        url: serverURL + 'getUserBranch',
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res)
            if (res[0].branch_code != '') {
                userbranch = res[0].branch_code + '~' + res[0].branch_name
            }
            $('#user_branch').val(res[0].branch_code)
            if (res[0].code.toUpperCase() == 'SUPERADMIN' || res[0].change_branch_code == '1') {
                $('#user_branch').val('all')
                userbranch = 'all'
            }

        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _getBranch() {
    var branchcode = $('#user_branch').val();
    console.log(branchcode)
    $.ajax({
        url: serverURL + 'getBranch',
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            var html = '<option value="">--เลือกสาขา--</option>';
            for (var i = 0; i < res.length; i++) {
                html += ' <option value="' + res[i].code + '~' + res[i].name_1 + '">' + res[i].code + '~' + res[i].name_1 + '</option>'
            }
            console.log('userbranch ' + userbranch)
            $('.branch_code').html(html);
            if (userbranch == 'all') {
                $('.branch_code').removeAttr('disabled')
                $('.branch_code').val('')
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _getVatrate() {
    $.ajax({
        url: serverURL + 'getVatrate',
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res)
            if (res.length > 0) {
                $('#vat_rate').val(res[0].vat_rate)
            }

        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _getCustomer() {
    $.ajax({
        url: serverURL + 'getCustomer',
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            var html = '<option value="">--เลือกลูกค้า--</option>';
            for (var i = 0; i < res.length; i++) {
                html += ' <option value="' + res[i].code + '">' + res[i].code + '~' + res[i].name_1 + '</option>'
            }

            $('.select_cust').html(html);
            $('.select_cust').select2({
                theme: "bootstrap"
            });
        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _getBrand() {
    $.ajax({
        url: serverURL + 'getBrand',
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            var html = '<option value="">--เลือกแบรนด์--</option>';
            for (var i = 0; i < res.length; i++) {
                html += ' <option value="' + res[i].code + '">' + res[i].code + '~' + res[i].name_1 + '</option>'
            }

            $('.select_brand').html(html);
            $('.select_brand').select2({
                theme: "bootstrap"
            });
        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _getSaler() {
    $.ajax({
        url: serverURL + 'getSaler',
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            var html = '<option value="">--พนักงานขาย--</option>';
            for (var i = 0; i < res.length; i++) {
                html += ' <option value="' + res[i].code + '">' + res[i].code + '~' + res[i].name_1 + '</option>'
            }

            $('.select_sale').html(html);
            $('.select_sale').select2({
                theme: "bootstrap"
            });
        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _alertMsgBox() {
    $.ajax({
        url: serverURL + 'getAlertMsg',
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            if (res.length > 0) {
                var html = 'เอกสาร รหัส <br>';
                for (var i = 0; i < res.length; i++) {
                    var stat = '';
                    if (res[i].status == 0) {
                        stat = '<b style="color:red">ไม่อนุมัติ</b> หมายเหตุ:' + res[i].reason;
                    } else if (res[i].status == 1) {
                        stat = '<b style="color:#ffff99">รออนุมัติ</b>';
                    } else if (res[i].status == 2) {
                        stat = '<b style="color:#66ffff">อนุมัติแล้ว</b>';
                    }
                    html += res[i].doc_no + ' สถานะ: ' + stat + '<br>';
                }
                $("#alert-doc-msg").html(html);
                $('#showAlertMsg').show();
            }

        },
        error: function (res) {
            console.log(res)
        },
    });
}

function _gotocreate() {
    $('#doc_list').hide();
    $('#create_doc').show();
    var currentdate = new Date();
    var datetime = 'SD' + currentdate.getFullYear() + '' + (currentdate.getMonth() + 1) + '' + currentdate.getDate() + '' + currentdate.getHours() + '' + currentdate.getMinutes() + '' + currentdate.getSeconds() + '' + uuidv4().toUpperCase()
    var username = $('#user_namex').val();
    var user_code = $('#user_code').val();
    $('#doc_no').val(datetime);
    $('#doc_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
    $('#remark').val('');
    $('#sale_type').val('0')
    $('#vat_type').val('0')
    if (userbranch == 'all') {
        $('#branch_code').val('')
    } else {
        $('#branch_code').val(userbranch)
    }
    $('#project_name').val('')
    $('#contact').val('')
    $("#radio_rival1").prop("checked", true);
    $('#user_creator').val(user_code + '~' + username)
    $('#cust_code').val('');
    $('#contactor').val('');
    $('#saler_code').val('').trigger('change');
    $('#total_exp_discount').html('0.00');
    $('#total_exp_each').html('0.00');
    $('#total_selling_price').html('0.00');
    item_detail = [
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

    _displayTable()
}

function _backtolist() {
    $('#doc_list').show();
    $('#create_doc').hide();
}

function _saveDocApprove() {
    var doc_no = $('#doc_no').val();
    var doc_date = $('#doc_date').val();
    var cust_code = $('#cust_code').val();
    var contactor = $('#contactor').val();
    var saler_code = $('#saler_code').val();
    var sale_type = $('#sale_type').val();
    var vat_type = $('#vat_type').val();
    var vat_rate = $('#vat_rate').val();
    var remark = $('#remark').val();
    var branch = $('#branch_code').val()
    var project_name = $('#project_name').val();
    var contact = $('#contact').val();
    var radioValue = $("input[name='radio_rival']:checked").val();
    var branch_code = ''
    var branch_name = ''

    var creator = $('#user_creator').val();
    var creator_code = '';
    if (creator != '') {
        creator_code = creator.split('~')[0]
    }
    if (branch != '') {
        branch_code = branch.split('~')[0]
        branch_name = " สาขา " + branch.split('~')[1]
    }
    var details = [];
    var msg = '';
    if (doc_no == '') {
        msg += 'เลขที่เอกสาร \n'
    }
    if (doc_date == '') {
        msg += 'วันที่เอกสาร \n'
    }
    if (cust_code == '') {
        msg += 'รหัสลูกค้า \n'
    }
    if (branch_code == '') {
        msg += 'สาขา \n'
    }
    for (var i = 0; i < item_detail.length; i++) {
        if (item_detail[i].cost_qty <= 0) {
            msg += item_detail[i].item_code + ' จำนวน \n'
        }
        if (item_detail[i].cost_factory_price == '') {
            item_detail[i].cost_factory_price = 0;
        }
        if (item_detail[i].sale_price <= 0) {
            msg += item_detail[i].item_code + ' ราคาขาย \n'
        }
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
                    unit_name: item_detail[i].unit_name,
                    stand_value: item_detail[i].stand_value,
                    divide_value: item_detail[i].divide_value,
                    purchases: item_detail[i].purchases,
                    ratio: item_detail[i].ratio,
                    cost_qty: removeCommas(item_detail[i].cost_qty.toFixed(2)),
                    cost_weight: removeCommas(item_detail[i].cost_weight),
                    cost_sum_weight: removeCommas(item_detail[i].cost_sum_weight.toFixed(2)),
                    cost_factory_price: removeCommas(item_detail[i].cost_factory_price.toFixed(2)),
                    cost_sum_cost: removeCommas(item_detail[i].cost_sum_cost.toFixed(2)),
                    exp_discount: item_detail[i].exp_discount,
                    sale_discount: item_detail[i].sale_discount,
                    exp_car: removeCommas(item_detail[i].exp_car.toFixed(2)),
                    exp_other: removeCommas(item_detail[i].exp_other.toFixed(2)),
                    exp_doc: removeCommas(item_detail[i].exp_doc.toFixed(2)),
                    exp_tax: removeCommas(item_detail[i].exp_tax.toFixed(2)),
                    exp_other2: removeCommas(item_detail[i].exp_other2.toFixed(2)),
                    exp_each: removeCommas(item_detail[i].exp_each.toFixed(2)),
                    sale_sum_lao_cost: removeCommas(item_detail[i].sale_sum_lao_cost.toFixed(2)),
                    sale_sum_cost: removeCommas(item_detail[i].sale_sum_cost.toFixed(2)),
                    sale_price: removeCommas(item_detail[i].sale_price.toFixed(2)),
                    sale_sum_amount: removeCommas(item_detail[i].sale_sum_amount.toFixed(2)),
                    sale_profit: removeCommas(item_detail[i].sale_profit.toFixed(2)),
                    sale_percent_profit: removeCommas(item_detail[i].sale_percent_profit.toFixed(2)),
                    selling_price: removeCommas(item_detail[i].selling_price.toFixed(2)),
                }
                details.push(json_detail);
            }
        }
//console.log(details)
        if (details.length > 0) {
            var total_cost_qty = $('#total_cost_qty').html();
            var total_cost_weight = $('#total_cost_weight').html();
            var total_cost_sum_weight = $('#total_cost_sum_weight').html();
            var total_cost_purchases = $('#total_cost_purchases').html();
            var total_cost_factory_price = $('#total_cost_factory_price').html();
            var total_cost_sum_cost = $('#total_cost_sum_cost').html();
            var total_exp_discount = '0.00';
            var total_selling_price = $('#total_selling_price').html();
            var total_exp_car = $('#total_exp_car').html();
            var total_exp_other = $('#total_exp_other').html();
            var total_exp_doc = $('#total_exp_doc').html();
            var total_exp_tax = $('#total_exp_tax').html();
            var total_exp_other2 = $('#total_exp_other2').html();
            var total_exp_each = $('#total_exp_each').html();
            var total_sale_sum_lao_cost = $('#total_sale_sum_lao_cost').html();
            var total_sale_sum_cost = $('#total_sale_sum_cost').html();
            var total_sale_price = $('#total_sale_price').html();
            var total_sale_sum_amount = $('#total_sale_sum_amount').html();
            var total_sale_profit = $('#total_sale_profit').html();
            var total_sale_percent_profit = $('#total_sale_percent_profit').html();
            total_sale_percent_profit = total_sale_percent_profit.replace('%', '')

            var json_data = {
                doc_no: doc_no,
                project_name: project_name,
                contact: contact,
                contactor: contactor,
                radioValue: radioValue,
                doc_date: doc_date,
                cust_code: cust_code,
                creator_code: creator_code,
                saler_code: saler_code,
                branch_code: branch_code,
                sale_type: sale_type,
                vat_type: vat_type,
                vat_rate: vat_rate,
                total_cost_qty: removeCommas(total_cost_qty),
                total_cost_weight: 0,
                total_cost_sum_weight: 0,
                total_cost_purchases: removeCommas(total_cost_purchases),
                total_cost_factory_price: removeCommas(total_cost_factory_price),
                total_cost_sum_cost: 0,
                total_exp_discount: 0,
                total_exp_car: 0,
                total_exp_other: 0,
                total_exp_doc: 0,
                total_exp_tax: 0,
                total_exp_other2: 0,
                total_exp_each: removeCommas(total_exp_each),
                total_sale_sum_lao_cost: removeCommas(total_sale_sum_lao_cost),
                total_sale_sum_cost: removeCommas(total_sale_sum_cost),
                total_sale_price: removeCommas(total_sale_price),
                total_sale_sum_amount: removeCommas(total_sale_sum_amount),
                total_sale_profit: removeCommas(total_sale_profit),
                total_selling_price: removeCommas(total_selling_price),
                total_sale_percent_profit: removeCommas(total_sale_percent_profit),
                remark: remark,
                status: 1,
                data: JSON.stringify(details)
            }
            console.log(json_data)
            $.ajax({
                url: serverURL + 'saveDocSo',
                method: 'POST',
                data: json_data,
                success: function (res) {
                    console.log(res)
                    swal("บันทึกข้อมูลสำเร็จ", "", "success")
                    _getListData('');
                    $('#create_doc').hide();
                    $('#doc_list').show();
                },
                error: function (res) {
                    console.log(res)
                },
            });
        } else {
            alert('กรุณาเพิ่มรายละเอียด')
        }
    }
}


function _saveDoc() {
    var doc_no = $('#doc_no').val();
    var doc_date = $('#doc_date').val();
    var cust_code = $('#cust_code').val();
    var contactor = $('#contactor').val();
    var saler_code = $('#saler_code').val();
    var sale_type = $('#sale_type').val();
    var vat_type = $('#vat_type').val();
    var vat_rate = $('#vat_rate').val();
    var remark = $('#remark').val();
    var branch = $('#branch_code').val()
    var project_name = $('#project_name').val();
    var contact = $('#contact').val();
    var radioValue = $("input[name='radio_rival']:checked").val();
    var branch_code = ''
    var branch_name = ''

    var creator = $('#user_creator').val();
    var creator_code = '';
    if (creator != '') {
        creator_code = creator.split('~')[0]
    }
    if (branch != '') {
        branch_code = branch.split('~')[0]
        branch_name = " สาขา " + branch.split('~')[1]
    }
    var details = [];
    var msg = '';
    if (doc_no == '') {
        msg += 'เลขที่เอกสาร \n'
    }
    if (doc_date == '') {
        msg += 'วันที่เอกสาร \n'
    }
    if (cust_code == '') {
        msg += 'รหัสลูกค้า \n'
    }
    if (branch_code == '') {
        msg += 'สาขา \n'
    }
    for (var i = 0; i < item_detail.length; i++) {
        if (item_detail[i].cost_qty <= 0) {
            msg += item_detail[i].item_code + ' จำนวน \n'
        }
        if (item_detail[i].cost_factory_price == '') {
            item_detail[i].cost_factory_price = 0;
        }
        if (item_detail[i].sale_price <= 0) {
            msg += item_detail[i].item_code + ' ราคาขาย \n'
        }
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
                    unit_name: item_detail[i].unit_name,
                    stand_value: item_detail[i].stand_value,
                    divide_value: item_detail[i].divide_value,
                    purchases: item_detail[i].purchases,
                    ratio: item_detail[i].ratio,
                    cost_qty: removeCommas(item_detail[i].cost_qty.toFixed(2)),
                    cost_weight: removeCommas(item_detail[i].cost_weight),
                    cost_sum_weight: removeCommas(item_detail[i].cost_sum_weight.toFixed(2)),
                    cost_factory_price: removeCommas(item_detail[i].cost_factory_price.toFixed(2)),
                    cost_sum_cost: removeCommas(item_detail[i].cost_sum_cost.toFixed(2)),
                    exp_discount: item_detail[i].exp_discount,
                    sale_discount: item_detail[i].sale_discount,
                    exp_car: removeCommas(item_detail[i].exp_car.toFixed(2)),
                    exp_other: removeCommas(item_detail[i].exp_other.toFixed(2)),
                    exp_doc: removeCommas(item_detail[i].exp_doc.toFixed(2)),
                    exp_tax: removeCommas(item_detail[i].exp_tax.toFixed(2)),
                    exp_other2: removeCommas(item_detail[i].exp_other2.toFixed(2)),
                    exp_each: removeCommas(item_detail[i].exp_each.toFixed(2)),
                    sale_sum_lao_cost: removeCommas(item_detail[i].sale_sum_lao_cost.toFixed(2)),
                    sale_sum_cost: removeCommas(item_detail[i].sale_sum_cost.toFixed(2)),
                    sale_price: removeCommas(item_detail[i].sale_price.toFixed(2)),
                    sale_sum_amount: removeCommas(item_detail[i].sale_sum_amount.toFixed(2)),
                    sale_profit: removeCommas(item_detail[i].sale_profit.toFixed(2)),
                    sale_percent_profit: removeCommas(item_detail[i].sale_percent_profit.toFixed(2)),
                    selling_price: removeCommas(item_detail[i].selling_price.toFixed(2)),
                }
                details.push(json_detail);
            }
        }
//console.log(details)
        if (details.length > 0) {
            var total_cost_qty = $('#total_cost_qty').html();
            var total_cost_weight = $('#total_cost_weight').html();
            var total_cost_sum_weight = $('#total_cost_sum_weight').html();
            var total_cost_purchases = $('#total_cost_purchases').html();
            var total_cost_factory_price = $('#total_cost_factory_price').html();
            var total_cost_sum_cost = $('#total_cost_sum_cost').html();
            var total_exp_discount = '0.00';
            var total_selling_price = $('#total_selling_price').html();
            var total_exp_car = $('#total_exp_car').html();
            var total_exp_other = $('#total_exp_other').html();
            var total_exp_doc = $('#total_exp_doc').html();
            var total_exp_tax = $('#total_exp_tax').html();
            var total_exp_other2 = $('#total_exp_other2').html();
            var total_exp_each = $('#total_exp_each').html();
            var total_sale_sum_lao_cost = $('#total_sale_sum_lao_cost').html();
            var total_sale_sum_cost = $('#total_sale_sum_cost').html();
            var total_sale_price = $('#total_sale_price').html();
            var total_sale_sum_amount = $('#total_sale_sum_amount').html();
            var total_sale_profit = $('#total_sale_profit').html();
            var total_sale_percent_profit = $('#total_sale_percent_profit').html();
            total_sale_percent_profit = total_sale_percent_profit.replace('%', '')

            var json_data = {
                doc_no: doc_no,
                project_name: project_name,
                contact: contact,
                contactor: contactor,
                radioValue: radioValue,
                doc_date: doc_date,
                cust_code: cust_code,
                creator_code: creator_code,
                saler_code: saler_code,
                branch_code: branch_code,
                sale_type: sale_type,
                vat_type: vat_type,
                vat_rate: vat_rate,
                total_cost_qty: removeCommas(total_cost_qty),
                total_cost_weight: 0,
                total_cost_sum_weight: 0,
                total_cost_purchases: removeCommas(total_cost_purchases),
                total_cost_factory_price: removeCommas(total_cost_factory_price),
                total_cost_sum_cost: 0,
                total_exp_discount: 0,
                total_exp_car: 0,
                total_exp_other: 0,
                total_exp_doc: 0,
                total_exp_tax: 0,
                total_exp_other2: 0,
                total_exp_each: removeCommas(total_exp_each),
                total_sale_sum_lao_cost: removeCommas(total_sale_sum_lao_cost),
                total_sale_sum_cost: removeCommas(total_sale_sum_cost),
                total_sale_price: removeCommas(total_sale_price),
                total_sale_sum_amount: removeCommas(total_sale_sum_amount),
                total_sale_profit: removeCommas(total_sale_profit),
                total_selling_price: removeCommas(total_selling_price),
                total_sale_percent_profit: removeCommas(total_sale_percent_profit),
                remark: remark,
                status: 0,
                data: JSON.stringify(details)
            }
            console.log(json_data)
            $.ajax({
                url: serverURL + 'saveDocSo',
                method: 'POST',
                data: json_data,
                success: function (res) {
                    console.log(res)
                    swal("บันทึกข้อมูลสำเร็จ", "", "success")
                    _getListData('');
                    $('#create_doc').hide();
                    $('#doc_list').show();
                },
                error: function (res) {
                    console.log(res)
                },
            });
        } else {
            alert('กรุณาเพิ่มรายละเอียด')
        }
    }
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

$(document).ready(function () {
    _getSaler()
    _getBrand()
    _getUserBranch()

    setTimeout(function () {
        _getBranch()
    }, 500);
    var currentdate = new Date();
    $('#from_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
    $('#to_date').val(currentdate.getFullYear() + '-' + ('0' + (currentdate.getMonth() + 1)).slice(-2) + '-' + ('0' + currentdate.getDate()).slice(-2));
    $('.cust_box_click').on('click', function () {
        $('#modalCust').modal('show');
    });
    $('#showDetail').on('click', function () {
        _showDetail();
    });


    $(document).delegate('.detail_show', 'click', function (event) {
        var code = $(this).attr('data-index');

        $('.detail_' + code).toggle();
    });


});
function _Process() {
    var from_date = $('#from_date').val();
    var to_date = $('#to_date').val();
    var ar_code = $('#cust_code').val();
    var sale_code = $('#saler_code').val();
    var branch_code = $('#branch_code').val();
    var brand_code = $('#brand_code').val();
    item_detail = [];
    $.ajax({
        url: serverURL + 'getReportSA?fromdate=' + from_date + '&todate=' + to_date + '&custcode=' + ar_code + '&salecode=' + sale_code + '&brandcode=' + brand_code + '&branchcode=' + branch_code,
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res)
            item_detail = res;
            _showDetail();
        },
        error: function (res) {
            console.log(res)
        },
    });
}


function _showDetail() {

    if (item_detail.length > 0) {
        var html = '';
        html += `<table class="table table-hover "><thead><tr style='background-color:#e3f2fd'>`
        html += `                   <th class="text-center"></th>
                                    <th class="text-center">สาขา</th>
                                    <th class="text-center">วันที่</th>
                                    <th nowrap class="text-center">ใบอนุมัติ</th>
                                    <th nowrap class="text-center">ใบเสนอราคา</th>
                                    <th nowrap class="text-center">ใบสั่งซื้อสั่งจอง</th>
                                    <th nowrap class="text-center">ร้านค้า</th>
                                    <th nowrap class="text-center">สถานที่ส่ง</th>
                                    <th nowrap class="text-center">แบรนด์</th>
                                    <th nowrap class="text-center">รายการสินค้า</th>
                                    <th nowrap class="text-center">ใบสั่งซื้อ</th>
                                    <th nowrap class="text-center">ใบขาย</th>
                                    <th nowrap class="text-center">ยอดซื้อ</th>
                                    <th nowrap class="text-center">ยอดขาย</th>
                                    <th nowrap class="text-center">กำไร</th>
                                    <th nowrap class="text-center">%กำไร</th>
                                    <th nowrap class="text-center">จำนวนรถ</th>
                                    <th nowrap class="text-center">พนักงานขาย</th>
                                </tr>`


        html += ` </thead><tbody>`
        var total_cost = 0;
        var total_sale = 0;
        var total_bill = 0;
        var total_profit = 0;
        var total_profit_percent = 0;
        item_detail.forEach(function (data, index) {
            total_cost += data.total_sum_cost;
            total_sale += data.total_sum_amount;
            total_bill += 1;
            total_profit += data.total_profit;
            total_profit_percent += data.total_profit_percent;
            if (index % 2) {
                html += `<tr style='background-color:#f1f8e9'>`
            } else {
                html += `<tr style='background-color:#fff8e1'>`
            }
            html += `               <td class="text-center detail_show" nowrap data-index='${index}'><button class='btn btn-warning'>รายละเอียด</button></td>
                                    <td class="text-left" nowrap>${data.branch_name}</td>
                                    <td class="text-left" nowrap>${formatdate(data.doc_date)}</td>
                                    <td class="text-left" nowrap>${data.sa_doc_no}</td>
                                    <td class="text-left" nowrap>${data.sa_doc_no}</td>
                                    <td class="text-left" nowrap>${data.ba_doc_no}</td>
                                    <td class="text-left" nowrap >${data.cust_name}</td>
                                    <td class="text-left" nowrap>${data.contactor}</td>
                                    <td class="text-left" nowrap>${data.brand_name}</td>
                                    <td class="text-left" nowrap>${data.group_name}</td>
                                    <td class="text-left" nowrap>${data.po_doc_no}</td>
                                    <td class="text-left" nowrap>${data.inv_doc_no}</td>
                                    <td class="text-right" nowrap>${formatnumber(data.total_sum_cost)}</td>
                                    <td class="text-right" nowrap>${formatnumber(data.total_sum_amount)}</td>
                                    <td class="text-right" nowrap>${formatnumber(data.total_profit)}</td>
                                    <td class="text-right" nowrap>${formatnumber(data.total_profit_percent)}%</td>
                                    <td class="text-right" nowrap>1</td>
                                    <td class="text-left" nowrap>${data.sale_name}</td>
                                </tr>`

            html += `       <tr class="detail_${index}" style='display:none;background-color:#e3f2fd'>
                                    <td class="text-center" nowrap>#</td>
                                    <td class="text-center" colspan='5' nowrap>สินค้า</td>
                                    <td class="text-center" nowrap>แบรนด์</td>
                                    <td class="text-center" nowrap>รายการสินค้า</td>
                                    <td class="text-center" nowrap>หน่วยนับ</td>
                                    <td class="text-center" nowrap>ต้นทุน</td>
                                    <td class="text-center" nowrap>ราคาขาย</td>
                                    <td class="text-center" nowrap>ส่วนลด</td>
                                    <td class="text-center" nowrap>จำนวน</td>
                                    <td class="text-center" nowrap>รวมทุน</td>
                                    <td class="text-center" nowrap>รวมขาย</td>
                                    <td class="text-center" nowrap>กำไร</td>
                                    <td class="text-center" nowrap>%กำไร</td>
                                    <td class="text-center" nowrap></td>
                                </tr>`
            data.detail.forEach(function (detail, i) {

                html += `       <tr class="detail_${index}" style='display:none'>
                                    <td class="text-center"  nowrap>${i + 1}</td>
                                    <td class="text-left" colspan='5' nowrap>${detail.item_code}~${detail.item_name}</td>
                                    <td class="text-left" nowrap>${detail.brand_name}</td>
                                    <td class="text-left" nowrap>${detail.group_name}</td>
                                    <td class="text-center" nowrap>${detail.unit_name}</td>
                                    <td class="text-right" nowrap>${formatnumber(detail.cost)}</td>
                                    <td class="text-right" nowrap>${formatnumber(detail.price)}</td>
                                    <td class="text-right" nowrap>${detail.discount}</td>
                                    <td class="text-right" nowrap>${formatnumber2(detail.qty)}</td>
                                    <td class="text-right" nowrap>${formatnumber(detail.sum_cost)}</td>
                                    <td class="text-right" nowrap>${formatnumber(detail.sum_amount)}</td>
                                    <td class="text-right" nowrap>${formatnumber(detail.profit)}</td>
                                    <td class="text-right" nowrap>${formatnumber(detail.profit_percent)}%</td>
                                    <td class="text-right" nowrap></td>
                                </tr>`


            });
            html += `</tbody>`
        });
    }
    html += `<tfoot>
    <tr style='background-color:#e3f2fd'>
     <th class="text-center"></th>
                        <th class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-center"></th>
                                    <th nowrap class="text-right">ยอดรวม</th>
                                    <th nowrap class="text-right">${formatnumber(total_cost)}</th>
                                    <th nowrap class="text-right">${formatnumber(total_sale)}</th>
                                    <th nowrap class="text-right">${formatnumber(total_profit)}</th>
                                    <th nowrap class="text-right">${formatnumber(total_profit_percent)}</th>
                                    <th nowrap class="text-right">${formatnumber2(total_bill)}</th>
                                    <th nowrap class="text-center"></th>
    </tr>
                     </tfoot>
                    `;
    html += `</table>`
    if (item_detail.length > 0) {
        $('#show_list_detail').html(html);
    }else{
        $('#show_list_detail').html('');
    }


}

function _getUserBranch() {

    $.ajax({
        url: serverURL + 'getUserBranch',
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res)
            if (res[0].branch_code != '') {
                userbranch = res[0].branch_code + '~' + res[0].branch_name
            }
            $('#user_branch').val(res[0].branch_code)
            if (res[0].code.toUpperCase() == 'SUPERADMIN' || res[0].change_branch_code == '1') {
                $('#user_branch').val('all')
                userbranch = 'all'
            }

        },
        error: function (res) {
            console.log(res)
        },
    });
}

function  _getBranch() {
    var branchcode = $('#user_branch').val();
    console.log(branchcode)
    $.ajax({
        url: serverURL + 'getBranch',
        method: 'GET',
        cache: false,
        success: function (res) {

            console.log(res)
            var html = '<option value="">--เลือกสาขา--</option>';
            for (var i = 0; i < res.length; i++) {
                html += ' <option value="' + res[i].code + '">' + res[i].code + '~' + res[i].name_1 + '</option>'
            }
            console.log('userbranch ' + userbranch)
            $('.branch_code').html(html);
            if (userbranch == 'all') {
                $('.branch_code').removeAttr('disabled')
                $('.branch_code').val('')
            }


        },
        error: function (res) {
            console.log(res)
        },
    });
}
function formatnumber2(nStr)
{

    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    var data = x1 + x2;
    return data;
}
function formatnumber(nStr)
{
    nStr = parseFloat(nStr).toFixed(2);
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    var data = x1 + x2;
    return data;
}
function formatdate(date) {

    var d = new Date(date);
    var dd = d.getDate();
    var mm = d.getMonth() + 1;
    var yy = d.getFullYear();
    return dd + "/" + mm + "/" + yy;
}
function _searchCopyCust() {
    var search_copy_cust_input = $('#search_copy_cust_input').val()
    // console.log(search_cust_input)

    $.ajax({
        url: serverURL + 'getCustomer?search=' + search_copy_cust_input,
        method: 'GET',
        cache: false,
        success: function (res) {
            var html = '';
            for (var i = 0; i < res.length; i++) {
                html += "<li class = 'list-group-item list-group-item-action select-copy-cust' data-code='" + res[i].code + "' data-name='" + res[i].name_1 + "'> " + res[i].code + '~' + res[i].name_1 + " </li>"
            }
            $('#list_search_copy_cust').html(html)

        },
        error: function (res) {
            console.log(res)
        },
    });
}

$(document).delegate('.select-cust', 'click', function (event) {
    var code = $(this).attr('data-code')
    $('#cust_code').val(code);
    $('#modalCust').modal('hide');
});
$(document).delegate('.select-copy-cust', 'click', function (event) {
    var code = $(this).attr('data-code')
    $('#copy_cust_code').val(code);
    $('#copy_contactor').val('');
    $('#modalCopyCust').modal('hide');
});
$(document).delegate('.select-contact', 'click', function (event) {
    var name = $(this).attr('data-name')
    $('#contactor').val(name);
    $('#modalContactor').modal('hide');
});
$(document).delegate('.select-copy-contact', 'click', function (event) {
    var name = $(this).attr('data-name')
    $('#copy_contactor').val(name);
    $('#modalCopyContactor').modal('hide');
});
function _searchContact() {
    var search_contactor_input = $('#search_contactor_input').val();
    var cust_code = $('#cust_code').val();
    // console.log(search_cust_input)

    $.ajax({
        url: serverURL + 'getContact?cust=' + cust_code + '&search=' + search_contactor_input,
        method: 'GET',
        cache: false,
        success: function (res) {
            console.log(res)
            var html = '';
            for (var i = 0; i < res.length; i++) {
                if (res[i].name == undefined) {
                    res[i].name = '';
                }
                if (res[i].telephone == undefined) {
                    res[i].telephone = '';
                }
                if (res[i].address == undefined) {
                    res[i].address = '';
                }
                html += "<li class = 'list-group-item list-group-item-action select-contact' data-name='" + res[i].name + "' data-tel='" + res[i].telephone + "' data-address='" + res[i].address + "'> " + res[i].name + ' | ' + res[i].telephone + "|" + res[i].address + "</li>"
            }
            $('#list_search_contact').html(html)

        },
        error: function (res) {
            console.log(res)
        },
    });
}
function _searchCust() {
    var search_cust_input = $('#search_cust_input').val()
    // console.log(search_cust_input)

    $.ajax({
        url: serverURL + 'getCustomer?search=' + search_cust_input,
        method: 'GET',
        cache: false,
        success: function (res) {
            var html = '';
            for (var i = 0; i < res.length; i++) {
                html += "<li class = 'list-group-item list-group-item-action select-cust' data-code='" + res[i].code + "' data-name='" + res[i].name_1 + "'> " + res[i].code + '~' + res[i].name_1 + " </li>"
            }
            $('#list_search_cust').html(html)

        },
        error: function (res) {
            console.log(res)
        },
    });
}


function printData(data) {
    window.open('../print/index.jsp' + "?docno=" + data, "_blank");
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

function _getListData(data) {
    var branch = $('#user_branch').val();
    $.ajax({
        url: serverURL + 'getDocSo?branch=' + branch + '&search=' + data,
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
function _showListDetail(data) {
    var html = '';
    for (var i = 0; i < data.length; i++) {

        var saletype = ''
        var vattype = ''
        var salecolor = 'black'
        var vatcolor = 'black'
        if (data[i].sale_type == '0') {
            saletype = 'ขายเงินเชื่อ'
            salecolor = 'blue'
        } else if (data[i].sale_type == '1') {
            saletype = 'ขายเงินสด'
            salecolor = 'green'
        }

        if (data[i].vat_type == '0') {
            vattype = 'ภาษีแยกนอก'
            vatcolor = 'blue'
        } else if (data[i].vat_type == '1') {
            vattype = 'ภาษีรวมใน'
            vatcolor = 'green'
        } else if (data[i].vat_type == '2') {
            vattype = 'ภาษีอัตราศูนย์'
            vatcolor = 'red'
        }

        if (data[i].contactor == undefined) {
            data[i].contactor = '';
        }
        html += '<div class="card">';
        html += '   <div class="card-body">'
        html += '        <div class="row">'
        html += '            <div class="col-sm-6 "><h5 class=" text-left" ><b>' + data[i].doc_no + '</b></h5></div>'
        html += '            <div class="col-sm-6 "><h5 class=" text-right">วันที่ ' + data[i].doc_date + '</h5></div>'
        html += '        </div>'
        html += '        <div class="row">'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">ชื่อโครงการ:<b > ' + data[i].project_name + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">ผู้ติดต่อ:<b > ' + data[i].contact + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">ประเภทการขาย:<b style="color:' + salecolor + '"> ' + saletype + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">ประเภทภาษี:<b style="color:' + vatcolor + '"> ' + vattype + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">สาขา:<b > ' + data[i].branch_code + '~' + data[i].branch_name + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">ลูกค้า:<b > ' + data[i].cust_code + '~' + data[i].cust_name + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">รวมมูลค่า:<b > ' + formatNumber(data[i].total_sale_sum_amount) + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">ผู้เปิดใบอนุมัติ: <b > ' + data[i].creator_code + '~' + data[i].creator_name + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">พนักงานขาย: <b > ' + data[i].saler_code + '~' + data[i].saler_name + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">สถานที่ส่ง:<b > ' + data[i].contactor + '</b></h6>'
        html += '            </div>'
        html += '            <div class="col-sm-6">'
        html += '                <h6 class=" text-left">หมายเหตุ:<b > ' + data[i].remark + '</b></h6>'
        html += '            </div>'
        if (data[i].reason != '' && data[i].reason != undefined) {
            html += '            <div class="col-sm-6">'
            html += '                <h6 class=" text-left"><b style="color:red">ไม่ผ่านอนุมัติ หมายเหตุ: ' + data[i].reason + '</b></h6>'
            html += '            </div>'
        }
        html += '        </div>'
        html += '        <button class="btn btn-success send_approve" data-docno="' + data[i].doc_no + '"><i class="fa fa-share "></i> ส่งไปอนุมัติ</button>'
        html += '        <button class="btn btn-info show_detail" data-docno="' + data[i].doc_no + '"><i class="fa fa-search"></i> รายละเอียด</button>'
        html += '        <button class="btn copy-doc" style="background-color:#aa00c7;color:#fff" data-docno="' + data[i].doc_no + '"><i class="fa fa-clone"></i> คัดลอกเอกสาร</button>'

        html += '        <button class="btn btn-danger del_doc"  data-docno="' + data[i].doc_no + '"><i class="fa fa-trash"></i> ลบเอกสาร</button>'
        html += '    </div>'
        html += '</div>'
    }
    $('#show_list_detail').html(html);
}

function delLine(data) {
    console.log(data)
    item_detail.splice(data, 1);
    _displayTable()

}

function _displayTable() {
    var html = '';
    var total_cost_qty = 0.00;
    var total_cost_weight = 0.00;
    var total_cost_sum_weight = 0.00;
    var total_cost_purchases = 0.00;
    var total_cost_factory_price = 0.00;
    var total_cost_sum_cost = 0.00;
    var total_selling_price = 0.00;
    var total_exp_discount = 0.00;
    var total_exp_car = 0.00;
    var total_exp_other = 0.00;
    var total_exp_doc = 0.00;
    var total_exp_tax = 0.00;
    var total_exp_other2 = 0.00;
    var total_exp_each = 0.00;
    var total_sale_sum_lao_cost = 0.00;
    var total_sale_sum_cost = 0.00;
    var total_sale_price = 0.00;
    var total_sale_sum_amount = 0.00;
    var total_sale_profit = 0.00;
    var total_sale_percent_profit = 0.00;
    var row_purchase = 0;
    var row_cost_factory = 0;
    var row_exp_each = 0;
    var row_cost_lao = 0;
    var row_sale_price = 0;
    var row_selling_price = 0;
    for (var i = 0; i < item_detail.length; i++) {
        if (item_detail[i].purchases > 0) {
            row_purchase += 1;
        }
        if (item_detail[i].cost_factory_price > 0) {
            row_cost_factory += 1;
        }
        if (item_detail[i].exp_each > 0) {
            row_exp_each += 1;
        }
        if (item_detail[i].sale_sum_lao_cost > 0) {
            row_cost_lao += 1;
        }
        if (item_detail[i].sale_price > 0) {
            row_sale_price += 1;
        }
        if (item_detail[i].selling_price > 0) {
            row_selling_price += 1;
        }

        total_cost_qty += parseFloat(item_detail[i].cost_qty);
        total_cost_purchases += parseFloat(item_detail[i].purchases);
        total_cost_factory_price += parseFloat(item_detail[i].cost_factory_price);
        total_exp_each += parseFloat(item_detail[i].exp_each);
        total_cost_sum_cost += parseFloat(item_detail[i].cost_sum_cost);
        total_sale_sum_cost += parseFloat(item_detail[i].sale_sum_cost);
        total_sale_sum_amount += parseFloat(item_detail[i].sale_sum_amount);
        total_sale_profit += parseFloat(item_detail[i].sale_profit);
        total_selling_price += parseFloat(item_detail[i].selling_price);
        total_sale_sum_lao_cost += parseFloat(item_detail[i].sale_sum_lao_cost);
        total_sale_price += parseFloat(item_detail[i].sale_price);
        html += '<tr>'
        html += '<td class="text-center">' + (i + 1) + '</td>'
        if (item_detail[i].item_code != '') {
            html += '<td class="text-left btn-search" style="cursor:pointer" index="item_' + i + '">' + item_detail[i].item_code + '~' + item_detail[i].item_name + '</td>'
        } else {
            html += '<td class="text-left btn-search" style="cursor:pointer" index="item_' + i + '"></td>'
        }
        html += '<td class="text-center">' + item_detail[i].unit_name + '</td>'
        html += '<td class="text-right editIt" index="costqty_' + i + '">' + formatNumber(item_detail[i].cost_qty) + '</td>'
        html += '<td class="text-right" style="color:#9E00FF">' + formatNumber(item_detail[i].purchases) + '</td>'
        html += '<td class="text-right editIt" index="costfactory_' + i + '">' + formatNumber(item_detail[i].cost_factory_price) + '</td>'

        html += '<td class="text-right editIt" index="exp_' + i + '">' + formatNumber(item_detail[i].exp_each) + '</td>'
        html += '<td class="text-right" >' + formatNumber(item_detail[i].sale_sum_lao_cost) + '</td>'
        html += '<td class="text-right" >' + formatNumber(item_detail[i].sale_sum_cost) + '</td>'
        html += '<td class="text-right editIt" index="saleprice_' + i + '">' + formatNumber(item_detail[i].sale_price) + '</td>'
        html += '<td class="text-right editIt" index="discount_' + i + '">' + item_detail[i].sale_discount + '</td>'
        html += '<td class="text-right">' + formatNumber(item_detail[i].selling_price) + '</td>'
        html += '<td class="text-right">' + formatNumber(item_detail[i].sale_sum_amount) + '</td>'
        html += '<td class="text-right">' + formatNumber(item_detail[i].sale_profit) + '</td>'
        html += '<td class="text-right">' + formatNumber(item_detail[i].sale_percent_profit) + "%" + '</td>'
        html += '<td class="text-center"><i class="fa fa-minus-circle" style="cursor:pointer;color:red" onclick="delLine(' + i + ')"></i></td>'
        html += '</tr>'
    }
    if (row_purchase > 0) {
        total_cost_purchases = total_cost_purchases / row_purchase;
    } else {
        total_cost_purchases = 0.00;
    }
    if (row_cost_factory > 0) {
        total_cost_factory_price = total_cost_factory_price / row_cost_factory;
    } else {
        total_cost_factory_price = 0.00;
    }
    if (row_exp_each > 0) {
        total_exp_each = total_exp_each / row_exp_each;
    } else {
        total_exp_each = 0.00;
    }
    if (row_cost_lao > 0) {
        total_sale_sum_lao_cost = total_sale_sum_lao_cost / row_cost_lao;
    } else {
        total_sale_sum_lao_cost = 0.00;
    }
    if (row_sale_price > 0) {
        total_sale_price = total_sale_price / row_sale_price;
    } else {
        total_sale_price = 0.00;
    }
    if (row_selling_price > 0) {
        total_selling_price = total_selling_price / row_selling_price;
    } else {
        total_selling_price = 0.00;
    }

    total_sale_percent_profit = (parseFloat(total_sale_profit / total_sale_sum_amount) * 100);
    if (isNaN(total_sale_sum_lao_cost)) {
        total_sale_sum_lao_cost = 0.00
    }
    if (isNaN(total_sale_price)) {
        total_sale_price = 0.00
    }
    if (isNaN(total_sale_percent_profit)) {
        total_sale_percent_profit = 0.00
    }


    $('#total_cost_qty').html(formatNumber(total_cost_qty))

    $('#total_cost_purchases').html(formatNumber(total_cost_purchases))
    $('#total_cost_factory_price').html(formatNumber(total_cost_factory_price))

    $('#total_exp_each').html(formatNumber(total_exp_each))

    $('#total_sale_sum_lao_cost').html(formatNumber(total_sale_sum_lao_cost))
    $('#total_sale_sum_cost').html(formatNumber(total_sale_sum_cost))
    $('#total_sale_price').html(formatNumber(total_sale_price))
    $('#total_selling_price').html(formatNumber(total_selling_price))
    $('#total_sale_sum_amount').html(formatNumber(total_sale_sum_amount))
    $('#total_sale_profit').html(formatNumber(total_sale_profit))
    $('#total_sale_percent_profit').html(formatNumber(total_sale_percent_profit) + "%")

    $('#item_detail').html(html);
}
