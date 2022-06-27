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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_CityOperations implements CityOperations {

    @Override
    public int insertCity(String Naziv, String PostBr) {
        Connection conn = DB.getInstance().getConnection();

        String provera = "select IdG from Grad where PostBr=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setString(1, PostBr);
            
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji grad sa navedeim postanskim brojem!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Grad (Naziv,PostBr) values(?,?)";
        try ( PreparedStatement stm = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {

            stm.setString(1, Naziv);
            stm.setString(2, PostBr);
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
    public int deleteCity(String... strings) {

        Connection conn = DB.getInstance().getConnection();

        String query = "delete from Grad where Naziv =?";

        int cnt = 0;

        for (int i = 0; i < strings.length; ++i) {
            try ( PreparedStatement stm = conn.prepareStatement(query);) {

                stm.setString(1, strings[i]);

                cnt += stm.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return cnt;

    }

    @Override
    public boolean deleteCity(int IdG) {

        Connection conn = DB.getInstance().getConnection();

        String query = "delete from Grad where IdG=?";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, IdG);

            int i = stm.executeUpdate();

            return i == 1;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    @Override
    public List<Integer> getAllCities() {

        Connection conn = DB.getInstance().getConnection();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select IdG from Grad")) {

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

}
