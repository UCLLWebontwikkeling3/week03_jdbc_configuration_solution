package domain.db;

import domain.model.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CountryDBSQL implements CountryDB {
    private Properties properties = new Properties();
    private String url = "jdbc:postgresql://databanken.ucll.be:51819/webontwerp?currentSchema=web3";

    public CountryDBSQL() {
        properties.setProperty("user", "r123456");
        properties.setProperty("password", "pass");
        Secret.setPass(properties);
        properties.setProperty("ssl", "true");
        properties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        properties.setProperty("sslmode","prefer");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DbException(e.getMessage(), e);
        }

    }

    /**
     * Stores the given country in the database
     * @param country The country to be added
     * @throws DbException if the given country is null
     * @throws DbException if the given country can not be added
     */
    @Override
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

    @Override
    /**
     * Returns a list with all countries stored in the database
     * @return An arraylist with all countries stored in the database
     * @throws DbException when there are problems with the connection to the database
     */
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
