package top.icss.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author cd
 * @desc 反射工具类
 * @create 2020/3/26 11:30
 * @since 1.0.0
 */
public class ReflectUtils {

    /**
     * 是否循环迭代
     */
    private final static boolean recursive = true;

    /**
     * 扫描 包下面所有Class
     * @param packageName 包名称
     * @param <T>
     * @return
     */
    public static <T> List<Class<T>> getClass(String packageName){
        List<Class<T>> list = new ArrayList<>();

        String packageNamePath = packageName;
        packageNamePath = packageNamePath.replace(".", "/");

        Enumeration<URL> resources;
        try {
            //定义一个枚举的集合 并进行循环来处理这个目录下的things
            resources = Thread.currentThread().getContextClassLoader().getResources(packageNamePath);
            //循环迭代
            while (resources.hasMoreElements()){
                URL url = resources.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //如果是以文件的形式保存在服务器上
                if("file".equals(protocol)){
                    System.err.println("file类型的扫描");
                    String filePath = URLDecoder.decode(url.getFile(), "utf-8");
                    // 获取此包的目录 建立一个File
                    File dir = new File(filePath);
                    list.addAll(getClass(dir, packageName));
                }else if("jar".equals(protocol)){
                    System.err.println("jar类型的扫描");
                    JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                    list.addAll(getClass(jar, packageName, packageNamePath));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }

    /**
     * 迭代查找文件类
     * @param filePath
     * @param packageName
     * @param <T>
     * @return
     */
    private static <T> List<Class<T>> getClass(File filePath, String packageName){
        List<Class<T>> classes = new ArrayList<>();
        if(!filePath.exists()){
            return classes;
        }

        // 如果存在 就获取包下的所有文件 包括目录
        File[] files = filePath.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || file.getName().endsWith(".class");
            }
        });

        for (File file : files){
            // 如果是目录 则继续扫描
            if(file.isDirectory()){
                classes.addAll(getClass(file, packageName + "." + file.getName()));
            }else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String fileName = file.getName();
                String className = fileName.substring(0, fileName.length() - 6);
                className = packageName + "." + className;
                try {
                    //这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    //Class<T> cls = (Class<T>) Class.forName(className);
                    Class<T> cls = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(className);
                    classes.add(cls);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

        return classes;
    }


    /**
     * 获取jar包中的class
     * @param jar
     * @param packageName
     * @param packageNamePath
     * @param <T>
     * @return
     */
    public static  <T> List<Class<T>> getClass(JarFile jar, String packageName, String packageNamePath){
        List<Class<T>> classes = new ArrayList<>();
        if(jar == null){
            return classes;
        }

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()){
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果是以/开头的
            if (name.charAt(0) == '/') {
                // 获取后面的字符串
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageNamePath)) {
                int idx = name.lastIndexOf('/');
                // 如果以"/"结尾 是一个包
                if (idx != -1) {
                    // 获取包名 把"/"替换成"."
                    packageName = name.substring(0, idx)
                            .replace('/', '.');
                }

                // 如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive) {
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(".class")
                            && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        try {
                            //这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                            //Class<T> cls = (Class<T>) Class.forName(className);
                            Class<T> cls = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(className);
                            classes.add(cls);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return classes;
    }





    public static void main(String[] args) {
        System.out.println(Arrays.toString(getClass("top.icss").toArray()));
    }
}
