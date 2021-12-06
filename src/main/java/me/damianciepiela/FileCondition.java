package me.damianciepiela;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileCondition {

    private final File file;
    private final Lock lock = new ReentrantLock();

    public FileCondition(File file) {
        this.file = file;
    }

   public void writeToFile(String line) {
        this.lock.lock();
       try {
           BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file));
           bufferedWriter.write(line);
           bufferedWriter.newLine();
           bufferedWriter.flush();
           bufferedWriter.close();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if(((ReentrantLock)this.lock).isHeldByCurrentThread()) {
               this.lock.unlock();
           }
       }
   }

}
