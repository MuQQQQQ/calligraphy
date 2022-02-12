package extract;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import extract.dao.DataDao;
import extract.handler.Handler;

/**
 * Created by TT on 2019/9/3.
 */
public class Main {

    public static Connection getCon() throws SQLException {
        Connection con;
        con = DriverManager.getConnection("jdbc:sqlite:E:/TimeStamp/人像摄影精选集一(45000张)/db");
        return con;

    }

    public static Connection getCon2() throws SQLException {
        Connection con;
        con = DriverManager.getConnection("jdbc:sqlite:E:/XMTT/calligraphy/中国古代书画经典珍藏高清图集3600张/db");
        return con;

    }

    public static void main(String[] args) throws SQLException, IOException {
        DataDao dataDao = new DataDao();
        Handler handler = new Handler();
        handler.setDataDao(dataDao);
        handler.setCon(getCon());
        handler.setFile("E:/TimeStamp/人像摄影精选集一(45000张)");
        List<Map<String, Object>> list=handler.selectTitles();
        for (Map<String,Object> map : list) {
            String title = (String) map.get("标题");
            if (title.equals("说明文档")) {
                continue;
            }
            handler.select(title);
        }
    }


}
