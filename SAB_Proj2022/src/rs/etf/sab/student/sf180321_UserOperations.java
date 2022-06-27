/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import com.microsoft.sqlserver.jdbc.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author Srdjan
 */
public class sf180321_UserOperations implements UserOperations {

    private boolean provera_lozinke(String lozinka) {

        if (lozinka.length() < 8) {
            System.out.println("Lozinka mora imati bar 8 karaktera!");
            return false;
        }

        boolean upper = false;
        boolean lower = false;
        boolean number = false;
        boolean sign = false;

        for (int i = 0; i < lozinka.length(); ++i) {

            if (Character.isUpperCase(lozinka.charAt(i))) {
                upper = true;
            } else if (Character.isLowerCase(lozinka.charAt(i))) {
                lower = true;
            } else if (StringUtils.isInteger("" + lozinka.charAt(i))) {
                number = true;
            } else {
                sign = true;
            }

        }

        if (upper == false) {
            System.out.println("Lozinka mora imati bar 1 veliko slovo");
        }

        if (lower == false) {
            System.out.println("Lozinka mora imati bar 1 malo slovo");
        }

        if (number == false) {
            System.out.println("Lozinka mora imati bar 1 cifru");
        }

        if (sign == false) {
            System.out.println("Lozinka mora imati bar 1 znak");
        }

        return (upper && lower && number && sign);
    }

    @Override
    public boolean insertUser(String korime, String ime, String prezime, String lozinka, int idA) {
        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdK from Korisnik where Korime=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, korime);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Vec postoji korisnik sa zadataim korisnickim imenom!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!Character.isUpperCase(ime.charAt(0))) {
            System.out.println("Ime mora poceti sa velikim pocetnim slovom!");
            return false;
        }

        if (!Character.isUpperCase(prezime.charAt(0))) {
            System.out.println("Prezime mora poceti sa velikim pocetnim slovom!");
            return false;
        }

        if (!provera_lozinke(lozinka)) {
            return false;
        }

        String provera = "select IdA from Adresa where IdA=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera)) {

            stm.setInt(1, idA);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji adresa sa zadatim Id-em!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Korisnik (Ime,Prezime,Korime,Sifra,IdA) values(?,?,?,?,?)";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setString(1, ime);
            stm.setString(2, prezime);
            stm.setString(3, korime);
            stm.setString(4, lozinka);
            stm.setInt(5, idA);

            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            // Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    @Override
    public boolean declareAdmin(String korime) {

        Connection conn = DB.getInstance().getConnection();
        String provera1 = "select IdK from Korisnik where Korime=?";
        int idKor = -1;
        try ( PreparedStatement stm = conn.prepareStatement(provera1)) {

            stm.setString(1, korime);

            ResultSet rs = stm.executeQuery();

            if (!rs.next()) {
                System.out.println("Ne postoji korisnik sa zadataim korisnickim imenom!");
                return false;
            }
            idKor = rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String provera2 = "select IdK from Administrator where IdK=?";
        try ( PreparedStatement stm = conn.prepareStatement(provera2)) {

            stm.setInt(1, idKor);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                System.out.println("Zadati korisnik je vec administrator!");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "Insert into Administrator (idK) values(?)";

        try ( PreparedStatement stm = conn.prepareStatement(query);) {

            stm.setInt(1, idKor);

            stm.executeUpdate();

            return true;

        } catch (SQLException ex) {
            // Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public int getSentPackages(String... strings) {

        Connection conn = DB.getInstance().getConnection();
        String provera = "select Korime from Korisnik";
        boolean barJedan = false;

        try ( PreparedStatement stm = conn.prepareStatement(provera);  ResultSet rs = stm.executeQuery();) {

            while (rs.next()) {

                for (int i = 0; i < strings.length; ++i) {
                    if (strings[i].equals(rs.getString(1))) {
                        barJedan = true;
                        break;
                    }
                }

                if (barJedan) {
                    break;
                }

            }
            if (!barJedan) {
                System.out.println("Nema ni 1 user u bazi!");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        int cnt = 0;
        
        String query="Select count(IdI) from Isporuka I join Korisnik K on (I.IdK=K.IdK) where Korime=?";
        for(int i=0;i<strings.length;++i){
        try(PreparedStatement stm=conn.prepareStatement(query);){
            
            stm.setString(1, strings[i]);
            ResultSet rs=stm.executeQuery();
            
            if(rs.next()){
                cnt+=rs.getInt(1);
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(sf180321_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        return cnt;

    }

    @Override
    public int deleteUsers(String... usernames) {

        Connection conn = DB.getInstance().getConnection();

        String query = "delete from Korisnik where KorIme =?";

        int cnt = 0;

        for (int i = 0; i < usernames.length; ++i) {
            try ( PreparedStatement stm = conn.prepareStatement(query);) {

                stm.setString(1, usernames[i]);

                cnt += stm.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(sf180321_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return cnt;

    }

    @Override
    public List<String> getAllUsers() {
        Connection conn = DB.getInstance().getConnection();
        try ( Statement stm = conn.createStatement();  ResultSet rs = stm.executeQuery("select KorIme from Korisnik")) {

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

}
