/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DriveOperation;

/**
 *
 * @author Srdjan
 */
public class sf180321_DriveOperation implements DriveOperation {

    public static class Paket {

        public int IdPak;
        public int X;
        public int Y;
        public int IdAOd;
        public int IdADo;

        public double Tezina;

        public Paket(int IdPak, int X, int Y, int IdAOd, int IdADo, double Tezina) {
            this.IdAOd = IdAOd;
            this.IdADo = IdADo;
            this.IdPak = IdPak;
            this.X = X;
            this.Y = Y;
            this.Tezina = Tezina;
        }

        public void setX(int X) {
            this.X = X;
        }

        public void setY(int Y) {
            this.Y = Y;
        }

    }

    public void sortiraj_listu_paketa_euklid(List<Paket> lista, int X, int Y) {

        int k = 0;
        int XX = X;
        int YY = Y;
        while (k != lista.size() - 1) {

            for (int i = k; i < lista.size() - 1; ++i) {
                for (int j = i + 1; j < lista.size(); ++j) {

                    double distancai = Math.sqrt(Math.pow(lista.get(i).X - XX, 2) + Math.pow(lista.get(i).Y - YY, 2));
                    double distancaj = Math.sqrt(Math.pow(lista.get(j).X - XX, 2) + Math.pow(lista.get(j).Y - YY, 2));

                    if (distancai > distancaj) {
                        Paket pom = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, pom);

                    }

                }
            }

            XX = lista.get(k).X;
            YY = lista.get(k).Y;
            k++;

        }

    }

    public List<Paket> promeni_kooridnate_liste(List<Paket> lista, Connection conn) {

        List<Paket> list = new ArrayList<>(lista);

        for (int i = 0; i < list.size(); ++i) {

            try ( PreparedStatement stm = conn.prepareStatement("select X,Y from Adresa where IdA=?")) {

                int adr = list.get(i).IdADo;

                stm.setInt(1, adr);

                ResultSet rs = stm.executeQuery();

                if (rs.next()) {

                    list.get(i).setX(rs.getInt(1));
                    list.get(i).setY(rs.getInt(2));

                }

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_DriveOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return list;

    }

    @Override
    public boolean planingDrive(String username) {

        List<Paket> lista_paketa_izgrada = new ArrayList<>();

        Connection conn = DB.getInstance().getConnection();
        String provera = "select K.IdK from Kurir KU join Korisnik K on (KU.IdK=K.IdK) where Korime=? and KU.Status=0";

        int IdK = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Kurir sa zadatim imenom ili nije slobodan ili ne postoji!");
                return false;
            }
            IdK = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select IdG from Korisnik K join Adresa A on (A.IdA=K.IdA) where IdK=?";

        int IdG = -1;

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdK);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                IdG = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select IdM,X,Y,A.IdA from Magacin M join Adresa A on (M.IdA=A.IdA) where IdG=?";

        int IdM = -1;
        int Xmag = -1;
        int Ymag = -1;
        int IdAmag = -1; //adresa magacina

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdG);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("U gradu u kome je Kurir nema magacina?");
                return false;
            }

            IdM = rs.getInt(1);
            Xmag = rs.getInt(2);
            Ymag = rs.getInt(3);
            IdAmag = rs.getInt(4);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        //DOHVACEN IdM u Gradu gde se nalazi Kurir!

        provera = "select V.IdVoz,Nosivost from Vozilo V join Parkirana P on (P.IdVoz=V.IdVoz) where IdM=? and V.IdVoz not in( select IdVoz from Vozi)";

        int IdVoz = -1;
        double Nosivost = 0;
        double TezinaPaketa = 0;

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdM);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("U magacinu trenutno nema slobodnih vozila!");
                return false;
            }

            IdVoz = rs.getInt(1);
            Nosivost = (rs.getBigDecimal(2)).doubleValue();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Dohvaceno IDVoz iz Magacina
        //Provera da li treba pokupiti pakete iz istog grada
        provera = "select IdI,X,Y,I.IdAOd,I.IdADo,Tezina from Isporuka I join Adresa A on (I.IdAOd=A.IdA) where IdG=? and StatusPaketa=1 order by VremePrihvatanja DESC"; //dohvata sve pakete cija ponuda je prihvacena!

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdG);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {

                Paket pak = new Paket(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getBigDecimal(6).doubleValue());

                if (TezinaPaketa + pak.Tezina <= Nosivost) {
                    lista_paketa_izgrada.add(pak);
                    TezinaPaketa += pak.Tezina;
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        // sortiraj_listu_paketa_euklid(lista_paketa_izgrada, Xmag, Ymag);
        //dodavanje vozaca u Vozi entitet
        provera = "insert into Vozi (IdK,IdVoz,PredjeniPut,TrenutnaLokacija) values(?,?,0,?)"; //dohvata sve pakete cija ponuda je prihvacena!

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdK);
            stm.setInt(2, IdVoz);
            stm.setInt(3, IdAmag);

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Paket> lista_paketa_magacin = new ArrayList<>();
        provera = "select IdI,X,Y,I.IdAOd,I.IdADo,Tezina from Isporuka I join Adresa A on (I.IdADo=A.IdA) where StatusPaketa=2 and Lokacija=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdAmag);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Paket pak = new Paket(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getBigDecimal(6).doubleValue());

                if (TezinaPaketa + pak.Tezina <= Nosivost) {
                    lista_paketa_magacin.add(pak);
                    TezinaPaketa += pak.Tezina;

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!lista_paketa_izgrada.isEmpty() || !lista_paketa_magacin.isEmpty()) { //u suprotvnom nema plana voznje

            //Brisanje vozila iz Parkirana!
            provera = "delete from Parkirana where IdVoz=?";

            try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                stm.setInt(1, IdVoz);

                stm.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            int polazna_adresa = IdAmag; //ako se treba vratiti u magacin onda isporuka krene od magacina u suprotnom od poslednje pokupljenog paketa u gradu

            if (lista_paketa_magacin.isEmpty()) {
                polazna_adresa = lista_paketa_izgrada.get(lista_paketa_izgrada.size() - 1).IdAOd;
            }

            int Xt = -1;
            int Yt = -1;

            provera = "select X,Y from Adresa where IdA=?";

            try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                stm.setInt(1, polazna_adresa);

                ResultSet rs = stm.executeQuery();

                if (rs.next()) {

                    Xt = rs.getInt(1);
                    Yt = rs.getInt(2);

                }

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            //ubacivanja Automobila u Vozi
//            provera = "insert into Vozi (IdK,IdVoz,TrenutnaLokacija) values(?,?,?)";
//
//            try ( PreparedStatement stm = conn.prepareStatement(provera)) {
//
//                stm.setInt(1, IdK);
//                stm.setInt(2, IdVoz);
//                stm.setInt(3, polazna_adresa);
//                stm.executeUpdate();
//
//            } catch (SQLException ex) {
//                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
//            }
            //Promena statusa Kuriru
            provera = "update Kurir set Status=1 where IdK=?";

            try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                stm.setInt(1, IdK);

                stm.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (int i = 0; i < lista_paketa_izgrada.size(); ++i) {
                provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(0,?,?,?)"; // status 0 znaci da gledam IdAOd

                try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                    stm.setInt(1, lista_paketa_izgrada.get(i).IdPak);
                    stm.setInt(2, IdK);
                    stm.setInt(3, IdVoz);
                    stm.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //DEO ZA MAGACIN DA KOMBI ODE U MAGACIN PRVO
            provera = "select IdI,Tezina from Isporuka where Lokacija=? and StatusPaketa!=3"; // dohvata sve pakete koji su u magacinu
            List<Integer> paketi_u_magacinu = new ArrayList<>();

            try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                stm.setInt(1, IdAmag);

                ResultSet rs = stm.executeQuery();

                while (rs.next()) {
                    double tezina = rs.getBigDecimal(2).doubleValue();
                    if (TezinaPaketa + tezina <= Nosivost) {
                        TezinaPaketa += tezina;
                        paketi_u_magacinu.add(rs.getInt(1));
                    }

                }

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int i = 0; i < paketi_u_magacinu.size(); ++i) {
                provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(4,?,?,?)"; // status 4 znaci da treba pokupiti pakete u magacinu

                try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                    stm.setInt(1, paketi_u_magacinu.get(i));
                    stm.setInt(2, IdK);
                    stm.setInt(3, IdVoz);
                    stm.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            List<Paket> lista_za_isporuku = promeni_kooridnate_liste(lista_paketa_izgrada, conn); //sad se paketi isporucuju 

            lista_za_isporuku.addAll(lista_paketa_magacin);

            sortiraj_listu_paketa_euklid(lista_za_isporuku, Xt, Yt); //sortira celu listu za isporuku na osnovu euklidske distance 

//            System.out.println("LISTA:");
//            for (int i = 0; i < lista_za_isporuku.size(); ++i) {
//                System.out.println(lista_za_isporuku.get(i).IdPak);
//            }
            int IdGrad = -1;

            for (int i = 0; i < lista_za_isporuku.size(); ++i) {

                provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(1,?,?,?)"; // status 1 znaci da gledam IdAOd

                try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                    stm.setInt(1, lista_za_isporuku.get(i).IdPak);
                    stm.setInt(2, IdK);
                    stm.setInt(3, IdVoz);
                    stm.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }

                List<Integer> ponovo_kupljenje_lista = new ArrayList<>();

                provera = "select IdG from Isporuka I join Adresa A on(A.IdA=I.IdADo) where IdI=? "; // Da li za taj grad gde se isporucuje ima jos posiljki za kupljenje

                try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                    stm.setInt(1, lista_za_isporuku.get(i).IdPak);

                    ResultSet rs = stm.executeQuery();

                    if (rs.next()) {

                        if (IdGrad == -1) {
                            IdGrad = rs.getInt(1);
                        }
                        if (IdGrad != rs.getInt(1) || i == lista_za_isporuku.size() - 1) {
                            IdGrad = rs.getInt(1);

                            if (i != lista_za_isporuku.size() - 1) {
                                provera = "delete from PrevoziSe where IdI=? and IdK=? and IdVoz=? and StatusPak=1";

                                try ( PreparedStatement stmm = conn.prepareStatement(provera)) {

                                    stmm.setInt(1, lista_za_isporuku.get(i).IdPak);
                                    stmm.setInt(2, IdK);
                                    stmm.setInt(3, IdVoz);
                                    stmm.executeUpdate();

                                } catch (SQLException ex) {
                                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            provera = "select IdI,Tezina from Isporuka I join Adresa A on(I.IdAOd=A.IdA) where IdG=? and StatusPaketa=1 and IdI not in("
                                    + "select IdI from PrevoziSe) order by VremePrihvatanja ASC";

                            try ( PreparedStatement stmmm = conn.prepareStatement(provera)) {

                                stmmm.setInt(1, IdGrad);

                                ResultSet rsss = stmmm.executeQuery();
                                while (rsss.next()) {

                                    if (TezinaPaketa + rsss.getBigDecimal(2).doubleValue() <= Nosivost) {
                                        TezinaPaketa += rsss.getBigDecimal(2).doubleValue();
                                        ponovo_kupljenje_lista.add(rsss.getInt(1));
                                    }

                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(3,?,?,?)"; // kupljenje ali lete u magacin

                            for (int j = 0; j < ponovo_kupljenje_lista.size(); ++j) {
                                try ( PreparedStatement stmm = conn.prepareStatement(provera)) {

                                    stmm.setInt(1, ponovo_kupljenje_lista.get(j));
                                    stmm.setInt(2, IdK);
                                    stmm.setInt(3, IdVoz);
                                    stmm.executeUpdate();

                                } catch (SQLException ex) {
                                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            //PROVERA ZA MAGACIN BEGIN---------------------------------------------------------------------------------------
                            int adr_magacina_u_trenutnom_gradu = -1;

                            provera = "select M.IdA from  Adresa A join Magacin M on (M.IdA=A.IdA) where IdG=?"; // Magacin u trenutnom gradu

                            try ( PreparedStatement stmm = conn.prepareStatement(provera)) {

                                stmm.setInt(1, IdGrad);
                                ResultSet rss = stmm.executeQuery();
                                if (rss.next()) {
                                    adr_magacina_u_trenutnom_gradu = rss.getInt(1);
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            //DEO ZA MAGACIN DA KOMBI ODE U MAGACIN PRVO
                            provera = "select IdI,Tezina from Isporuka where Lokacija=?"; // dohvata sve pakete koji su u magacinu
                            paketi_u_magacinu = new ArrayList<>();

                            try ( PreparedStatement stmm2 = conn.prepareStatement(provera)) {

                                stmm2.setInt(1, adr_magacina_u_trenutnom_gradu);

                                ResultSet rss2 = stmm2.executeQuery();

                                while (rss2.next()) {
                                    double tezina = rss2.getBigDecimal(2).doubleValue();
                                    if (TezinaPaketa + tezina <= Nosivost) {
                                        TezinaPaketa += tezina;
                                        paketi_u_magacinu.add(rss2.getInt(1));
                                    }

                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            for (int j = 0; j < paketi_u_magacinu.size(); ++j) {
                                provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(4,?,?,?)"; // status 4 znaci da treba pokupiti pakete u magacinu

                                try ( PreparedStatement stmm2 = conn.prepareStatement(provera)) {

                                    stmm2.setInt(1, paketi_u_magacinu.get(j));
                                    stmm2.setInt(2, IdK);
                                    stmm2.setInt(3, IdVoz);
                                    stmm2.executeUpdate();

                                } catch (SQLException ex) {
                                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            //PROVERA ZA MAGACIN END---------------------------------------------------------------------------------------

                            if (i != lista_za_isporuku.size() - 1) {

                                provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(1,?,?,?)"; // status 1 znaci da gledam IdAOd

                                try ( PreparedStatement stmm = conn.prepareStatement(provera)) {

                                    stmm.setInt(1, lista_za_isporuku.get(i).IdPak);
                                    stmm.setInt(2, IdK);
                                    stmm.setInt(3, IdVoz);
                                    stmm.executeUpdate();

                                } catch (SQLException ex) {
                                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            for (int i = 0; i < lista_za_isporuku.size(); ++i) {
                System.out.println(lista_za_isporuku.get(i).IdPak);
            }

            ArrayList<Paket> lista_za_magacin = new ArrayList<>();

            for (int i = 0; i < lista_za_isporuku.size(); ++i) {

                int AdresaIsporuka = lista_za_isporuku.get(i).IdADo;

                provera = "select IdI,Tezina from Isporuka where IdAOd=?"; // status 1 znaci da gledam IdAOd

                try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                    stm.setInt(1, AdresaIsporuka);

                    ResultSet rs = stm.executeQuery();

                    while (rs.next()) {

                        TezinaPaketa -= lista_za_isporuku.get(i).Tezina;

                        if (TezinaPaketa + rs.getBigDecimal(2).doubleValue() <= Nosivost) {
                            TezinaPaketa += rs.getBigDecimal(2).doubleValue();

                            Paket pak = new Paket(rs.getInt(1), 0, 0, -1, -1, rs.getBigDecimal(2).doubleValue());
                            lista_za_magacin.add(pak);
                        }

                    }

                } catch (SQLException ex) {
                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            for (int i = 0; i < lista_za_magacin.size(); ++i) {

                provera = "insert into PrevoziSe (StatusPak,IdI,IdK,IdVoz) values(2,?,?,?)"; // status 2 znaci da je Paket za magacin

                try ( PreparedStatement stm = conn.prepareStatement(provera)) {

                    stm.setInt(1, lista_za_magacin.get(i).IdPak);
                    stm.setInt(2, IdK);
                    stm.setInt(3, IdVoz);

                    stm.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return true;

        }

        return false;

    }

    @Override
    public int nextStop(String username) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select K.IdK,IdVoz,A.IdG from Vozi V join Korisnik K on (V.IdK=K.IdK) join Adresa A on(A.IdA=K.IdA) where Korime=?";

        int IdK = -1;
        int IdVoz = -1;
        int GradKurira = -1;

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Kurir sa zadatim imenom ili nije slobodan ili ne postoji!");
                return -5;
            }
            IdK = rs.getInt(1);
            IdVoz = rs.getInt(2);
            GradKurira = rs.getInt(3);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select M.IdA from Magacin M join Adresa A on(M.IdA=A.IdA) where IdG=?";

        int adresa_magacina = -1;

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {
            stm.setInt(1, GradKurira);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Fatalna greska!");
                return -5;
            }
            adresa_magacina = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select V.TrenutnaLokacija,X,Y,IdG from Vozi V join Adresa A on (V.TrenutnaLokacija=A.IdA) where IdVoz=? and IdK=?";

        int trenutnaLokacija = -1;
        int trenutni_grad = -1;
        int X = -1;
        int Y = -1;

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdVoz);
            stm.setInt(2, IdK);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Fatalna greska!");
                return -5;
            }
            trenutnaLokacija = rs.getInt(1);
            X = rs.getInt(2);
            Y = rs.getInt(3);
            trenutni_grad = rs.getInt(4);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select IdI,StatusPak from PrevoziSe where StatusPak!=2 and IdK=? and IdVoz=?";

        int IdI = -1;
        int status = 0;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdK);
            stm.setInt(2, IdVoz);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {

                IdI = rs.getInt(1);
                status = rs.getInt(2);

                if (status == 0 || status == 3) {//kupi paket iz grada ili leti u magacin

                    String query = "Select X,Y,Cena,A.IdA from Isporuka I join Adresa A on(I.IdAOd=A.IdA) where IdI=?";

                    try ( PreparedStatement stm2 = conn.prepareStatement(query)) {

                        stm2.setInt(1, IdI);
                        ResultSet rs2 = stm2.executeQuery();
                        int Xdest = -1;
                        int Ydest = -1;
                        int adr_dest = -1;
                        double Cena = -1;

                        if (rs2.next()) {
                            Xdest = rs2.getInt(1);
                            Ydest = rs2.getInt(2);
                            Cena = rs2.getBigDecimal(3).doubleValue();
                            adr_dest = rs2.getInt(4);

                            double put = Math.sqrt(Math.pow(Xdest - X, 2) + Math.pow(Ydest - Y, 2));

                            if (status != 3) {
//                                query = "update Kurir set Profit=Profit+? where IdK=?";
//                                try ( PreparedStatement stm3 = conn.prepareStatement(query)) {
//                                    stm3.setBigDecimal(1, BigDecimal.valueOf(Cena));
//                                    stm3.setInt(2, IdK);
//
//                                    stm3.executeUpdate();
//
//                                }
                            }
                            if (status == 3) {
                                query = "update PrevoziSe set StatusPak=2 where IdI=?";
                                try ( PreparedStatement stm3 = conn.prepareStatement(query)) {
                                    stm3.setInt(1, IdI);

                                    stm3.executeUpdate();

                                }
                            }

                            query = "update Vozi set PredjeniPut= PredjeniPut + ?, TrenutnaLokacija=? where IdK=? and IdVoz=?";
                            try ( PreparedStatement stm3 = conn.prepareStatement(query)) {
                                stm3.setBigDecimal(1, BigDecimal.valueOf(put));
                                stm3.setInt(2, adr_dest);
                                stm3.setInt(3, IdK);
                                stm3.setInt(4, IdVoz);

                                stm3.executeUpdate();

                            }

                        }

                    }

                    query = "delete from PrevoziSe where IdI=? and StatusPak=0";

                    try ( PreparedStatement stm5 = conn.prepareStatement(query)) {
                        stm5.setInt(1, IdI);

                        stm5.executeUpdate();

                    }

                    query = "update Isporuka set StatusPaketa=2  where IdI=?";

                    try ( PreparedStatement stmm = conn.prepareStatement(query)) {

                        stmm.setInt(1, IdI);
                        stmm.executeUpdate();
                    }

                    query = "insert into TrenutnoUVozilu (IdVoz,IdI) values(?,?)";

                    try ( PreparedStatement stmm = conn.prepareStatement(query)) {

                        stmm.setInt(1, IdVoz);
                        stmm.setInt(2, IdI);

                        stmm.executeUpdate();
                    }

                    return -2;

                } else if (status == 4) { //odes u magacin i pokupis ih sve

                    int TrenutnaAdresaPaketa = -1;
                    String proba = "Select Lokacija from Isporuka where IdI=?";

                    try ( PreparedStatement stm1 = conn.prepareStatement(proba)) {
                        stm1.setInt(1, IdI);
                        ResultSet rs1 = stm1.executeQuery();

                        if (rs1.next()) {
                            TrenutnaAdresaPaketa = rs1.getInt(1);
                        }

                    }
                    int IdMagacinaNovo = -1;

                    proba = "Select IdM from Magacin where IdA=?";

                    try ( PreparedStatement stm1 = conn.prepareStatement(proba)) {
                        stm1.setInt(1, TrenutnaAdresaPaketa);
                        ResultSet rs1 = stm1.executeQuery();

                        if (rs1.next()) {
                            IdMagacinaNovo = rs1.getInt(1);
                        }

                    }
                    List<Integer> paketi = new ArrayList<>();
                    proba = "Select IdI from Isporuka where Lokacija=? and IdI not in(select IdI from TrenutnoUVozilu)";

                    try ( PreparedStatement stm1 = conn.prepareStatement(proba)) {
                        stm1.setInt(1, TrenutnaAdresaPaketa);
                        ResultSet rs1 = stm1.executeQuery();

                        while (rs1.next()) {
                            paketi.add(rs1.getInt(1));
                        }

                    }
                    proba = "Insert into TrenutnoUVozilu (IdVoz,IdI) values(?,?)";
                    for (int k = 0; k < paketi.size(); ++k) {
                        try ( PreparedStatement stm1 = conn.prepareStatement(proba)) {
                            stm1.setInt(1, IdVoz);
                            stm1.setInt(2, paketi.get(k));
                            stm1.executeUpdate();

                        }

                        String proba2 = "Update PrevoziSe set StatusPak=2 where IdI=? and IdVoz=? and StatusPak=4";
                        try ( PreparedStatement stm1 = conn.prepareStatement(proba2)) {
                            stm1.setInt(1, paketi.get(k));
                            stm1.setInt(2, IdVoz);
                            stm1.executeUpdate();

                        }

                    }

                    proba = "select X,Y from Adresa where IdA=?";
                    try ( PreparedStatement stm7 = conn.prepareStatement(proba)) {

                        stm7.setInt(1, TrenutnaAdresaPaketa);
                        ResultSet rss = stm7.executeQuery();

                        if (rss.next()) {
                            int Xmag = rss.getInt(1);
                            int Ymag = rss.getInt(2);

                            double put = Math.sqrt(Math.pow(Xmag - X, 2) + Math.pow(Ymag - Y, 2));

                            proba = "update Vozi set PredjeniPut= PredjeniPut + ?, TrenutnaLokacija=? where IdK=? and IdVoz=?";
                            try ( PreparedStatement stm3 = conn.prepareStatement(proba)) {
                                stm3.setBigDecimal(1, BigDecimal.valueOf(put));
                                stm3.setInt(2, TrenutnaAdresaPaketa);
                                stm3.setInt(3, IdK);
                                stm3.setInt(4, IdVoz);

                                stm3.executeUpdate();
                            }
                        }

                    }

                    return -2;

                } else { //dostavlja paket

                    //Dostava Paketa
                    int OdredisnaAdr = -1;
                    int Xodr = -1;
                    int Yodr = -1;

                    String proba = "Select IdADo,X,Y from Isporuka I join Adresa A on (I.IdADo=A.IdA) where IdI=?";

                    try ( PreparedStatement stm1 = conn.prepareStatement(proba)) {
                        stm1.setInt(1, IdI);
                        ResultSet rs1 = stm1.executeQuery();

                        if (rs1.next()) {
                            OdredisnaAdr = rs1.getInt(1);
                        }
                        OdredisnaAdr = rs1.getInt(1);
                        Xodr = rs1.getInt(2);
                        Yodr = rs1.getInt(3);

                    }
                    double put = Math.sqrt(Math.pow(Xodr - X, 2) + Math.pow(Yodr - Y, 2));

                    String query = "update Vozi set PredjeniPut= PredjeniPut + ?, TrenutnaLokacija=? where IdK=? and IdVoz=?";
                    try ( PreparedStatement stm3 = conn.prepareStatement(query)) {
                        stm3.setBigDecimal(1, BigDecimal.valueOf(put));
                        stm3.setInt(2, OdredisnaAdr);
                        stm3.setInt(3, IdK);
                        stm3.setInt(4, IdVoz);

                        stm3.executeUpdate();

                    }

                    query = "delete from PrevoziSe where IdI=? and StatusPak=1";

                    try ( PreparedStatement stm5 = conn.prepareStatement(query)) {
                        stm5.setInt(1, IdI);

                        stm5.executeUpdate();

                    }

                    query = "update Isporuka set StatusPaketa=3, Lokacija=IdADo  where IdI=?";

                    try ( PreparedStatement stmm = conn.prepareStatement(query)) {

                        stmm.setInt(1, IdI);
                        stmm.executeUpdate();
                    }

                    query = "delete from TrenutnoUVozilu where IdI=?";

                    try ( PreparedStatement stmm = conn.prepareStatement(query)) {
                        stmm.setInt(1, IdI);

                        stmm.executeUpdate();
                    }

                    query = "update Kurir set BrIsporPaketa= BrIsporPaketa + 1 where IdK=?";

                    try ( PreparedStatement stmm = conn.prepareStatement(query)) {
                        stmm.setInt(1, IdK);

                        stmm.executeUpdate();
                    }
                    
                    query = "select Cena from Isporuka where IdI=?";
                    BigDecimal Cena=BigDecimal.ZERO;
                    try ( PreparedStatement stm3 = conn.prepareStatement(query)) {
                        stm3.setInt(1, IdI);
                        
                        ResultSet rs3=stm3.executeQuery();
                        if(rs3.next())
                            Cena=rs3.getBigDecimal(1);
                        
                    }
                    

                    query = "update Kurir set Profit=Profit+? where IdK=?";
                    try ( PreparedStatement stm3 = conn.prepareStatement(query)) {
                        stm3.setBigDecimal(1, Cena);
                        stm3.setInt(2, IdK);

                        stm3.executeUpdate();

                    }

                    return IdI;

                }

            } else {//nema vise paketa, povratak u magacin

                String proba = "select X,Y from Adresa where IdA=?";
                double predjeni_put = 0;
                try ( PreparedStatement stm7 = conn.prepareStatement(proba)) {

                    stm7.setInt(1, adresa_magacina);
                    ResultSet rss = stm7.executeQuery();

                    if (rss.next()) {
                        int Xmag = rss.getInt(1);
                        int Ymag = rss.getInt(2);

                        double put = Math.sqrt(Math.pow(Xmag - X, 2) + Math.pow(Ymag - Y, 2));

                        proba = "select PredjeniPut from Vozi where IdK=? and IdVoz=?";
                        try ( PreparedStatement stm3 = conn.prepareStatement(proba)) {
                            stm3.setInt(1, IdK);
                            stm3.setInt(2, IdVoz);

                            ResultSet rs33 = stm3.executeQuery();
                            if (rs33.next()) {
                                predjeni_put = rs33.getBigDecimal(1).doubleValue() + put;
                            }

                        }

                    }

                }

                proba = "select Potrosnja, TipGoriva from Vozilo where IdVoz=?";

                double potrosnja = -1;
                int tipGoriva = 0;
                double cena_goriva = 0;

                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {

                    stmend.setInt(1, IdVoz);

                    ResultSet rsss = stmend.executeQuery();
                    if (rsss.next()) {
                        potrosnja = rsss.getBigDecimal(1).doubleValue();
                        tipGoriva = rsss.getInt(2);
                    }

                }
                switch (tipGoriva) {
                    case 0:
                        //plin
                        cena_goriva = 15;
                        break;
                    case 1:
                        //dizel
                        cena_goriva = 32;
                        break;
                    //benzin
                    default:
                        cena_goriva = 36;
                        break;
                }

                double cena_na_gorivo = predjeni_put * potrosnja * cena_goriva;

                proba = "update Kurir set Profit=Profit-?,Status=0 where IdK=?";
                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {

                    stmend.setBigDecimal(1, BigDecimal.valueOf(cena_na_gorivo));
                    stmend.setInt(2, IdK);

                    stmend.executeUpdate();

                }
                //STAVLJANJE PAKETA U MAGACIN
                List<Integer> paketi_za_magacin = new ArrayList<>();
                proba = "select IdI from PrevoziSe where (StatusPak=2 or StatusPak=3) and IdVoz=? and IdK=?";
                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {

                    stmend.setInt(1, IdVoz);
                    stmend.setInt(2, IdK);

                    ResultSet rs_mag = stmend.executeQuery();

                    while (rs_mag.next()) {
                        paketi_za_magacin.add(rs_mag.getInt(1));
                    }

                }

                proba = "delete from PrevoziSe where IdVoz=? and IdK=? and (StatusPak=2 or StatusPak=3)";
                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {
                    stmend.setInt(1, IdVoz);
                    stmend.setInt(2, IdK);
                    stmend.executeUpdate();

                }

                for (int i = 0; i < paketi_za_magacin.size(); ++i) {
                    proba = "update Isporuka set Lokacija=? where IdI=?";
                    try ( PreparedStatement stmend = conn.prepareStatement(proba)) {
                        stmend.setInt(1, adresa_magacina);
                        stmend.setInt(2, paketi_za_magacin.get(i));
                        stmend.executeUpdate();

                    }

                    proba = "delete from TrenutnoUVozilu where IdVoz=? and IdI=?";
                    try ( PreparedStatement stmend = conn.prepareStatement(proba)) {
                        stmend.setInt(1, IdVoz);
                        stmend.setInt(2, paketi_za_magacin.get(i));
                        stmend.executeUpdate();

                    }
                }

                //brisanje Vozila i Kurira iz Vozi
                proba = "delete from Vozi where IdVoz=? and IdK=?";
                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {

                    stmend.setInt(1, IdVoz);
                    stmend.setInt(2, IdK);
                    stmend.executeUpdate();
                }
                //parkiranje Vozila u Magacin

                int IdM = -1;
                proba = "select IdM from Magacin where IdA=?";
                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {

                    stmend.setInt(1, adresa_magacina);
                    ResultSet rssss = stmend.executeQuery();
                    if (rssss.next()) {
                        IdM = rssss.getInt(1);

                    }

                }

                proba = "Insert into Parkirana (IdM,IdVoz) values(?,?)";
                try ( PreparedStatement stmend = conn.prepareStatement(proba)) {

                    stmend.setInt(1, IdM);
                    stmend.setInt(2, IdVoz);
                    stmend.executeUpdate();
                }

                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;

    }

    @Override
    public List<Integer> getPackagesInVehicle(String username) {
        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        String provera1 = "select K.IdK from Kurir KU join Korisnik K on (KU.IdK=K.IdK) where Korime=?";
        int idK = -1;

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji Kurir sa datim Korisnickim imenom!");
                return list;
            }
            idK = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        int idVoz = -1;

        provera1 = "select IdVoz from Vozi where IdK=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, idK);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {

                return list;
            }
            idVoz = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select I.IdI from TrenutnoUVozilu T join Isporuka I on(T.IdI=I.IdI) where IdVoz=? and I.StatusPaketa!=3";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, idVoz);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {

                list.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

}
