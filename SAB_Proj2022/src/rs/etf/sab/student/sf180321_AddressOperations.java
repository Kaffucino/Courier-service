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
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_AddressOperations implements AddressOperations {

    @Override
    public int insertAddress(String ulica, int broj, int idG, int x, int y) {

        Connection conn = DB.getInstance().getConnection();

        String provera = "select IdG from Grad where IdG=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, idG);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji grad sa zadatim Id-em!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Adresa (Ulica,Broj,X,Y,IdG) values(?,?,?,?,?)";

        try ( PreparedStatement stm = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {

            stm.setString(1, ulica);
            stm.setInt(2, broj);
            stm.setInt(3, x);
            stm.setInt(4, y);
            stm.setInt(5, idG);

            stm.executeUpdate();

            ResultSet rs = stm.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }

        } catch (SQLException ex) {
            // Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;

    }

    @Override
    public int deleteAddresses(String Ulica, int Broj) {

        Connection conn = DB.getInstance().getConnection();

        String query = "delete from Adresa where Ulica=? and Broj=?";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setString(1, Ulica);

            stm.setInt(2, Broj);

            int i = 0;
            i = stm.executeUpdate();

            return i;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;

    }

    @Override
    public boolean deleteAdress(int idA) {

        Connection conn = DB.getInstance().getConnection();

        String query = "delete from Adresa where IdA=?";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, idA);

            int i = stm.executeUpdate();

            return i == 1;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    @Override
    public int deleteAllAddressesFromCity(int idG) {

        Connection conn = DB.getInstance().getConnection();

        String provera = "select IdG from Grad where IdG=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, idG);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji grad sa zadatim Id-em!");
                return 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "delete from Adresa where IdG=?";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, idG);

            int i = 0;
            i = stm.executeUpdate();

            return i;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;

    }

    @Override
    public List<Integer> getAllAddresses() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> list = new ArrayList<>();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select IdA from Adresa")) {

            while (rs.next()) {

                list.add(rs.getInt(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idG) {
        Connection conn = DB.getInstance().getConnection();

        String provera = "select IdG from Grad where IdG=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, idG);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji grad sa zadatim Id-em!");
                return null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sql = "select IdA from Adresa where IdG=?";
        List<Integer> list = new ArrayList<>();

        try ( PreparedStatement stm = conn.prepareStatement(sql);) {

            stm.setInt(1, idG);

            ResultSet rs = stm.executeQuery();

            

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
