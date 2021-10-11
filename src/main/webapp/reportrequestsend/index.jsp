<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%    String pageName = "รายงานขอเบิก-เบิกสินค้า";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "reportrequestsend");
    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/report/requestsend.js"));
    HttpSession _sess = request.getSession();
%>

<style>
    table {
        color: #000 !important;
    }
</style>>
<jsp:include  page="../theme/header.jsp" flush="true" />

<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=session.getAttribute("branch_code")%>" id="user_branch">
<input type="hidden" value="${user_name}" id="user_namex">
<input type="hidden" id="hSubLink" value="${sublink}">
<div class="row">
    <div class="col-lg-6">
        <div class="form-group">
            <label>เลขที่เอกสาร</label>
            <input type="text" class="form-control" id="search" >
        </div>
    </div>
    <div class="col-lg-3">
        <div class="form-group">
            <label>สถานะ</label>
            <select class="form-control" id="doc_status" >
                <option value="">--ทั้งหมด--</option>
                <option value="0">Incomplete</option>
                <option value="1">Complete</option>
            </select>
        </div>

    </div>  
</div>  
<div class="row">
    <div class="col-lg-3">
        <div class="form-group">
            <label>วันที่ขอเบิก</label>
            <input type="date" class="form-control" id="req_from_date" placeholder="จากวันที่">
        </div>
    </div>
    <div class="col-lg-3">
        <div class="form-group">
            <label>ถึงวันที่</label>
            <input type="date" class="form-control" id="req_to_date" placeholder="จากวันที่">
        </div>
    </div>
    <div class="col-lg-3">
        <div class="form-group">
            <label>วันที่เบิก</label>
            <input type="date" class="form-control" id="send_from_date" placeholder="จากวันที่">
        </div>
    </div>
    <div class="col-lg-3">
        <div class="form-group">
            <label>ถึงวันที่</label>
            <input type="date" class="form-control" id="send_to_date" placeholder="จากวันที่">
        </div>
    </div>
</div>
<div class="row">
    <div class="col-lg-4">
        <div class="form-group">
            <label>ต้นทางสาขา</label>
            <select class="form-control" id="from_branch_code"></select>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="form-group">
            <label>คลัง</label>
            <select class="form-control" id="from_wh_code"></select>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="form-group">
            <label>ที่เก็บ</label>
            <select class="form-control" id="from_shelf_code"><option value=''>---ทั้งหมด---</option></select>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-lg-4">
        <div class="form-group">
            <label>ปลายทางสาขา</label>
            <select class="form-control" id="to_branch_code"></select>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="form-group">
            <label>คลัง</label>
            <select class="form-control" id="to_wh_code"></select>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="form-group">
            <label>ที่เก็บ</label>
            <select class="form-control" id="to_shelf_code"><option value=''>---ทั้งหมด---</option></select>
        </div>
    </div>
</div>
<div class="row" style="margin-bottom: 0.5rem">
    <div class="col-md-12">
        <button class="btn btn-primary" id="btn-search"><i class="fas fa-play"></i> ประมวลผล</button>
        <button class="btn btn-warning" id="btn-showdetail"><i class="fas fa-file"></i> รายละเอียด</button>
        <button class="btn btn-info" id="btn-copy"><i class="fas fa-copy"></i> Copy to clipboard</button>
    </div>

</div>
<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-striped table-bordered" id="tableId">
                <thead>
                    <tr >
                        <td nowrap rowspan="2" style="vertical-align : middle;text-align:center;background-color: #dbdbdb"></td>
                        <td nowrap rowspan="2" style="vertical-align : middle;text-align:center;background-color: #dbdbdb">เลขที่ใบขอ</td>
                        <td nowrap rowspan="2" style="vertical-align : middle;text-align:center;background-color: #dbdbdb">วันที่ขอ</td>
                        <td nowrap rowspan="2" style="vertical-align : middle;text-align:center;background-color: #dbdbdb">เลขที่ใบเบิก</td>
                        <td nowrap rowspan="2" style="vertical-align : middle;text-align:center;background-color: #dbdbdb">วันที่เบิก</td>
                        <td nowrap rowspan="2" style="vertical-align : middle;text-align:center;background-color: #dbdbdb">ผู้จัดทำ</td>
                        <td colspan="3" style="vertical-align : middle;text-align:center;background-color: #ff9933">คลังต้นทาง(เบิกออก)</td>
                        <td colspan="3" style="vertical-align : middle;text-align:center;background-color: #99ff33">คลังปลายทาง(รับ)</td>
                        <td colspan="7" style="vertical-align : middle;text-align:center;background-color: #ffff66">รายการ</td>
                        <td rowspan="2" style="vertical-align : middle;text-align:center;background-color: #ccffff">สถานะ</td>
                    </tr>
                    <tr>
                        <td class="text-center" style="background-color: #ff9933">สาขา</td>
                        <td class="text-center" style="background-color: #ff9933">คลัง</td>
                        <td class="text-center" style="background-color: #ff9933">ที่เก็บ</td>
                        <td class="text-center" style="background-color: #99ff33">สาขา</td>
                        <td class="text-center" style="background-color: #99ff33">คลัง</td>
                        <td class="text-center" style="background-color: #99ff33">ที่เก็บ</td>
                        <td class="text-center" style="background-color: #ffff66">รายการ</td>
                        <td class="text-center" style="background-color: #ffff66">ขอ</td>
                        <td class="text-center" style="background-color: #ffff66">เบิก</td>
                        <td class="text-center" style="background-color: #ffff66">รับ</td>
                        <td class="text-center" style="background-color: #ffff66">คืน</td>
                        <td class="text-center" style="background-color: #ffff66">ค้างคืน</td>
                        <td class="text-center" style="background-color: #ffff66">ค้างรับ</td>
                    </tr>
                <thead>
                <tbody id="show_list_detail">

                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include  page="../theme/footer.jsp" flush="true" />
