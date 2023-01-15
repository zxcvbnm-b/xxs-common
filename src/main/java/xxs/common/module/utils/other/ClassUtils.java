package xxs.common.module.utils.other;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
public class ClassUtils {
    public static Class<?> getClassFirst(String packageName,String className) {
        Class<?> clazz=null;
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    clazz = findClassFirstInPackageByFile(packageName, filePath, recursive,className);
                    if(clazz!=null){
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 在package对应的路径下找到所有的class
     */
    private static Class<?> findClassFirstInPackageByFile(String packageName, String filePath, final boolean recursive,
                                                     String className) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                Class<?> clazz = findClassFirstInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, className);
                if(clazz!=null){
                    return clazz;
                }
            } else {
                String classFileName = file.getName().substring(0, file.getName().length() - 6);
                try {
                    if(classFileName.equals(className)){
                        return Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + classFileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return  null;
    }
}