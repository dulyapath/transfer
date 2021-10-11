<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<style>
    /* Absolute Center Spinner */
    .loading {
        position: fixed;
        z-index: 999;
        height: 2em;
        width: 2em;
        overflow: show;
        margin: auto;
        top: 0;
        left: 0;
        bottom: 0;
        right: 0;
    }

    /* Transparent Overlay */
    .loading:before {
        content: '';
        display: block;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: radial-gradient(rgba(20, 20, 20,.8), rgba(0, 0, 0, .8));

        background: -webkit-radial-gradient(rgba(20, 20, 20,.8), rgba(0, 0, 0,.8));
    }

    /* :not(:required) hides these rules from IE9 and below */
    .loading:not(:required) {
        /* hide "loading..." text */
        font: 0/0 a;
        color: transparent;
        text-shadow: none;
        background-color: transparent;
        border: 0;
    }

    .loading:not(:required):after {
        content: '';
        display: block;
        font-size: 10px;
        width: 1em;
        height: 1em;
        margin-top: -0.5em;
        -webkit-animation: spinner 150ms infinite linear;
        -moz-animation: spinner 150ms infinite linear;
        -ms-animation: spinner 150ms infinite linear;
        -o-animation: spinner 150ms infinite linear;
        animation: spinner 150ms infinite linear;
        border-radius: 0.5em;
        -webkit-box-shadow: rgba(255,255,255, 0.75) 1.5em 0 0 0, rgba(255,255,255, 0.75) 1.1em 1.1em 0 0, rgba(255,255,255, 0.75) 0 1.5em 0 0, rgba(255,255,255, 0.75) -1.1em 1.1em 0 0, rgba(255,255,255, 0.75) -1.5em 0 0 0, rgba(255,255,255, 0.75) -1.1em -1.1em 0 0, rgba(255,255,255, 0.75) 0 -1.5em 0 0, rgba(255,255,255, 0.75) 1.1em -1.1em 0 0;
        box-shadow: rgba(255,255,255, 0.75) 1.5em 0 0 0, rgba(255,255,255, 0.75) 1.1em 1.1em 0 0, rgba(255,255,255, 0.75) 0 1.5em 0 0, rgba(255,255,255, 0.75) -1.1em 1.1em 0 0, rgba(255,255,255, 0.75) -1.5em 0 0 0, rgba(255,255,255, 0.75) -1.1em -1.1em 0 0, rgba(255,255,255, 0.75) 0 -1.5em 0 0, rgba(255,255,255, 0.75) 1.1em -1.1em 0 0;
    }

    /* Animation */

    @-webkit-keyframes spinner {
        0% {
            -webkit-transform: rotate(0deg);
            -moz-transform: rotate(0deg);
            -ms-transform: rotate(0deg);
            -o-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
            -moz-transform: rotate(360deg);
            -ms-transform: rotate(360deg);
            -o-transform: rotate(360deg);
            transform: rotate(360deg);
        }
    }
    @-moz-keyframes spinner {
        0% {
            -webkit-transform: rotate(0deg);
            -moz-transform: rotate(0deg);
            -ms-transform: rotate(0deg);
            -o-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
            -moz-transform: rotate(360deg);
            -ms-transform: rotate(360deg);
            -o-transform: rotate(360deg);
            transform: rotate(360deg);
        }
    }
    @-o-keyframes spinner {
        0% {
            -webkit-transform: rotate(0deg);
            -moz-transform: rotate(0deg);
            -ms-transform: rotate(0deg);
            -o-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
            -moz-transform: rotate(360deg);
            -ms-transform: rotate(360deg);
            -o-transform: rotate(360deg);
            transform: rotate(360deg);
        }
    }
    @keyframes spinner {
        0% {
            -webkit-transform: rotate(0deg);
            -moz-transform: rotate(0deg);
            -ms-transform: rotate(0deg);
            -o-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
            -moz-transform: rotate(360deg);
            -ms-transform: rotate(360deg);
            -o-transform: rotate(360deg);
            transform: rotate(360deg);
        }
    }
</style>

<%    String docno = "";
    String status = "1";
    if (request.getParameter("d") != null) {
        docno = request.getParameter("d");
    }
    if (request.getParameter("s") != null) {
        status = request.getParameter("s");
    }
    String pageName = "แบบฟอร์มเบิกสินค้า";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "cancelsend");

    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/transsend/formpackshow.js"));
    HttpSession _sess = request.getSession();
%>

<jsp:include  page="../theme/header.jsp" flush="true" />
<div class="loading">Loading&#8230;</div>

<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=status%>" id="form-status">
<input type="hidden" value="<%=docno%>" id="form-mode">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=session.getAttribute("branch_code")%>" id="user_branch">
<input type="hidden" value="${user_name}" id="user_namex">
<input type="hidden" id="hSubLink" value="${sublink}">
<input type="hidden" id="rt_doc_no" value="">
<input type="hidden" id="rt_doc_date" value="">
<input type="hidden" id="rt_remark" value="">
<input type="hidden" id="rt_user_code" value="">
<input type="hidden" id="rt_from_bh" value="">
<input type="hidden" id="rt_from_wh" value="">
<input type="hidden" id="rt_from_sh" value="">
<input type="hidden" id="rt_to_bh" value="">
<input type="hidden" id="rt_to_wh" value="">
<input type="hidden" id="rt_to_sh" value="">


<div class="row">
    <div class="col-lg-12">
        <div class="card ">
            <div class="card-body">
                <div id="accordion">
                    <div class="card">
                        <div class="card-header" id="headingOne">
                            <h5 class="mb-0">
                                <h4 data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne" style="color:#4e73df;cursor:pointer">
                                    แบบฟอร์มเบิกสินค้า
                                </h4>
                            </h5>
                        </div>

                        <div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion">
                            <div class="card-body">


                                <div class="row">
                                    <div class="col-12">
                                        <p ><b>เลขที่ใบเบิก</b> : <span id="doc_no"></span> <b style="margin-left:1.25em"> วันที่:</b> <span id="doc_date"></span> <b style="margin-left:1.25em">ผู้ขอ:</b> <span id="creator_code"></span></p>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-12">
                                        <p ><b>ขอเบิกจาก สาขา:</b> <span id="from_bh"></span> <b style="margin-left:1.25em"> คลัง:</b> <span id="from_wh"></span> <b style="margin-left:1.25em">ที่เก็บ:</b> <span id="from_sh"></span></p>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-12">
                                        <p ><b>รับเข้าที่ สาขา:</b> <span id="to_bh"></span> <b style="margin-left:1.25em"> คลัง:</b> <span id="to_wh"></span> <b style="margin-left:1.25em">ที่เก็บ:</b> <span id="to_sh"></span></p>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-12">
                                        <p ><b>หมายเหคุ:</b> <span id="remark"></span> </p>
                                    </div>
                                </div>
                                <hr style="margin-bottom: 10px;margin-top: 15px;">
                                <h4>เบิกสินค้า</h4>
                                <div class="row">

                                    <div class="col-3">
                                        <label class="col-form-label"> เลขที่เอกสาร</label>
                                        <input type="text" class="form-control" id="wid_docno" readonly="true">
                                    </div>
                                    <div class="col-3">
                                        <label class="col-form-label">วันที่</label>
                                        <input type="date" class="form-control" id="wid_docdate" readonly="true">
                                    </div>
                                    <div class="col-3">
                                        <label class="col-form-label">หมายเหตุ</label>
                                        <input type="text" class="form-control" id="wid_remark" readonly="true">
                                    </div>
                                    <div class="col-3">
                                        <label class="col-form-label">ราคา</label>
                                        <input type="text" class="form-control" id="price_formular" readonly="true">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin-top: 10px">


                    <div class="col-12 table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">#</th>
                                    <th class="text-center">รหัสสินค้า</th>
                                    <th class="text-center">ชื่อสินค้า</th>
                                    <th class="text-center">หน่วยนับ</th>
                                    <th class="text-center">จำนวนขอ</th>
                                    <th class="text-center">คงเหลือ</th>
                                    <th class="text-center">จำนวนจัดได้</th>
                                    <th class="text-center">ราคาขาย</th>
                                </tr>
                            </thead>
                            <tbody id="item_detail">

                            </tbody>
                        </table>
                    </div>
                </div>
                <hr>
                <div class="row">
                    <div class="col-6">
                        <a href="cancel.jsp" class="btn btn-warning btn-back">กลับสู่เมนูกลับ</a>
                    </div>

                </div>
            </div>
        </div>
        <div class="modal fade" id="modalSearch" tabindex="-1" role="dialog"  aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">ค้นหาสินค้า</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <input type="hidden" id="line_index">
                    <input type="hidden" id="line_action">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-6">
                                <input type="text" class="form-control" id="search_name" placeholder="ค้นหาสินค้า">
                            </div>
                            <div class="col-sm-4">
                                <button  class="btn btn-success mb-2" onclick="_searchItem()">ค้นหา</button>
                            </div>

                        </div>
                        <ul class="list-group" id="list_search_item">


                        </ul>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-warning" data-dismiss="modal">ยกเลิก</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="modalUnit" tabindex="-1" role="dialog"  aria-hidden="true">
            <div class="modal-dialog modal-sm" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">เลือกหน่วยนับ</h5>
                    </div>
                    <div class="modal-body">
                        <ul class="list-group" id="list_unit_item">


                        </ul>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-warning" data-dismiss="modal">ยกเลิก</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include  page="../theme/footer.jsp" flush="true" />
