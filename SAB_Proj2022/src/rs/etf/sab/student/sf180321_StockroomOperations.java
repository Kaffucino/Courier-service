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
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_StockroomOperations implements StockroomOperations {

    @Override
    public int insertStockroom(int idA) {

        Connection conn = DB.getInstance().getConnection();

        String query = "insert into Magacin (IdA) values(?)";

        String provera1 = "select IdG from Adresa where IdA=?";
        int idG = -1;

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, idA);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                idG = rs.getInt(1);
            } else {
                System.out.println("Ne postoji adresa sa zadatim Id-em");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera2 = "select IdG\n"
                + "\n"
                + "from Magacin M join Adresa A on(M.IdA=A.IdA)\n"
                + "\n"
                + "where A.IdG=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera2)) {

            stm.setInt(1, idG);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji magacin u tom gradu!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        try ( PreparedStatement stm = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {

            stm.setInt(1, idA);
            stm.executeUpdate();

            ResultSet rs = stm.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    @Override
    public boolean deleteStockroom(int IdM) {

        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdM from Magacin where IdM=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdM);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji magacin za zadatim ID-em");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select IdI from Isporuka I join Magacin M on (I.Lokacija=M.IdA) where IdM=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdM);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Magacin nije prazan!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "delete from Magacin where IdM=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdM);

            stm.executeUpdate();
            System.out.println("Uspesno obrisan magacin!");
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public int deleteStockroomFromCity(int IdG) {
        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdG from Grad where IdG=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdG);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji grad za zadatim ID-em");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select IdM from Magacin M join Adresa A on (M.IdA=A.IdA)  where IdG=?";
        int IdM = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdG);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji magacin u zadatom gradu");
                return -1;
            }

            IdM = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "select IdI from Isporuka I join Magacin M on (I.Lokacija=M.IdA) where IdM=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdM);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Magacin nije prazan!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        provera1 = "delete from Magacin where IdM=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setInt(1, IdM);

            stm.executeUpdate();
            System.out.println("Uspesno obrisan magacin!");
            return IdM;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }

    @Override
    public List<Integer> getAllStockrooms() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> list = new ArrayList<>();

        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select IdM from Magacin")) {

            while (rs.next()) {

                list.add(rs.getInt(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;

    }

}
