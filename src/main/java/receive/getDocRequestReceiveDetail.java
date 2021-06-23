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

@WebServlet(name = "docrequestreceive-detail", urlPatterns = {"/getDocRequestReceiveDetail"})
public class getDocRequestReceiveDetail extends HttpServlet {

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
            String query1 = "select doc_no,wid_doc,fg_doc,rim_doc,doc_date as date,COALESCE((select doc_date from ic_trans where doc_no = wid_doc and trans_flag = 56 ),null) as wid_docdate,COALESCE((select doc_date from ic_trans where doc_no = fg_doc and trans_flag = 60 ),null) as fg_date,COALESCE((select remark from ic_trans where doc_no = fg_doc and trans_flag = 60 ),null) as fg_remark,COALESCE((select doc_date from ic_trans where doc_no = rim_doc and trans_flag = 58 ),null) as rim_date,COALESCE((select remark from ic_trans where doc_no = rim_doc and trans_flag = 58 ),null) as rim_remark,COALESCE((select remark from ic_trans where doc_no = wid_doc  and trans_flag = 56),'') as wid_remark,to_char(doc_date,'DD/MM/YYYY') as doc_date,to_char(doc_time,'HH:MM') as doc_time,user_code,branch_code,wh_code,shelf_code,to_branch_code,to_wh_code,to_shelf_code,remark,status,COALESCE((select name_1 from ic_warehouse where ic_warehouse.code = wh_code),'')as wh_name,COALESCE((select name_1 from ic_warehouse where ic_warehouse.code = to_wh_code),'')as to_wh_name,COALESCE((select name_1 from ic_shelf where ic_shelf.code = shelf_code and ic_shelf.whcode = wh_code),'')as shelf_name,COALESCE((select name_1 from ic_shelf where ic_shelf.code = to_shelf_code and ic_shelf.whcode = to_wh_code),'')as to_sh_name,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = branch_code),'')as branch_name,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = to_branch_code),'')as to_branch_name,COALESCE((select name_1 from erp_user where upper(erp_user.code) = upper(user_code)),'')as user_name  from ic_transfer_doc_temp where doc_no = '" + request.getParameter("docno") + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            JSONObject obj = new JSONObject();
            while (__rsHead.next()) {
                branch_code = __rsHead.getString("branch_code");
                wh_code = __rsHead.getString("wh_code");
                obj.put("doc_no", __rsHead.getString("doc_no"));
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
                obj.put("wid_date", __rsHead.getString("wid_docdate"));
                obj.put("wid_remark", __rsHead.getString("wid_remark"));
                obj.put("fg_doc", __rsHead.getString("fg_doc"));
                obj.put("fg_date", __rsHead.getString("fg_date"));
                obj.put("fg_remark", __rsHead.getString("fg_remark"));
                obj.put("rim_doc", __rsHead.getString("rim_doc"));
                obj.put("rim_date", __rsHead.getString("rim_date"));
                obj.put("rim_remark", __rsHead.getString("rim_remark"));
            }
            JSONArray jsarrDetail = new JSONArray();
            String query2 = "select *,COALESCE((select name_1 from ic_unit where ic_unit.code = ic_transfer_detail_temp.unit_code),'')as un_name,\n"
                    + "			      COALESCE((select balance_qty from ( select \n"
                    + "                              ic_inventory.code as ic_code, \n"
                    + "                              ic_inventory.name_1 as ic_name , \n"
                    + "                              unit_standard as ic_unit_code, \n"
                    + "                              (unit_standard_stand_value/unit_standard_divide_value) as unit_ratio, \n"
                    + "                              coalesce(temp1.balance_qty, 0)  as balance_qty, \n"
                    + "                              coalesce(temp1.balance_amount, 0) as balance_amount, \n"
                    + "                              temp1.wh_code \n"
                    + "                              from ic_inventory \n"
                    + "                              left join (select \n"
                    + "                              item_code as ic_code, \n"
                    + "                              (select name_1 from ic_inventory where ic_inventory.code=item_code) as ic_name, \n"
                    + "                              (select unit_standard from ic_inventory where ic_inventory.code=item_code) as ic_unit_code, \n"
                    + "                              coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty, \n"
                    + "                              coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then sum_of_cost else 0 end)),0) as balance_amount, \n"
                    + "                              sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) then calc_flag*(qty*(stand_value/divide_value)) else 0 end) as qty_in, \n"
                    + "                              sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2))  then calc_flag*sum_of_cost else 0 end) as amount_in, \n"
                    + "                              -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then calc_flag*qty*(stand_value/divide_value) else 0 end) as qty_out, \n"
                    + "                              -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then calc_flag*sum_of_cost else 0 end) as amount_out \n"
                    + "                              ,wh_code from ic_trans_detail \n"
                    + "                              where ic_trans_detail.wh_code = '" + wh_code + "' and ic_trans_detail.last_status=0 and ic_trans_detail.item_type<>5 and (select item_type from ic_inventory where ic_inventory.code = ic_trans_detail.item_code)<>1 and doc_date_calc<='now()' group by item_code,wh_code ) as temp1 \n"
                    + "                              on temp1.ic_code = ic_inventory.code where temp1.ic_code = ic_transfer_detail_temp.item_code  and ic_unit_code = ic_transfer_detail_temp.unit_code \n"
                    + "                            ) as temp2 ),0)as balance_qty,\n"
                    + "                            COALESCE((select coalesce((case when balance_qty=0 then 0 else balance_amount/balance_qty end )) as average_cost                      \n"
                    + "                              from ( \n"
                    + "                              select \n"
                    + "                              ic_inventory.code as ic_code, \n"
                    + "                              ic_inventory.name_1 as ic_name , \n"
                    + "                              unit_standard as ic_unit_code, \n"
                    + "                              (unit_standard_stand_value/unit_standard_divide_value) as unit_ratio, \n"
                    + "                              coalesce(temp1.balance_qty, 0)  as balance_qty, \n"
                    + "                              coalesce(temp1.balance_amount, 0) as balance_amount, \n"
                    + "                              coalesce(temp1.qty_in, 0) as qty_in, \n"
                    + "                              coalesce(temp1.amount_in, 0) as amount_in, \n"
                    + "                              coalesce(temp1.qty_out, 0) as qty_out, \n"
                    + "                              coalesce(temp1.amount_out, 0) as amount_out, \n"
                    + "                              temp1.wh_code \n"
                    + "                       \n"
                    + "                              from ic_inventory \n"
                    + "                       \n"
                    + "                              left join (select \n"
                    + "                              item_code as ic_code, \n"
                    + "                              (select name_1 from ic_inventory where ic_inventory.code=item_code) as ic_name, \n"
                    + "                              (select unit_standard from ic_inventory where ic_inventory.code=item_code) as ic_unit_code, \n"
                    + "                              coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty, \n"
                    + "                              coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then sum_of_cost else 0 end)),0) as balance_amount, \n"
                    + "                              sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) then calc_flag*(qty*(stand_value/divide_value)) else 0 end) as qty_in, \n"
                    + "                              sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2))  then calc_flag*sum_of_cost else 0 end) as amount_in, \n"
                    + "                              -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then calc_flag*qty*(stand_value/divide_value) else 0 end) as qty_out, \n"
                    + "                              -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then calc_flag*sum_of_cost else 0 end) as amount_out \n"
                    + "                              ,wh_code from ic_trans_detail \n"
                    + "                              where ic_trans_detail.wh_code = '" + wh_code + "' and ic_trans_detail.last_status=0 and ic_trans_detail.item_type<>5 and (select item_type from ic_inventory where ic_inventory.code = ic_trans_detail.item_code)<>1 and doc_date_calc<='now()' group by item_code,wh_code ) as temp1 \n"
                    + "                              on temp1.ic_code = ic_inventory.code where temp1.ic_code = ic_transfer_detail_temp.item_code  and ic_unit_code = ic_transfer_detail_temp.unit_code "
                    + "                            ) as temp2 ),0)as average_cost\n"
                    + "                            from ic_transfer_detail_temp where doc_no = '" + request.getParameter("docno") + "' order by line_number";
            System.out.println("query2 " + query2);
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
                obj2.put("balance_qty", __rsHead2.getString("balance_qty"));
                obj2.put("average_cost", __rsHead2.getString("average_cost"));
                obj2.put("receive_qty", __rsHead2.getString("receive_qty"));
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
