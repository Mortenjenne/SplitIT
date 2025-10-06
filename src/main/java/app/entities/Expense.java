package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Expense {
    private int expenseId;
    private int userId;
    private int groupId;
    private String description;
    private double amount;
    private Timestamp createdAt;
}
