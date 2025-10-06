package app.persistence;

import app.entities.Group;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class GroupMapperTest {
    private static ConnectionPool testPool;
    private GroupMapper groupMapper;

    @BeforeAll
    static void setUpDatabase(){
        testPool = ConnectionPool.getInstance(
                "postgres",
                "postgres",
                "jdbc:postgresql://localhost:5432/spiltit?currentSchema=test",
                "spiltit"
        );

        try (Connection con = testPool.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS groups CASCADE");
            stmt.execute("CREATE TABLE groups (" +
                    "group_id SERIAL PRIMARY KEY," +
                    "name VARCHAR(50) NOT NULL" +
                    ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            fail("Database connection failed");
        }
    }


    @BeforeEach
    void setUp() {
        groupMapper = new GroupMapper(testPool);

        try (Connection con = testPool.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute("DELETE FROM groups CASCADE");

            stmt.execute("ALTER SEQUENCE groups_group_id_seq RESTART WITH 1");

            stmt.execute("INSERT INTO groups (name) VALUES " +
                    "('Festival'), " +
                    "('Polterabend'), " +
                    "('Norge 2025'), " +
                    "('Sommerhus Oktober')");

        } catch (SQLException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @Test
    void testConnection() throws SQLException {
        assertNotNull(testPool.getConnection());
    }

    @Test
    void testGetGroupById() throws DatabaseException {
        Group group = groupMapper.getGroupById(1);
        assertNotNull(group);
        assertEquals(group,new Group(1,"Festival"));
    }

    @Test
    void testDeleteGroup() throws DatabaseException {
        boolean isDeleted = groupMapper.deleteGroup(1);
        assertTrue(isDeleted);
        int expectedNumOfGroups = groupMapper.getAllGroups().size();
        assertEquals(3,expectedNumOfGroups);
    }

    @Test
    void createGroup() {
    }

    @Test
    void updateGroup() {
    }
}