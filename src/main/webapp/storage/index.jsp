<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%    String pageName = "กำหนดคลัง";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "store");
    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/store/store.js"));
    HttpSession _sess = request.getSession();
%>

<jsp:include  page="../theme/header.jsp" flush="true" />

<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=session.getAttribute("branch_code")%>" id="user_branch">
<input type="hidden" value="${user_name}" id="user_namex">
<input type="hidden" id="hSubLink" value="${sublink}">

<div class="row">
    <div class="col-lg-12">
        <div class="input-group mb-3">
            <input type="text" class="form-control" id="search_name" placeholder="ค้นหาพนักงาน...." >
            <div class="input-group-append">
                <button class="btn btn-outline-secondary" type="button" id="btn-search"><i class="fa fa-search"></i></button>
            </div>
        </div>
    </div>
    <div class="col-lg-12 table-responsive">
        <table class="table table-striped">
            <thead>
                <tr>

                    <th rowspan="2" colspan="2" class="text-center">พนักงาน</th>
                    <th colspan="3" class="text-center">ปลายทาง(ผู้ขอเบิก/ผู้รับสินค้า)</th>

                    <th colspan="3" class="text-center">ต้นทาง(ผู้เบิก)</th>
                    <th colspan="3" class="text-center">คลังที่ขอเบิกได้</th>
                </tr>
                <tr>

                    <th class="text-center">สาขา</th>
                    <th class="text-center">คลัง</th>
                    <th class="text-center">ที่เก็บ</th>
                    <th class="text-center">สาขา</th>
                    <th class="text-center">คลัง</th>
                    <th class="text-center">ที่เก็บ</th>
                    <th class="text-center">สาขา</th>
                    <th class="text-center">คลัง</th>
                    <th class="text-center">ที่เก็บ</th>
                </tr>
            </thead>
            <tbody id="item_body">

            </tbody>
        </table>
    </div>
</div>
<div class="modal" id="formModal" role="dialog">
    <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" >กำหนดคลังและที่เก็บ  <span id="emp_name"></span></h5>
            </div>
            <input type="hidden" id="emp_code">

            <div class="modal-body">
                <div class="row">
                    <div class="col-md-4" >
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="" id="is_direct">
                            <label class="form-check-label" for="is_direct">
                                <b>สามารถใช้เบิกออก(ไม่มีใบขอเบิก)</b>
                            </label>
                        </div>
                    </div>
                    <div class="col-md-4" >
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="" id="is_del_history">
                            <label class="form-check-label" for="is_del_history">
                                <b>สามารถเข้าดูประวัติการลบได้</b>
                            </label>
                        </div>
                    </div>
                </div>
                <hr>
                <div class="row" style="margin-top:5px">
                    <div class="col-md-12">
                        <h6>ค่าเริ่มต้นขอเบิก</h6>
                    </div>
                    <div class="col-md-4">
                        <label >สาขา</label>
                        <select class="form-control branch_select" id="defualt_branch_code" style="width: 100%"></select>
                    </div>


                    <div class="col-md-4">
                        <label >คลัง</label>
                        <select class="form-control defualt_wh_code" id="defualt_wh_code"  style="width: 100%"></select>
                    </div>
                    <div class="col-md-4" >
                        <label >ที่เก็บ</label>
                        <select class="form-control defualt_shelf_code" id="defualt_shelf_code"  style="width: 100%" disabled></select>
                    </div>
                </div>
                <div class="row" style="margin-top:10px">
                    <div class="col-md-12">
                        <h6>ค่าเริ่มต้นรับเข้า</h6>
                    </div>
                    <div class="col-md-4">
                        <label >สาขา</label>
                        <select class="form-control branch_select" id="defualt_to_branch_code" style="width: 100%"></select>
                    </div>

                    <div class="col-md-4">
                        <label >คลัง</label>
                        <select class="form-control defualt_to_wh_code" id="defualt_to_wh_code"  style="width: 100%"></select>
                    </div>
                    <div class="col-md-4" >
                        <label >ที่เก็บ</label>
                        <select class="form-control defualt_to_shelf_code" id="defualt_to_shelf_code"  style="width: 100%" disabled></select>
                    </div>
                </div>
                <div class="row" style="margin-top:10px">
                    <div class="col-md-12">
                        <h6>ค่าเริ่มต้นเบิกออก(ไม่มีใบขอเบิก)</h6>
                    </div>
                    <div class="col-md-4">
                        <label >สาขา</label>
                        <select class="form-control branch_select" id="defualt_direct_branch_code" style="width: 100%"></select>
                    </div>

                    <div class="col-md-4">
                        <label >คลัง</label>
                        <select class="form-control defualt_direct_wh_code" id="defualt_direct_wh_code"  style="width: 100%"></select>
                    </div>
                    <div class="col-md-4" >
                        <label >ที่เก็บ</label>
                        <select class="form-control defualt_direct_shelf_code" id="defualt_direct_shelf_code"  style="width: 100%" disabled></select>
                    </div>
                </div>
                <hr>
                <div class="row">
                    <div class="col-md-4">
                        <div class="row">
                            <div class="col-12">
                                <p>ปลายทาง(ผู้ขอเบิก/ผู้รับสินค้า)</p>
                            </div>
                            <div class="col-12">
                                <label >สาขา</label>
                                <select class="form-control branch_select" id="to_bh" multiple="multiple" style="width: 100%"></select>
                            </div>
                            <div class="col-12">
                                <label >คลัง</label>
                                <select class="form-control wh_select" id="to_wh" multiple="multiple" style="width: 100%"></select>
                            </div>
                            <div class="col-12" style="margin-top:10px">
                                <label >ที่เก็บ</label>
                                <select class="form-control shelf_select" id="to_sh" multiple="multiple" style="width: 100%" disabled></select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="row">
                            <div class="col-12">
                                <p>ต้นทาง(ผู้เบิก)</p>
                            </div>
                            <div class="col-12">
                                <label >สาขา</label>
                                <select class="form-control branch_select" id="from_bh" multiple="multiple" style="width: 100%"></select>
                            </div>
                            <div class="col-12">
                                <label >คลัง</label>
                                <select class="form-control wh_select2" id="from_wh"  multiple="multiple" style="width: 100%"></select>
                            </div>
                            <div class="col-12" style="margin-top:10px">
                                <label >ที่เก็บ</label>
                                <select class="form-control shelf_select2" id="from_sh"  multiple="multiple" style="width: 100%" disabled></select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="row">
                            <div class="col-12">
                                <p>คลังที่ขอเบิกได้</p>
                            </div>
                            <div class="col-12">
                                <label >สาขา</label>
                                <select class="form-control branch_select" id="rev_bh" multiple="multiple" style="width: 100%"></select>
                            </div>
                            <div class="col-12">
                                <label >คลัง</label>
                                <select class="form-control wh_select3" id="rev_wh"  multiple="multiple" style="width: 100%"></select>
                            </div>
                            <div class="col-12" style="margin-top:10px">
                                <label >ที่เก็บ</label>
                                <select class="form-control shelf_select3" id="rev_sh"  multiple="multiple" style="width: 100%" disabled></select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="btn-save">บันทึก</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">ยกเลิก</button>
            </div>
        </div>
    </div>
</div>
<jsp:include  page="../theme/footer.jsp" flush="true" />
