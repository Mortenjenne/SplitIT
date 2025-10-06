package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;

public class UserMapper {

    public User login(String name, String password) throws DatabaseException {
        String sql = "SELECT user_id, name, password" +
                "FROM users WHERE name = ? AND password = ?";
        User user = null;

        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1,name);
            ps.setString(2,password);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                int newId = rs.getInt(1);
                user = new User(newId,name,password);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Couldn't find user");
        }
        return user;
    }

    public User getUserById(int userId) throws DatabaseException {
        User user = null;
        String sql = "SELECT user_id, name, password FROM users WHERE user_id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("name");
                String password = rs.getString("password");

                user = new User(id, name, password);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af bruger med id " + userId + ": " + e.getMessage(), e);
        }
        return user;
    }

    public boolean deleteMember(int user_id) throws DatabaseException {
        boolean result = false;
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, user_id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1){
                    result = true;
                }
            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af alle medlemmer: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
        }
        return result;
    }

    public User createUser(String name, String password) throws DatabaseException {
        String sql = "INSERT INTO users (name, password) VALUES (?,?)";
        User user = null;
        try (Connection con = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, password);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                user = new User(userId,name,password);
                return user;
            }
            throw new DatabaseException("User could not be created");

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DatabaseException("Username or email already exists", e);
        } catch (SQLException e) {
            throw new DatabaseException("Database error creating user", e);
        }
    }

    public boolean updateUser(User user) throws DatabaseException {
        boolean result = false;
        String sql = "UPDATE users SET name = ?, password = ? WHERE user_id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getPassword());
                ps.setLong(3, user.getUserId());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1){
                    result = true;
                }
            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af alle medlemmer: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
        }
        return result;
    }
}

