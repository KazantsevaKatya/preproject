package jm.task.core.jdbc.service;

import jm.task.core.jdbc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jm.task.core.jdbc.util.Util.getConnectionDB;

public class UserServiceImpl implements UserService {


    public void createUsersTable() {
        try (Connection connection = getConnectionDB();
             Statement statement = connection.createStatement()) {

            String query = "CREATE TABLE IF NOT EXISTS \"user\" " +
                    "(id bigserial primary key, name varchar(255), last_name varchar(255), age SMALLINT);";
            statement.execute(query);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void dropUsersTable() {
        String query = "DROP TABLE IF EXISTS \"user\";";
        try (Connection connection = getConnectionDB();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        String query = "INSERT INTO \"user\" (name, last_name, age) VALUES (?, ?, ?)";

        try (Connection connection = getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);

            if (preparedStatement.executeUpdate() > 0) {
                System.out.println("Пользователь с именем: " + name +
                        " успешно добавлен в таблицу.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeUserById(long id) {
        String query = "DELETE FROM \"user\" WHERE id = (?)";

        try (Connection connection = getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectUsersQuery = "SELECT * FROM \"user\"";

        try (Connection connection = getConnectionDB();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(selectUsersQuery);

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getByte("age"));
                userList.add(user);
                System.out.println(user);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return userList;
    }

    public void cleanUsersTable() {
        List<User> userList = getAllUsers();

        if (!userList.isEmpty()) {
            for (User user : userList) {
                removeUserById(user.getId());
            }
        }
    }
}
