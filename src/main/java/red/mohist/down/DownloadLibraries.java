package red.mohist.down;

import static it.unimi.dsi.fastutil.io.TextIO.BUFFER_SIZE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import red.mohist.configuration.MohistConfigUtil;
import red.mohist.util.FileUtil;
import red.mohist.util.HttpUtil;
import red.mohist.util.i18n.Message;

public class DownloadLibraries implements Runnable {

    public static final String FIND_LOCATE = "http://ip.taobao.com//service/getIpInfo.php?ip=myip";

    @Override
    public void run() {
        String url = null;
        String fileName = "libraries.zip";
        try {
            JsonObject json = new JsonParser().parse(HttpUtil.doGet(FIND_LOCATE)).getAsJsonObject();
            String locate = json.getAsJsonObject("data").get("country_id").getAsString();
            if (locate.equals("CN")) {
                System.out.println("Detected China IP, Using Gitee Mirror.");
                url = "https://mohist-community.gitee.io/mohistdown/libraries.zip"; //Gitee Mirror
            }
        } catch (Exception e) {
            System.out.println("IP Detected Failed ! Using Github Mirror.");
            url = "https://github.com/Mohist-Community/Mohist/releases/download/libraries/libraries.zip"; //Github Mirror
        }
        new Download(url, fileName);
        File file = new File(fileName);
        unZip(file);
    }


    public static void unZip(File srcFile) throws RuntimeException {
        long start = System.currentTimeMillis();
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "The file indicated does not exist.");
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            System.out.println(Message.getFormatString("file.unzip.start", new Object[]{ srcFile}));
            System.out.println(Message.getFormatString("file.unzip.now", new Object[]{ srcFile, Download.getSize(srcFile.length())}));
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (entry.isDirectory()) {
                    String dirPath = entry.getName();
                    File dir = new File(MohistConfigUtil.getMohistJarPath() + dirPath);
                    dir.mkdirs();
                } else {
                    File targetFile = new File(MohistConfigUtil.getMohistJarPath() + entry.getName());
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println(Message.getFormatString("file.unzip.ok", new Object[]{(end - start)}));
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if(zipFile != null){
                try {
                    zipFile.close();
                    FileUtil.deleteFile(srcFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
