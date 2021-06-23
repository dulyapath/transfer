
$(document).ready(function () {



    var doc_no = $('#doc_no').val()
    $.ajax({
        url: '../getDocRimDetail?docno=' + doc_no,
        method: 'GET',
        success: function (res) {

            console.log(res)
            var __rsHTML = '';

            __rsHTML += "<div class='divHeader' style='padding: 5px 0'>";
            if (res.length > 0) {

                __rsHTML += "<div class='row' style='font-size: 18px;'>";
                __rsHTML += "<div class='col-sm-12 text-center'>";
                __rsHTML += "<p class='text-center'>รับคืนสินค้าจากการเบิก</p>";
                __rsHTML += "</div>";
                __rsHTML += "</div>";
                __rsHTML += "<div class='row' style='font-size: 18px;'>";
                __rsHTML += "<div class='col-sm-6 '>";
                __rsHTML += "<p>เลขที่เอกสาร: " + res[0].doc_no + "</p>";
                __rsHTML += "</div>";
                __rsHTML += "<div class='col-sm-6 '>";
                __rsHTML += "<p class='text-right'>วันที่: " + res[0].doc_date + " เวลา: " + res[0].doc_time + "</p>";
                __rsHTML += "</div>";
                __rsHTML += "</div>";
                __rsHTML += "<div class='row' style='font-size: 18px;'>";
                __rsHTML += "<div class='col-sm-8 '>";
                __rsHTML += "<p >สาขา: " + res[0].branch_name + "(" + res[0].branch_code + ") คลัง: " + res[0].wh_name + "(" + res[0].wh_code + ")   ที่เก็บ: " + res[0].shelf_name + "(" + res[0].shelf_code + ")</p>";
                __rsHTML += "</div>";
                __rsHTML += "<div class='col-sm-4 text-right'>";
                __rsHTML += "<p >เลขที่ใบเบิก: " + res[0].billing_no + " </p>";
                __rsHTML += "</div>";
                __rsHTML += "</div>";
                __rsHTML += "<div class='row' style='font-size: 18px;'>";
                __rsHTML += "<div class='col-sm-6 '>";
                __rsHTML += "<p >หมายเหตุ: " + res[0].remark + " </p>";
                __rsHTML += "</div>";


                __rsHTML += "<div class='col-sm-6 '>";
                __rsHTML += "<p class='text-right'>ผู้สร้าง:" + res[0].creator_code + '~' + res[0].creator_name + "</p>";
                __rsHTML += "</div>";
                __rsHTML += "</div>";

                $("#content-list").html(__rsHTML);

                _displayTable(res[0].detail)

            }


        },
        error: function (res) {
            console.log(res)
        },
    });
});
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

function _displayTable(item_detail) {
    var html = '';

    for (var i = 0; i < item_detail.length; i++) {

        html += '<tr>'
        html += '<td class="text-center">' + (i + 1) + '</td>'
        html += '<td nowrap class="text-left ">' + item_detail[i].item_code + '</td>'
        html += '<td class="text-left ">' + item_detail[i].item_name + '</td>'
        html += '<td class="text-center">' + item_detail[i].unit_name + '(' + item_detail[i].unit_code + ') </td>'
        html += '<td class="text-right">' + formatNumber(parseFloat(item_detail[i].qty)) + '</td>'
        html += '<td class="text-right">' + formatNumber(parseFloat(item_detail[i].price)) + '</td>'
        html += '<td class="text-right">' + formatNumber(parseFloat(item_detail[i].sum_amount)) + '</td>'
        html += '</tr>'
    }

    $('#item_detail').html(html);

    setTimeout(function () {
        window.print();
        window.close();
    }, 2000);
}

