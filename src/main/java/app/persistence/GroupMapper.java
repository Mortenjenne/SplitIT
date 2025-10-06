package app.persistence;

import app.entities.Group;
import app.exceptions.DatabaseException;
import java.sql.*;

public class GroupMapper {

    public Group getGroupById(int groupId) throws DatabaseException {
        Group group = null;
        String sql = "SELECT group_id, name FROM users WHERE group_id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("group_id");
                String name = rs.getString("name");

                 group = new Group(id,name);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af bruger med id " + groupId + ": " + e.getMessage(), e);
        }
        return group;
    }

    public boolean deleteGroup(int groupId) throws DatabaseException {
        boolean result = false;
        String sql = "DELETE FROM group WHERE group_id = ?";
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, groupId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1){
                    result = true;
                }
            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af alle grupper " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
        }
        return result;
    }

    public Group createGroup(String name) throws DatabaseException {
        String sql = "INSERT INTO group (name) VALUES (?)";
        Group group = null;
        try (Connection con = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int groupId = rs.getInt(1);
                group = new Group(groupId, name);
                return group;
            }
        }catch (SQLException e){
            throw new DatabaseException("Group could not be created");
        }
        return group;
    }

    public boolean updateGroup(Group group) throws DatabaseException {
        boolean result = false;
        String sql = "UPDATE group SET name = ? WHERE group_id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, group.getName());
                ps.setInt(2,group.getGroupId());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1){
                    result = true;
                }
            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af alle grupper: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette forbindelse til databasen: " + e.getMessage(), e);
        }
        return result;
    }
}

