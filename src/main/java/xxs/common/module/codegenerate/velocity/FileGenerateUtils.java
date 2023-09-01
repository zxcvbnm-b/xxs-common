package xxs.common.module.codegenerate.velocity;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.*;
import java.util.Date;

/**
 * 文件生成工具
 * @author xiongsongxu
 */
public class FileGenerateUtils {
    /**
     * 获取真正的输出文件名称  如果文件存在，那么添加日期作为文件标识 （这样会导致java文件和类名不一样）
     */
    public static String getRealOutFilePathName(String outFilePathName, boolean coverExistFile) {
        String realOutFilePathName = outFilePathName;
        if (!coverExistFile) {
            return realOutFilePathName;
        }
        File outFile = new File(realOutFilePathName);
        if (outFile.exists()) {
            String fileName = outFilePathName.substring(outFilePathName.lastIndexOf("\\") + 1, outFilePathName.lastIndexOf("."));
            String filePost = outFilePathName.substring(outFilePathName.lastIndexOf(".") + 1);
            String filePre = outFilePathName.substring(0, outFilePathName.lastIndexOf("\\") + 1);
            String newFileName = fileName + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH-mm-ss");
            realOutFilePathName = filePre + newFileName + "." + filePost;
        }
        return realOutFilePathName;
    }

    public static void writeFile(File file, String content, String fileEncoding, boolean append) throws IOException {

        String path = file.getPath();
        path = path.substring(0, path.lastIndexOf("\\"));
        /*要有文件夹才能生成文件*/
        File file2 = new File(path);
        file2.mkdirs();
        // 如果文件不存在，那么追加的话会创建文件然后追加
        FileOutputStream fos = new FileOutputStream(file, append);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        try (BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(content);
            bw.flush();
        } finally {
            osw.close();
            fos.close();
        }

    }
}
