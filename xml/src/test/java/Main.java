import com.axml.AndroidManifest;

import java.io.File;
import java.io.IOException;

/**
 * Created by Sens on 2021/8/27.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        System.out.println("file->" + file);

        AndroidManifest androidManifest = new AndroidManifest(file);
        System.out.println("manifest done");

        byte[] datas = androidManifest.toBytes();

        AndroidManifest manifest = new AndroidManifest(datas);
        System.out.println(manifest);
    }
}
