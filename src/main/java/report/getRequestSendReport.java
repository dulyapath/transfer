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

@WebServlet(name = "getRequestSendReport-sale", urlPatterns = {"/getRequestSendReport"})
public class getRequestSendReport extends HttpServlet {

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
            search = " and doc_no like '%" + request.getParameter("search") + "%' or wid_doc like '%" + request.getParameter("search") + "%' or fg_doc like '%" + request.getParameter("search") + "%' or rim_doc like '%" + request.getParameter("search") + "%'  ";
        }

        JSONArray jsarr = new JSONArray();

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";

            /* String query1 = "select * from (select \n"
                    + "is_direct,\n"
                    + "doc_no,\n"
                    + "case when is_direct = 1 then wid_doc else doc_no end as doc_nox,case when is_direct = 1 then '' else to_char(doc_date,'DD/MM/YYYY') end as doc_datex,wid_doc,\n"
                    + "(select doc_date from ic_trans where ic_trans.doc_no = wid_doc) as wid_date,\n"
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
                    + "\n"
                    + "case when remark like '%สร้างจาก%'  then 'sce' else 'main' end as cmd\n"
                    + "from ic_transfer_doc_temp where remark not like '%สร้างจาก%' " + searchReqDate + _fromBranch + _fromWh + _fromSh + _toBranch + _toWh + _toSh + search + " and wid_doc != '' order by create_datetime desc) as temp where 1=1 and cmd = 'main' " + searchSendDate + " limit 200";
             */
            String query1 = " select * from (select \n"
                    + "is_direct,\n"
                    + "doc_no,is_trans_send,\n"
                    + "case when is_direct = 1 then wid_doc else doc_no end as doc_nox,case when is_direct = 1 then '' else to_char(doc_date,'DD/MM/YYYY') end as doc_datex,coalesce(wid_doc,'') as wid_doc,\n"
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
                    + "(select sum(qty) from ic_transfer_detail_temp where ic_transfer_detail_temp.doc_no = ic_transfer_doc_temp.doc_no) as request_qty,\n"
                    + "(select sum(event_qty) from ic_transfer_detail_temp where ic_transfer_detail_temp.doc_no = ic_transfer_doc_temp.doc_no) as send_qty,\n"
                    + "(select count(*) from ic_transfer_detail_temp where ic_transfer_detail_temp.doc_no = ic_transfer_doc_temp.doc_no) as list_item,\n"
                    + "case when remark like '%สร้างจาก%'  then 'sce' else 'main' end as cmd\n"
                    + "from ic_transfer_doc_temp where 1=1  " + searchReqDate + _fromBranch + _fromWh + _fromSh + _toBranch + _toWh + _toSh + search + " order by create_datetime asc) as temp where 1=1 and ( cmd = 'main'  or is_trans_send = 1) and wid_doc != ''  " + searchSendDate + " ";

            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            while (__rsHead.next()) {
                Double _total_doc = 0.0;
                Double _total_qty = 0.0;
                Double _total_event_qty = 0.0;
                Double _total_receive_qty = 0.0;
                Double _total_return_qty = 0.0;
                Double _total_wait_return_qty = 0.0;
                Double _total_wait_receive = 0.0;
                JSONObject obj = new JSONObject();

        
                obj.put("is_direct", __rsHead.getString("is_direct"));
                obj.put("doc_no", __rsHead.getString("doc_no"));
                obj.put("doc_date", __rsHead.getString("doc_datex"));
                obj.put("wid_doc", __rsHead.getString("wid_doc"));
                obj.put("wid_date", __rsHead.getString("wid_date"));
                obj.put("wid_date_format", __rsHead.getString("wid_date_format"));
                obj.put("wid_creator_code", __rsHead.getString("wid_creator_code"));
                obj.put("wid_creator_name", __rsHead.getString("wid_creator_name"));

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
                obj.put("list_item", __rsHead.getString("list_item"));
                _total_event_qty = Double.parseDouble(__rsHead.getString("send_qty"));

                obj.put("cmd", __rsHead.getString("cmd"));

                JSONArray jsarrz = new JSONArray();
                String query2 = "select doc_no,status,wid_doc,coalesce(fg_doc,'') as fg_doc,coalesce(rim_doc,'')as rim_doc,(select count(*) from ic_transfer_detail_temp where doc_no = ic_transfer_doc_temp.doc_no) as item_count ,\n"
                        + "(select sum(qty) from ic_transfer_detail_temp where ic_transfer_detail_temp.doc_no = ic_transfer_doc_temp.doc_no) as request_qty,\n"
                        + "(select sum(event_qty) from ic_transfer_detail_temp where ic_transfer_detail_temp.doc_no = ic_transfer_doc_temp.doc_no) as send_qty,\n"
                        + "(select sum(receive_qty) from ic_transfer_detail_temp where ic_transfer_detail_temp.doc_no = ic_transfer_doc_temp.doc_no) as rec_qty,\n"
                        + "case when coalesce(rim_doc,'') != '' then coalesce((select sum(qty) from ic_transfer_trans_detail_temp where doc_no = rim_doc),0) else 0 end as wait_rim_qty,\n"
                        + "case when coalesce(rim_doc,'') != '' then coalesce((select sum(qty) from ic_trans_detail where doc_no = rim_doc),0) else 0 end as rim_qty\n"
                        + "from ic_transfer_doc_temp where  wid_doc = '" + __rsHead.getString("wid_doc") + "' order by create_datetime asc";
                //System.out.println("query2 " + query2);
                PreparedStatement __stmt2 = __conn.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet __rsHead2 = __stmt2.executeQuery();
                int receive_status = 0;
                while (__rsHead2.next()) {

                    JSONObject objz = new JSONObject();

                    _total_wait_return_qty += Double.parseDouble(__rsHead2.getString("wait_rim_qty"));
                    _total_return_qty += Double.parseDouble(__rsHead2.getString("rim_qty"));
                    if (!__rsHead2.getString("fg_doc").equals("")) {
                        receive_status = 1;
                        _total_receive_qty += Double.parseDouble(__rsHead2.getString("rec_qty"));
                    } else {
                        receive_status = 0;
                    }

                    objz.put("doc_no", __rsHead2.getString("doc_no"));
                    objz.put("wid_doc", __rsHead2.getString("wid_doc"));
                    objz.put("fg_doc", __rsHead2.getString("fg_doc"));
                    objz.put("rim_doc", __rsHead2.getString("rim_doc"));
                    objz.put("item_count", __rsHead2.getString("item_count"));
                    objz.put("request_qty", __rsHead2.getString("request_qty"));
                    objz.put("send_qty", __rsHead2.getString("send_qty"));
                    objz.put("rec_qty", __rsHead2.getString("rec_qty"));
                    objz.put("wait_rim_qty", __rsHead2.getString("wait_rim_qty"));
                    objz.put("rim_qty", __rsHead2.getString("rim_qty"));
                    objz.put("status", __rsHead2.getString("status"));

                    Double wait_receive_qty = 0.00;
                    wait_receive_qty = (Double.parseDouble(__rsHead2.getString("send_qty")) - Double.parseDouble(__rsHead2.getString("rec_qty")) - Double.parseDouble(__rsHead2.getString("wait_rim_qty")) - Double.parseDouble(__rsHead2.getString("rim_qty")));

                    objz.put("wait_receive_qty", wait_receive_qty);
                    String where_rim = "";

                    JSONArray jsarrzx = new JSONArray();
                    String query3 = "select item_code,item_name,unit_code,qty,event_qty,receive_qty from ic_transfer_detail_temp where doc_no = '" + __rsHead2.getString("doc_no") + "'";
                    //System.out.println("query3 " + query3);
                    PreparedStatement __stmt3 = __conn.prepareStatement(query3, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet __rsHead3 = __stmt3.executeQuery();
                    while (__rsHead3.next()) {
                        JSONObject objz3 = new JSONObject();
                        objz3.put("item_code", __rsHead3.getString("item_code"));
                        objz3.put("item_name", __rsHead3.getString("item_name"));
                        objz3.put("qty", __rsHead3.getString("qty"));
                        objz3.put("event_qty", __rsHead3.getString("event_qty"));
                        if (receive_status == 1) {
                            objz3.put("receive_qty", __rsHead3.getString("receive_qty"));
                        } else {
                            objz3.put("receive_qty", "0");
                        }

                        objz3.put("wait_return_qty", "0");
                        objz3.put("return_qty", "0");

                        if (!__rsHead2.getString("rim_doc").equals("")) {
                            String query4 = "select qty from ic_transfer_trans_detail_temp where doc_no = '" + __rsHead2.getString("rim_doc") + "' and item_code = '" + __rsHead3.getString("item_code") + "' and unit_code = '" + __rsHead3.getString("unit_code") + "'";
                            //System.out.println("query4 " + query4);
                            PreparedStatement __stmt4 = __conn.prepareStatement(query4, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ResultSet __rsHead4 = __stmt4.executeQuery();
                            while (__rsHead4.next()) {
                                objz3.put("wait_return_qty", __rsHead4.getString("qty"));
                            }
                            __stmt4.close();
                            __rsHead4.close();
                            String query5 = "select qty from ic_trans_detail where doc_no = '" + __rsHead2.getString("rim_doc") + "' and item_code = '" + __rsHead3.getString("item_code") + "' and unit_code = '" + __rsHead3.getString("unit_code") + "'";
                            //System.out.println("query5 " + query5);
                            PreparedStatement __stmt5 = __conn.prepareStatement(query5, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ResultSet __rsHead5 = __stmt5.executeQuery();
                            while (__rsHead5.next()) {
                                objz3.put("return_qty", __rsHead5.getString("qty"));
                            }
                            __stmt5.close();
                            __rsHead5.close();
                        } else {
                            objz3.put("wait_return_qty", "0");
                            objz3.put("return_qty", "0");
                        }
                        jsarrzx.put(objz3);
                    }
                    __stmt3.close();
                    __rsHead3.close();

                    objz.put("detail", jsarrzx);
                    jsarrz.put(objz);
                }
                __stmt2.close();
                __rsHead2.close();
                _total_wait_receive = _total_event_qty - _total_receive_qty - _total_wait_return_qty - _total_return_qty;
                String now_status = "0";
                if ((_total_event_qty - _total_receive_qty - _total_return_qty) == 0) {
                    obj.put("doc_status", "1");
                    now_status = "1";
                } else {
                    obj.put("doc_status", "0");
                    now_status = "0";
                }

                obj.put("total_receive_qty", _total_receive_qty);
                obj.put("total_wait_return_qty", _total_wait_return_qty);
                obj.put("total_return_qty", _total_return_qty);
                obj.put("total_wait_receive", _total_wait_receive);
                obj.put("detail", jsarrz);
                if (!doc_status.equals("")) {
                    if (doc_status.equals(now_status)) {
                        jsarr.put(obj);
                    }
                } else {
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
