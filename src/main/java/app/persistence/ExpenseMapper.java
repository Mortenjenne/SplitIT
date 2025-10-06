package app.persistence;

import app.entities.Expense;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseMapper {

        public Expense createExpense(int userId, int groupId, String description, double amount) throws DatabaseException {
            String sql = "INSERT INTO expense (user_id, group_id, description, amount) VALUES (?,?,?,?)";
            Expense expense = null;

            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, userId);
                ps.setInt(2, groupId);
                ps.setString(3,description);
                ps.setDouble(4,amount);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1) {
                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    int expenseId = rs.getInt(1);

                    expense = new Expense(expenseId, userId, groupId, description, amount, new Timestamp(System.currentTimeMillis()));
                    }
            } catch (SQLException e) {
                throw new DatabaseException("Couldn't create expense: " + e.getMessage());
            }
            return expense;
        }

        public Expense getExpenseById(int expenseId) throws DatabaseException
        {
            Expense expense = null;

            String sql = "select * from expense e join users u on u.user_id = e.user_id WHERE e.expense_id = ?";

            try (
                    Connection connection = ConnectionPool.getInstance().getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                ps.setInt(1, expenseId);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                {
                    int userId = rs.getInt("user_id");
                    int groupId = rs.getInt("group_id");
                    String description = rs.getString("description");
                    double amount = rs.getDouble("amount");
                    Timestamp timeStamp = rs.getTimestamp("created_at");


                    expense = new Expense(expenseId,userId,groupId,description,amount, timeStamp);
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Fejl ved hentning af expense med id = " + expenseId + e.getMessage());
            }
            return expense;
        }

        public List<Post> getAllPosts() throws DatabaseException
        {
            List<Post> posts = new ArrayList<>();
            String sql = "SELECT p.post_id, p.title, p.message, p.image, u.username AS author_name, p.created_at, p.user_id\n" +
                    "FROM post p\n" +
                    "JOIN users u ON u.user_id = p.user_id;";

            try (
                    Connection connection = ConnectionPool.getInstance().getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {

                ResultSet rs = ps.executeQuery();
                while (rs.next())
                {
                    int postId = rs.getInt("post_id");
                    String title = rs.getString("title");
                    String message = rs.getString("message");
                    String authorName = rs.getString("author_name");
                    int userId = rs.getInt("user_id");
                    Timestamp timeStamp = rs.getTimestamp("created_at");
                    byte[] image = rs.getBytes("image");
                    posts.add(new Post(postId, title, message, authorName, timeStamp,userId,image));
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Fejl ved hentning af alle filer!!!!" + e.getMessage());
            }
            return posts;
        }

        public boolean updatePost(int postId, String title, String message, byte[] image) throws DatabaseException {

            String sql = "UPDATE post SET title = ?, message = ?, image = ? WHERE post_id = ?";
            boolean result = false;

            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, title);
                ps.setString(2, message);

                if (image != null) {
                    ps.setBytes(3, image);
                } else {
                    ps.setNull(3, java.sql.Types.BINARY);
                }

                ps.setInt(4, postId);

                int rowsAffected = ps.executeUpdate();

                if(rowsAffected == 1){
                    result = true;
                }

            } catch (SQLException e) {
                throw new DatabaseException("Couldn't update post: " + e.getMessage());
            }
            return result;
        }

        public int getTotalPostUpvoteCount(int postId) throws DatabaseException {
            String sql = "SELECT COUNT(*) AS total \n" +
                    "FROM post_upvotes \n" +
                    "WHERE post_id = ?";
            int result = 0;

            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1, postId);
                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    result = rs.getInt("total");
                }

            } catch (SQLException e) {
                throw new DatabaseException("Couldn't get total upvotes " + e.getMessage());
            }

            return result;
        }

        public boolean upvotePost(int userId, int postId) throws DatabaseException {
            String sql = "INSERT INTO post_upvotes (user_id, post_id) \n" +
                    "VALUES (?, ?)";
            boolean result = false;

            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ps.setInt(2,postId);
                int rowsAffected = ps.executeUpdate();

                if(rowsAffected == 1){
                    result = true;
                } else {
                    throw new DatabaseException("No post found with id: " + postId);
                }

            } catch (SQLException e) {
                throw new DatabaseException("Couldn't upvote post: " + e.getMessage());
            }

            return result;
        }

        public boolean deleteUserUpVote(int user_id, int post_id) throws DatabaseException {
            String sql = "DELETE FROM post_upvotes \n" +
                    "WHERE user_id = ? AND post_id = ?";
            boolean result = false;

            try(Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1,user_id);
                ps.setInt(2,post_id);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1)
                {
                    result = true;
                }

            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af kommentare vha. userId og postId");
            }
            return result;
        }

        public boolean hasUserUpVotedPost(int user_id, int post_id) throws DatabaseException {
            String sql = "SELECT COUNT(*) \n" +
                    "FROM post_upvotes \n" +
                    "WHERE user_id = ? AND post_id = ?";
            boolean result = false;

            try(Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1,user_id);
                ps.setInt(2,post_id);

                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    result = rs.getInt(1) > 0;
                }

            } catch (SQLException e) {
                throw new DatabaseException("Fejl ved hentning af kommentare vha. userId og postId");
            }
            return result;
        }

        public boolean delete(int postId) throws DatabaseException
        {
            String sql = "delete from post where post_id = ?";
            boolean result = false;

            try (
                    Connection connection = ConnectionPool.getInstance().getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                ps.setInt(1, postId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Fejl ved sletning af en post" + e.getMessage());
            }
            return result;
        }

    }

}
