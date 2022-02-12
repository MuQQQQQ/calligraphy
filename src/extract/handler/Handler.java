package extract.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import extract.dao.DataDao;

import java.io.File;
/**
 * Created by TT on 2019/9/3.
 */
public class Handler {
    private DataDao dataDao;
    private Connection con;
    private String file;
    private String title;

    public DataDao getDataDao() {
        return dataDao;
    }

    public void setDataDao(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<Map<String, Object>> selectTitles() throws SQLException {
        List<Map<String, Object>> list = dataDao.selectTitles(con);
        return list;
    }
    public void select(String key) throws SQLException {
        List<Map<String, Object>> list = dataDao.select(con, "%" + key + "%");
        for (Map<String, Object> map : list) {
            String title = (String) map.get("title");
            this.title = title;
            // System.out.println(map.get("content")+"---------54");
            File directory = new File(this.file + "/" + title);
            directory.mkdir();
            
            System.out.println("正在导出 " + title);
            InputStream is = (InputStream) map.get("content");
            handler(is);
            System.out.println(title + "导出完毕");
            
        }
    }

    public void handler(InputStream is) {
        try {
            try {
                extract(is, this.file, this.title);
              
            } finally {
               
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void extract(InputStream is, String file, String title) throws IOException {
        //流数据zlib解压
        InflaterInputStream inflaterInputStream = new InflaterInputStream(is);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        if (inflaterInputStream.markSupported()) {
            inflaterInputStream.mark(0);
        }
        if (inflaterInputStream.markSupported()) {
            inflaterInputStream.reset();
        }
        while (inflaterInputStream.available() != 0) {
            buffer.write(inflaterInputStream.read());
        }
        inflaterInputStream.close();
        byte[] bbb = buffer.toByteArray();
        // os.write(bbb, 0, bbb.length);
        String decompressHexString = bytesToHexString(bbb);
        // //十六进制的字符串翻译（解释）成字符串
        String hexString = hexStringToString(decompressHexString);
        String[] temp = hexString.split("\n");
        System.out.println(temp);
        boolean flag = false;
        int cnt = 0;
        for (int i = 0; i < temp.length; ++i) {
            if (flag) {
                byte[] b = toBytes(temp[i].trim());
                File fileObj = new File(file+"/"+title, title.trim()+Integer.toString(cnt) + ".jpg");
                FileOutputStream os = new FileOutputStream(fileObj);
                os.write(b, 0, b.length);
                os.close();
                ++cnt;
                flag = false;
            }
            if (temp[i].charAt(0) == 'T') {
                flag = true;
            }
        }
        // InflaterInputStream iis = new InflaterInputStream(is);
        // int len, count = buf.length;
        // int un = 1474, idx = 0, ret;
        // byte[] head = new byte[un];
        
        
        // // os.write(b, 0, b.length);
        // //截掉文件头
        // while (un > 0) {
        //     ret = iis.read(head, idx, un);
        //     if (ret >= 0) {
        //         idx += ret;
        //         un -= ret;
        //     } else {
        //         throw new IOException("a");
        //     }
        // }
        // // while ((len = iis.read(buf)) >= 0) {
        // //     String decompressHexString = bytesToHexString(buf);
        // //     String hexString = hexStringToString(decompressHexString);
        // //     byte[] b = toBytes(hexString.trim());
        // //     os.write(buf, 0, buf.length);

        // // }
        // //boolean head = true;
        // boolean start = false;
        // while ((len = iis.read(buf)) >= 0) {
        //     //每次都要读取偶数位的长度
        //     while (len < count && len != 0) {
        //         len += iis.read(buf, len, count - len);
        //     }
        //     // os.write(buf, 0, len);
        //     //解压之后的流转成十六进制的字符串
        //     String decompressHexString = bytesToHexString(buf);
        //     // //十六进制的字符串翻译（解释）成字符串
        //     String hexString = hexStringToString(decompressHexString);
        //     String content="";
        //     if (hexString.indexOf("FFD8") > 0) {
        //         start = true;
        //         content = hexString.substring(hexString.indexOf("FFD8"), hexString.length()).trim();
        //         byte[] b = toBytes(content);
        //         os.write(b, 0, b.length);
        //     } else if (hexString.indexOf("FFD9") > 0) {
        //         start = false;
        //         content = hexString.substring(0, hexString.indexOf("FFD9")+4).trim();
        //         byte[] b = toBytes(content);
        //         os.write(b, 0, b.length);
        //         break;
        //     } else if (start) {
        //         content = hexString.trim();
        //         byte[] b = toBytes(content);
        //         os.write(b, 0, b.length);
        //     }
            
            // if (hexString != null) {
            //     //截掉文件尾巴
            //     if (hexString.indexOf("-1") > 0) {
            //         String content = hexString.substring(0, hexString.indexOf("-1")).trim();
            //         //字符串再转字节流，写入文件
            //         byte[] b = toBytes(content);
            //         os.write(b, 0, b.length);
            //         break;
            //     } else {
            //         byte[] b = toBytes(hexString.trim());
            //         os.write(b, 0, b.length);
            //         buf = new byte[count];
            //     }
            // }
        // }
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    //字节流转十六进制的字符串
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    //十六进制字符串转成字节流
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            try {
                bytes[i] = (byte) Integer.parseInt(subStr, 16);
            } catch (Exception e) {
                System.out.println("----------"+str+"----------183");
            }
        }

        return bytes;
    }


    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null) {
            return null;
        }
        return str.getBytes();
    }

    public static String byte2hex(byte[] b) // 二进制转字符串
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }

        }
        return sb.toString();
    }

    public static byte[] readStream(InputStream in, boolean close) throws IOException {
        byte[] var3;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            copy(in, out, new byte[8192]);
            var3 = out.toByteArray();
        } finally {
            if (close) {
                in.close();
            }

        }

        return var3;
    }

    public static void copy(InputStream in, OutputStream os, byte[] buf) throws IOException {
        boolean var3 = false;

        int len;
        while ((len = in.read(buf)) >= 0) {
            if (len > 0) {
                os.write(buf, 0, len);
            }
        }

    }


    public static String xor(String content) {
        content = change(content);
        String[] b = content.split(" ");
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
        if (a < 10) {
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();
        }
        return Integer.toHexString(a);
    }

    public static String change(String content) {
        String str = "";
        for (int i = 0; i < content.length(); i++) {
            if (i % 2 == 0) {
                str += " " + content.substring(i, i + 1);
            } else {
                str += content.substring(i, i + 1);
            }
        }
        System.out.println(str.trim());
        return str.trim();
    }


}
