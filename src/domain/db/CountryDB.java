package domain.db;

import domain.model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CountryDB {
    private Properties properties = new Properties();
    private String url = "jdbc:postgresql://databanken.ucll.be:51718/lector?currentSchema=web3";

    public CountryDB() {
        properties.setProperty("user", "r123456");
        properties.setProperty("password", "pass");
        Secret.setPass(properties);
        properties.setProperty("ssl", "true");
        properties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DbException(e.getMessage(), e);
        }

    }

    public void add(Country country) {
        if (country == null) {
            throw new DbException("Nothing to add.");
        }
        String sql = "INSERT INTO country (name, capital, inhabitants, votes) VALUES ('"
                + country.getName() + "', '" + country.getCapital() + "', "
                + country.getNumberInhabitants() + ", " + country.getVotes() + ")";
        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public List<Country> getAll() {
        List<Country> countries = new ArrayList<Country>();
        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()){
            ResultSet result = statement.executeQuery("SELECT * FROM country");
            while (result.next()) {
                String name = result.getString("name");
                String capital = result.getString("capital");
                int numberOfVotes = Integer.parseInt(result.getString("votes"));
                int numberOfInhabitants = Integer.parseInt(result.getString("inhabitants"));
                Country country = new Country(name, numberOfInhabitants, capital, numberOfVotes);
                countries.add(country);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage(), e);
        }
        return countries;
    }
}
