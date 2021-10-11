<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%    String pageName = "กำหนดค่าเริ่มต้น";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "setting");
    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/setting/setting.js"));
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
        <div class="card">
            <div class="card-body" style="color:#000">
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" id="send_instock">
                    <label class="form-check-label" for="send_instock">
                        ห้ามเบิกสินค้าติดลบ(เมนูเบิกออก,เบิกออกไม่มีใบขอเบิก)
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" id="show_balance" >
                    <label class="form-check-label" for="show_balance">
                        แสดงยอดคงเหลือ (เมนูขอเบิก)
                    </label>
                </div>
            </div>
            <div class="card-footer">
                <button class="btn btn-success" id="btn-save"><i class="fa fa-save"></i> บันทึก</button>
            </div>
        </div>
    </div>

</div>

<jsp:include  page="../theme/footer.jsp" flush="true" />
