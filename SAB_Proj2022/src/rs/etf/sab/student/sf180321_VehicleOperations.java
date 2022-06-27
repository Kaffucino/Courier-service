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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_VehicleOperations implements VehicleOperations {

    @Override
    public boolean insertVehicle(String RegBr, int tip, BigDecimal gorivo, BigDecimal kapacitet) {

        Connection conn = DB.getInstance().getConnection();

        if (tip != 0 && tip != 1 && tip != 2) {
            System.out.println("Tip gotiva nije odgovarajuci!");
            return false;
        }

        String provera = "select IdVoz from Vozilo where RegBR=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera);) {

            stm.setString(1, RegBr);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji vozilo sa navedenom registarskom tablicom!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Vozilo (TipGoriva,RegBR,Potrosnja,Nosivost) values(?,?,?,?)";
        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, tip);
            stm.setString(2, RegBr);
            stm.setBigDecimal(3, gorivo);
            stm.setBigDecimal(4, kapacitet);

            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public int deleteVehicles(String... strings) {
        Connection conn = DB.getInstance().getConnection();

        String query = "delete from Vozilo where RegBR =?";

        int cnt = 0;

        for (int i = 0; i < strings.length; ++i) {
            try ( PreparedStatement stm = conn.prepareStatement(query);) {

                stm.setString(1, strings[i]);

                cnt += stm.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return cnt;

    }

    @Override
    public List<String> getAllVehichles() {

        Connection conn = DB.getInstance().getConnection();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select RegBR from Vozilo")) {

            List<String> list = new ArrayList<>();
            while (rs.next()) {

                list.add(rs.getString(1));

            }

            return list;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    @Override
    public boolean changeFuelType(String RegBR, int tip) {

        Connection conn = DB.getInstance().getConnection();

        int IdVoz = -1;

        if (tip != 0 && tip != 1 && tip != 2) {
            System.out.println("Tip gotiva nije odgovarajuci!");
            return false;
        }

        String provera = "select IdVoz from Vozilo where RegBR=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera);) {

            stm.setString(1, RegBR);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem ne postoji!");
                return false;
            }
            IdVoz = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera2 = "select IdVoz from Parkirana where IdVoz=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera2);) {

            stm.setInt(1, IdVoz);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem nije parkirano!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "update Vozilo set TipGoriva=? where IdVoz=?";
        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, tip);
            stm.setInt(2, IdVoz);
            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean changeConsumption(String RegBR, BigDecimal cons) {

        Connection conn = DB.getInstance().getConnection();

        int IdVoz = -1;

        String provera = "select IdVoz from Vozilo where RegBR=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera);) {

            stm.setString(1, RegBR);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem ne postoji!");
                return false;
            }
            IdVoz = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera2 = "select IdVoz from Parkirana where IdVoz=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera2);) {

            stm.setInt(1, IdVoz);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem nije parkirano!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "update Vozilo set Potrosnja=? where IdVoz=?";
        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setBigDecimal(1, cons);
            stm.setInt(2, IdVoz);
            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean changeCapacity(String RegBR, BigDecimal kap) {
        Connection conn = DB.getInstance().getConnection();

        int IdVoz = -1;

        String provera = "select IdVoz from Vozilo where RegBR=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera);) {

            stm.setString(1, RegBR);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem ne postoji!");
                return false;
            }
            IdVoz = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera2 = "select IdVoz from Parkirana where IdVoz=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera2);) {

            stm.setInt(1, IdVoz);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem nije parkirano!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "update Vozilo set Nosivost=? where IdVoz=?";
        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setBigDecimal(1, kap);
            stm.setInt(2, IdVoz);
            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean parkVehicle(String RegBR, int IdM) {

        Connection conn = DB.getInstance().getConnection();

        int IdVoz = -1;

        String provera = "select IdVoz from Vozilo where RegBR=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera);) {

            stm.setString(1, RegBR);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Vozilo sa datim registarskim brojem ne postoji!");
                return false;
            }
            IdVoz = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera1 = "select IdM from Magacin where IdM=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera1);) {

            stm.setInt(1, IdM);
            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Magacin sa datim ID-em ne postoji!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera2 = "select IdVoz from Vozi where IdVoz=?";

        try ( PreparedStatement stm = conn.prepareStatement(provera2);) {

            stm.setInt(1, IdVoz);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Voznja je trenutno u toku!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Parkirana (IdM,IdVoz) values(?,?)";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, IdM);
            stm.setInt(2, IdVoz);

            stm.executeUpdate();
            
            System.out.println("Vozilo uspesno parkirano u magacin!");
            return true;
           

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

}
