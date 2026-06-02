package persistentie;

import domein.Speler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpelerMapper {

    private static final String INSERT_SPELER = "INSERT INTO G53.speler (gebruikersnaam, geboortejaar, aantalGewonnen, aantalGespeeld)"
            + "VALUES (?, ?, ?, ?)";
    
    private static final String GEEF_SPELER = "SELECT * FROM G53.speler WHERE gebruikersnaam = ?";
    
    private static final String GEEF_ALLE_SPELERS = "SELECT * FROM G53.speler";
            
    public void voegToe(Speler speler) 
    {
    	try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(INSERT_SPELER)) 
        {
            query.setString(1, speler.getGebruikersnaam());
            query.setInt(2, speler.getGeboortejaar());
            query.setInt(3, speler.getAantalGewonnen());
            query.setInt(4, speler.getAantalGespeeld());
            
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    
    public Speler geefSpeler(String gebruikersnaam) {
        Speler speler = null;

        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(GEEF_SPELER)) {
            query.setString(1, gebruikersnaam);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) 
                {
                    int geboortejaar = rs.getInt("geboortejaar");
                    int aantalGewonnen = rs.getInt("aantalGewonnen");
                    int aantalGespeeld = rs.getInt("aantalGespeeld");

                    speler = new Speler(gebruikersnaam, geboortejaar, aantalGewonnen, aantalGespeeld);               
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return speler;
    }
    
    public List<Speler> geefAlleSpelers() {
        List<Speler> spelers = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(GEEF_ALLE_SPELERS);
                ResultSet rs = query.executeQuery()) {
            
            while (rs.next()) {
                String gebruikersnaam = rs.getString("gebruikersnaam");
                int geboortejaar = rs.getInt("geboortejaar");
                int aantalGewonnen = rs.getInt("aantalGewonnen");
                int aantalGespeeld = rs.getInt("aantalGespeeld");
                
                spelers.add(new Speler(gebruikersnaam, geboortejaar, aantalGewonnen, aantalGespeeld));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        
        return spelers;
    }


    public void update(Speler speler) {
        String UPDATE_SPELER = "UPDATE G53.speler SET aantalGewonnen = ?, aantalGespeeld = ? WHERE gebruikersnaam = ?";
        
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
             PreparedStatement query = conn.prepareStatement(UPDATE_SPELER)) {
            
            // Zet de nieuwe waarden voor aantalGewonnen en aantalGespeeld
            query.setInt(1, speler.getAantalGewonnen());
            query.setInt(2, speler.getAantalGespeeld());
            query.setString(3, speler.getGebruikersnaam());
            
            query.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Fout bij het bijwerken van speler", ex);
        }
    }
}
