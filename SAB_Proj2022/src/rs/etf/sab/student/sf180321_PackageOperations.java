/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_PackageOperations implements PackageOperations {

    @Override
    public int insertPackage(int adrOd, int adrDo, String korime, int tipPaketa, BigDecimal tezina) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select X,Y from Adresa where IdA=?";
        int xOd = -1, yOd = -1, xDo = -1, yDo = -1;
        int IdK = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, adrOd);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji zadata adresa Od!");
                return -1;
            }
            xOd = rs.getInt(1);
            yOd = rs.getInt(2);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, adrDo);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji zadata adresa Do!");
                return -1;
            }
            xDo = rs.getInt(1);
            yDo = rs.getInt(2);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (xOd == xDo && yOd == yDo) {
            System.out.println("Adresa Od i Adresa Do su iste!");
            return -1;
        }
        provera = "select IdK from Korisnik where Korime=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setString(1, korime);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji korisnik za zadatim korisnickim imenom!");
                return -1;
            }
            IdK = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (tipPaketa < 0 || tipPaketa > 3) {
            System.out.println("Ne postoji zadati tip Paketa!");
            return -1;
        }

        String query = "insert into Isporuka (IdAOd,IdADo,IdK,TipPaketa,Tezina,StatusPaketa,Cena,VremeKreiranja,Lokacija) values(?,?,?,?,?,0,0,?,?)";
        try ( PreparedStatement stm = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stm.setInt(1, adrOd);
            stm.setInt(2, adrDo);
            stm.setInt(3, IdK);
            stm.setInt(4, tipPaketa);
            stm.setBigDecimal(5, tezina);

            //stm.setDate(6, Date.valueOf(LocalDate.now()));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            stm.setString(6, dtf.format(now));
            stm.setInt(7, adrOd);

            stm.executeUpdate();

            ResultSet rs = stm.getGeneratedKeys();

            if (rs.next()) {
                System.out.println("Paket je uspesno kreiran!");

                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int IdI) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select Status,Cena from Ponuda where IdI=?";

        int status = -1;
        BigDecimal cena = BigDecimal.ZERO;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return false;
            }
            status = rs.getInt(1);
            cena = rs.getBigDecimal(2);
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status != 0) { //status=0 paket kreiran
            System.out.println("Paket se ne moze prihvatiti jer nije u odgovarajucem statusu!");
            return false;
        }

        String query = "Update Ponuda set Status=1 where IdI=? "; //prihvacena ponuda

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdI);

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "Update Isporuka set StatusPaketa=1, Cena=?, VremePrihvatanja=? where IdI=? "; //prihvacena ponuda

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setBigDecimal(1, cena);

            // stm.setDate(2, LocalDateTime.);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            stm.setString(2, dtf.format(now));

            stm.setInt(3, IdI);

            stm.executeUpdate();

            System.out.println("Ponuda je uspesno prihvacena!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    @Override
    public boolean rejectAnOffer(int IdI) {
        Connection conn = DB.getInstance().getConnection();
        String provera = "select Status from Ponuda where IdI=?";

        int status = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return false;
            }
            status = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status != 0) { //status=0 paket kreiran
            System.out.println("Paket se ne moze odbiti jer nije u odgovarajucem statusu!");
            return false;
        }

        String query = "Update Ponuda set Status=4 where IdI=? "; //odbijena ponuda

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdI);

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "Update Isporuka set StatusPaketa=4 where IdI=? "; //odbijena ponuda

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdI);

            stm.executeUpdate();

            System.out.println("Ponuda je uspesno odbijena!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public List<Integer> getAllPackages() {

        Connection conn = DB.getInstance().getConnection();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select IdI from isporuka")) {

            List<Integer> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getInt(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int tip) {

        Connection conn = DB.getInstance().getConnection();
        String sql = "Select IdI from Isporuka where TipPaketa=?";

        try ( PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, tip);
            ResultSet rs = stm.executeQuery();

            List<Integer> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getInt(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {

        Connection conn = DB.getInstance().getConnection();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select IdI from isporuka where StatusPaketa=1 or StatusPaketa=2")) {

            List<Integer> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getInt(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int IdG) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "Select IdI from Isporuka I join Adresa A on (I.IdAOd=A.IdA) where IdG=? and (StatusPaketa=1 or StatusPaketa=2)";

        try ( PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, IdG);
            ResultSet rs = stm.executeQuery();

            List<Integer> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getInt(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int IdG) {

        Connection conn = DB.getInstance().getConnection();
        String sql = "Select IdI from Isporuka I join Adresa A on (I.Lokacija=A.IdA) where (Lokacija=IdAOd or Lokacija=IdADo) and IdG=? ";
        List<Integer> list = new ArrayList<>();
        try ( PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, IdG);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {

                list.add(rs.getInt(1));

            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        sql = "Select IdI from Isporuka I join Adresa A on (I.Lokacija=A.IdA) join Magacin M on (I.Lokacija=M.IdA) where IdG=?";
        try ( PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, IdG);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                if (!list.contains(rs.getInt(1))) {
                    list.add(rs.getInt(1));
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        int index=-1;
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select IdI from TrenutnoUVozilu")) {

            while (rs.next()) {

                if (list.contains(rs.getInt(1))) {
                    
                   index=list.indexOf(rs.getInt(1));
                   list.remove(index);
                    System.out.println("Uspeh");
                }

            }
            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;

    }

    @Override
    public boolean deletePackage(int IdI) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select StatusPaketa from Isporuka where IdI=?";

        int status = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return false;
            }
            status = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status != 0 && status != 4) {
            System.out.println("Paket se moze obrisati samo ako je kreiran ili odbijen!");
            return false;
        }

        provera = "delete from Ponuda where IdI=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "delete from Isporuka where IdI=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            stm.executeUpdate();
            System.out.println("Paket je uspesno obrisan!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeWeight(int IdI, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        String provera = "select StatusPaketa from Isporuka where IdI=?";

        int status = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return false;
            }
            status = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status != 0) {
            System.out.println("Paket moze menjati tezinu samo kad je kreiran!");
            return false;
        }

        provera = "update Isporuka set Tezina=? where IdI=? ";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setBigDecimal(1, bd);
            stm.setInt(2, IdI);

            stm.executeUpdate();
            System.out.println("Uspesno ste promenili tezinu paketa!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeType(int IdI, int tip) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select StatusPaketa from Isporuka where IdI=?";

        int status = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return false;
            }
            status = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status != 0) {
            System.out.println("Paket moze menjati tip samo kad je kreiran!");
            return false;
        }

        if (tip < 0 || tip > 3) {
            System.out.println("Tip paketa nije odgovarajuci!");
            return false;
        }

        provera = "update Isporuka set TipPaketa=? where IdI=? ";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, tip);
            stm.setInt(2, IdI);

            stm.executeUpdate();
            System.out.println("Uspesno ste promenili tip paketa!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int getDeliveryStatus(int IdI) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select StatusPaketa from Isporuka where IdI=?";

        int status = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return -1;
            }
            status = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;

    }

    @Override
    public BigDecimal getPriceOfDelivery(int IdI) {
        Connection conn = DB.getInstance().getConnection();
        String provera = "select Cena from Isporuka where IdI=?";

        BigDecimal cena = BigDecimal.ZERO;

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return BigDecimal.ZERO;
            }

            cena = rs.getBigDecimal(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cena;
    }

    @Override
    public int getCurrentLocationOfPackage(int IdI) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select IdI from Isporuka where IdI=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select IdI from TrenutnoUVozilu where IdI=? ";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Paket se trenutno nalazi u vozilu!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select A.IdG from Isporuka I join Adresa A on (I.IdAOd=A.IdA) join Adresa A2 on (I.IdADo=A2.IdA) where (Lokacija = IdAOd or Lokacija = IdADo) and IdI=? ";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Paket se nalazi na pocetnoj ili krajnjoj adresi!");
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera = "select IdG from Isporuka I join Magacin M on (I.Lokacija=M.IdA) join Adresa A on (M.IdA=A.IdA) where IdI=? ";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Paket se nalazi u magacinu!");
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Paket se nalazi u vozilu");
        return -1;

    }

    @Override
    public Date getAcceptanceTime(int IdI) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select StatusPaketa,VremePrihvatanja from Isporuka where IdI=?";

        int status = -1;
        Date datum=null;
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, IdI);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji paket sa zadatim ID-em!");
                return null;
            }
            status = rs.getInt(1);
            datum=rs.getDate(2);
            
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (status == 0 || status == 4) {
            System.out.println("Ponuda za ovaj paket nije prihvacena!");
            return null;
        }
        
        return datum;

    }

}
