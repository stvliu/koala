package org.openkoala.koala.deploy.curd.module.analysis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;

import org.openkoala.koala.deploy.curd.module.util.ClassLoaderClear;
import org.openkoala.koala.pojo.MavenProject;
import org.openkoala.koala.pojo.ModuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 类    名：CURDClassLoader.java
 *   
 * 功能描述：类加载器，加载选中项目中的WEB项目中的所有类，以便进行下一步分析
 *  
 * 创建日期：2013-1-21下午2:49:41     
 * 
 * 版本信息：
 * 
 * 版权信息：Copyright (c) 2013 Koala All Rights Reserved
 * 
 * 作    者：lingen(lingen.liu@gmail.com)
 * 
 * 修改记录： 
 * 修 改 者    修改日期     文件版本   修改说明
 */
public class CURDClassLoader {
    private static final Logger logger = LoggerFactory.getLogger(CURDClassLoader.class);

    private static URLClassLoader classloader;

    private MavenProject mavenProject;

    private List<URL> urlList = new ArrayList<URL>();

    private CURDClassLoader(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
        initLoader();
        URL[] urls = new URL[] {};
        classloader = new URLClassLoader(urlList.toArray(urls));
    };

    public static CURDClassLoader getCURDClassLoader(MavenProject mavenProject) {
        CURDClassLoader classLoader = new CURDClassLoader(mavenProject);
        return classLoader;
    }

    private void initLoader() {
        try {
            List<MavenProject> childs = this.mavenProject.getChilds();
            for (MavenProject web : childs) {
                if (web.getType().equals(ModuleType.War)) {
                    String dir = this.mavenProject.getPath() + "/" + web.getName() + "/target/"
                            + (web.getArtifactId() + "-" + web.getVersion()) + "/WEB-INF/classes/";
                    String libs = this.mavenProject.getPath() + "/" + web.getName() + "/target/"
                            + (web.getArtifactId() + "-" + web.getVersion()) + "/WEB-INF/lib/";
                    loadeWeb(dir, libs);
                } else {
                    String dir = this.mavenProject.getPath() + "/" + web.getName() + "/target/classes/";
                    loadJar(dir);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Class forName(String name) throws ClassNotFoundException {
        return classloader.loadClass(name);
    }

    private void loadJar(String dir) throws MalformedURLException {
        urlList.add(new URL("file:" + dir));
    }

    private void loadeWeb(String dir, String libs) throws MalformedURLException {
        urlList.add(new URL("file:" + dir));
        File lib = new File(libs);
        File[] jarFiles = lib.listFiles();
        for (File jarFile : jarFiles) {
            logger.info("addJar:" + jarFile.getAbsolutePath());
            urlList.add(new URL("file:" + jarFile.getAbsolutePath()));
        }
    }

    public void close() {
        ClassLoaderClear.getClassLoaderClear().close(this.classloader);
    }
    
}