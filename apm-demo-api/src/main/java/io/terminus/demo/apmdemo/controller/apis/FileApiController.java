package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.utils.Dic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.Paths;

@RestController
@RequestMapping(path = "/api/file", method = RequestMethod.GET)
public class FileApiController {

    static final String folder = ".apm-demo"; // System.getProperty("java.io.tmpdir");
    static {
        File dir = new File(folder);
        System.out.println("java.io.tmpdir = " + dir.getAbsoluteFile());
        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                System.out.println("mkdirs field");
            }
        }
    }

    @GetMapping("/read")
    @ResponseBody
    public Object readFile() {
        File dir = new File(folder);
        if(!dir.exists()) {
           if(!dir.mkdirs()) {
               return new Dic().set("error", "mkdirs field");
           }
        }
        File file = Paths.get(dir.getPath(), "test.txt").toFile();
        int readtotal = 0;
        if(!file.exists()) {
            return new Dic().set("read", readtotal);
        }
        InputStream in = null;
        try {
            byte[]  buffer = new byte[100*1024];
            in = new FileInputStream(file);
            int byteread = 0;
            while ((byteread = in.read(buffer)) > 0) {
                readtotal += byteread;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        return new Dic().set("read", readtotal);
    }

    @GetMapping("/write")
    @ResponseBody
    public Object writeFile(@RequestParam(defaultValue = "1024") int size) {
        if(size > 5*1024*1024) {
            return new Dic().set("error","size too large");
        }
        File dir = new File(folder);
        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                return new Dic().set("error", "mkdirs field");
            }
        }
        File file = Paths.get(dir.getPath(), "test.txt").toFile();
        int writetotal = 0;
        OutputStream out = null;
        try {
            byte[] buffer = "abcdefg".getBytes("UTF-8");
            out = new FileOutputStream(file);
            while (writetotal < size) {
                out.write(buffer);
                writetotal += buffer.length;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                }
            }
        }
        return new Dic().set("write", writetotal);
    }
}
