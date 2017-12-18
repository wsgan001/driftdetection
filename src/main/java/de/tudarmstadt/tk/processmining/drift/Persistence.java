package de.tudarmstadt.tk.processmining.drift;

import java.io.*;

/**
 * @author Alexander Seeliger on 18.12.2017.
 */
public class Persistence {

    public static void saveToDisk(Object e, String file) {
        try {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                try (ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                    out.writeObject(e);
                }
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static <T> T loadFromDisk(String file, Class<T> type) {
        try {
            T e = null;

            try(FileInputStream fileIn = new FileInputStream(file)) {
                try(ObjectInputStream in = new ObjectInputStream(fileIn)) {
                    e = (T) in.readObject();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            return e;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        }
    }

}
