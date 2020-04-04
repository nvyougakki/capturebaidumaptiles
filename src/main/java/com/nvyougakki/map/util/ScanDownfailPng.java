package com.nvyougakki.map.util;

import com.nvyougakki.map.bean.Config;
import com.nvyougakki.map.bean.PicAxis;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @ClassName ScanDownfailPng
 * @Description TODO
 * @Author 女友Gakki
 * @Date 2020/3/21 15:55
 */
public class ScanDownfailPng {

    public static int nowCount = 0;
    public static String nowPath = "";
    private static Config config = new Config();

    //扫描空文件，并重新下载
    public static void countEmptyFile(File f, int count) throws URISyntaxException, IOException {
        if(f.isDirectory()) {
            File[] files = f.listFiles();
            for(File tMpf : files) {
                countEmptyFile(tMpf, count);
            }
        } else {
            nowCount++;
            nowPath = f.getAbsolutePath();
            if(f.length() == 0) {
                count++;
                System.err.println(f.getAbsolutePath());
                String[] arr = nowPath.split("\\\\");
                int x = Integer.parseInt(arr[4]);
                int y = Integer.parseInt(arr[5].split("\\.")[0]);
                int z = Integer.parseInt(arr[3]);
                PicAxis p = new PicAxis(x, y, z, config);

                InputStream ips = MapUtil.getPicIps(p.getUrl());
                File tmpf = new File(config.getFileRootPath() + "temp/" + z + "/" + x + "/" + y + config.getPicSuffix());
                if(!tmpf.getParentFile().exists()) tmpf.getParentFile().mkdirs();
                if(!tmpf.exists()) tmpf.createNewFile();
                FileOutputStream fos = new FileOutputStream(tmpf);
                IOUtils.copy(ips, fos);
                p.startDownload();
            }
        }
    }
    public static File f = null;
    public static FileOutputStream fos = null;
   /* public static void writFilePathToTxt(String text) throws IOException, URISyntaxException {
        URI uri = MapUtil.class.getClassLoader().getResource("downloadFail.txt").toURI();
        if(f == null) {
            f = new File(uri);
        }
        if(fos == null) {
            fos = new FileOutputStream(f, true);
        }
        fos.write((text + "\r\n").getBytes());
    }*/

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        File f = new File("F:\\tiles\\blueTiles");
//        File f = new File("F:\\tiles\\blueTiles\\3\\1\\0.png");
//        System.out.println(f.length());
//        f.delete()
//        f.createNewFile();

        new Thread(() -> {
            try {
                countEmptyFile(f, 0);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            Thread.sleep(3000);
            System.out.println("已排查：" + nowCount + "；排查到:" + nowPath);
        }
    }

}
