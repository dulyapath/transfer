<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="utils.RandomID"%>
</div>
</div>
</div>

<%
    List js = request.getAttribute("js") == null ? new ArrayList<String>() : (List) request.getAttribute("js");
    String v = "?_=" + RandomID.rand();
%>
<!-- Bootstrap core JavaScript-->
<script src="${sublink}vendor/jquery/jquery.min.js"></script>
<script src="${sublink}vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

<!-- Core plugin JavaScript-->
<script src="${sublink}vendor/jquery-easing/jquery.easing.min.js"></script>

<!-- Custom scripts for all pages-->
<script src="${sublink}js/sb-admin-2.min.js"></script>


<%
    if (js.size() > 0) {
        for (int i = 0; i < js.size(); i++) {
            out.print("<script src=\"" + js.get(i).toString() + v + "\"></script>");
        }
    }
%>
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/js/select2.min.js"></script>
</body>

</html>

