package utils;

import Model.MigrateTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import Model.MigrateTableModel;
import Model.MigrateColumnModel;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

public class MigrateDatabase {

    Vector<MigrateTableModel> tables;
    Vector<MigrateTableModel> tablesProvider;
    HttpSession session = null;

    StringBuilder logVerify = new StringBuilder();

    private void structTable() {
        tables = new Vector<MigrateTableModel>();
        tablesProvider = new Vector<MigrateTableModel>();

        // ################### ic_inventory_price_list #########################
        MigrateTableModel ICTransferDoc = new MigrateTableModel("ic_transfer_doc_temp");

        ICTransferDoc.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICTransferDoc.addColumns(new MigrateColumnModel("doc_no", "character varying", 255, "NOT NULL"));
        ICTransferDoc.addColumns(new MigrateColumnModel("doc_date", "date"));
        ICTransferDoc.addColumns(new MigrateColumnModel("doc_time", "time without time zone", "DEFAULT now()"));
        ICTransferDoc.addColumns(new MigrateColumnModel("user_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("wh_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("shelf_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("branch_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("to_wh_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("to_shelf_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("to_branch_code", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("remark", "text"));
        ICTransferDoc.addColumns(new MigrateColumnModel("status", "smallint", "DEFAULT 0"));
        ICTransferDoc.addColumns(new MigrateColumnModel("price_formula", "smallint", "DEFAULT 0"));
        ICTransferDoc.addColumns(new MigrateColumnModel("wid_doc", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("fg_doc", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("rim_doc", "character varying", 255));
        ICTransferDoc.addColumns(new MigrateColumnModel("is_direct", "numeric", "DEFAULT 0"));
        ICTransferDoc.addColumns(new MigrateColumnModel("is_print", "numeric", "DEFAULT 0"));
        ICTransferDoc.addColumns(new MigrateColumnModel("is_trans_send", "numeric", "DEFAULT 0"));
        ICTransferDoc.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));

        ICTransferDoc.addConstraint("ic_transfer_doc_temp_pk PRIMARY KEY (doc_no)");
        tables.add(ICTransferDoc);

        MigrateTableModel ICTransferDetail = new MigrateTableModel("ic_transfer_detail_temp");
        ICTransferDetail.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICTransferDetail.addColumns(new MigrateColumnModel("doc_no", "character varying", 255, "NOT NULL"));
        ICTransferDetail.addColumns(new MigrateColumnModel("item_code", "character varying", 255));
        ICTransferDetail.addColumns(new MigrateColumnModel("item_name", "character varying", 255));
        ICTransferDetail.addColumns(new MigrateColumnModel("unit_code", "character varying", 255));
        ICTransferDetail.addColumns(new MigrateColumnModel("balance", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("average_cost", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("wid_balance", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("qty", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("event_qty", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("receive_qty", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("line_number", "numeric", "DEFAULT 0"));
        ICTransferDetail.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        ICTransferDetail.addConstraint("ic_transfer_detail_temp_pk PRIMARY KEY (roworder)");
        tables.add(ICTransferDetail);

        MigrateTableModel ICWareHouse = new MigrateTableModel("erp_user_storage");
        ICWareHouse.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICWareHouse.addColumns(new MigrateColumnModel("emp_code", "character varying", 255, "NOT NULL"));
        ICWareHouse.addColumns(new MigrateColumnModel("branch_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("wh_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("shelf_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("to_branch_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("to_wh_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("to_shelf_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("rev_branch_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("rev_wh_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("rev_shelf_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_branch_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_wh_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_shelf_code", "text"));

        ICWareHouse.addColumns(new MigrateColumnModel("defualt_direct_branch_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_direct_wh_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_direct_shelf_code", "text"));

        ICWareHouse.addColumns(new MigrateColumnModel("is_direct", "smallint", "DEFAULT 0"));
        ICWareHouse.addColumns(new MigrateColumnModel("is_del_history", "smallint", "DEFAULT 0"));

        ICWareHouse.addColumns(new MigrateColumnModel("defualt_to_branch_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_to_wh_code", "text"));
        ICWareHouse.addColumns(new MigrateColumnModel("defualt_to_shelf_code", "text"));

        ICWareHouse.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        ICWareHouse.addConstraint("erp_user_storage_pk PRIMARY KEY (emp_code)");
        tables.add(ICWareHouse);

        MigrateTableModel APARTransferTransTemp = new MigrateTableModel("ap_ar_trans_detail_temp");
        APARTransferTransTemp.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("doc_no", "character varying", 255, "NOT NULL"));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("trans_type", "smallint", "DEFAULT 0"));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("trans_flag", "smallint", "DEFAULT 0"));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("doc_date", "date"));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("billing_date", "date"));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("billing_no", "character varying", 255));
        APARTransferTransTemp.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        APARTransferTransTemp.addConstraint("ap_ar_trans_detail_temp_pk PRIMARY KEY (roworder)");
        tables.add(APARTransferTransTemp);

        MigrateTableModel ICTransferTransTemp = new MigrateTableModel("ic_transfer_trans_temp");
        ICTransferTransTemp.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("doc_no", "character varying", 255, "NOT NULL"));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("trans_type", "smallint", "DEFAULT 0"));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("trans_flag", "smallint", "DEFAULT 0"));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("doc_date", "date"));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("doc_time", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("branch_code", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("wh_from", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("location_from", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("doc_format_code", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("creator_code", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("last_editor_code", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("remark", "character varying", 255));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("total_amount", "numeric", "DEFAULT 0"));
        ICTransferTransTemp.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        ICTransferTransTemp.addConstraint("ic_transfer_trans_temp_pk PRIMARY KEY (doc_no)");
        tables.add(ICTransferTransTemp);

        MigrateTableModel ICTransferTransDetail = new MigrateTableModel("ic_transfer_trans_detail_temp");
        ICTransferTransDetail.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("doc_no", "character varying", 255, "NOT NULL"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("ref_doc_no", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("trans_type", "smallint", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("trans_flag", "smallint", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("doc_date", "date"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("doc_time", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("item_code", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("item_name", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("unit_code", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("line_number", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("qty", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("branch_code", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("wh_code", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("shelf_code", "character varying", 255));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("price", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("sum_amount", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("sum_of_cost", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("average_cost", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("stand_value", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("divide_value", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("ratio", "numeric", "DEFAULT 0"));
        ICTransferTransDetail.addColumns(new MigrateColumnModel("calc_flag", "smallint", "DEFAULT 0"));

        ICTransferTransDetail.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        ICTransferTransDetail.addConstraint("ic_transfer_trans_detail_temp_pk PRIMARY KEY (roworder)");
        tables.add(ICTransferTransDetail);

        MigrateTableModel ICTransferLog = new MigrateTableModel("ic_transfer_log");
        ICTransferLog.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICTransferLog.addColumns(new MigrateColumnModel("doc_no", "character varying", 255, "NOT NULL"));
        ICTransferLog.addColumns(new MigrateColumnModel("wid_doc", "character varying", 255));
        ICTransferLog.addColumns(new MigrateColumnModel("approve_code", "character varying", 255));
        ICTransferLog.addColumns(new MigrateColumnModel("screen", "numeric", "DEFAULT 0"));
        ICTransferLog.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        ICTransferLog.addConstraint("ic_transfer_log_pk PRIMARY KEY (roworder)");
        tables.add(ICTransferLog);

        MigrateTableModel ICTransferSetting = new MigrateTableModel("ic_transfer_setting");
        ICTransferSetting.addColumns(new MigrateColumnModel("roworder", "serial", "NOT NULL"));
        ICTransferSetting.addColumns(new MigrateColumnModel("code", "character varying", 255));
        ICTransferSetting.addColumns(new MigrateColumnModel("send_instock", "numeric", "DEFAULT 0"));
        ICTransferSetting.addColumns(new MigrateColumnModel("show_balance", "numeric", "DEFAULT 0"));
        ICTransferSetting.addColumns(new MigrateColumnModel("create_datetime", "timestamp without time zone", "DEFAULT now()"));
        ICTransferSetting.addConstraint("ic_transfer_setting_pk PRIMARY KEY (roworder)");
        ICTransferSetting.addAfterScript("INSERT INTO ic_transfer_setting (code,send_instock,show_balance) VALUES ('S0001',0,0)");
        tables.add(ICTransferSetting);

    }

    public void verify(String provider, String dbname, HttpSession session) {
        this.session = session;
        verify(provider, dbname);
    }

    public void verify(String provider, String dbname) {
        Connection __conn = null;
        Connection __connProvider = null;

        structTable();
        dbname = dbname.toLowerCase();
        provider = provider.toLowerCase();

        _routine __routine = new _routine();

        __conn = __routine._connect(dbname, "SMLConfig" + provider.toUpperCase() + ".xml");
        __connProvider = __routine._connect("smlerpmain" + provider);

        try {

            for (MigrateTableModel table : tables) {
                verifyTable(__conn, table);
            }

            for (MigrateTableModel table : tablesProvider) {
                verifyTable(__connProvider, table);
            }
            afterScript(__connProvider);

        } finally {
            logStatus("complete", "", 1);

            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (__connProvider != null) {
                try {
                    __connProvider.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    private void afterScript(Connection __conn) {
        try {
            String sqlCheck = "SELECT module_code FROM sml_web_module WHERE module_code = 'settings'";

            PreparedStatement stmt = __conn.prepareStatement(sqlCheck);
            ResultSet rs = stmt.executeQuery();

            boolean valNotExists = true;
            while (rs.next()) {
                valNotExists = false;
            }

            if (valNotExists) {
                String afterScript = "DELETE FROM sml_web_module WHERE web_flag=1;";
                afterScript += "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'cargroup', 'รหัสรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'carmaster', 'รายละเอียดรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'carstatus', 'สถานะรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'cartype', 'ประเภทรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'tmsroute', 'จัดการเส้นทาง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'drivermaster', 'จัดการพนักงานขนส่ง', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'maintanance', 'Maintanance', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'oil', 'Oil', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'routedetails', 'การเดินรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipment', 'คิวส่งสินค้า', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentapprove', 'ปล่อยรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentcancel', 'ยกเลิก', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentfinished', 'ปิดจ๊อบ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'trucktracking', 'ติดตามรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'shipmentreport', 'รายงานตรวจสอบ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonque', 'เหตุผลจัดคิว', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonapprove', 'เหตุผลปล่อยรถ', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'reasonclosejob', 'เหตุผลปิดjob', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'permission', 'จัดการสิทธิ์', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'group', 'กลุ่มผู้ใช้งาน', '0', '1');"
                        + "INSERT INTO sml_web_module (module_code,module_name,is_disable,web_flag) VALUES ( 'settings', 'ตั้งค่า', '0', '1');";

                PreparedStatement stmtBeforeScript = __conn.prepareStatement(afterScript);
                stmtBeforeScript.executeUpdate();
            }

        } catch (SQLException ex) {
//            ex.printStackTrace();
        }
    }

    private void verifyTable(Connection __conn, MigrateTableModel table) {

        try {
            PreparedStatement stmtBeforeScript = __conn.prepareStatement(table.getAfterScript());
            stmtBeforeScript.executeUpdate();
        } catch (SQLException ex) {

        }

        try {

            String sqlCheckTable = "";

            JSONObject logTable = new JSONObject();

            sqlCheckTable = "SELECT table_name FROM information_schema.tables WHERE table_name = '" + table.getTableName() + "';";
            PreparedStatement stmt = __conn.prepareStatement(sqlCheckTable);
            ResultSet rs = stmt.executeQuery();

            table.setExists(false);
            while (rs.next()) {
                table.setExists(true);
            }
            rs.close();
            stmt.close();

            if (table.isExists()) {
                logStatus("t", table.getTableName(), 1);
                verifyColumn(__conn, table);
            } else {

                PreparedStatement stmtTable = __conn.prepareStatement(table.getCreateTableScript());
                stmtTable.executeUpdate();
                stmtTable.close();

                PreparedStatement stmtAfterScript = __conn.prepareStatement(table.getAfterScript());
                stmtAfterScript.executeUpdate();

                logStatus("t", table.getTableName(), 1);
            }

            String indexsScript = table.getAddIndexScript();

            if (!indexsScript.equals("")) {
                try {
                    PreparedStatement stmtIndexs = __conn.prepareStatement(indexsScript);
                    stmtIndexs.executeUpdate();
                } catch (SQLException e) {

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logStatus("t", table.getTableName(), 0);
            logStatus("sql_err", e.getMessage(), 0);
        }
    }

    private void verifyColumn(Connection __conn, MigrateTableModel table) {

        String columnLastCheck = "";
        try {

            String sqlCheckColumn = "";

            sqlCheckColumn = "SELECT column_name FROM information_schema.columns WHERE table_name = '" + table.getTableName() + "'";
            Statement stmtCol = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rsCol = stmtCol.executeQuery(sqlCheckColumn);

            Vector<String> colExists = new Vector<String>();

            while (rsCol.next()) {
                colExists.add(rsCol.getString("column_name"));
            }

            rsCol.close();
            stmtCol.close();

            for (MigrateColumnModel column : table.getColumns()) {
                column.setExists(colExists.contains(column.getName()));
                if (!column.isExists()) {
                    columnLastCheck = column.getName();

                    Statement stmtColAdd = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmtColAdd.executeUpdate(column.getAddColumnScript(table.getTableName()));
                    stmtColAdd.close();

                    logStatus("c", column.getName(), 1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logStatus("c", columnLastCheck, 0);
            logStatus("sql_err", e.getMessage(), 0);
        }
    }

    private void sessionLog() {
        if (session != null) {
            session.setAttribute("verify_log", logVerify.toString());
        }
    }

    private void logStatus(String code, String msg, int status) {
        logVerify.append(code + "," + msg + "," + status + "\n");
        sessionLog();
    }

}
