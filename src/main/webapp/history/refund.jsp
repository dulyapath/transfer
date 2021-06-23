<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%    String pageName = "ยกเลิกใบรับสินค้า";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "reply");
    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/history/refund.js"));
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
    <div class="col-md-4">
        <label>ค้นหา</label>
        <div class="input-group mb-3">
            <input type="text" class="form-control" id="search_name" placeholder="เลขที่เอกสาร,ผู้ขอ...." >

        </div>
    </div>
    <div class="col-lg-4">
        <div class="form-group">
            <label>จากวันที่</label>
            <input type="date" class="form-control" id="search_from_date" placeholder="จากวันที่">
        </div>
    </div>
    <div class="col-lg-4">
        <div class="form-group">
            <label>ถึงวันที่</label>
            <input type="date" class="form-control" id="search_to_date" placeholder="จากวันที่">
        </div>
    </div>

</div>
<div class="row" style="margin-bottom: 0.5rem">
    <div class="col-md-12">
        <button class="btn btn-primary" id="btn-search"><i class="fas fa-play"></i> ประมวลผล</button>
    </div>
</div>
<div class="row" id="show_list_detail">

</div>
<jsp:include  page="../theme/footer.jsp" flush="true" />
