import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Scanner;

public class util {
    private static Connection con;
    private static NumberFormat nf;
    private static Scanner s;

    // Koneksi ke database
    public static Connection conDB() {
        if (con == null) {
            try {
                con = DriverManager.getConnection("jdbc:sqlite:tokoparfum.db");
                System.out.println("Berhasil terhubung ke database tokoparfum.db");
            } catch (SQLException e) {
                System.out.println("Gagal terhubung ke database: " + e.getMessage());
                System.exit(1);
            }
        }
        return con;
    }

    // Input scanner
    public static String input() {
        if (s == null) {
            s = new Scanner(System.in);
        }
        return s.nextLine().trim();
    }

    // Validasi angka (Integer)
    public static boolean isValidNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Format angka dengan format Indonesia
    public static String formatAngka(Integer angka) {
        if (nf == null) {
            nf = NumberFormat.getInstance(new Locale("id", "ID"));
        }
        return nf.format(angka);
    }


    // Menunggu input dari user sebelum melanjutkan
    public static void tahan(String pesanTahan) {
        System.out.println();
        System.out.print(pesanTahan);
        input(); // Tunggu hingga user menekan Enter
    }
    
    // Method untuk menghapus file database (reset database)
    public static void resetDatabase() {
        // Menghapus file database
        File dbFile = new File("tokoparfum.db");
        if (dbFile.exists()) {
            if (dbFile.delete()) {
                System.out.println("Database telah dihapus.");
            } else {
                System.out.println("Gagal menghapus database.");
            }
        }
    }
    
    // Method untuk mencetak judul menu
    public static void cetakJudul(String judul) {
        System.out.println();
        pembatas(30);
        System.out.println(judul);
        pembatas(30);
        System.out.println();
    }

    // Garis pembatas
    public static void pembatas(int panjang) {
        System.out.println();
        for (int i = 0; i < panjang; i++) {
            System.out.print("-");
        }
        System.out.println(); 
    }
    
    
    
    // MEMBUAT TABEL

    // Method untuk mencetak data dalam format tabel
    public static void tampilkanData(ArrayList<LinkedHashMap<String, String>> list) {
        if (list.isEmpty()) {
            System.out.println("Tidak ada data untuk ditampilkan.");
            return;
        }

        // Menentukan panjang kolom untuk tiap data
        LinkedHashMap<String, Integer> charLength = getTableCharLength(list);

        // Cetak garis atas tabel
        cetakGaris(charLength);

        // Cetak header kolom
        LinkedHashMap<String, String> header = list.get(0);
        for (String key : header.keySet()) {
            System.out.print("|");
            cetakCell(charLength.get(key), key);
        }
        System.out.println("|");

        // Cetak garis pemisah
        cetakGaris(charLength);

        // Cetak baris data
        for (LinkedHashMap<String, String> row : list) {
            for (String key : row.keySet()) {
                System.out.print("|");
                cetakCell(charLength.get(key), (row.get(key) == null || row.get(key).isEmpty()) ? " " : row.get(key));
            }
            System.out.println("|");
        }

        // Cetak garis bawah tabel
        cetakGaris(charLength);
    }


    // Menghitung panjang kolom
    private static LinkedHashMap<String, Integer> getTableCharLength(ArrayList<LinkedHashMap<String, String>> list) {
        LinkedHashMap<String, Integer> charLength = new LinkedHashMap<>();
        if (!list.isEmpty()) {
            LinkedHashMap<String, String> firstRow = list.get(0);
            for (String key : firstRow.keySet()) {
                charLength.put(key, key.length()); // Set length for header columns
            }
            // Iterate through each row to calculate max length for each column
            for (LinkedHashMap<String, String> row : list) {
                for (String key : row.keySet()) {
                    String value = row.get(key) == null ? "" : row.get(key);
                    charLength.put(key, Math.max(charLength.get(key), value.length()));
                }
            }
        }
        return charLength;
    }

    // Menampilkan garis pembatas tabel
    private static void cetakGaris(LinkedHashMap<String, Integer> charLength) {
        for (String key : charLength.keySet()) {
            System.out.print("+");
            for (int i = 0; i < (charLength.get(key) + 2); i++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }

    // Mencetak cell dengan padding sesuai panjang
    private static void cetakCell(Integer length, String str) {
        int maxLength = length - str.length() + 1;
        if (Character.isDigit(str.charAt(0))) {
            // Jika string diawali angka, rata kanan
            for (int i = 0; i < maxLength; i++) {
                System.out.print(" ");
            }
            System.out.print(str);
            System.out.print(" ");
        } else {
            // Jika string tidak diawali angka, rata kiri
            System.out.print(" ");
            System.out.print(str);
            for (int i = 0; i < maxLength; i++) {
                System.out.print(" ");
            }
        }
    }
}
