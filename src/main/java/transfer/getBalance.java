package transfer;

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

@WebServlet(name = "getBalance-item", urlPatterns = {"/getBalance"})
public class getBalance extends HttpServlet {

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

            String query1 = "select\n"
                    + "        ic_code, ic_name, balance_qty, ic_unit_code,\n"
                    + "        case when balance_qty=0 then 0 else balance_amount/balance_qty end as average_cost,\n"
                    + "        coalesce(((select average_cost from ic_trans_detail where ic_trans_detail.last_status=0 and ic_trans_detail.item_code=temp2.ic_code and doc_date_calc<='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) order by doc_date_calc desc,doc_time desc ,line_number desc  offset 0 limit 1 )*unit_ratio),0) as average_cost_end,\n"
                    + "        case when balance_qty=0 then 0 else balance_amount end as balance_amount, qty_in, amount_in,\n"
                    + "        case when qty_in=0 then 0 else amount_in/qty_in end as average_cost_in,\n"
                    + "        qty_out, amount_out,\n"
                    + "        case when qty_out=0 then 0 else amount_out/qty_out end as average_cost_out,\n"
                    + "        wh_code\n"
                    + "\n"
                    + "        from (\n"
                    + "        select\n"
                    + "        ic_inventory.code as ic_code,\n"
                    + "        ic_inventory.name_1 as ic_name ,\n"
                    + "        unit_standard as ic_unit_code,\n"
                    + "        (unit_standard_stand_value/unit_standard_divide_value) as unit_ratio,\n"
                    + "        coalesce(temp1.balance_qty, 0)  as balance_qty,\n"
                    + "        coalesce(temp1.balance_amount, 0) as balance_amount,\n"
                    + "        coalesce(temp1.qty_in, 0) as qty_in,\n"
                    + "        coalesce(temp1.amount_in, 0) as amount_in,\n"
                    + "        coalesce(temp1.qty_out, 0) as qty_out,\n"
                    + "        coalesce(temp1.amount_out, 0) as amount_out,\n"
                    + "        temp1.wh_code\n"
                    + "\n"
                    + "        from ic_inventory\n"
                    + "\n"
                    + "        left join (select\n"
                    + "        item_code as ic_code,\n"
                    + "        (select name_1 from ic_inventory where ic_inventory.code=item_code) as ic_name,\n"
                    + "        (select unit_standard from ic_inventory where ic_inventory.code=item_code) as ic_unit_code,\n"
                    + "        coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty,\n"
                    + "        coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then sum_of_cost else 0 end)),0) as balance_amount,\n"
                    + "        sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) then calc_flag*(qty*(stand_value/divide_value)) else 0 end) as qty_in,\n"
                    + "        sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2))  then calc_flag*sum_of_cost else 0 end) as amount_in,\n"
                    + "        -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then calc_flag*qty*(stand_value/divide_value) else 0 end) as qty_out,\n"
                    + "        -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then calc_flag*sum_of_cost else 0 end) as amount_out\n"
                    + "        ,wh_code from ic_trans_detail\n"
                    + "        where ic_trans_detail.wh_code = '" + request.getParameter("whcode") + "' and ic_trans_detail.last_status=0 and ic_trans_detail.item_type<>5 and (select item_type from ic_inventory where ic_inventory.code = ic_trans_detail.item_code)<>1 and doc_date_calc<='now()' group by item_code,wh_code ) as temp1\n"
                    + "        on temp1.ic_code = ic_inventory.code where temp1.ic_code = '" + request.getParameter("code") + "'  and ic_unit_code = '" + request.getParameter("unit") + "' "
                    + "        ) as temp2 ";

            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {

                JSONObject obj = new JSONObject();

                obj.put("balance_qty", __rsHead.getString("balance_qty"));
                obj.put("average_cost_end", __rsHead.getString("average_cost_end"));
                obj.put("average_cost", __rsHead.getString("average_cost"));
                jsarr.put(obj);
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
