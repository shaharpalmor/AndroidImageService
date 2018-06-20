package com.example.owner.myapplication;

import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class TcpConnection {

    private Socket socket;
    private OutputStream outputStream;
    private List<File> allFiles;
    public TcpConnection() {
    }

    public void connect() {
        try {
            InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
            socket = new Socket(serverAddr, 6666);
            try {
                outputStream = socket.getOutputStream();
            } catch (Exception e) {
                Log.e("TCP", "S: Error:", e);
            }
        } catch (Exception e) {
            Log.e("TCP", "S: Error:", e);
        }

    }

    public void getAllFilesFromAllDirs() {
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        //get the dirs
        File[] fileOrDir = dcim.listFiles();
        List<File> picsFilesList = new ArrayList<File>();
        int len =fileOrDir.length;
        if (fileOrDir != null) {
            for (int i=0; i <len; i++) {
                //check if dir
                if (fileOrDir[i].isDirectory()) {
                    getOneFile(fileOrDir[i], picsFilesList);
                } else if(fileOrDir[i].toString().contains(".jpg")) { //check if file
                    picsFilesList.add(fileOrDir[i]);
                }
            }
        }
        //update the member
        allFiles = picsFilesList;
    }

    public void getOneFile(File dir, List<File> picsFilesList) {
        File[] dirFiles = dir.listFiles();
        int len = dirFiles.length;
        for (int i=0; i <len; i++) {
            if (dirFiles[i].isDirectory()) {
                getOneFile(dirFiles[i], picsFilesList);
            } else if(dirFiles[i].toString().contains(".jpg")) {
                picsFilesList.add(dirFiles[i]);
            }
        }
    }

    public void startConnection(final NotificationManager notificationManager, final NotificationCompat.Builder builder) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
                //File dcim = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
                //if (dcim == null) {
                  //  return;
                //}

                //File[] files = dcim.listFiles();
                getAllFilesFromAllDirs();
                double numberOfPictures = allFiles.size();
                double count = 0;

                if (allFiles != null) {
                    for (File file : allFiles) {
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            Bitmap bm = BitmapFactory.decodeStream(fis);
                            byte[] imgbyte = getBytesFromBitmap(bm);
                            try {
                                int imageLength = imgbyte.length;

                                //sends the size of the array bytes.
                                String picSizeString = imageLength + "";
                                outputStream.write(picSizeString.getBytes(), 0, picSizeString.getBytes().length);
                                outputStream.flush();
                                Thread.sleep(100);

                                //sends the name of file.
                                String fileNameString = file.getName();
                                outputStream.write(fileNameString.getBytes(), 0, fileNameString.getBytes().length);
                                outputStream.flush();
                                Thread.sleep(100);

                                //sends the array bytes.
                                outputStream.write(imgbyte, 0, imgbyte.length);
                                outputStream.flush();
                                Thread.sleep(500);

                            } catch (Exception e1) {
                                Log.e("TCP", "S: Error:", e1);
                            }
                        } catch (Exception e2) {
                            Log.e("TCP", "S: Error:", e2);
                        }

                        count++;
                        int myProgress = (int) ((count / numberOfPictures) * 100);
                        String message = myProgress + "%";
                        builder.setProgress(100, myProgress, false);
                        builder.setContentText(message);
                        notificationManager.notify(1, builder.build());

                    }
                    try {
                        String toSend = "Stop Transfer\n";
                        outputStream.write(toSend.getBytes(), 0, toSend.getBytes().length);
                        outputStream.flush();

                        builder.setContentTitle("Finish transfer");
                        builder.setContentText("Image Service finish backing up your photos");
                        notificationManager.notify(1, builder.build());

                    } catch (Exception e3) {
                        Log.e("TCP", "S: Error:", e3);

                        builder.setContentTitle("Error");
                        builder.setContentText("Image Service could not transfer your photos");
                        notificationManager.notify(1, builder.build());
                    }
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

    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            Log.e("TCP", "S: Error:", e);
        }
    }
}




