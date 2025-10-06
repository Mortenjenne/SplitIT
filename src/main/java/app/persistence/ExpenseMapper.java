package app.persistence;

import app.entities.Expense;
import app.exceptions.DatabaseException;
import java.sql.*;

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
                throw new DatabaseException("Kunne ikke oprette udgift " + e.getMessage());
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
                throw new DatabaseException("Fejl ved hentning af udgift med id = " + expenseId + e.getMessage());
            }
            return expense;
        }

    public Expense getExpenseAndUserById(int expenseId) throws DatabaseException
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
            throw new DatabaseException("Fejl ved hentning af udgift med id = " + expenseId + e.getMessage());
        }
        return expense;
    }


        public boolean updateExpense(int expenseId, String description, double amount) throws DatabaseException {

            String sql = "UPDATE expense SET description = ?, amount = ? WHERE exspense_id = ?";
            boolean result = false;

            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, description);
                ps.setDouble(2, amount);
                ps.setInt(3,expenseId);

                int rowsAffected = ps.executeUpdate();

                if(rowsAffected == 1){
                    result = true;
                }

            } catch (SQLException e) {
                throw new DatabaseException("Kunne ikke opdatere udgift " + e.getMessage());
            }
            return result;
        }


        public boolean deleteExpense(int expenseId) throws DatabaseException
        {
            String sql = "delete from expense where expence_id = ?";
            boolean result = false;

            try (
                    Connection connection = ConnectionPool.getInstance().getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                ps.setInt(1, expenseId);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Fejl ved sletning af en udgift" + e.getMessage());
            }
            return result;
        }

    }

