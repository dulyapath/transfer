package receive;

import send.*;
import transfer.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils._global;
import utils._routine;

@WebServlet(name = "docrimreceive-detail", urlPatterns = {"/getDocRimDetail"})
public class getRimDocDetail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder __html = new StringBuilder();

        HttpSession _sess = request.getSession();
        String keyword = "", barcode = "";

        DecimalFormat decim = new DecimalFormat("#,###.##");

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {

            return;
        }

        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        String __provider = _sess.getAttribute("provider").toString().toLowerCase();

        JSONArray jsarr = new JSONArray();

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";
            String branch_code = "";
            String wh_code = "";
            String query1 = "select doc_no,doc_date,doc_time,wh_from,remark,creator_code,COALESCE((select name_1 from erp_user where upper(erp_user.code) = upper(creator_code)),'')as creator_name,location_from,branch_code,COALESCE((select name_1 from ic_shelf where ic_shelf.code = location_from and ic_shelf.whcode = wh_from),'')as shelf_name,COALESCE((select name_1 from ic_warehouse where ic_warehouse.code = wh_from),'')as wh_name,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = branch_code),'')as branch_name,COALESCE((select billing_no from ap_ar_trans_detail where doc_no = 'MRIM2021318113321B933'),'') as billing_no  from ic_trans where doc_no = '" + request.getParameter("docno") + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            JSONObject obj = new JSONObject();
            while (__rsHead.next()) {
                obj.put("branch_code", __rsHead.getString("branch_code"));
                obj.put("doc_no", __rsHead.getString("doc_no"));
                obj.put("doc_date", __rsHead.getString("doc_date"));
                obj.put("doc_time", __rsHead.getString("doc_time"));
                obj.put("wh_code", __rsHead.getString("wh_from"));
                obj.put("shelf_code", __rsHead.getString("location_from"));
                obj.put("wh_name", __rsHead.getString("wh_name"));
                obj.put("branch_name", __rsHead.getString("branch_name"));
                obj.put("shelf_name", __rsHead.getString("shelf_name"));
                obj.put("remark", __rsHead.getString("remark"));
                obj.put("creator_code", __rsHead.getString("creator_code"));
                obj.put("creator_name", __rsHead.getString("creator_name"));
                obj.put("billing_no", __rsHead.getString("billing_no"));

            }
            JSONArray jsarrDetail = new JSONArray();
            String query2 = "select item_code,item_name,unit_code,COALESCE((select name_1 from ic_unit where ic_unit.code = ic_trans_detail.unit_code),'')as unit_name,qty,price,sum_amount from ic_trans_detail where doc_no = '" + request.getParameter("docno") + "' order by line_number";
            System.out.println("query2 " + query2);
            PreparedStatement __stmt2 = __conn.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead2 = __stmt2.executeQuery();

            while (__rsHead2.next()) {
                JSONObject obj2 = new JSONObject();

                obj2.put("item_code", __rsHead2.getString("item_code"));
                obj2.put("item_name", __rsHead2.getString("item_name"));
                obj2.put("unit_code", __rsHead2.getString("unit_code"));
                obj2.put("unit_name", __rsHead2.getString("unit_name"));
                obj2.put("qty", __rsHead2.getString("qty"));
                obj2.put("price", __rsHead2.getString("price"));
                obj2.put("sum_amount", __rsHead2.getString("sum_amount"));
                jsarrDetail.put(obj2);

            }

            obj.put("detail", jsarrDetail);

            if (obj.has("doc_no")) {
                if (!obj.getString("doc_no").equals("")) {
                    jsarr.put(obj);
                }
            }

            __rsHead.close();

            __stmt.close();

        } catch (SQLException e) {
            __html.append(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            __html.append(e.getMessage());
            e.printStackTrace();
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        response.getWriter().print(jsarr);
    }

}
