package com.bfa.kafka.test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Check {
  public static void main(String[] argv) throws Exception {
    List<String> classNames = new ArrayList<String>();
    ZipInputStream zip = new ZipInputStream(new FileInputStream("/tmp/s/bfa-connectors.jar"));
    for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
      if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
        // This ZipEntry represents a class. Now, what class does it represent?
        String className = entry.getName().replace('/', '.'); // including ".class"
        classNames.add(className.substring(0, className.length() - ".class".length()));
      }
    }
  }
}
