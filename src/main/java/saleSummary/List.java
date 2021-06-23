package saleSummary;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.ResponeUtil;
import utils._global;
import utils._routine;

@WebServlet(name = "saleSummaryList", urlPatterns = {"/saleSummaryList"})
public class List extends HttpServlet {

    private String __strDatabaseName = "";
    private String __strProviderCode = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject __objResult = new JSONObject("{'success': false}");

        HttpSession session = request.getSession();
        this.__strDatabaseName = session.getAttribute("dbname").toString();
        this.__strProviderCode = session.getAttribute("provider").toString();

        if (!request.getParameterMap().containsKey("action_name")) {

        } else {
            String __strActionName = (request.getParameter("action_name") != null && !request.getParameter("action_name").isEmpty()) ? request.getParameter("action_name") : "";
            Connection conn = null;
            try {
                _routine __routine = new _routine();
                conn = __routine._connect(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));
                switch (__strActionName) {
                    case "loadProductDesc":
                        __objResult = this.loadProductDesc(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                    case "loadBranch":
                        __objResult = this.loadBranch(conn);
                        break;
                    case "loadWarehouse":
                        __objResult = this.loadWarehouse(conn);
                        break;
                    case "loadLineInfo":
                        __objResult = this.loadLineInfo(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                    case "loadBarInfo":
                        __objResult = this.loadBarInfo(conn, ResponeUtil.str2Json(request.getParameter("data")));
                        break;
                }
            } catch (SQLException | ParseException ex) {
                __objResult.put("err_msg", ex.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        }

        response.getWriter().print(__objResult);
    }

    private JSONObject loadLineInfo(Connection conn, JSONObject params) throws ParseException, SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");
        SimpleDateFormat __dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
        SimpleDateFormat __dateFormatData = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);

        String __strFromDate = !params.isNull("from_date") && !params.getString("from_date").isEmpty() ? params.getString("from_date") : "";
        String __strToDate = !params.isNull("to_date") && !params.getString("to_date").isEmpty() ? params.getString("to_date") : "";
        String __strWarehouse = !params.isNull("warehouse") && !params.getString("warehouse").isEmpty() ? params.getString("warehouse") : "";
        String __strItemCode = !params.isNull("item_code") && !params.getString("item_code").isEmpty() ? params.getString("item_code") : "";

        Date __dateFrom = __dateFormat.parse(__strFromDate);
        Date __dateTo = __dateFormat.parse(__strToDate);

        String __strQueryExtend = "";
        __strQueryExtend += !__strWarehouse.equals("") ? " AND wh_code='" + __strWarehouse + "'" : "";
        __strQueryExtend += !__strItemCode.equals("") ? " AND item_code='" + __strItemCode + "'" : "";
        if (__strFromDate.equals(__strToDate)) {
            __strQueryExtend += " AND doc_date = '" + __dateFormatData.format(__dateFrom) + "' ";
        } else {
            __strQueryExtend += " AND doc_date BETWEEN '" + __dateFormatData.format(__dateFrom) + "' AND '" + __dateFormatData.format(__dateTo) + "' ";
        }

        String __strQuery = "select *,case when sum_amount_exclude_vat<>0 then (profit*100)/sum_amount_exclude_vat else 0 end as profit_persent from (select *,sum_amount_exclude_vat-sum_of_cost as profit from (select trans_flag,inquiry_type,sum(qty*(stand_value/divide_value)) as qty,sum(sum_amount_exclude_vat) as sum_amount_exclude_vat,sum(case when sum_of_cost_1 is null then 0 else sum_of_cost_1 end) as sum_of_cost from ic_trans_detail where (trans_flag in (44,46,48) and last_status=0) " + __strQueryExtend + " AND COALESCE(set_ref_line,'')='' group by trans_flag,inquiry_type) as temp1) as temp2  order by trans_flag,inquiry_type";
        Statement __stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rs = __stmt.executeQuery(__strQuery);
        ArrayList __data = ResponeUtil._resultSetToArrayList(__rs);

        String html = "";
        html += "<table class='table table-striped text-center' style='margin-bottom: 0;'>";
        html += "<thead>";
        html += "<th class='text-center'>ประเภท</th>";
        if (__strItemCode.length() > 0) {
            html += "<th class='text-center'>จำนวน</th>";
        }
        html += "<th class='text-center'>ยอดรวมทั้งสิ้น (ไม่รวมภาษี)</th>";
        html += "<th class='text-center'>ต้นทุนทั้งสิ้น</th>";
        html += "<th class='text-center'>กำไรขั้นต้น</th>";
        html += "<th class='text-center'>กำไรขั้นต้น (%)</th>";
        html += "</thead>";
        html += "<tbody>";
        String body = this.getLineInfoDetail(2, __data, 0, 0, __strItemCode);
        html += body.equals("") ? "<tr><td colspan=6>ไม่พบข้อมูล</td></tr>" : body;
        html += "</tbody>";
        html += "</table>";

        __objTmp.put("data", html);
        __objTmp.put("success", true);
        return __objTmp;
    }

    private JSONObject loadBarInfo(Connection conn, JSONObject params) throws ParseException, SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");
        SimpleDateFormat __dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
        SimpleDateFormat __dateFormatData = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);

        String __strFromDate = !params.isNull("from_date") && !params.getString("from_date").isEmpty() ? params.getString("from_date") : "";
        String __strToDate = !params.isNull("to_date") && !params.getString("to_date").isEmpty() ? params.getString("to_date") : "";
        String __strWarehouse = !params.isNull("warehouse") && !params.getString("warehouse").isEmpty() ? params.getString("warehouse") : "";
        String __strItemCode = !params.isNull("item_code") && !params.getString("item_code").isEmpty() ? params.getString("item_code") : "";

        Date __dateFrom = __dateFormat.parse(__strFromDate);
        Date __dateTo = __dateFormat.parse(__strToDate);

        String __strQueryExtend = "";
        __strQueryExtend += !__strWarehouse.equals("") ? " AND wh_code='" + __strWarehouse + "'" : "";
        __strQueryExtend += !__strItemCode.equals("") ? " AND item_code='" + __strItemCode + "'" : "";
        if (__strFromDate.equals(__strToDate)) {
            __strQueryExtend += " AND doc_date = '" + __dateFormatData.format(__dateFrom) + "' ";
        } else {
            __strQueryExtend += " AND doc_date BETWEEN '" + __dateFormatData.format(__dateFrom) + "' AND '" + __dateFormatData.format(__dateTo) + "' ";
        }

        String __strQuery = "select *,case when sum_amount_exclude_vat<>0 then (profit*100)/sum_amount_exclude_vat else 0 end as profit_persent from (select *,coalesce((select name_1 from erp_user where code=sale_code),'ไม่พบชื่อ') as sale_name,sum_amount_exclude_vat-sum_of_cost as profit from (select 0 as sale_type,sale_code,trans_flag,case when inquiry_type is null then 0 else inquiry_type end as inquiry_type,sum(qty*(stand_value/divide_value)) as qty,sum(sum_amount_exclude_vat) as sum_amount_exclude_vat,sum(case when sum_of_cost_1 is null then 0 else sum_of_cost_1 end) as sum_of_cost from ic_trans_detail where (trans_flag in (44,46,48) and last_status=0) " + __strQueryExtend + " AND COALESCE(set_ref_line,'')='' group by sale_code,trans_flag,case when inquiry_type is null then 0 else inquiry_type end) as temp1) as temp2  order by sale_type,sale_code,trans_flag,inquiry_type";
        Statement __stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rs = __stmt.executeQuery(__strQuery);
        ArrayList __data = ResponeUtil._resultSetToArrayList(__rs);

        String html = "";
        html += "<table class='table table-striped text-center' style='margin-bottom: 0;'>";
        html += "<thead>";
        html += "<th class='text-center'>รหัสพนักงานขาย</th>";
        html += "<th class='text-center'>ชื่อพนักงานขาย</th>";
        html += "<th class='text-center'>ประเภท</th>";
        if (__strItemCode.length() > 0) {
            html += "<th class='text-center'>จำนวน</th>";
        }
        html += "<th class='text-center'>ยอดรวมทั้งสิ้น (ไม่รวมภาษี)</th>";
        html += "<th class='text-center'>ต้นทุนทั้งสิ้น</th>";
        html += "<th class='text-center'>กำไรขั้นต้น</th>";
        html += "<th class='text-center'>กำไรขั้นต้น (%)</th>";
        html += "</thead>";
        html += "<tbody>";
        String body = this.getBarInfoDetail(3, __data, "", "", 0, 0, __strItemCode);
        html += body.equals("") ? "<tr><td colspan=8>ไม่พบข้อมูล</td></tr>" : body;
        html += "</tbody>";
        html += "</table>";

        __objTmp.put("data", html);
        __objTmp.put("success", true);
        return __objTmp;
    }

    private JSONObject loadProductDesc(Connection conn, JSONObject params) throws SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");
        String __strItemCode = !params.isNull("item_code") && !params.getString("item_code").isEmpty() ? params.getString("item_code") : "";

        String __strQuery = "SELECT code,name_1,unit_cost FROM ic_inventory WHERE code='" + __strItemCode + "'";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        JSONArray __jsonResult = ResponeUtil.query2Array(__result);

        __objTmp.put("data", __jsonResult);
        __objTmp.put("success", true);

        return __objTmp;
    }

    private JSONObject loadBranch(Connection conn) throws SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");

        String __strQuery = "SELECT code,name_1 FROM erp_branch_list ORDER BY code";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        JSONArray __jsonResult = ResponeUtil.query2Array(__result);

        __objTmp.put("data", __jsonResult);
        __objTmp.put("success", true);

        return __objTmp;
    }

    private JSONObject loadWarehouse(Connection conn) throws SQLException {
        JSONObject __objTmp = new JSONObject("{'success': false}");

        String __strQuery = "SELECT code,name_1 FROM ic_warehouse ORDER BY code";
        PreparedStatement __stmt = conn.prepareStatement(__strQuery);
        ResultSet __result = __stmt.executeQuery();
        JSONArray __jsonResult = ResponeUtil.query2Array(__result);

        __objTmp.put("data", __jsonResult);
        __objTmp.put("success", true);

        return __objTmp;
    }

    public String getLineInfoDetail(int level, ArrayList data, int transFlag, int inquiryType, String itemCode) {
        int __count = 0;
        DecimalFormat __dc = new DecimalFormat();
        __dc.applyPattern("###,###,###.00");
        StringBuilder __html = new StringBuilder();
        BigDecimal __qty = new BigDecimal(0.0);
        BigDecimal __totalAmount = new BigDecimal(0.0);
        BigDecimal __sumOfCost = new BigDecimal(0.0);
        BigDecimal __profit = new BigDecimal(0.0);
        BigDecimal __profitPersent = new BigDecimal(0.0);
        String __class = (__count % 2 == 0) ? "odd" : "even";
        String __trColor = "class=\'" + __class + "\'";
        String __boldBegin = "";
        String __boldEnd = "";
        String __type = "";
        switch (level) {
            case 1:
                __trColor = "bgcolor='#81DAF5'";
                __boldBegin = "<b><div style='text-shadow: #666666 2px 2px 4px;'>";
                __boldEnd = "</div></b>";
                switch (transFlag) {
                    case 44:
                        __type = "รวมขาย";
                        break;
                    case 46: {
                        __type = "รวมเพิ่มหนี้";
                    }
                    break;
                    case 48: {
                        __type = "รวมรับคืน";
                    }
                    break;
                }
                break;
            case 2:
                __type = "รวมทั้งสิ้น";
                __trColor = "bgcolor='cyan'";
                __count = -1;
                __boldBegin = "<b><div style='text-shadow: #666666 2px 2px 4px;'>";
                __boldEnd = "</div></b>";
                break;
            default:
                switch (transFlag) {
                    case 44: {
                        // ขาย
                        switch (inquiryType) {
                            case 0:
                                __type = "ขายเชื่อ";
                                break;
                            case 1:
                                __type = "ขายสด";
                                break;
                            case 2:
                                __type = "ขายเชื่อ (บริการ)";
                                break;
                            case 3:
                                __type = "ขายสด (บริการ)";
                                break;
                        }
                    }
                    break;
                    case 46: {
                        // เพิ่มหนี้
                        switch (inquiryType) {
                            case 0:
                                __type = "เพิ่มหนี้";
                                break;
                            case 1:
                                __type = "เพิ่มหนี้ (ไม่ตัดสต๊อก)";
                                break;
                            case 2:
                                __type = "เพิ่มหนี้ (บริการ)";
                                break;
                        }
                    }
                    break;
                    case 48: {
                        // รับคืน
                        switch (inquiryType) {
                            case 0:
                                __type = "รับคืนเงินเชื่อ";
                                break;
                            case 1:
                                __type = "รับคืนเงินสด";
                                break;
                            case 2:
                                __type = "รับคืนเงินเชื่อ (ไม่ตัดสต๊อก)";
                                break;
                            case 3:
                                __type = "รับคืนเงินสด (ไม่ตัดสต๊อก)";
                                break;
                            case 4:
                                __type = "รับคืนเงินเชื่อ (บริการ)";
                                break;
                            case 5:
                                __type = "รับคืนเงินสด (บริการ)";
                                break;
                        }
                    }
                    break;
                }
        }
        for (int __row = 0; __row < data.size(); __row++) {
            BigDecimal __multiply = new BigDecimal(1.0);
            HashMap __data = (HashMap) data.get(__row);
            int __transFlag = (Integer) __data.get("trans_flag");
            int __inquiryType = (Integer) __data.get("inquiry_type");
            if (__transFlag == 48) {
                __multiply = new BigDecimal(-1.0);
            }
            if (level == 2 || (level == 1 && transFlag == __transFlag) || (level == 0 && transFlag == __transFlag && inquiryType == __inquiryType)) {
                __qty = __qty.add(((BigDecimal) __data.get("qty")).multiply(__multiply));
                __totalAmount = __totalAmount.add(((BigDecimal) __data.get("sum_amount_exclude_vat")).multiply(__multiply));
                __sumOfCost = __sumOfCost.add(((BigDecimal) __data.get("sum_of_cost")).multiply(__multiply));
                __profit = __profit.add(((BigDecimal) __data.get("profit")).multiply(__multiply));
            }
        }
        switch (level) {
            case 1:
                for (int __loop = 0; __loop < 5; __loop++) {
                    __html.append(this.getLineInfoDetail(0, data, transFlag, __loop, itemCode));
                }
                break;
            case 2:
                __html.append(this.getLineInfoDetail(1, data, 44, 0, itemCode));
                __html.append(this.getLineInfoDetail(1, data, 46, 0, itemCode));
                __html.append(this.getLineInfoDetail(1, data, 48, 0, itemCode));
                break;
        }
        if (__qty.equals(new BigDecimal(0.0)) == false || __totalAmount.equals(new BigDecimal(0.0)) == false || __sumOfCost.equals(new BigDecimal(0.0)) == false || __profit.equals(new BigDecimal(0.0)) == false) {
            __html.append("<tr ").append(__trColor).append(">");
            __html.append("<td align='left'><font color='black'>").append(__boldBegin).append("&nbsp;").append(__type).append("&nbsp;").append(__boldEnd).append("</font></td>");
            if (itemCode.length() > 0) {
                __html.append("<td align='right'><font color='blue'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__qty == null) ? 0 : __qty)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            }
            __html.append("<td align='right'><font color='blue'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__totalAmount == null) ? 0 : __totalAmount)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("<td align='right'><font color='red'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__sumOfCost == null) ? 0 : __sumOfCost)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("<td align='right'><font color='blue'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__profit == null) ? 0 : __profit)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __profitPersent = (__totalAmount.equals(new BigDecimal(0.0))) ? new BigDecimal(0.0) : __profit.multiply(new BigDecimal(100.0)).divide(__totalAmount, MathContext.DECIMAL64);
            __html.append("<td align='right'><font color='blue'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__profitPersent == null) ? 0 : __profitPersent)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("</tr>");
        }
        return __html.toString();
    }

    public String getBarInfoDetail(int level, ArrayList data, String saleCode, String saleName, int transFlag, int inquiryType, String itemCode) {
        int __count = 0;
        DecimalFormat __dc = new DecimalFormat();
        __dc.applyPattern("###,###,###.00");
        StringBuilder __html = new StringBuilder();
        BigDecimal __qty = new BigDecimal(0.0);
        BigDecimal __totalAmount = new BigDecimal(0.0);
        BigDecimal __sumOfCost = new BigDecimal(0.0);
        BigDecimal __profit = new BigDecimal(0.0);
        BigDecimal __profitPersent = new BigDecimal(0.0);
        String __class = (__count % 2 == 0) ? "odd" : "even";
        String __trColor = "class=\'" + __class + "\'";
        String __boldBegin = "";
        String __boldEnd = "";
        String __headFontColor = "black";
        String __type = "";
        String __saleCode = saleCode;
        String __saleName = saleName;
        switch (level) {
            case 1:
                __boldBegin = "<b><div style='text-shadow: #666666 2px 2px 4px;'>";
                __boldEnd = "</div></b>";
                __trColor = "bgcolor='#81DAF5'";
                __headFontColor = "#0B4C5F";
                switch (transFlag) {
                    case 44:
                        __type = "รวมขาย";
                        break;
                    case 46: {
                        __type = "รวมเพิ่มหนี้";
                    }
                    break;
                    case 48: {
                        __type = "รวมรับคืน";
                    }
                    break;
                }
                break;
            case 2:
                __headFontColor = "blue";
                __type = "รวมตามพนักงาน";
                __trColor = "bgcolor='#A9F5A9'";
                __count = -1;
                __boldBegin = "<b><div style='text-shadow: #666666 2px 2px 4px;'>";
                __boldEnd = "</div></b>";
                break;
            case 3:
                __saleCode = "";
                __saleName = "";
                __type = "รวมทั้งสิ้น";
                __trColor = "bgcolor='cyan'";
                __count = -1;
                __boldBegin = "<b><div style='text-shadow: #666666 2px 2px 4px;'>";
                __boldEnd = "</div></b>";
                break;
            default:
                switch (transFlag) {
                    case 44: {
                        // ขาย
                        switch (inquiryType) {
                            case 0:
                                __type = "ขายเชื่อ";
                                break;
                            case 1:
                                __type = "ขายสด";
                                break;
                            case 2:
                                __type = "ขายเชื่อ (บริการ)";
                                break;
                            case 3:
                                __type = "ขายสด (บริการ)";
                                break;
                        }
                    }
                    break;
                    case 46: {
                        // เพิ่มหนี้
                        switch (inquiryType) {
                            case 0:
                                __type = "เพิ่มหนี้";
                                break;
                            case 1:
                                __type = "เพิ่มหนี้ (ไม่ตัดสต๊อก)";
                                break;
                            case 2:
                                __type = "เพิ่มหนี้ (บริการ)";
                                break;
                        }
                    }
                    break;
                    case 48: {
                        // รับคืน
                        switch (inquiryType) {
                            case 0:
                                __type = "รับคืนเงินเชื่อ";
                                break;
                            case 1:
                                __type = "รับคืนเงินสด";
                                break;
                            case 2:
                                __type = "รับคืนเงินเชื่อ (ไม่ตัดสต๊อก)";
                                break;
                            case 3:
                                __type = "รับคืนเงินสด (ไม่ตัดสต๊อก)";
                                break;
                            case 4:
                                __type = "รับคืนเงินเชื่อ (บริการ)";
                                break;
                            case 5:
                                __type = "รับคืนเงินสด (บริการ)";
                                break;
                        }
                    }
                    break;
                }
        }
        for (int __row = 0; __row < data.size(); __row++) {
            BigDecimal __multiply = new BigDecimal(1.0);
            HashMap __data = (HashMap) data.get(__row);
            int __transFlag = (Integer) __data.get("trans_flag");
            int __inquiryType = (Integer) __data.get("inquiry_type");
            String __saleCodeGet = (String) __data.get("sale_code");
            if (__transFlag == 48) {
                __multiply = new BigDecimal(-1.0);
            }
            if (level == 3 || (level == 2 && __saleCodeGet.equals(saleCode)) || (level == 1 && transFlag == __transFlag && __saleCodeGet.equals(saleCode)) || (level == 0 && transFlag == __transFlag && inquiryType == __inquiryType && __saleCodeGet.equals(saleCode))) {
                __qty = __qty.add(((BigDecimal) __data.get("qty")).multiply(__multiply));
                __totalAmount = __totalAmount.add(((BigDecimal) __data.get("sum_amount_exclude_vat")).multiply(__multiply));
                __sumOfCost = __sumOfCost.add(((BigDecimal) __data.get("sum_of_cost")).multiply(__multiply));
                __profit = __profit.add(((BigDecimal) __data.get("profit")).multiply(__multiply));
            }
        }
        switch (level) {
            case 1:
                for (int __loop = 0; __loop < 5; __loop++) {
                    __html.append(this.getBarInfoDetail(0, data, saleCode, saleName, transFlag, __loop, itemCode));
                }
                break;
            case 2:
                __html.append(this.getBarInfoDetail(1, data, saleCode, saleName, 44, 0, itemCode));
                __html.append(this.getBarInfoDetail(1, data, saleCode, saleName, 46, 0, itemCode));
                __html.append(this.getBarInfoDetail(1, data, saleCode, saleName, 48, 0, itemCode));
                break;
            case 3:
                ArrayList __saleCodeList = new ArrayList<>();
                ArrayList __saleNameList = new ArrayList<>();
                for (int __row = 0; __row < data.size(); __row++) {
                    HashMap __data = (HashMap) data.get(__row);
                    String __saleCodeGet = (String) __data.get("sale_code");
                    Boolean __found = false;
                    for (int __find = 0; __find < __saleCodeList.size(); __find++) {
                        if (__saleCodeList.get(__find).equals(__saleCodeGet)) {
                            __found = true;
                            break;
                        }
                    }
                    if (__found == false) {
                        __saleCodeList.add(__saleCodeGet);
                        __saleNameList.add((String) __data.get("sale_name"));
                    }
                }
                for (int __saleLoop = 0; __saleLoop < __saleCodeList.size(); __saleLoop++) {
                    String __saleCodeLoop = (String) __saleCodeList.get(__saleLoop);
                    String __saleNameLoop = (String) __saleNameList.get(__saleLoop);
                    __html.append(this.getBarInfoDetail(2, data, __saleCodeLoop, __saleNameLoop, 0, 0, itemCode));
                }
                break;
        }
        if (__qty.equals(new BigDecimal(0.0)) == false || __totalAmount.equals(new BigDecimal(0.0)) == false || __sumOfCost.equals(new BigDecimal(0.0)) == false || __profit.equals(new BigDecimal(0.0)) == false) {
            __html.append("<tr ").append(__trColor).append(">");
            __html.append("<td align='left'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__saleCode).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("<td align='left'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__saleName).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("<td align='left'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__type).append("&nbsp;").append(__boldEnd).append("</font></td>");
            if (itemCode.length() > 0) {
                __html.append("<td align='right'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__qty == null) ? 0 : __qty)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            }
            __html.append("<td align='right'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__totalAmount == null) ? 0 : __totalAmount)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("<td align='right'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__sumOfCost == null) ? 0 : __sumOfCost)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("<td align='right'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__profit == null) ? 0 : __profit)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __profitPersent = (__totalAmount.equals(new BigDecimal(0.0))) ? new BigDecimal(0.0) : __profit.multiply(new BigDecimal(100.0)).divide(__totalAmount, MathContext.DECIMAL64);
            __html.append("<td align='right'><font color='").append(__headFontColor).append("'>").append(__boldBegin).append("&nbsp;").append(__dc.format((__profitPersent == null) ? 0 : __profitPersent)).append("&nbsp;").append(__boldEnd).append("</font></td>");
            __html.append("</tr>");
        }
        return __html.toString();
    }

}
