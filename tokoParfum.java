public class tokoParfum {
    public static void main(String[] args) {
        System.out.println("Menyiapkan data...");

        // Migration: Method untuk membuat tabel dari semua class menu
        penjual.buatTabel(); // buat tabel produk
        penjual.buatDataAwal();
        LaporanPenjualan.buatTabelPenjualan();

        // mulai
        System.out.println();
        System.out.println("+--------------------------------------------+");
        System.out.println("|             Aplikasi Toko Parfum           |");
        System.out.println("+--------------------------------------------+");
        System.out.println("|                Created By                  |");
        System.out.println("|                Alfarobby27                 |");
        System.out.println("+--------------------------------------------+");

        util.tahan("Silahkan Tekan Enter ⏎ Untuk Memulai Aplikasi...");

        // mulai navigasi
        menuUtama();
    }

    public static void menuUtama() {
        util.cetakJudul("Menu Utama");
        System.out.println("1. Sebagai Penjual");
        System.out.println("2. Sebagai Pembeli");
        System.out.println();
        System.out.println("0. Keluar");
        System.out.println();
        System.out.print("Silakan pilih menu (0-2) : ");

        switch (util.input()) {
            case "1": penjual.menuPenjual(); break;
            case "2": pembeli.menuPembeli(); break;
            case "0": 
                System.out.println();
                System.out.println("Terima kasih telah menggunakan aplikasi kami..."); 
                util.resetDatabase();
                System.exit(0);
            default: 
                System.out.println();
                System.out.println("Nomor menu tidak valid! Silakan pilih menu dengan angka 0 sampai 2"); 
                menuUtama(); break;
        }
    }
}
