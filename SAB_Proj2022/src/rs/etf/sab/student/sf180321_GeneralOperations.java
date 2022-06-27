/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

public class sf180321_GeneralOperations implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
        String query = "delete from Ponuda where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Zahtev where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from PrevoziSe where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Vozio where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
          query = "delete from TrenutnoUVozilu where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        query = "delete from Vozi where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Parkirana where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Vozilo where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Magacin where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Kurir where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Administrator where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Isporuka where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Korisnik where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Adresa where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Grad where 1=1";

        try ( PreparedStatement stm = conn.prepareStatement(query)) {

            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Baza je uspesno obrisana!");
    }

}
