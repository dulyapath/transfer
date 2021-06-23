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

@WebServlet(name = "docsend2-save", urlPatterns = {"/saveDocSend2"})
public class saveDocSend2 extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        HttpSession _sess = request.getSession();
        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {

            return;
        }

        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        String __provider = _sess.getAttribute("provider").toString().toLowerCase();

        String __doc_no = request.getParameter("doc_no");
        String __doc_date = request.getParameter("doc_date");
        String __user_code = request.getParameter("user_code");
        String __remark = request.getParameter("remark");
        String __from_bh = request.getParameter("from_bh");
        String __from_wh = request.getParameter("from_wh");
        String __from_sh = request.getParameter("from_sh");
        String __to_bh = request.getParameter("to_bh");
        String __to_wh = request.getParameter("to_wh");
        String __to_sh = request.getParameter("to_sh");

        String __wid_doc_no = request.getParameter("wid_docno");
        String __wid_doc_date = request.getParameter("wid_docdate");
        String __wid_remark = request.getParameter("wid_remark");
        String __wid_doc_time = request.getParameter("wid_doctime");
        String __price_formula = request.getParameter("price_formula");

        String data = request.getParameter("data");

        HttpSession session = request.getSession(true);

        JSONArray jsonArray = new JSONArray(data);

        StringBuilder _insert_trans_temp_sale_temp = new StringBuilder();
        StringBuilder _insert_trans_sale_temp = new StringBuilder();
        StringBuilder _update_price = new StringBuilder();
        StringBuilder _insert_trans_temp_sale_details_temp = new StringBuilder();
        StringBuilder _insert_trans_sale_details_temp = new StringBuilder();
        _routine __routine = new _routine();
        StringBuilder __result = new StringBuilder();

        try {
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String query1 = "select doc_no  from ic_transfer_doc_temp where doc_no = '" + __wid_doc_no + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            __rsHead.next();
            int row = __rsHead.getRow();
            //System.out.println("row " + row);
            if (row > 0) {
                PreparedStatement __stmt_delete = __conn.prepareStatement("delete from ic_transfer_trans_detail_temp where doc_no = '" + __wid_doc_no + "';delete from ic_transfer_trans_temp where doc_no = '" + __wid_doc_no + "';delete from ic_transfer_doc_temp where doc_no = '" + __wid_doc_no + "';delete from ic_transfer_detail_temp where doc_no = '" + __wid_doc_no + "';");
                __stmt_delete.executeUpdate();
                __stmt_delete.close();
            }

            Double sum_amount = 0.0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                //System.out.println(obj);
                sum_amount += Double.parseDouble(obj.get("sum_of_cost").toString());
                _insert_trans_temp_sale_details_temp.append("insert into ic_transfer_detail_temp (line_number,doc_no,item_code,item_name,unit_code,qty,wid_balance,event_qty,receive_qty,average_cost) values "
                        + "(" + obj.get("line_number") + ",'" + __wid_doc_no + "','" + obj.get("item_code") + "','" + obj.get("item_name") + "','" + obj.get("unit_code") + "'," + obj.get("qty") + "," + obj.get("balance_qty") + "," + obj.get("event_qty") + "," + obj.get("event_qty") + "," + obj.get("average_cost") + ");");
                _insert_trans_sale_details_temp.append("insert into ic_transfer_trans_detail_temp (trans_type,trans_flag,doc_date,doc_time,doc_no,item_code,item_name,unit_code,line_number,qty,branch_code,wh_code,shelf_code,average_cost,sum_of_cost,stand_value,divide_value,ratio) values "
                        + "(3,56,'" + __wid_doc_date + "','" + __wid_doc_time + "','" + __wid_doc_no + "','" + obj.get("item_code") + "','" + obj.get("item_name") + "','" + obj.get("unit_code") + "','" + obj.get("line_number") + "','" + obj.get("event_qty") + "','" + __from_bh + "','" + __from_wh + "','" + __from_sh + "','" + obj.get("average_cost") + "','" + obj.get("sum_of_cost") + "',"
                        + "(select stand_value from ic_unit_use where ic_code='" + obj.get("item_code") + "' and code='" + obj.get("unit_code") + "'),(select divide_value from ic_unit_use where ic_code='" + obj.get("item_code") + "' and code='" + obj.get("unit_code") + "'),(select ratio from ic_unit_use where ic_code='" + obj.get("item_code") + "' and code='" + obj.get("unit_code") + "'));");
            }
            _insert_trans_temp_sale_temp.append("insert into ic_transfer_doc_temp (price_formula,doc_no,status,wid_doc,doc_date,remark,user_code,wh_code,shelf_code,branch_code,to_wh_code,to_shelf_code,to_branch_code,is_direct) "
                    + "values ('" + __price_formula + "','" + __wid_doc_no + "',6,'" + __wid_doc_no + "','" + __wid_doc_date + "','" + __wid_remark + "','" + __user_code + "','" + __from_wh + "','" + __from_sh + "','" + __from_bh + "','" + __to_wh + "','" + __to_sh + "','" + __to_bh + "',1)");

            _insert_trans_sale_temp.append("insert into ic_transfer_trans_temp (trans_type,trans_flag,doc_date,doc_no,doc_time,branch_code,wh_from,location_from,doc_format_code,creator_code,last_editor_code,remark,total_amount) "
                    + "values (3,56,'" + __wid_doc_date + "','" + __wid_doc_no + "','" + __wid_doc_time + "','" + __from_bh + "','" + __from_wh + "','" + __from_sh + "','MWID','" + __user + "','" + __user + "','" + __wid_remark + "'," + sum_amount + ");");
            System.out.println(_insert_trans_temp_sale_temp.toString());
            System.out.println(_insert_trans_temp_sale_details_temp.toString());
            System.out.println(_insert_trans_sale_details_temp.toString());
            System.out.println(_insert_trans_sale_temp.toString());
            PreparedStatement __stmt_trans = __conn.prepareStatement(_insert_trans_sale_temp.toString());
            PreparedStatement __stmt_update = __conn.prepareStatement(_insert_trans_temp_sale_temp.toString());
            PreparedStatement __stmt_detail_temp = __conn.prepareStatement(_insert_trans_temp_sale_details_temp.toString());
            PreparedStatement __stmt_detail = __conn.prepareStatement(_insert_trans_sale_details_temp.toString());
            __stmt_trans.executeUpdate();
            __stmt_trans.close();
            __stmt_update.executeUpdate();
            __stmt_update.close();
            __stmt_detail.executeUpdate();
            __stmt_detail.close();
            __stmt_detail_temp.executeUpdate();
            __stmt_detail_temp.close();
            __conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(e);
        }

        response.getWriter().print("success");
    }

}
