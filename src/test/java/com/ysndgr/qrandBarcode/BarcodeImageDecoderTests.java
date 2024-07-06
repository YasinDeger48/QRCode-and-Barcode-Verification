package com.ysndgr.qrandBarcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

public class BarcodeImageDecoderTests {

    @DataProvider(name = "barcodeImages")
    public Object[][] barcodeImages() {
        return new Object[][]{
                {"src/test/resources/BarcodeImages/gs1DataBar.gif"},
                {"src/test/resources/BarcodeImages/gs1DigitalLink.gif"},
                {"src/test/resources/QRImages/image2.png"},
                {"src/test/resources/QRImages/image3.png"}
        };
    }

    @Test (dataProvider = "barcodeImages")
    public void testBarcode(String path) throws IOException {

        //String path = "src/main/resources/BarcodeImages/gs1DigitalLink.gif";
        File input = new File(path);
        BufferedImage image = ImageIO.read(input);

        // Decode the QR code
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = null;

        try {
            result = new MultiFormatReader().decode(bitmap);
            System.out.println("Decoded Text: " + result.getText());
            System.out.println("Create date = " + getAndDecodeUnixTime(result));
            System.out.println("Barcode Format = " + result.getBarcodeFormat());
            navigateAndWait5Seconds(result.getText(),path);


        } catch (NotFoundException e) {
            System.out.println("QR code is not found");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("QR decoding error");
        }
    }

    //If barcode contains link
    private static void navigateAndWait5Seconds(String url, String path){
        if(!path.contains("/BarcodeImages/")) {
            WebDriver driver = new ChromeDriver();
            driver.get(url);
            waitFor(5);
            driver.quit();
        }
    }
    // convert unix time
    private static Date getAndDecodeUnixTime(Result result){
        long timestamp = result.getTimestamp();
        return new Date(timestamp);
    }

    private static void waitFor(long seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}