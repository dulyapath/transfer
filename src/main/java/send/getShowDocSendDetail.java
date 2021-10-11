package send;

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

@WebServlet(name = "getShowDocSendDetail-detail", urlPatterns = {"/getShowDocSendDetail"})
public class getShowDocSendDetail extends HttpServlet {

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
            String query1 = "select price_formula,doc_no,wid_doc,doc_date as date,COALESCE((select to_char(doc_date,'DD/MM/YYYY') from ic_trans where doc_no = wid_doc and trans_flag = 56 ),null) as wid_date,COALESCE((select doc_time from ic_trans where doc_no = wid_doc and trans_flag = 56 ),'') as wid_time,COALESCE((select doc_date from ic_trans where doc_no = wid_doc and trans_flag = 56 ),null) as wid_docdate,COALESCE((select remark from ic_trans where doc_no = wid_doc  and trans_flag = 56),'') as wid_remark,to_char(doc_date,'DD/MM/YYYY') as doc_date,to_char(doc_time,'HH24:MI') as doc_time,user_code,branch_code,wh_code,shelf_code,to_branch_code,to_wh_code,to_shelf_code,remark,status,COALESCE((select name_1 from ic_warehouse where ic_warehouse.code = wh_code),'')as wh_name,COALESCE((select name_1 from ic_warehouse where ic_warehouse.code = to_wh_code),'')as to_wh_name,COALESCE((select name_1 from ic_shelf where ic_shelf.code = shelf_code and ic_shelf.whcode = wh_code),'')as shelf_name,COALESCE((select name_1 from ic_shelf where ic_shelf.code = to_shelf_code and ic_shelf.whcode = to_wh_code),'')as to_sh_name,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = branch_code),'')as branch_name,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = branch_code),'')as branch_name,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = to_branch_code),'')as to_branch_name,COALESCE((select name_1 from erp_user where upper(erp_user.code) = upper(user_code)),'')as user_name,"
                    + "COALESCE((select creator_code from ic_trans where doc_no = wid_doc and trans_flag = 56),'')as creator_code, "
                    + "COALESCE((select name_1 from erp_user where upper(erp_user.code) = upper(COALESCE((select creator_code from ic_trans where doc_no = wid_doc and trans_flag = 56),''))),'')as creator_name "
                    + "  from ic_transfer_doc_temp where doc_no = '" + request.getParameter("docno") + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            JSONObject obj = new JSONObject();
            String price_level = "0";
            while (__rsHead.next()) {
                branch_code = __rsHead.getString("branch_code");
                wh_code = __rsHead.getString("wh_code");
                price_level = __rsHead.getString("price_formula");
                obj.put("doc_no", __rsHead.getString("doc_no"));
                obj.put("price_formula", __rsHead.getString("price_formula"));
                obj.put("date", __rsHead.getString("date"));
                obj.put("doc_date", __rsHead.getString("doc_date"));
                obj.put("doc_time", __rsHead.getString("doc_time"));
                obj.put("user_code", __rsHead.getString("user_code"));
                obj.put("user_name", __rsHead.getString("user_name"));
                obj.put("branch_code", __rsHead.getString("branch_code"));
                obj.put("wh_code", __rsHead.getString("wh_code"));
                obj.put("shelf_code", __rsHead.getString("shelf_code"));
                obj.put("to_branch_code", __rsHead.getString("to_branch_code"));
                obj.put("to_wh_code", __rsHead.getString("to_wh_code"));
                obj.put("to_shelf_code", __rsHead.getString("to_shelf_code"));
                obj.put("status", __rsHead.getString("status"));
                obj.put("wh_name", __rsHead.getString("wh_name"));
                obj.put("to_wh_name", __rsHead.getString("to_wh_name"));
                obj.put("shelf_name", __rsHead.getString("shelf_name"));
                obj.put("to_sh_name", __rsHead.getString("to_sh_name"));
                obj.put("branch_name", __rsHead.getString("branch_name"));
                obj.put("to_branch_name", __rsHead.getString("to_branch_name"));
                obj.put("remark", __rsHead.getString("remark"));
                obj.put("wid_doc", __rsHead.getString("wid_doc"));
                obj.put("wid_docdate", __rsHead.getString("wid_docdate"));
                obj.put("wid_remark", __rsHead.getString("wid_remark"));
                obj.put("wid_date", __rsHead.getString("wid_date"));
                obj.put("wid_time", __rsHead.getString("wid_time"));
                obj.put("creator_name", __rsHead.getString("creator_name"));
                obj.put("creator_code", __rsHead.getString("creator_code"));
            }
            JSONArray jsarrDetail = new JSONArray();
            String query2 = "select *,COALESCE((select price_" + price_level + " from ic_inventory_price_formula where ic_code = ic_transfer_detail_temp.item_code and unit_code = ic_transfer_detail_temp.unit_code limit 1),'')as price,COALESCE((select name_1 from ic_unit where ic_unit.code = ic_transfer_detail_temp.unit_code),'')as un_name\n"
                    + "      from ic_transfer_detail_temp where doc_no = '" + request.getParameter("docno") + "' order by line_number";
            //System.out.println("query2 " + query2);
            PreparedStatement __stmt2 = __conn.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead2 = __stmt2.executeQuery();

            while (__rsHead2.next()) {

      
                    JSONObject obj2 = new JSONObject();
                    obj2.put("item_code", __rsHead2.getString("item_code"));
                    obj2.put("item_name", __rsHead2.getString("item_name"));
                    obj2.put("unit_code", __rsHead2.getString("unit_code"));
                    obj2.put("unit_name", __rsHead2.getString("un_name"));
                    obj2.put("qty", __rsHead2.getString("qty"));
                    obj2.put("event_qty", __rsHead2.getString("event_qty"));
                    obj2.put("line_number", __rsHead2.getString("line_number"));
                    obj2.put("balance", __rsHead2.getString("balance"));
                    obj2.put("balance_qty", __rsHead2.getString("wid_balance"));
                    obj2.put("average_cost", __rsHead2.getString("average_cost"));
                    obj2.put("price", __rsHead2.getString("price"));
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
