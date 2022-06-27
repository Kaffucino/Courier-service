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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_CourierOperations implements CourierOperations {

    @Override
    public boolean insertCourier(String username, String driving_licence) {

        Connection conn = DB.getInstance().getConnection();
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

        provera1 = "select IdK from Kurir where IdK=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdKor);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Ovaj korisnik je vec kurir!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select IdZ from Zahtev Z join Korisnik K on (Z.IdZ=K.Idk) where Korime=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Ovaj korisnik se nalazi u Zahtevu vec!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select IdZ from Zahtev where VozackaDozvola=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, driving_licence);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Zadata vozacka dozvola je vec u Zahtevima!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select IdK from Kurir where VozackaDozvola=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, driving_licence);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Zadata vozacka dozvola vec postoji!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);

        }

        String query = "Insert into Kurir (IdK,BrIsporPaketa,Status,Profit,VozackaDozvola) values(?,0,0,0,?)";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, IdKor);
            stm.setString(2, driving_licence);

            stm.executeUpdate();

            System.out.println("Kurir je uspesno dodat u bazu!");

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean deleteCourier(String username) {

        Connection conn = DB.getInstance().getConnection();

        String provera1 = "select KK.IdK from Kurir KK join Korisnik K on (KK.IdK=K.IdK) where Korime=? ";
        int IdK = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, username);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji kurir!");
                return false;
            }
            IdK = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        provera1 = "Delete from Kurir where IdK=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdK);

            stm.executeUpdate();
            System.out.println("Obrisan kurir uspesno!");

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "Select Korime from Korisnik K1 join Kurir K2 on (K1.IdK=K2.IdK) where Status=?";
        try ( PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, i);
            ResultSet rs = stm.executeQuery();

            List<String> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getString(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    @Override
    public List<String> getAllCouriers() {

        Connection conn = DB.getInstance().getConnection();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select Korime from Korisnik K1 join Kurir K2 on(K1.IdK=K2.IdK) order by Profit DESC")) {

            List<String> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getString(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {

        Connection conn = DB.getInstance().getConnection();
        BigDecimal avg = BigDecimal.ZERO;

        if (i == -1) {

            String sql = "select avg(Profit) from Kurir";

            try ( PreparedStatement stm = conn.prepareStatement(sql)) {

                ResultSet rs = stm.executeQuery();

                if (rs.next()) {
                    avg = rs.getBigDecimal(1);
                }

                return avg;

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        String sql = "select avg(Profit) from Kurir where BrIsporPaketa=?";

        try ( PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, i);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                avg = rs.getBigDecimal(1);

                if (avg.compareTo(BigDecimal.ZERO) < 0) {
                    avg = BigDecimal.ONE;
                }

            }

            return avg;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return avg;
    }

}
