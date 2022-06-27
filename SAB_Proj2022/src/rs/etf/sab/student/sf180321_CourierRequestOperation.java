/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author Srdjan
 */
public class sf180321_CourierRequestOperation implements CourierRequestOperation {

    @Override
    public boolean insertCourierRequest(String username, String driver_licence) {

        Connection conn = DB.getInstance().getConnection();

        String provera = "select K.IdK from Korisnik K join Kurir KU on(K.IdK=KU.IdK) where Korime=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Navedeni korisnik je vec Kurir!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera1 = "select IdK from Korisnik where Korime=?";

        int IdKor = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji korisnik za zadatim korisnickim imenom!");
                return false;
            }
            IdKor = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select VozackaDozvola from Kurir where VozackaDozvola=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, driver_licence);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji kurir s ovakvom vozackom dozvolom!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select VozackaDozvola from Zahtev where VozackaDozvola=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, driver_licence);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji zahtev s ovakvom vozackom dozvolom!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Zahtev (IdZ,VozackaDozvola) values(?,?)";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdKor);
            stm.setString(2, driver_licence);

            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String username) {

        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdZ from Zahtev Z join Korisnik K on (Z.IdZ=K.IdK) where Korime=?";

        int IdZ = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji zahtev sa zadatim korisnickim imenom!");
                return false;
            }
            IdZ = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "delete from Zahtev where IdZ=?";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdZ);

            stm.executeUpdate();
            System.out.println("Zahtev je uspesno obrisan!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String username, String driver_licence) {

        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdZ from Zahtev Z join Korisnik K on (Z.IdZ=K.IdK) where Korime=?";

        int IdZ = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji zahtev za zadatim korisnickim imenom!");
                return false;
            }
            IdZ = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        provera1 = "select VozackaDozvola from Kurir where VozackaDozvola=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, driver_licence);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji kurir s ovakvom vozackom dozvolom!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

//        provera1 = "select VozackaDozvola from Zahtev where VozackaDozvola=?";
//        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {
//
//            stm.setString(1, driver_licence);
//
//            ResultSet rs = stm.executeQuery();
//
//            if (rs.next()) {
//                System.out.println("Vec postoji zahtev s ovakvom vozackom dozvolom!");
//                return false;
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
//        }

        String query = "update Zahtev set VozackaDozvola=? where IdZ=?";
        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setString(1, driver_licence);

            stm.setInt(2, IdZ);

            stm.executeUpdate();

            System.out.println("Uspesno ste promenili vozacku dozvolu u zahtevu!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public List<String> getAllCourierRequests() {

        Connection conn = DB.getInstance().getConnection();
        List<String> list = new ArrayList<>();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select KorIme from Zahtev Z join Korisnik K on (Z.IdZ=K.IdK)")) {

            while (rs.next()) {

                list.add(rs.getString(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public boolean grantRequest(String username) {

        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdZ, VozackaDozvola from Zahtev Z join Korisnik K on (Z.IdZ=K.IdK) where Korime=?";

        int IdZ = -1;
        String vozacka = null;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji zahtev za zadatim korisnickim imenom!");
                return false;
            }
            IdZ = rs.getInt(1);
            vozacka = rs.getString(2);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "Insert into Kurir (IdK,BrIsporPaketa,Status,Profit,VozackaDozvola) values(?,0,0,0,?)";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdZ);
            stm.setString(2, vozacka);

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Zahtev where IdZ=?";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdZ);
            stm.executeUpdate();

            System.out.println("Uspesno ste proglasili Kurira");

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

}
