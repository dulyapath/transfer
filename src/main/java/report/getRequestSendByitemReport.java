package report;

import history.*;
import receive.*;
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

@WebServlet(name = "getRequestSendbyitemReport-sale", urlPatterns = {"/getRequestSendByitemReport"})
public class getRequestSendByitemReport extends HttpServlet {

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

        String searchReqDate = "";
        String searchSendDate = "";
        String _fromBranch = "";
        String _fromWh = "";
        String _fromSh = "";
        String _toBranch = "";
        String _toWh = "";
        String _toSh = "";
        String search = "";

        if (!request.getParameter("rfd").equals("") && !request.getParameter("rtd").equals("")) {
            if (request.getParameter("rfd").equals(request.getParameter("rtd"))) {
                searchReqDate = " and doc_date = '" + request.getParameter("rfd") + "' ";
            } else {
                searchReqDate = " and doc_date between '" + request.getParameter("rfd") + "' and '" + request.getParameter("rtd") + "' ";
            }

        }
        if (!request.getParameter("sfd").equals("") && !request.getParameter("std").equals("")) {
            if (request.getParameter("sfd").equals(request.getParameter("std"))) {
                searchSendDate = " and wid_date = '" + request.getParameter("sfd") + "'";
            } else {
                searchSendDate = " and wid_date between '" + request.getParameter("sfd") + "' and '" + request.getParameter("std") + "' ";
            }

        }

        System.out.println("searchSendDate" + searchSendDate);

        if (!request.getParameter("fbc").equals("")) {
            _fromBranch = " and branch_code = '" + request.getParameter("fbc") + "' ";
        }
        String where_item_like = "";
        String searchitem = request.getParameter("item");
        if (!searchitem.equals("")) {
            String[] sptSearches = searchitem.split(" ");
            if (sptSearches.length > 1) {

                where_item_like += " and (";

                for (int i = 0; i < sptSearches.length; i++) {
                    if (i == 0) {
                        where_item_like += " ( UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        where_item_like += " or UPPER(item_name) LIKE UPPER('%" + sptSearches[i] + "%')) ";

                    } else {
                        where_item_like += " and ( UPPER(item_code) LIKE UPPER('%" + sptSearches[i] + "%') ";
                        where_item_like += " or UPPER(item_name) LIKE UPPER('%" + sptSearches[i] + "%')) ";

                    }
                }
                where_item_like += " )";
            } else {
                where_item_like = " and item_code LIKE upper('%" + searchitem + "%') or item_name LIKE upper('%" + searchitem + "%') ";
            }
        }
        if (!request.getParameter("fwc").equals("")) {
            _fromWh = " and wh_code = '" + request.getParameter("fwc") + "' ";
        }

        if (!request.getParameter("fsc").equals("")) {
            _fromSh = " and shelf_code = '" + request.getParameter("fsc") + "' ";
        }

        if (!request.getParameter("tbc").equals("")) {
            _toBranch = " and to_branch_code = '" + request.getParameter("tbc") + "' ";
        }

        if (!request.getParameter("twc").equals("")) {
            _toWh = "  and to_wh_code = '" + request.getParameter("twc") + "' ";
        }
        String doc_status = "";
        if (!request.getParameter("docst").equals("")) {
            doc_status = request.getParameter("docst");
        }

        if (!request.getParameter("tsc").equals("")) {
            _toSh = " and to_shelf_code = '" + request.getParameter("tsc") + "' ";
        }
        if (!request.getParameter("search").equals("")) {
            search = " and ic_transfer_doc_temp.doc_no like '%" + request.getParameter("search") + "%' or ic_transfer_doc_temp.wid_doc like '%" + request.getParameter("search") + "%' or ic_transfer_doc_temp.fg_doc like '%" + request.getParameter("search") + "%' or ic_transfer_doc_temp.rim_doc like '%" + request.getParameter("search") + "%'  ";
        }

        JSONArray jsarr = new JSONArray();

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";

            String query1 = " select * from (select \n"
                    + "ic_transfer_doc_temp.is_direct,\n"
                    + "ic_transfer_doc_temp.doc_no,is_trans_send,\n"
                    + "case when is_direct = 1 then wid_doc else ic_transfer_doc_temp.doc_no end as doc_nox,case when is_direct = 1 then '' else to_char(doc_date,'DD/MM/YYYY') end as doc_datex,coalesce(wid_doc,'') as wid_doc,\n"
                    + "(select doc_date from ic_trans where ic_trans.doc_no = wid_doc) as wid_date,coalesce(fg_doc,'') as fg_doc,coalesce(rim_doc,'') as rim_doc, \n"
                    + "to_char((select doc_date from ic_trans where ic_trans.doc_no = wid_doc),'DD/MM/YYYY') as wid_date_format,\n"
                    + "(select creator_code from ic_trans where ic_trans.doc_no = wid_doc) as wid_creator_code,\n"
                    + "(select name_1 from erp_user where code = (select creator_code from ic_trans where ic_trans.doc_no = wid_doc)) as wid_creator_name,\n"
                    + "branch_code,\n"
                    + "wh_code,\n"
                    + "shelf_code,\n"
                    + "(select name_1 from erp_branch_list where code = branch_code) as branch_name,\n"
                    + "(select name_1 from ic_warehouse where code = wh_code) as wh_name,\n"
                    + "(select name_1 from ic_shelf where code = shelf_code and whcode = wh_code) as shelf_name,\n"
                    + "\n"
                    + "to_branch_code,\n"
                    + "to_wh_code,\n"
                    + "to_shelf_code,\n"
                    + "(select name_1 from erp_branch_list where code = to_branch_code) as to_branch_name,\n"
                    + "(select name_1 from ic_warehouse where code = to_wh_code) as to_wh_name,\n"
                    + "(select name_1 from ic_shelf where code = to_shelf_code and whcode = to_wh_code) as to_shelf_name,\n"
                    + "ic_transfer_detail_temp.qty as request_qty,\n"
                    + "ic_transfer_detail_temp.event_qty as send_qty,\n"
                    + "case when fg_doc != '' then ic_transfer_detail_temp.receive_qty else 0 end as receive_qty,\n"
                    + "coalesce((select qty from ic_transfer_trans_detail_temp where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code),0) as wait_rim_qty,\n"
                    + "case when coalesce(rim_doc,'') != '' then coalesce((select qty from ic_trans_detail where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code),0) else 0 end as rim_qty,\n"
                    + "(ic_transfer_detail_temp.event_qty - (case when fg_doc != '' then ic_transfer_detail_temp.receive_qty else 0 end) - coalesce((select qty from ic_transfer_trans_detail_temp where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code),0) - coalesce((select qty from ic_trans_detail where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code),0) ) as wait_receive_qty,\n"
                    + "coalesce((select qty from ic_transfer_trans_detail_temp where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code and unit_code = ic_transfer_detail_temp.unit_code),0) as wait_return_qty,\n"
                    + "coalesce((select qty from ic_trans_detail where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code and unit_code = ic_transfer_detail_temp.unit_code),0) as return_qty,\n"
                    + "case when (ic_transfer_detail_temp.event_qty - (case when fg_doc != '' then ic_transfer_detail_temp.receive_qty else 0 end) - coalesce((select qty from ic_trans_detail where doc_no = ic_transfer_doc_temp.rim_doc and item_code = ic_transfer_detail_temp.item_code and unit_code = ic_transfer_detail_temp.unit_code),0) ) = 0 then 1 else 0 end as doc_status,\n"
                    + "ic_transfer_detail_temp.item_code,\n"
                    + "ic_transfer_detail_temp.item_name,\n"
                    + "ic_transfer_detail_temp.unit_code,"
                    + "case when remark like '%สร้างจาก%'  then 'sce' else 'main' end as cmd\n"
                    + "from ic_transfer_doc_temp \n"
                    + "INNER JOIN ic_transfer_detail_temp  ON ic_transfer_doc_temp.doc_no=ic_transfer_detail_temp.doc_no where 1=1  " + where_item_like + searchReqDate + _fromBranch + _fromWh + _fromSh + _toBranch + _toWh + _toSh + search + " order by doc_no,item_code asc) as temp where 1=1 and ( cmd = 'main'  or is_trans_send = 1) and wid_doc != ''  " + searchSendDate + " ";

            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            while (__rsHead.next()) {

                JSONObject obj = new JSONObject();

                obj.put("is_direct", __rsHead.getString("is_direct"));
                obj.put("doc_no", __rsHead.getString("doc_no"));
                obj.put("doc_date", __rsHead.getString("doc_datex"));
                obj.put("wid_doc", __rsHead.getString("wid_doc"));
                obj.put("wid_date", __rsHead.getString("wid_date"));
                obj.put("wid_date_format", __rsHead.getString("wid_date_format"));
                obj.put("wid_creator_code", __rsHead.getString("wid_creator_code"));
                obj.put("wid_creator_name", __rsHead.getString("wid_creator_name"));
                obj.put("fg_doc", __rsHead.getString("fg_doc"));
                obj.put("rim_doc", __rsHead.getString("rim_doc"));
                obj.put("branch_code", __rsHead.getString("branch_code"));
                obj.put("wh_code", __rsHead.getString("wh_code"));
                obj.put("shelf_code", __rsHead.getString("shelf_code"));
                obj.put("branch_name", __rsHead.getString("branch_name"));
                obj.put("wh_name", __rsHead.getString("wh_name"));
                obj.put("shelf_name", __rsHead.getString("shelf_name"));

                obj.put("to_branch_code", __rsHead.getString("to_branch_code"));
                obj.put("to_wh_code", __rsHead.getString("to_wh_code"));
                obj.put("to_shelf_code", __rsHead.getString("to_shelf_code"));
                obj.put("to_branch_name", __rsHead.getString("to_branch_name"));
                obj.put("to_wh_name", __rsHead.getString("to_wh_name"));
                obj.put("to_shelf_name", __rsHead.getString("to_shelf_name"));

                obj.put("request_qty", __rsHead.getString("request_qty"));
                obj.put("send_qty", __rsHead.getString("send_qty"));
                obj.put("receive_qty", __rsHead.getString("receive_qty"));
                obj.put("wait_rim_qty", __rsHead.getString("wait_rim_qty"));
                obj.put("rim_qty", __rsHead.getString("rim_qty"));
                obj.put("wait_receive_qty", __rsHead.getString("wait_receive_qty"));
                obj.put("wait_return_qty", __rsHead.getString("wait_return_qty"));
                obj.put("return_qty", __rsHead.getString("return_qty"));
                obj.put("cmd", __rsHead.getString("cmd"));
                obj.put("doc_status", __rsHead.getString("doc_status"));

                obj.put("item_code", __rsHead.getString("item_code"));
                obj.put("item_name", __rsHead.getString("item_name"));
                obj.put("unit_code", __rsHead.getString("unit_code"));

                String now_status = "0";
                //System.out.println("sum" + (Double.parseDouble(__rsHead.getString("request_qty")) - Double.parseDouble(__rsHead.getString("receive_qty")) - Double.parseDouble(__rsHead.getString("return_qty"))));
                if ((Double.parseDouble(__rsHead.getString("request_qty")) - Double.parseDouble(__rsHead.getString("receive_qty")) - Double.parseDouble(__rsHead.getString("return_qty"))) < 1) {

                    obj.put("doc_status", "1");
                    now_status = "1";
                } else {
                    obj.put("doc_status", "0");
                    now_status = "0";
                }
                //System.out.println("doc_status " + doc_status);
               // System.out.println("now_status " + now_status);
                if (!doc_status.equals("")) {

                    if (doc_status.equals(now_status)) {
                        jsarr.put(obj);
                    }
                } else {
                    jsarr.put(obj);
                }

           
            }

            __stmt.close();

            __rsHead.close();

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
