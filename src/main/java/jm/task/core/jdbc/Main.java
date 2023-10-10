
//Алгоритм работы приложения
// В методе main класса Main должны происходить следующие операции:
//
//Создание таблицы User(ов)
//
//Добавление 4 User(ов) в таблицу с данными на свой выбор.
// После каждого добавления должен быть вывод в консоль (User с именем –name добавлен в базу данных)
//
//Получение всех User из базы и вывод в консоль (должен быть переопределен toStringв классе User)
//
//Очистка таблицы User(ов)
//
//Удаление таблицы
package jm.task.core.jdbc;

import jm.task.core.jdbc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jm.task.core.jdbc.util.Util.getConnectionDB;

public class Main {

    public static void main(String[] args) {

        // Создали таблицу пользователей
        createTable();

        // Добавили пользователей в таблицу
        insertAllUsers();

        // Выборка пользователей из таблицы
        selectAllUsers();

        // Удаление всех пользователей из табицы
        deleteAllUsers();

        // Удаление таблицы пользователей
        dropUsersTable();
    }



    private static boolean createTable() {
        try (Connection connection = getConnectionDB();
             Statement statement = connection.createStatement()) {

            String query = "CREATE TABLE IF NOT EXISTS \"user\" " +
                    "(id bigserial primary key, name varchar(255), last_name varchar(255), age SMALLINT);";
            return !statement.execute(query);

        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Добавить группу пользователей.
     */
    private static void insertAllUsers() {
        List<User> userList = createUsersEntity();
        for (User item : userList) {
            saveUser(item);
        }
    }

    private static List<User> createUsersEntity() {
        List<User> result = new ArrayList<>();

        User userOne = new User();
        userOne.setName("testNameOne");
        userOne.setLastName("testLastNameOne");
        userOne.setAge((byte) 10);
        result.add(userOne);

        User userTwo = new User();
        userTwo.setName("testNameTwo");
        userTwo.setLastName("testLastNameTwo");
        userTwo.setAge((byte) 10);
        result.add(userTwo);

        User userThree = new User();
        userThree.setName("testNameThree");
        userThree.setLastName("testLastNameThree");
        userThree.setAge((byte) 10);
        result.add(userThree);

        User userFour = new User();
        userFour.setName("testNameFour");
        userFour.setLastName("testLastNameFour");
        userFour.setAge((byte) 10);
        result.add(userFour);

        return result;
    }

    /**
     * Сохранить в БД одного пользователя.
     *
     * @param user Сущность пользователя.
     * @return Признак успешности операции сохранения в БД.
     */
    private static boolean saveUser(User user) {
        String query = "INSERT INTO \"user\" (name, last_name, age) VALUES (?, ?, ?)";

        try (Connection connection = getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setByte(3, user.getAge());

            if (preparedStatement.executeUpdate() > 0) {
                System.out.println("Пользователь с именем: " + user.getName() +
                        " успешно добавлен в таблицу.");
                return true;
            }
            return false;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Выборка всех пользователей из БД, без фильтров.
     */
    private static List<User> selectAllUsers() {
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

    /**
     * Удаление всех пользователей из БД.
     * @throws SQLException
     */
    private static void deleteAllUsers() {
        List<User> userList = selectAllUsers();

        if (!userList.isEmpty()) {
            for (User user : userList) {
                deleteUserById(user.getId());
            }
        }
    }

    /**
     * Удалить одного пользователя из БД по ID.
     *
     * @param id ID пользователя.
     * @return Признак успешности операции удаления в БД.
     */
    public static boolean deleteUserById(Long id) {
        String query = "DELETE FROM \"user\" WHERE id = (?)";

        try (Connection connection = getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удалить таблицу.
     * @return
     */
    private static boolean dropUsersTable() {
        String query = "DROP TABLE IF EXISTS \"user\";";
        try (Connection connection = getConnectionDB();
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query) > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
