package org.code4j.jproxy.util;/**
 * Description : 
 * Created by YangZH on 16-5-26
 *  下午1:49
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Description :
 * Created by YangZH on 16-5-26
 * 下午1:49
 */

public class ImageUtil {

    public static boolean isImage(byte[] content){
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if (image == null) {
                return false;
            }
            System.out.println("high: " + image.getHeight());
        } catch(IOException ex) {
            return false;
        }
        return true;
    }

}
